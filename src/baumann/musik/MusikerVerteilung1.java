/**
 *
 */
package baumann.musik;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.Between;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.PersonUtils;

/**
 * @author baumann
 *
 */
public class MusikerVerteilung1 {

	private static Map<String, Pair<String, Point2D>> idn2NameKoo;
	private static CrossProductFrequency orteEpochenCount = new CrossProductFrequency();
	// Hilfsgrößen, Zeile und Spalte der Tabelle:
	private static final Set<Integer> epochen = new TreeSet<>();
	private static final Set<String> ortIDNs = new HashSet<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		System.err.println("orte----");

		idn2NameKoo = OrteMitKoordinatenDB.getAngereichernteMap();

		System.err.println(idn2NameKoo.size());

		final Set<String> musikberufeIDNs = MusikberufeDatabase.getMusikberufe()
				.keySet();

		final RecordReader persReader = RecordReader
				.getMatchingReader(Constants.Tp);
		persReader.setStreamFilter(new ContainsTag("548", GNDTagDB.getDB()));
		int counter = 0;
		for (final Record record : persReader) {

			// if (counter > 30000)
			// break;

			if (!PersonUtils.isMusiker(record, musikberufeIDNs))
				continue;

			final List<String> wirkungsorteIDNs = PersonUtils
					.getWirkungsorte(record);
			wirkungsorteIDNs.retainAll(idn2NameKoo.keySet());
			if (wirkungsorteIDNs.isEmpty())
				continue;

			final Between<LocalDate> wirkungsdaten = PersonUtils
					.getWirkungsdaten(record);
			if (wirkungsdaten == null)
				continue;

			counter++;
			// System.out.println(counter);

			final int epocheAnfZehntel = Math
					.floorDiv(wirkungsdaten.lowerBound.getYear(), 10);
			final int epocheEndeZehntel = Math
					.floorDiv((wirkungsdaten.higherBound.getYear()), 10);

			for (int i = epocheAnfZehntel; i < epocheEndeZehntel; i++) {
				for (final String ortID : wirkungsorteIDNs) {
					final Integer epoche = i * 10;
					orteEpochenCount.addValues(ortID, Integer.toString(epoche));
					epochen.add(epoche);
					ortIDNs.add(ortID);
					if (epoche < 1000)
						System.err.println(record.getId());
				}
			}

		}

		StreamUtils.safeClose(persReader);
		System.out.println("fertig");
		// epochen.forEach(System.out::println);

		System.out.println("Epochen:" + epochen.size());
		System.out.println("orte: " + ortIDNs.size());
		System.out.println("gesamt: " + orteEpochenCount.size());
		// ausgabe();
		ausgabe(orteEpochenCount,
				"D:\\Analysen\\baumann\\musiker_wirkungsorte");
	}

	/**
	 * @param orteEpochenCount2
	 * @param string
	 * @throws FileNotFoundException
	 */
	public static void ausgabe(final CrossProductFrequency orteEpochenCount,
			final String datei) throws FileNotFoundException {
		final PrintStream stream = new PrintStream(datei);
		final String ueberschrift = StringUtils.concatenate("\t", "idn",
				"jahrzehnt", "count");
		stream.println(ueberschrift);
		orteEpochenCount.forEach(coll ->
		{
			// ortID, epoche
			final String first = StringUtils.concatenate("\t", coll);
			final long second = orteEpochenCount.get(coll);
			stream.println(StringUtils.concatenate("\t", first, second));
		});
		StreamUtils.safeClose(stream);

	}

	/**
	 * @throws IOException
	 *
	 */
	public static void ausgabe() throws IOException {

		final PrintStream stream = new PrintStream(
				"D:\\Analysen\\baumann\\musiker_geo_1.txt");

		final String ueberschrift = StringUtils.concatenate("\t", "idn", "name",
				"x", "y", StringUtils.concatenate("\t", epochen));
		stream.println(ueberschrift);
		ortIDNs.forEach(ortID ->
		{
			final StringBuilder out = new StringBuilder(ortID);

			final Pair<String, Point2D> nameKoo = idn2NameKoo.get(ortID);
			out.append("\t" + nameKoo.first);
			out.append("\t" + nameKoo.second.getX());
			out.append("\t" + nameKoo.second.getY());
			epochen.forEach(epocheInt ->
			{
				final String epoche = Integer.toString(epocheInt);
				final Long count = orteEpochenCount.get(ortID, epoche);
				out.append("\t" + count);
			});
			stream.println(out);

		});
		StreamUtils.safeClose(stream);

	}

}
