/**
 *
 */
package baumann.musik;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import baumann.TuDatabase;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.filtering.Between;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.PersonUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class MusikerVerteilung2 {

	private static Map<String, Pair<String, Point2D>> idn2NameKoo;
	private static CrossProductFrequency orteEpochenCount = new CrossProductFrequency();
	private static Frequency<String> komponistenCount = new Frequency<>();
	private static Map<String, String> tu2Comp;

	// Hilfsgrößen, Zeile und Spalte der Tabelle:
	private static final Set<Integer> epochen = new TreeSet<>();
	private static final Set<String> ortIDNs = new HashSet<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		System.err.println("titel->Komponist");
		tu2Comp = TuDatabase.getTu2Komponist();
		final Set<String> musikWerke = tu2Comp.keySet();
		System.err.println("Titeldaten flöhen");
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);

		final Predicate<String> filter3210 = new ContainsTag("3210",
				BibTagDB.getDB());
		final Predicate<String> filter3211 = new ContainsTag("3211",
				BibTagDB.getDB());
		final Predicate<String> filter5100 = new ContainsTag("5100",
				BibTagDB.getDB());

		final Predicate<String> streamFilterEST = filter3210.or(filter3211);
		reader.setStreamFilter(filter5100.or(streamFilterEST));
		int counter = 0;

		// 1. iteration über Titel:
		for (final Record record : reader) {

			final List<String> idnsTu = RecordUtils.getContentsOfFirstSubfield(record, '9',
					"3210", "3211");
			// Wir wissen noch nicht, ob da ein Musikwerk drin ist:
			idnsTu.addAll(SubjectUtils.getRSWKidsSet(record));
			// Aber jetzt:
			idnsTu.retainAll(musikWerke);
			if (idnsTu.isEmpty())
				continue;

			counter++;
			// if (counter > 3000)
			// break;

			// System.err.println(record);

			idnsTu.forEach(idnTu ->
			{
				final String komp = tu2Comp.get(idnTu);
				komponistenCount.add(komp);
			});
		}
		;

		// Aufräumen:
		StreamUtils.safeClose(reader);
		tu2Comp = null;
		System.gc();
		counter = 0;

		idn2NameKoo = OrteMitKoordinatenDB.getAngereichernteMap();
		System.err.println("Personen flöhen");
		final RecordReader persReader = RecordReader
				.getMatchingReader(Constants.Tp);
		persReader.setStreamFilter(new ContainsTag("548", GNDTagDB.getDB()));

		// 2. Iteration über Personen:
		for (final Record record : persReader) {

			// if (counter > 30000)
			// break;
			final String idn = record.getId();

			final long anzahlSeinerWerke = komponistenCount.get(idn);
			if (anzahlSeinerWerke == 0L)
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
					orteEpochenCount.incrementValues(anzahlSeinerWerke, ortID,
							Integer.toString(epoche));
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
		MusikerVerteilung1.ausgabe(orteEpochenCount,
				"D:\\Analysen\\baumann\\musiker_aufführung.txt");
	}

	/**
	 * @throws IOException
	 *
	 */
	public static void ausgabe() throws IOException {

		final PrintStream stream = new PrintStream(
				"D:\\Analysen\\baumann\\musiker_geo_2.txt");

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
