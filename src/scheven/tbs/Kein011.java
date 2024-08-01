/**
 *
 */
package scheven.tbs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SystematikComparator;

/**
 * @author baumann
 *
 */
public class Kein011 {

	static String folder = "D:/Analysen/scheven";

	/**
	 * Die SWW, deren Werte von 011 und 012 voneinander abweichen.
	 */
	final static Set<String> gndIDs = new HashSet<>();

	/**
	 * 51XX mit DE-101.
	 */
	private static HashSet<Record> ohneKarten = new HashSet<>();
	private static HashSet<Record> geoMitKarten = new HashSet<>();
	static final Comparator<Record> myComp = Comparator
			.comparing(GNDUtils::getRecordType)
			.thenComparing(GNDUtils::getFirstGNDClassification,
					new SystematikComparator());

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final HashMap<String, Record> idn2rec = new HashMap<>();

		final String input = folder + "/" + "011vs012.txt";
		final RecordReader swReader = RecordReader.getMatchingReader(input);
		swReader.forEach(gndRec ->
		{
			RecordUtils.retainTags(gndRec, "003@", "005", "065", "100", "110",
					"111", "130", "150", "151");
			final String id = gndRec.getId();
			gndIDs.add(id);
			idn2rec.put(id, gndRec);
		});

		output(folder + "/" + "kein011_alle.txt", idn2rec.values());

		final RecordReader titReader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_D);
		titReader.setStreamFilter(new ContainsTag("5100", BibTagDB.getDB()));
		titReader.forEach(bibrec ->
		{

			final List<String> bibs = SubjectUtils.vergebendeBib51X9(bibrec);
			final List<Integer> indices = ListUtils.findMatchingIndices(bibs,
					"DE-101");
			indices.forEach(i ->
			{
				final List<Line> linesRSWK = SubjectUtils
						.getRSWKSequence(bibrec, i + 1);
				final List<String> rswkIds = SubfieldUtils
						.getContents(linesRSWK, '9');
				final Set<String> intersection = CollectionUtils
						.intersection(rswkIds, gndIDs);
				if (BibRecUtils.isKarte(bibrec)) {
					intersection
							.forEach(id -> geoMitKarten.add(idn2rec.get(id)));
				} else {
					intersection.forEach(id -> ohneKarten.add(idn2rec.get(id)));
				}
			});

		});

		// Ausgabe:
		output(folder + "/" + "kein011_in_5100_ohne_Karten.txt", ohneKarten);
		output(folder + "/" + "kein011_in_5100_Karten.txt", geoMitKarten);

	}

	/**
	 * @param filename
	 * @throws IOException
	 */
	private static void output(final String filename,
			final Collection<Record> records) throws IOException {
		final PrintWriter out = FileUtils.outputFile(filename, false);

		final List<Record> recsFound = new ArrayList<>(records);
		recsFound.sort(myComp);
		recsFound.forEach(
				rec -> out.println(StringUtils.concatenateTab(rec.getId(),
						RecordUtils.getDatatype(rec),
						GNDUtils.getFirstGNDClassification(rec),
						GNDUtils.getNameOfRecord(rec))));
		FileUtils.safeClose(out);
	}

}
