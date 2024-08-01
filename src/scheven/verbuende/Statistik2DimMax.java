/**
 *
 */
package scheven.verbuende;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.TreeMultimap;

/**
 * @author baumann
 *
 */
public class Statistik2DimMax {

	/**
	 *
	 */
	private static final int MIN = 10;

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

		final TreeMultimap<Integer, String> impact2verl = new TreeMultimap<>();
		// Tabellenbereiche
		impact2verl.add(MIN);
		impact2verl.add(15);
		impact2verl.add(20);
		impact2verl.add(25);
		impact2verl.add(30);
		impact2verl.add(35);
		impact2verl.add(40);
		impact2verl.add(50);

		for (final String verl : verl2Impact) {
			final int max = verl2Impact.get(verl).iterator().next();
			if (max >= MIN) {
				final int key = impact2verl.floorKey(max);
				impact2verl.add(key, verl);
			}
		}

		impact2verl.descendingKeySet().forEach(imp ->
		{
			System.out.println(StringUtils.concatenate("\t", imp,
					StringUtils.concatenate(", ", impact2verl.get(imp))));
			// System.out.println(StringUtils.concatenate("\t", imp,
			// impact2verl.get(imp).size()));
		});

		// System.out.println(impact2verl);

	}

}
