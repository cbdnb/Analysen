/**
 *
 */
package scheven.verbuende;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.utils.ISBNCounter;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import de.dnb.ie.utils.AcDatabase;

/**
 * @author baumann
 *
 */
public class Verbundstatistik20XX {

	/**
	 *
	 */
	private static final String JAHR = "17";

	private static String OUT_PATH = "D:/Analysen/henze/Statistik_20" + JAHR;

	private static PrintStream printStreamDiss;

	private static PrintStream printStreamBell;

	private static PrintStream printStreamRest;

	private static PrintStream printStreamKinder;

	private static PrintStream printStreamError;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		printStreamDiss = new PrintStream(OUT_PATH + "_Diss.txt");
		printStreamBell = new PrintStream(OUT_PATH + "_Bellet.txt");
		printStreamKinder = new PrintStream(OUT_PATH + "_Kinder.txt");
		printStreamRest = new PrintStream(OUT_PATH + "_Rest.txt");
		printStreamError = new PrintStream(OUT_PATH + "_Error.txt");

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_D);
		// WV 2015
		final Predicate<String> streamFilter = new ContainsTag("2105", '0',
				JAHR + ",A", BibTagDB.getDB());
		reader.setStreamFilter(streamFilter);
		reader.setByteLogging(true);

		final StatusAndCodeFilter filterStatus = StatusAndCodeFilter
				.filterGedrucktKeineUebersetzung();

		reader.stream().filter(filterStatus).forEach(record ->
		{
			System.err.println(reader.getBytesRead() / 1000000L + "MB");
			final String idn = record.getId();

			final String isbn = BibRecUtils.getBestISBN(record);
			if (isbn == null)
				return;

			final String sg = findSG(record);
			if (sg == null)
				return;

			final String title = BibRecUtils.getMainTitle(record);

			final String verlag = BibRecUtils.getNameOfFirstProducer(record);

			final String jahr = BibRecUtils.getYearOfPublicationString(record);

			final List<Integer> countList = ISBNCounter.getTotalCount(isbn);

			final String counts = StringUtils.concatenate("\t", countList);

			final String outLine = StringUtils.concatenate("\t", idn, sg, title,
					verlag, jahr, counts);

			//@formatter:on
			if (BibRecUtils.istHochschulschrift(record))
				printStreamDiss.println(outLine);
			else if (BibRecUtils.istBelletristik(record))
				printStreamBell.println(outLine);
			else if (BibRecUtils.istKinderbuch(record))
				printStreamKinder.println(outLine);
			else
				printStreamRest.println(outLine);

		});

		StreamUtils.safeClose(printStreamDiss);
		StreamUtils.safeClose(printStreamBell);
		StreamUtils.safeClose(printStreamRest);
		StreamUtils.safeClose(printStreamKinder);
		reader.close();

	}

	/**
	 * @param record
	 * @param idn
	 * @return
	 */
	public static String findSG(final Record record) {
		String sg = SGUtils.getFullDHSString(record, null);
		if (sg == null) { // übergeordneten versuchen
			final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
			if (idnBroader != null) {
				final Pair<String, TIEFE> pair = AcDatabase
						.getStatus(idnBroader);
				if (pair != null) {
					sg = pair.first;
				} else {
					printStreamError.println("Keine SG für: " + record.getId()
							+ " übergeordnet: " + idnBroader);
				}
			} else {
				printStreamError
						.println("Konnte keinen übergeordneten Satz finden: "
								+ record.getId());
			}
		}
		return sg;
	}

}
