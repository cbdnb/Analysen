/**
 *
 */
package steiger;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class DollarMmit1 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		RecordReader.getMatchingReader(Constants.Tu).forEach(record ->
		{
			RecordUtils.getContentsOfAllSubfields(record, "130", 'm')
					.forEach(sub ->
					{
						if (sub.contains("(1)"))
							System.out.println(record.getId() + ": "
									+ GNDUtils.getNameOfRecord(record));
					});
		});

	}

}
