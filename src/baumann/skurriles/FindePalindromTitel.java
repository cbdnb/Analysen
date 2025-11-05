/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.util.Comparator;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.BoundedPriorityQueue;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;

/**
 * @author baumann
 *
 */
public class FindePalindromTitel {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final BoundedPriorityQueue<Triplett<String, String, Integer>> idnTitLaengeQ = new BoundedPriorityQueue<>(
				20, Comparator.comparing(Triplett::getThird));

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_D);

		for (final Record record : reader) {
			final String titel = BibRecUtils.getMainTitle(record);
			if (titel == null || titel.isEmpty())
				continue;

			if (SKUtil.isPalindrome(titel)) {
				final String eff = SKUtil.getRelevantChars(titel);
				final int len = eff.length();

				final Triplett<String, String, Integer> idntitlen = new Triplett<>(
						record.getId(), titel, len);
				idnTitLaengeQ.add(idntitlen);

			}
		}
		idnTitLaengeQ.forEach(t -> System.out
				.println(StringUtils.concatenateTab(t.asList())));

	}

}
