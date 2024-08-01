/**
 *
 */
package boeth;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.basics.utils.DDC_Utils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class DollarS {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		reader.forEach(record ->
		{
			final String dollarS = RecordUtils.getContentOfSubfield(record,
					"4000", 'S');
			final String bbg = RecordUtils.getDatatype(record);
			if (dollarS != null)
				if ("Af".equals(bbg)) {
					if (DDC_Utils.isMainTableDDC(dollarS)) {
						System.out.println(record.getRawData());
						System.out.println();
					}

				}
		});

	}

}
