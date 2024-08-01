/**
 *
 */
package scheven;

import java.io.IOException;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.mx.Library;

/**
 * @author baumann
 *
 */
public class Dekanate {

	static final String FOLDER = "D:/Analysen/scheven/Dekanate/";

	static final String FILE = "Superintendenz.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(FOLDER + FILE);
		reader.forEach(record ->
		{
			final String idn = record.getId();
			final String nid = GNDUtils.getNID(record);
			final Line line151 = RecordUtils.getTheOnlyLine(record, "151");
			final String feld151 = RecordUtils.toPicaWithoutTag(line151);
			final Library verbund = GNDUtils.getVerbund(record);

			System.out.println(
					StringUtils.concatenateTab(idn, nid, feld151, verbund));
		});

	}

}
