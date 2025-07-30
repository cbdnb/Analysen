/**
 *
 */
package baumann.bk;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.TreeMultimap;
import de.dnb.basics.utils.HTMLUtils;
import de.dnb.basics.utils.OutputUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Indicator;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.DefaultLineFactory;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.line.LineParser;
import de.dnb.gnd.parser.tag.DefaultGNDTag;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.formatter.Pica3Formatter;

class BKUtil {

	/**
	 *
	 */
	private static final GNDTagDB GND_DB = GNDTagDB.getDB();

	static final String OHNE_UMBRUCH = "[^\\n]+";

	static final String NUMMER = "\\d\\d\\.\\d\\d";

	static final int NUM_LEN = "12.34".length();

	static final String SYN_HEADER = "Syn\\.: ";

	static final int SYN_LEN = "Syn.: ".length();

	static final String HIER_HEADER = "Hier: ";

	static final int HIER_LEN = "Hier: ".length();

	static final String ERL_HEADER = "Erl\\.: ";

	static final int ERL_LEN = "Erl.: ".length();

	static final String VERW_HEADER = "Verw\\.: ";

	static final int VERW_LEN = "Verw.: ".length();

	static final String KOPF_P = "(" + NUMMER + " " + OHNE_UMBRUCH + ")";
	static final String SYN_P = "(\\n" + SYN_HEADER + OHNE_UMBRUCH + ")?";
	static final String ERL_P = "(\\n" + ERL_HEADER + OHNE_UMBRUCH + ")?";
	static final String HIER_P = "(\\n" + HIER_HEADER + OHNE_UMBRUCH + ")?";
	static final String VERW_P = "(\\n" + VERW_HEADER + ".+" + ")?";
	static final Pattern RECORD_P = Pattern.compile(
			KOPF_P + SYN_P + ERL_P + HIER_P + VERW_P,
			Pattern.MULTILINE + Pattern.DOTALL);

	static final Multimap<String, String> nummer2registereintrag = new TreeMultimap<>();

