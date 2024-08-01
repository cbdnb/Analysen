/**
 *
 */
package baumann;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class Redaktionen {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		// TODO Auto-generated method stub
		final Set<String> teilnehmer = new HashSet<>();

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);
		reader.forEach(record ->
		{
			final String urheber = GNDUtils.getIsilUrheber(record);
			if (urheber != null)
				teilnehmer.add(urheber);
			final String verbund = GNDUtils.getIsilVerbund(record);
			if (verbund != null)
				teilnehmer.add(verbund);
		});
		System.out.println(teilnehmer.size());
	}

}
