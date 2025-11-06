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
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class FindePalindromDDC {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final BoundedPriorityQueue<Triplett<String, String, Integer>> idnTitLaengeQ = new BoundedPriorityQueue<>(
				20, Comparator.comparing(Triplett::getThird));

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_D);
		reader.setStreamFilter(new ContainsTag("5400", BibTagDB.getDB()));

		for (final Record record : reader) {
			final String ddc = SubjectUtils.getMainDDCNotation(record);

			if (ddc == null || ddc.isEmpty())
				continue;

			if (SKUtil.isPalindrome(ddc)) {
				final String eff = SKUtil.getRelevantChars(ddc);
				final int len = eff.length();

				final Triplett<String, String, Integer> idntitlen = new Triplett<>(
						record.getId(), ddc, len);
				idnTitLaengeQ.add(idntitlen);

			}
		}
		idnTitLaengeQ.forEach(t -> System.out
				.println(StringUtils.concatenateTab(t.asList())));

	}

}
