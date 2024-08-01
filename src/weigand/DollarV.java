/**
 *
 */
package weigand;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class DollarV {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Normdaten/DNBGND_u.dat.gz");
		final Frequency<String> frequency130 = new Frequency<>();
		final Frequency<String> frequency430 = new Frequency<>();
		reader.stream().filter(WorkUtils::isMusicalWork).forEach(record ->
		{
			final List<String> dolV130 = RecordUtils
					.getContentsOfAllSubfields(record, "130", 'v');
			frequency130.addCollection(dolV130);
			final List<String> dolV430 = RecordUtils
					.getContentsOfAllSubfields(record, "430", 'v');
			frequency430.addCollection(dolV430);
		});
		System.out.println(frequency130);
		System.out.println();
		System.out.println(frequency430);
	}

}
