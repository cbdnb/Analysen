/**
 *
 */
package scheven.verbuende;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.TreeMultimap;

/**
 * @author baumann
 *
 */
public class Statistik2DimMaxV0 {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final String[][] table = StringUtils.readTableFromClip();
		final MaximumMap verl2Impact = new MaximumMap();

		for (int i = 0; i < table.length; i++) {
			final String verl = table[i][0].trim();
			if (verl.equals("null"))
				continue;
			final String anzS = table[i][1];
			final int anz = Integer.parseInt(anzS);
			verl2Impact.add(verl, anz);
		}

		final Multimap<Integer, String> impact2verl = new TreeMultimap<>();

		int anzVerl = verl2Impact.getKeyCount();
		final int anzKaesten = 50;

		for (final String verl : verl2Impact) {
			final int max = verl2Impact.get(verl).iterator().next();
			impact2verl.add(max, verl);
		}

		final List<Integer> kastengrenzen = new ArrayList<>();

		for (final Integer impact : impact2verl) {
			if (impact < 10) {
				final int verlageProInpact = impact2verl.get(impact).size();
				anzVerl -= verlageProInpact;
			} else {
				break;
			}
		}
		final int zahlProKasten = anzVerl / anzKaesten;
		System.out.println("Anzahl berücksichtigter Verlage: " + anzVerl);
		System.out.println("pro Kasten: " + zahlProKasten);
		int aktuelleKastenFüllung = 0;

		for (final Integer impact : impact2verl) {
			if (impact >= 10) {
				final int verlageProInpact = impact2verl.get(impact).size();
				aktuelleKastenFüllung += verlageProInpact;
				if (aktuelleKastenFüllung >= zahlProKasten) {
					kastengrenzen.add(impact);
					aktuelleKastenFüllung = 0;
				}
			}
		}

		System.out.println(kastengrenzen);

		final TreeMap<Integer, String> set;

	}

}
