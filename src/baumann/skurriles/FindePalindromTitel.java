/**
 *
 */
package baumann.skurriles;

import java.io.IOException;

import de.dnb.basics.Constants;
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

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_D);

		int maxlen = 0;
		for (final Record record : reader) {
			final String titel = BibRecUtils.getMainTitle(record);
			if (titel == null || titel.isEmpty())
				continue;

			if (SKUtil.isPalindrome(titel)) {
				final String eff = SKUtil.getRelevantChars(titel);
				final int len = eff.length();
				if (len > maxlen) {
					maxlen = len;
					System.out.println(record.getId() + ": " + titel);
				}
			}
		}

	}

}
