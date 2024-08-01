/**
 *
 */
package baumann.alteSWW;

import java.io.FileNotFoundException;
import java.io.PrintStream;
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
public class FindePerson {

	static List<MyRecord> findSachRecords(String hsw) {
		// normieren:
		hsw = hsw.replace('<', '(');
		hsw = hsw.replace('>', ')');
		List<MyRecord> trefferListe = GND_DB2.getMyRecordsByName(hsw);
		if (!trefferListe.isEmpty())
			return trefferListe;
		trefferListe = GND_DB2.getMyRecordsByVWW(hsw);
		if (!trefferListe.isEmpty())
			return trefferListe;
		return Collections.emptyList();
	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args) throws FileNotFoundException {

		final Collection<String> swwNr = StringUtils.readLinesFromClip();
		System.err.println(swwNr.size());
		PERS_DB.load();
		GND_DB2.load();
		final PrintStream persDat = new PrintStream(
				PERS_DB.FOLDER_AND_PREFIX + "Personen.txt");
		swwNr.forEach(swN ->
		{
			final String[] split = swN.split("\t");
			final String sw = split[0];
			// sicherheitshalber:
			if (!findSachRecords(sw).isEmpty())
				return;
			final List<MyRecord> recsDupl = PERS_DB.getMyRecordsByName(sw);
			final List<MyRecord> recs = recsDupl.stream().distinct()
					.collect(Collectors.toList());
			List<MyRecord> newRecs = recs;
			if (recs.size() > FindeSW.MAX) {
				newRecs = FilterUtils.newFilteredList(recs,
						rec -> rec.satzart.contains("Ts"));
				// FilterUtils.filter(recs, rec -> rec.satzart.contains("Ts"));
				if (newRecs.size() > FindeSW.MAX)
					newRecs = newRecs.subList(0, FindeSW.MAX);
				else if (newRecs.isEmpty())
					newRecs = recs.subList(0, FindeSW.MAX);
			}

			if (!newRecs.isEmpty()) {
				final String cell = FindeSW.makeExcelRow(newRecs);
				System.err.println(swN + ": " + cell);
				persDat.println(swN + "\t" + cell);
			}
		});
		// sww.stream().map(FindeSW::findRecords).map(FindeSW::makeExcelCell)
		// .forEach(test1::println);
		StreamUtils.safeClose(persDat);

	}

}
