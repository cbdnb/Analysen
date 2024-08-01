/**
 *
 */
package scheven.verbuende;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.utils.SGUtils;

/**
 * @author baumann
 *
 */
public class Statistik2DimSGG {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final String[][] table = StringUtils.readTableFromClip();
		final CrossProductFrequency frequency = new CrossProductFrequency();
		for (int i = 0; i < table.length; i++) {
			final String sg = table[i][0];
			final String anzS = table[i][1];
			final int anz = Integer.parseInt(anzS);
			if (anz == 0)
				frequency.addValues(sg, 0);
			else if (anz <= 5)
				frequency.addValues(sg, 1);
			else if (anz > 5)
				frequency.addValues(sg, 5);
		}
		System.out.println(
				StringUtils.concatenate("\t", "DHS", "0", "1-5", ">5"));
		SGUtils.allDHSasString().forEach(sg ->
		{
			final double nullen = frequency.getCount(sg, 0);
			final double einser = frequency.getCount(sg, 1);
			final double fünfer = frequency.getCount(sg, 5);
			final double count = nullen + einser + fünfer;

			if (count > 0) {
				String concatenated = StringUtils.concatenate("\t", sg,
						nullen / count, einser / count, fünfer / count);
				concatenated = concatenated.replace('.', ',');
				System.out.println(concatenated);
			}
		});

	}

}
