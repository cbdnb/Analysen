/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.BoundedPriorityQueue;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class IE extends DownloadWorker {

	private static final String JJJJ = "2025";

	private static final int CAPACITY = 5;

	private static final BoundedPriorityQueue<Triplett<String, String, Long>> DDCS = new BoundedPriorityQueue<>(
			CAPACITY, BuchstabenZaehler.maxComparator);

	private static final BoundedPriorityQueue<Pair<String, Integer>> KETTEN_ZAHLEN = new BoundedPriorityQueue<>(
			CAPACITY, Comparator.comparing(Pair::getSecond));

	public static final Comparator<Triplett<String, List<Line>, Long>> LIST_COMPARATOR = Comparator
			.comparing(Triplett::getThird);

	private static final BoundedPriorityQueue<Triplett<String, List<Line>, Long>> ALLE_SWW = new BoundedPriorityQueue<>(
			CAPACITY, LIST_COMPARATOR);

	private static final BoundedPriorityQueue<Triplett<String, List<Line>, Long>> KETTEN = new BoundedPriorityQueue<>(
			CAPACITY, LIST_COMPARATOR);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final IE ie = new IE();
		ie.setStreamFilter(new StringContains("StatIE")
				.and(new StringContains(JJJJ + "-")));
		ie.setOutputFile(
				"D:/Analysen/baumann/skurriles/ie_am_titel_" + JJJJ + ".txt");
		ie.processGZipFile(Constants.TITEL_PLUS_EXEMPLAR_Z);

		ie.println("DDC");
		ie.printIterable(DDCS.ordered());
		ie.println();

		ie.println("Zahl der Ketten:");
		ie.printIterable(KETTEN_ZAHLEN.ordered());
		ie.println();

		ie.println("Längste Ketten:");
		KETTEN.ordered().forEach(kettenTrip ->
		{
			final String id = kettenTrip.first;
			final long len = kettenTrip.third;
			ie.println(id + ", Länge: " + len);
			ie.printIterable(kettenTrip.second);
			ie.println();
		});

		ie.println("Meiste SWW");
		ALLE_SWW.ordered().forEach(alleTrip ->
		{
			final String id = alleTrip.first;
			final long len = alleTrip.third;
			ie.println(id + ", Anzahl: " + len);
			ie.printIterable(alleTrip.second);
			ie.println();
		});

	}

	@Override
	protected void processRecord(final Record record) {
		final ArrayList<Line> ieStatLines = BibRecUtils.getIEStatistik(record);
		boolean found = false;
		for (final Line line : ieStatLines) {
			final String date = SubfieldUtils.getDollarD(line);
			if (date.startsWith(JJJJ))
				found = true;
		}
		if (!found)
			return;

		final String id = record.getId();
		final List<String> ddcs = SubjectUtils.getCompleteDDCNotations(record);
		ddcs.forEach(ddc ->
		{
			final int ddcLen = ddc.length();
			final Triplett<String, String, Long> newTriplett = new Triplett<String, String, Long>(
					id, ddc, (long) ddcLen);
			DDCS.add(newTriplett);

		});

		final int anzahlKetten = SubjectUtils.getNumberOfRSWKSequenzes(record);
		final Pair<String, Integer> newPair = new Pair<String, Integer>(id,
				anzahlKetten);
		KETTEN_ZAHLEN.add(newPair);

		for (int i = 1; i <= anzahlKetten; i++) {
			try {
				final List<Line> kette = SubjectUtils.getRSWKSequence(record,
						i);
				final int len = kette.size();
				final Triplett<String, List<Line>, Long> newTriplett = new Triplett<String, List<Line>, Long>(
						id, kette, (long) len);
				KETTEN.add(newTriplett);

			} catch (final IllegalArgumentException e) {
				System.err.println(id + "Fehlende Kette " + i);
				System.err.println();
			}
		}

		final List<Line> rswkLines = SubjectUtils.getRSWKLines(record);
		final int anzSWW = rswkLines.size();
		final Triplett<String, List<Line>, Long> newTriplett = new Triplett<String, List<Line>, Long>(
				id, rswkLines, (long) anzSWW);
		ALLE_SWW.add(newTriplett);

	}

}
