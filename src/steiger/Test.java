/**
 *
 */
package steiger;

import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Musikalbum musikalbum = new Musikalbum();
		final Record record = RecordUtils.readFromClip();
		musikalbum.processRecord(record);

	}

}
