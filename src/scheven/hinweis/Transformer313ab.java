/**
 *
 */
package scheven.hinweis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.Multiset;
import de.dnb.basics.utils.HTMLUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.GNDPersonLine;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.line.LineParser;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.PersonUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.formatter.Pica3Formatter;

/**
 * @author baumann
 *
 */
final class Transformer313ab extends Transformer {

	private static final String SIGNATUR = "31.3ab_gib";

	private static final String FOLDER = "D:/Analysen/scheven/Hinweis/";

	/**
	 * @param typ
	 */
	Transformer313ab(final char typ) {
		super(typ);
		try {
			line008 = LineParser.parseGND("008 gib");
		} catch (final IllFormattedLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	void make1XX(final Set<Pair<String, String>> kombi, final Record record) {
		final Multimap<Character, Pair<String, String>> signatureMap = HinweisDBUtil
				.getSignatureMap(kombi);

		String feld1XX = "";

		// s:
		final List<String> anteilS = new ArrayList<>();
		final List<Pair<String, String>> dollarSs = (List<Pair<String, String>>) signatureMap
				.getNullSafe('s');
		Collections.reverse(dollarSs);
		for (final Pair<String, String> dollarS : dollarSs) {
			try {
				final Line tempLine = LineParser.parseGND(
						"150 " + HinweisDBUtil.getTrueExpansion(dollarS));
				anteilS.add(ohneDollarGmitBlank(tempLine));
			} catch (final IllFormattedLineException e) {
			}
		}
		feld1XX += StringUtils.concatenate(" ", anteilS);

		// p:
		final Collection<Pair<String, String>> dollarPs = signatureMap
				.getNullSafe('p');
		if (!dollarPs.isEmpty())
			try {
				final Pair<String, String> first = ListUtils.getFirst(dollarPs);
				Line tempLine;
				tempLine = LineParser.parseGND(
						"100 " + HinweisDBUtil.getTrueExpansion(first));
				final String name = PersonUtils
						.getName((GNDPersonLine) tempLine, false);
				// name = mitKomma(tempLine); ???

				feld1XX += " " + name;
			} catch (final IllFormattedLineException e) {
				e.printStackTrace();
			}

		// b:
		final Collection<Pair<String, String>> dollarBs = signatureMap
				.getNullSafe('b');
		if (!dollarBs.isEmpty())
			try {
				final Pair<String, String> first = ListUtils.getFirst(dollarBs);
				final Line tempLine = LineParser.parseGND(
						"110 " + HinweisDBUtil.getTrueExpansion(first));
				final String bMitKomma = mitKomma(tempLine);
				feld1XX += " " + bMitKomma + "$g"
						+ HinweisDBUtil.getGeoName(first.first);
			} catch (final IllFormattedLineException e) {
				e.printStackTrace();
			}

		// g:
		final Collection<Pair<String, String>> dollarGs = signatureMap
				.getNullSafe('g');
		if (!dollarGs.isEmpty())
			try {
				final Pair<String, String> first = ListUtils.getFirst(dollarGs);
				final Line tempLine = LineParser.parseGND(
						"151 " + HinweisDBUtil.getTrueExpansion(first));
				final String gmitKomma = mitKomma(tempLine);
				// psg
				if (!dollarPs.isEmpty()) {
					feld1XX += "$g" + gmitKomma;
				} else {
					if (dollarSs.size() == 2)// ssg
						feld1XX += "$g" + gmitKomma;
					else
						feld1XX += " " + gmitKomma;// sg
				}

			} catch (final IllFormattedLineException e) {
				e.printStackTrace();
			}

		if (DEBUG)
			feld1XX += TEXT_DEBUG;
		// Jetzt die neue Ansetzung erzeugen:
		try {
			final Line line151 = LineParser.parseGND("151 " + feld1XX);
			record.add(line151);
		} catch (final IllFormattedLineException
				| OperationNotSupportedException e) {
		}
	}

	@Override
	protected void filter() {
		db.retainIfAllRecords(record -> GNDUtils.getGNDClassifications(record)
				.contains("31.3ab")
				|| GNDUtils.getEntityTypes(record).contains("gib"));

		db.retainIfKombi(kombi ->
		{
			final Multiset<Character> sigset = new Multiset<>(
					HinweisDBUtil.getSignature(kombi));
			/*
			 * sg bs ssg psg ps ssb
			 */
			return sigset.equals(sg) || sigset.equals(bs) || sigset.equals(ssg)
					|| sigset.equals(psg) || sigset.equals(ps)
					|| sigset.equals(ssb);
		});
	}

	@Override
	public void veraerbeiteP(final Record newRecord, final String idn260,
			final String expans260)
			throws OperationNotSupportedException, IllFormattedLineException {
		newRecord.add(LineParser
				.parseGND("500 !" + idn260 + "!" + expans260 + "$4rela"));
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
		final String datum = TimeUtils.getToday();
		final String signatur = SIGNATUR + "_" + datum;
		final PrintWriter outHTML = MyFileUtils
				.outputFile(FOLDER + "test_" + signatur + ".html", false);
		final PrintWriter log = MyFileUtils
				.outputFile(FOLDER + "log_" + signatur + ".txt", false);
		final PrintWriter prod = MyFileUtils
				.outputFile(FOLDER + "produktion_" + signatur + ".txt", false);
		final PrintWriter zuloeschen = MyFileUtils
				.outputFile(FOLDER + "zu_loeschen_" + signatur + ".txt", false);

		final Transformer transformer = new Transformer313ab('g');

		transformer.db.getKombis().forEach(kombi ->
		{
			try {
				final Record newRec = transformer.transform(kombi);

				final int size = transformer.db.getRecords(kombi).size();
				outHTML.println(HTMLUtils.heading(
						size + (size == 1 ? " Hinweisdatensatz"
								: " Hinweisdatensätze") + " zur Kombination:",
						4));
				kombi.forEach(pair -> outHTML.println(
						HTMLUtils.heading(pair.toString(), 0) + "<br>"));
				outHTML.println("<br>");
				outHTML.println(Pica3Formatter.simpleHTML(newRec));
				outHTML.println("<br><br><br>");
				prod.println(RecordUtils.toPica(newRec, Format.PICA3, false,
						null, '0'));
				prod.println();
				log.println(newRec);
				log.println();
			} catch (OperationNotSupportedException
					| IllFormattedLineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		transformer.idnsHinweis.forEach(zuloeschen::println);
		System.out.println("Anzahl der neu erzeugten Datensätze: "
				+ transformer.db.getAnzahlKombinationen());
		System.out.println("Anzahl der bearbeiteten Hinweissätze: "
				+ transformer.db.getAnzahlHinweissaetze());

		MyFileUtils.safeClose(outHTML);
		MyFileUtils.safeClose(log);
		MyFileUtils.safeClose(prod);
		MyFileUtils.safeClose(zuloeschen);
	}
}