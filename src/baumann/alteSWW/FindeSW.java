/**
 *
 */
package baumann.alteSWW;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.FilterUtils;

/**
 * @author baumann
 *
 */
public class FindeSW {

	static final int MAX = 3;

	static String makeExcelCell(final Collection<MyRecord> records) {
		final ArrayList<String> strings = FilterUtils.map(records,
				MyRecord::toStringShort);
		return StringUtils.makeExcelCellFromCollection(strings);
	}

	static String makeExcelRow(final Collection<MyRecord> records) {
		final ArrayList<String> strings = FilterUtils.map(records,
				MyRecord::toCells);
		return StringUtils.concatenate("\t", strings);
	}

	static List<MyRecord> findRecordsTrunk(String hsw) {
		// normieren:
		hsw = hsw.replace('<', '(');
		hsw = hsw.replace('>', ')');
		List<MyRecord> trefferListe = GND_DB2.getMyRecordsByName(hsw);
		if (!trefferListe.isEmpty())
			return trefferListe;
		trefferListe = GND_DB2.getMyRecordsByVWW(hsw);
		if (!trefferListe.isEmpty())
			return trefferListe;

		final int len = hsw.length();
		// Abstand vs. Abstände
		for (int i = 0; i < len * 3 / 5; i++) {
			hsw = hsw.substring(0, len - i);

			trefferListe = GND_DB2.getMyRecordsByName(hsw);
			if (!trefferListe.isEmpty())
				return trefferListe;
			trefferListe = GND_DB2.getMyRecordsByVWW(hsw);
			if (!trefferListe.isEmpty())
				return trefferListe;
			trefferListe = GND_DB2.getKoeRecords(hsw);
			if (!trefferListe.isEmpty())
				return trefferListe;
			// jetzt trunkiert suchen:
			trefferListe = GND_DB2.getRecordsByNamePrefix(hsw);
			if (!trefferListe.isEmpty())
				return trefferListe;
			trefferListe = GND_DB2.getRecordsByVWPrefix(hsw);
			if (!trefferListe.isEmpty())
				return trefferListe;
		}
		return Collections.emptyList();

	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args) throws FileNotFoundException {

		final Collection<String> sww = StringUtils.readLinesFromClip();
		System.err.println(sww.size());
		GND_DB2.load();
		final PrintStream test1 = new PrintStream(
				GND_DB2.FOLDER + "usw_l_2_gnd.txt");
		sww.forEach(sw ->
		{
			final List<MyRecord> recsDupl = findRecordsTrunk(sw);
			final List<MyRecord> recs = recsDupl.stream().distinct()
					.collect(Collectors.toList());
			List<MyRecord> newRecs = recs;
			if (recs.size() > MAX) {
				newRecs = FilterUtils.newFilteredList(recs,
						rec -> rec.satzart.contains("Ts"));
				// FilterUtils.filter(recs, rec -> rec.satzart.contains("Ts"));
				if (newRecs.size() > MAX)
					newRecs = newRecs.subList(0, MAX);
				else if (newRecs.isEmpty())
					newRecs = recs.subList(0, MAX);
			}

			final String cell = makeExcelRow(newRecs);
			System.err.println(cell);
			test1.println(cell);
		});
		// sww.stream().map(FindeSW::findRecords).map(FindeSW::makeExcelCell)
		// .forEach(test1::println);
		StreamUtils.safeClose(test1);

	}

}
