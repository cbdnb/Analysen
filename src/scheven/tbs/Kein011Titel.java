/**
 *
 */
package scheven.tbs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SystematikComparator;

/**
 * @author baumann
 *
 */
public class Kein011Titel {

	static String folder = "D:/Analysen/scheven";

	/**
	 * Die SWW, deren Werte von 011 und 012 voneinander abweichen.
	 */
	static final HashMap<String, Record> gndIdn2rec = new HashMap<>();

	static final Comparator<Record> myComp = Comparator
			.comparing(GNDUtils::getRecordType)
			.thenComparing(GNDUtils::getFirstGNDClassification,
					new SystematikComparator());

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		// GND-Download einlesen:
		System.err.println("GND lesen");
		final String input = folder + "/" + "011vs012.txt";
		final RecordReader swReader = RecordReader.getMatchingReader(input);
		swReader.forEach(gndRec ->
		{
			RecordUtils.retainTags(gndRec, "003@", "005", "065", "100", "110",
					"111", "130", "150", "151");
			final String id = gndRec.getId();
			gndIdn2rec.put(id, gndRec);
		});

		// Titel analysieren und ausgeben:
		System.err.println("Titel lesen");
		final PrintWriter out = FileUtils.outputFile(
				folder + "/" + "kein011_in_5100_mit_Titel.txt", false);

		final RecordReader titReader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_D);
		titReader.setStreamFilter(new ContainsTag("5100", BibTagDB.getDB()));
		titReader.forEach(bibrec ->
		{

			if (BibRecUtils.isKarte(bibrec))
				return;
			final List<String> bibs = SubjectUtils.vergebendeBib51X9(bibrec);
			final List<Integer> indices = ListUtils.findMatchingIndices(bibs,
					"DE-101");
			indices.forEach(i ->
			{
				final List<Line> linesRSWK = SubjectUtils
						.getRSWKSequence(bibrec, i + 1);
				final List<String> rswkIds = SubfieldUtils
						.getContents(linesRSWK, '9');
				rswkIds.forEach(gndID ->
				{
					if (gndIdn2rec.keySet().contains(gndID)) {
						final Record gndrRecord = gndIdn2rec.get(gndID);
						final String type = RecordUtils.getDatatype(gndrRecord);
						final String classif = GNDUtils
								.getFirstGNDClassification(gndrRecord);
						final String nameGND = GNDUtils
								.getNameOfRecord(gndrRecord);
						final String idTit = bibrec.getId();
						final String dhs = SGUtils.getDhsStringPair(bibrec).first;
						final String title = BibRecUtils.getMainTitle(bibrec);
						out.println((StringUtils.concatenateTab(gndID, type,
								classif, nameGND, idTit, dhs, title)));
					}
				});
			});
		});
		FileUtils.safeClose(out);

	}

}
