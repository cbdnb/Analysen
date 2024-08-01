/**
 *
 */
package baumann;

import java.io.IOException;

import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class MatheFremdsprache {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Normdaten/DNBGND_s.dat.gz");
		//@formatter:off
		final long c =reader
			.stream()
			.filter(rec -> GNDUtils.containsGNDClassification(rec, "28"))
			.filter(rec -> RecordUtils.containsField(rec, "750"))
			.count();
		System.out.println(c);

	}

}
