/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.BoundedPriorityQueue;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class FindePalindromGND {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final BoundedPriorityQueue<Triplett<String, String, Integer>> idnTitLaengeQ = new BoundedPriorityQueue<>(
				20, Comparator.comparing(Triplett::getThird));

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);

		for (final Record record : reader) {
			String name;
			try {
				name = GNDUtils.getSimpleName(record);
			} catch (final Exception e) {
				continue;
			}
			if (name == null || name.isEmpty())
				continue;

			final Set<String> verweiseUndName = GNDUtils.getVerweise(record);
			verweiseUndName.add(name);

			for (final String vwn : verweiseUndName) {
				if (SKUtil.isPalindrome(vwn)) {
					// System.err.println(vwn);
					final String eff = SKUtil.getRelevantChars(vwn);
					final int len = eff.length();

					final Triplett<String, String, Integer> idntitlen = new Triplett<>(
							record.getId(), vwn, len);
					idnTitLaengeQ.add(idntitlen);

				}
			}
		}
		idnTitLaengeQ.forEach(t -> System.out
				.println(StringUtils.concatenateTab(t.asList())));

	}

}