	private static void loadRegister() {
		if (!nummer2registereintrag.getKeySet().isEmpty())
			return;
		final Path path = FileSystems.getDefault()
				.getPath("D:/Analysen/baumann/bk_register.txt");
		List<String> lines;
		try {
			lines = Files.readAllLines(path);

		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}
		final Pattern pattern = Pattern.compile("(.+) : (" + NUMMER + ")");
		lines.forEach(line ->
		{
			final Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				final String nummer = matcher.group(2);
				final String registereintrag = matcher.group(1);
				nummer2registereintrag.add(nummer, registereintrag.trim());
			} else
				System.err.println(line);
		});
	}

	/**
	 *
	 * @param s
	 *            auch null oder whitespace
	 * @return Die Begriffe, die nach Pica Suchbegriffe wären, also Wörter ohne
	 *         Sonderzeichen
	 */
	public static Set<String> macheSuchbegriffe(final String s) {
		if (StringUtils.isNullOrWhitespace(s))
			return Collections.emptySet();
		final Pattern pattern = Pattern.compile("\\p{IsLatin}+");
		return StringUtils.getMatches(s, pattern).stream()
				.map(String::toLowerCase).collect(Collectors.toSet());
	}

	/**
	 *
	 * @param collection
	 *            auch null oder whitespace
	 * @return Die Begriffe, die nach Pica Suchbegriffe wären, also Wörter ohne
	 *         Sonderzeichen
	 */
	public static Set<String> macheSuchbegriffe(
			final Collection<String> collection) {
		if (collection == null)
			return Collections.emptySet();
		final Set<String> suchbegriffe = new LinkedHashSet<>();
		collection.forEach(suchbegriff -> suchbegriffe
				.addAll(macheSuchbegriffe(suchbegriff)));
		return suchbegriffe;
	}

	/**
	 * Parst die Druckfassung (wird vorher getrimmt) und füllt alle Felder
	 * vollständig auf. Daher können etliche Einträge mehrfach vorkommen. Bei
	 * der Umsetzung ins Pica-Format muss man die mehrfachen geeignet entfernen.
	 *
	 * @param recStr
	 *            auch null oder leer
	 * @return einen Record oder null
	 */
	public static BKRecord parseDruckfassung(final String recStr) {
		if (StringUtils.isNullOrWhitespace(recStr))
			return null;

		final Matcher recordMatcher = RECORD_P.matcher(recStr.trim());
		if (!recordMatcher.matches())
			return null;

		final BKRecord bkRecord = new BKRecord();

		final String kopf = recordMatcher.group(1);
		bkRecord.nummer = kopf.substring(0, NUM_LEN);
		final String namenS = kopf.substring(NUM_LEN + 1);
		final String[] namenSplits = namenS.split("\\, ");
		for (final String nam : namenSplits) {
			bkRecord.namen.add(nam);
		}

		loadRegister();
		final Collection<String> registerEintraege = nummer2registereintrag
				.getNullSafe(bkRecord.nummer);
		bkRecord.register.addAll(registerEintraege);

		/*
		 * Im Feld "Syn.:" stehen in der Regel Begriffe, die auch im Register
		 * stehen.
		 */
		String syn = recordMatcher.group(2);
		if (syn != null) {
			syn = syn.substring(SYN_LEN + 1); // +1 wg. \n
			final String[] syns = syn.split("\\s?;\\s?");
			bkRecord.syn = Arrays.asList(syns);
		}

		/*
		 * Feld Erl.: = Erläuterungen:
		 */
		final String erl = recordMatcher.group(3);
		if (erl != null) {
			bkRecord.erl = erl.substring(ERL_LEN + 1);
		}

		/*
		 * Feld Hier:
		 */
		String hier = recordMatcher.group(4);
		if (hier != null) {
			hier = hier.substring(HIER_LEN + 1); // +1 wg. \n
			bkRecord.hier = Arrays.asList(hier.split("\\s?;\\s?"));
		}

		/*
		 * Feld Verw.: = Verweisungen
		 */
		String verw = recordMatcher.group(5);
		if (verw != null) {
			verw = verw.substring(VERW_LEN + 1); // +1 wg. \n
			bkRecord.verw = Arrays.asList(verw.split("\\s?\\n\\s?"));
		}

		return bkRecord;
	}

	public static List<BKRecord> getRecords(final String s) {
		if (StringUtils.isNullOrWhitespace(s))
			return Collections.emptyList();
		final List<BKRecord> records = new ArrayList<>();

		// je nach Stringformat ist ein carriage return \r dabei oder nicht:
		final List<String> strings = Arrays.asList(s.split("((\r)?\n){2,}"));

		strings.forEach(rec ->
		{
			final BKRecord record = BKUtil.parseDruckfassung(rec.trim());
			if (record == null) {
				System.err.println(rec);
				System.err.println();
			} else
				records.add(record);
		});
		return records;
	}

	private static Line line005;
	private static Line line009;
	private static Line line011;

	static {
		try {
			line005 = LineParser.parseGND("005 Tk");
			line009 = LineParser.parseGND("009 002J Tk");
			line011 = LineParser.parseGND("011 kb");
		} catch (final IllFormattedLineException e) {

		}
	}

	/**
	 *
	 * @param bkRecord
	 *            Tc-Datensatz, wie er in der DNB genutzt wird. Die PPN ist
	 *            zufällig erzeugt.
	 * @return nicht null
	 */
	public static Record toDNB_Tc(final BKRecord bkRecord) {
		final Random random = new Random();
		final int r = random.nextInt(1000000) + 1;
		final Record record = new Record(IDNUtils.int2PPN(r), GND_DB);

		if (bkRecord != null) {
			try {
				record.add(line005);
				record.add(line009);
				record.add(line011);
				DefaultLineFactory factory = GNDTagDB.TAG_153.getLineFactory();
				final Collection<Subfield> subs = new LinkedList<>();
				subs.add(new Subfield(GNDTagDB.INDICATOR_153_A,
						bkRecord.nummer));
				// in der DNB ist $j nicht wiederholbar:
				subs.add(new Subfield(GNDTagDB.INDICATOR_153_J,
						StringUtils.concatenate(" ; ", bkRecord.namen)));
				factory.load(subs);
				record.add(factory.createLine());

				/*
				 * Menge an Suchbegriffen ,die Schritt für Schritt erweitert
				 * wird, um unnötige Verweisungen zu vermeiden.
				 *
				 * Priorität: name -> hier -> register -> freie Synonyme
				 */
				final Set<String> suchbegriffe = macheSuchbegriffe(
						bkRecord.namen);

				final Indicator tag553dollara = GNDTagDB.TAG_553
						.getIndicator('a');
				factory = GNDTagDB.TAG_553.getLineFactory();
				for (String hier : bkRecord.hier) {
					final Set<String> hierSuchBeg = macheSuchbegriffe(hier);
					if (suchbegriffe.containsAll(hierSuchBeg))
						continue;
					subs.clear();
					hier = hier.replace('<', '(');
					hier = hier.replace('>', ')');
					subs.add(new Subfield(tag553dollara, hier));
					subs.add(new Subfield(GNDTagDB.DOLLAR_4, "nsav"));
					factory.load(subs);
					record.add(factory.createLine());
				}
				suchbegriffe.addAll(macheSuchbegriffe(bkRecord.hier));

				/*
				 * In Feld 453 $Sa ... werden nur die Registereinträge
				 * aufgenommen, deren Suchbegriffe 1. nicht zu denene der Namen
				 * gehören 2. nicht im "Hier:"-Feld stehen
				 */
				factory = GNDTagDB.TAG_453.getLineFactory();
				final Indicator dollarS = GNDTagDB.TAG_453.getIndicator('S');
				final Subfield Sa = new Subfield(dollarS, "a");
				final Indicator tag453dollara = GNDTagDB.TAG_453
						.getIndicator('a');
				for (final String registerEintrag : bkRecord.register) {
					if (suchbegriffe
							.containsAll(macheSuchbegriffe(registerEintrag)))
						continue;
					subs.clear();
					subs.add(Sa);

					subs.add(new Subfield(tag453dollara, registerEintrag));
					factory.load(subs);
					record.add(factory.createLine());
				}
				suchbegriffe.addAll(macheSuchbegriffe(bkRecord.register));

				/*
				 * In Feld 453 $Sb ... werden nur die (freien) Synonyme
				 * aufgenommen, deren Suchbegriffe 1. nicht zu denene Namen
				 * gehören, 2. nicht zu denen der "Hier"-Verweisungen und 3.
				 * nicht zu denen der Registereinträge gehören.
				 */
				final Subfield Sb = new Subfield(dollarS, "b");
				for (final String freiesSyn : bkRecord.syn) {
					if (suchbegriffe.containsAll(macheSuchbegriffe(freiesSyn)))
						continue;
					subs.clear();
					subs.add(Sb);
					subs.add(new Subfield(tag553dollara, freiesSyn));
					factory.load(subs);
					record.add(factory.createLine());

				}

				final String erl = bkRecord.erl;
				if (erl != null) {
					final DefaultGNDTag tag900 = GNDTagDB.TAG_900;
					factory = tag900.getLineFactory();
					subs.clear();
					subs.add(new Subfield(tag900.getIndicator('a'), erl));
					factory.load(subs);
					record.add(factory.createLine());
				}

				factory = GNDTagDB.TAG_553.getLineFactory();
				final String patS = " -> (\\d\\d\\.\\d\\d) \\(.+\\)$";
				for (String verw : bkRecord.verw) {
					subs.clear();
					verw = verw.replaceFirst(patS, " siehe: $1");
					subs.add(new Subfield(tag553dollara, verw));
					subs.add(new Subfield(GNDTagDB.DOLLAR_4, "nsiv"));
					factory.load(subs);
					record.add(factory.createLine());
				}

			} catch (final IllFormattedLineException
					| OperationNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return record;
	}

	public static void main1(final String[] args) {
		final Collection<String> lines = StringUtils.readLinesFromClip();
		System.out.println(macheSuchbegriffe(lines));
	}

	public static void main(final String[] args) {
		final String s = StringUtils.readClipboard();
		final BKRecord bkRecord = BKUtil.parseDruckfassung(s);
		System.out.println(bkRecord);
		final Record dnb_Tc = toDNB_Tc(bkRecord);

		final String txt = Pica3Formatter.toHTML(dnb_Tc);

		final String table = HTMLUtils.tableFromCells(
				Arrays.asList(Arrays.asList(txt, txt), Arrays.asList(txt, txt)),
				0, 10, 0, 16);
		System.out.println(dnb_Tc);
		OutputUtils.show(table);
	}

}