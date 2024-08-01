/**
 *
 */
package scheven.expressionen;

import java.io.PrintWriter;

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
		final Record record = RecordUtils.readFromClip();
		Fassung_etc.bearbeite(record, new PrintWriter(System.out));
	}

}
