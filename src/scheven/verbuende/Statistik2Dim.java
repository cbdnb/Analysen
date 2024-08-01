/**
 *
 */
package scheven.verbuende;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;

/**
 * @author baumann
 *
 */
public class Statistik2Dim {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final String[][] table = StringUtils.readTableFromClip();
		final Frequency<String> frequency = new Frequency<>();
		for (int i = 0; i < table.length; i++) {
			final String sg = table[i][0];
			final String anzS = table[i][1];
			final int anz = Integer.parseInt(anzS);
			frequency.increment(sg, anz);
		}
		System.out.println(frequency);

	}

}
