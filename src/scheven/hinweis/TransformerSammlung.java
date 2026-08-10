/**
 *
 */
package scheven.hinweis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.Multiset;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Field;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.Repeatability;
import de.dnb.gnd.parser.line.GNDPersonLine;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.line.LineParser;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.PersonUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.formatter.Pica3Formatter;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 */
final class TransformerSammlung extends Transformer {

	private static final List<String> BUZZWORDS = Arrays.asList("Sammlung",
			"Nachlass", "Bibliothek", "Musikbibliothek");
	private static final List<String> IDN_BUZZWORDS = Arrays.asList("041288440",
			"041238117", "040064395", "041288386");
	private static final String FILE_NAME_OUT_PART = "ps_Sammlung";

	/**
	 * @param typ
	 */
	TransformerSammlung(final char typ) {
		super(typ);
		tbs = "d;h";
		try {
			line008 = LineParser.parseGND("008 win");
		} catch (final IllFormattedLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Die Sachbegriffe (Sammlung, Nachlass) aus idnExpansionkombi werden mit
	 * der Personenangabe verkettet.
	 */
	@Override
	void make1XX(final Set<Pair<String, String>> idnExpansionkombi,
			final Record newRecord) {

		final Collection<Record> records = db.getRecords(idnExpansionkombi);
		final Set<String> moeglicheNamen = new LinkedHashSet<>();
		records.forEach(hinweisRecord ->
		{
			final List<String> dollarxe = RecordUtils.getContents(hinweisRecord,
					"1..", 'x');
			// .getContentsOfAllSubfields(hinweisRecord, "100", 'x');
			if (dollarxe.isEmpty())
				return;
			final String dollarX = ListUtils.getLast(dollarxe);
			moeglicheNamen.add(dollarX);
		});
		moeglicheNamen.removeAll(BUZZWORDS);
		// Wenn es keine moeglichen Namen gibt, dann die Alternative:
		if (moeglicheNamen.isEmpty()) {
			// oder: moeglicheNamen.size() != 1, dann kein concatenate
			make1XXAlternative(idnExpansionkombi, newRecord);
			return;
		}
		// Versuch.
		// String moeglicherName = StringUtils.concatenate(" // ",
		// moeglicheNamen);
		// Wenn es mehrere Namen gibt, dann ist das am "//" zu
		// erkennen.

		// Es wurde aber zunächste eine andere Lösung besprochen: Der längste
		// ist 130, alle anderen (so vorhanden) kommen in die 430:
		String moeglicherName = "";
		int lmax = 0;
		for (final String name : moeglicheNamen) {
			final int lakt = name.length();
			if (lakt > lmax) {
				moeglicherName = name;
				lmax = lakt;
			}
		}
		moeglicheNamen.remove(moeglicherName);

		if (DEBUG)
			moeglicherName += TEXT_DEBUG;
		// Jetzt die neue Ansetzung erzeugen:
		try {
			final Line line130 = LineParser.parseGND("130 " + moeglicherName);
			newRecord.add(line130);
			for (final String name : moeglicheNamen) {
				final Line line430 = LineParser.parseGND("430 " + name);
				newRecord.add(line430);
			}
		} catch (final IllFormattedLineException
				| OperationNotSupportedException e) {
		}
	}

	/**
	 * Die Sachbegriffe (Sammlung, Nachlass) aus idnExpansionkombi werden mit
	 * der Personenangabe verkettet.
	 */
	//
	void make1XXAlternative(final Set<Pair<String, String>> idnExpansionkombi,
			final Record newRecord) {
		final Multimap<Character, Pair<String, String>> signatureMap = Util
				.getSignatureMap(idnExpansionkombi);

		String feld1XX = "";

		// s:
		final List<String> anteilS = new ArrayList<>();
		final List<Pair<String, String>> dollarSs = (List<Pair<String, String>>) signatureMap
				.getNullSafe('s');
		Collections.reverse(dollarSs);
		for (final Pair<String, String> dollarS : dollarSs) {
			try {
				final Line tempLine = LineParser
						.parseGND("150 " + Util.getName(dollarS));
				anteilS.add(Util.ohneDollarGmitBlank(tempLine));
			} catch (final IllFormattedLineException e) {
			}
		}
		feld1XX += StringUtils.concatenate(" ", anteilS);

		// + p:
		final Collection<Pair<String, String>> dollarPs = signatureMap
				.getNullSafe('p');
		if (!dollarPs.isEmpty())
			try {
				final Pair<String, String> first = ListUtils.getFirst(dollarPs);
				// tempLine, weil man das besser umwandeln kann, wenn der String
				// geparst ist.
				final GNDPersonLine tempLine = (GNDPersonLine) LineParser
						.parseGND("100 " + Util.getName(first));
				final String name = PersonUtils.getName(tempLine, false);
				feld1XX += " " + name;
			} catch (final IllFormattedLineException e) {
				e.printStackTrace();
			}

		if (DEBUG)
			feld1XX += TEXT_DEBUG;
		// Jetzt die neue Ansetzung erzeugen:
		try {
			final Line line130 = LineParser.parseGND("130 " + feld1XX);
			newRecord.add(line130);
		} catch (final IllFormattedLineException
				| OperationNotSupportedException e) {
		}
	}

	/**
	 * Anpassen.
	 */
	@Override
	protected void filter() {
		// Sammlung (IDN
		db.retainIfKombi(kombi ->
		{
			return kombi.stream().map(Pair::getFirst)
					.anyMatch(idn -> IDN_BUZZWORDS.contains(idn));// Sammlung
		});

		db.retainIfKombi(kombi ->
		{
			final Multiset<Character> sigset = new Multiset<>(
					Util.getSignature(kombi));
			return sigset.equals(ps);
		});
	}

	/**
	 * $4 saml (Sammler)
	 */
	@Override
	public void veraerbeiteP(final Record newRecord, final String idn260,
			final String expans260)
			throws OperationNotSupportedException, IllFormattedLineException {
		newRecord.add(LineParser
				.parseGND("500 !" + idn260 + "!" + expans260 + "$4saml"));
	}

	/**
	 * @param args
	 * @throws IllFormattedLineException
	 * @throws OperationNotSupportedException
	 * @throws IOException
	 *             out
	 */
	public static void main(final String[] args)
			throws OperationNotSupportedException, IllFormattedLineException,
			IOException {
		final BiMultimap<String, String> idSamml2Tb = BiMultimap.createSetMap();
		final RecordReader tbReader = RecordReader
				.getMatchingReader(Util.FOLDER + "TbSaml.txt");
		System.err.println("Lese Tb");
		tbReader.forEach(tbRecord ->
		{
			final String tbId = GNDUtils.getNID(tbRecord);
			String name = "";
			try {
				name = RDAFormatter.getPureRDAHeading(tbRecord);
			} catch (final IllFormattedLineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String tb = name + " (NID: " + tbId + ")";
			final Collection<String> samlIds = getSammlIDs(tbRecord);
			for (final String samlID : samlIds) {
				idSamml2Tb.add(samlID, tb);
			}
		});

		final PrintWriter outHTML = MyFileUtils.outputFile(
				Util.FOLDER + "test_" + FILE_NAME_OUT_PART + ".html", false);
		final PrintWriter log = MyFileUtils.outputFile(
				Util.FOLDER + "log_" + FILE_NAME_OUT_PART + ".txt", false);
		final PrintWriter prod = MyFileUtils.outputFile(
				Util.FOLDER + "produktion_" + FILE_NAME_OUT_PART + ".txt",
				false);
		final Transformer transformer = new TransformerSammlung('u');
		final PrintWriter zuloeschen = MyFileUtils.outputFile(
				Util.FOLDER + "zu_loeschen_" + FILE_NAME_OUT_PART + ".txt",
				false);

		final Frequency<String> name2count = new Frequency<>();
		final ListMultimap<String, Record> name2records = new ListMultimap<>();

		// Erst mal Datensätze aus den Kombinationen erzeugen.
		transformer.db.getIdnExpansionKombis().forEach(kombi ->
		{
			Record newRec;
			try {
				newRec = transformer.createRecord(kombi);
				final String name = GNDUtils.getNameOfRecord(newRec);
				name2count.add(name);
				name2records.add(name, newRec);
			} catch (OperationNotSupportedException
					| IllFormattedLineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		// Da für den Namen das letzte $x ausgewertet wird, kann es sein, dass
		// mehrere Datensätze den gleichen Namen haben. Diese Datensätze müssen
		// noch gemergt und verschönert werden. Danach wird ausgegeben:
		name2records.getKeySet().forEach(name ->
		{
			final List<Record> records = (List<Record>) name2records.get(name);
			final Record destRecord = records.get(0);
			for (int i = 1; i < records.size(); i++) {
				merge(destRecord, records.get(i));
			}

			// 667 ist durch das Mergen ein wenig durcheinander:
			final ArrayList<Line> lines667 = RecordUtils.getLines(destRecord,
					"667");
			lines667.sort((l1, l2) -> l1.toString().compareTo(l2.toString()));
			RecordUtils.removeTags(destRecord, "667");
			lines667.forEach(line ->
			{
				try {
					destRecord.addWithoutDuplicates(line);
				} catch (final OperationNotSupportedException e) {
					e.printStackTrace();
				}
			});

			// in 430 stehen die ursprünglichen IDNs am Ende, z.B.:
			// $valte Vorzugsbenennung aus dem Hinweisdatensatz 7509435-6
			final Field felder430 = RecordUtils
					.getFieldGivenAsString(destRecord, "430");

			final List<String> idnList = felder430.getLines().stream()
					.filter(line ->
					{
						final String dollarV = SubfieldUtils
								.getContentOfFirstSubfield(line, 'v');
						return dollarV != null;
					}).map(line ->
					{
						final String dollarV = SubfieldUtils
								.getContentOfFirstSubfield(line, 'v');
						return dollarV
								.substring(transformer.dollarV430.length() - 2);
					}).collect(Collectors.toList());
			final int size = idnList.size();
			outHTML.println("Es gibt " + size
					+ (size == 1 ? " Hinweisdatensatz" : " Hinweisdatensätze")
					+ " zum nachfolgenden Datensatz:");
			outHTML.println(idnList + "<br>");
			// Mögliche Tb erkennen:
			final Collection<String> samlIDs = getSammlIDs(destRecord);
			final Set<String> intersection = CollectionUtils
					.intersection(samlIDs, idSamml2Tb.getKeySet());
			if (!intersection.isEmpty()) {
				outHTML.println(
						"<br>Es gibt mögliche konkurrierende Tb-Datensätze: <br>");
				final Set<String> konkurr = new HashSet<>();
				intersection.forEach(samlID -> konkurr
						.addAll(idSamml2Tb.getValueSet(samlID)));
				outHTML.println(konkurr + "<br>");
			}

			outHTML.println("<br>");
			outHTML.println(Pica3Formatter.toHTML(destRecord));
			outHTML.println("<br><br><br>");
			prod.println(RecordUtils.toPica(destRecord, Format.PICA3, false,
					null, '0'));
			prod.println();
			log.println(destRecord);
			log.println();
		});

		System.out.println("Anzahl der neu erzeugten Datensätze: "
				+ transformer.db.getAnzahlKombinationen());
		System.out.println("Anzahl der bearbeiteten Hinweissätze: "
				+ transformer.db.getAnzahlHinweissaetze());
		transformer.idnsHinweis.forEach(zuloeschen::println);
		final Collection<String> greater = name2count.getGreater(1);
		System.out.println("Anzahl der mehrfachen Namen: " + greater.size());
		System.out.println("Die mehrfachen Namen: " + greater);
		MyFileUtils.safeClose(outHTML);
		MyFileUtils.safeClose(log);
		MyFileUtils.safeClose(prod);
		MyFileUtils.safeClose(zuloeschen);
	}

	private static Collection<String> getSammlIDs(final Record record) {
		final ArrayList<Line> lines500 = RecordUtils.getLines(record, "500");
		final List<Line> samlLines = GNDUtils.getLinesWithDollar4(lines500,
				"saml");
		return SubfieldUtils.getContentsOfFirstSubfields(samlLines, '9');
	}

	/**
	 * Sammelt alle Informationen aus sourceRecord und ergänzt sie in
	 * destRecord.
	 *
	 */

	static void merge(final Record destRecord, final Record sourceRecord) {
		if (sourceRecord == null)
			return;

		// Ländercode + Syst.:
		final Set<String> ccs = new LinkedHashSet<>(
				GNDUtils.getCountryCodes(destRecord));
		ccs.addAll(GNDUtils.getCountryCodes(sourceRecord));
		if (!ccs.isEmpty()) {
			try {
				final Line line043 = LineParser
						.parseGND("043 " + StringUtils.concatenate(";", ccs));
				RecordUtils.removeTags(destRecord, "043");
				destRecord.add(line043);
			} catch (OperationNotSupportedException
					| IllFormattedLineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		final Set<String> systs = new LinkedHashSet<>(
				GNDUtils.getGNDClassifications(destRecord));
		systs.addAll(GNDUtils.getGNDClassifications(sourceRecord));
		if (!systs.isEmpty()) {
			try {
				final Line line065 = LineParser
						.parseGND("065 " + StringUtils.concatenate(";", systs));
				RecordUtils.removeTags(destRecord, "065");
				destRecord.add(line065);
			} catch (OperationNotSupportedException
					| IllFormattedLineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Alle wiederholbaren Felder werden übernommen:
		sourceRecord.getLines().forEach(line ->
		{
			try {
				if (line.getTag().repeatability == Repeatability.REPEATABLE)
					destRecord.addWithoutDuplicates(line);
			} catch (final OperationNotSupportedException e) {
				//
			}
		});
	}
}