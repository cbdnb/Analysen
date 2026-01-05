/**
 *
 */
package baumann.musik;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.StatisticUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class TonartenStatistik {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Frequency<String> tonarten = new Frequency<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);
		reader.forEach(record ->
		{
			if (!WorkUtils.isMusicalWork(record))
				return;
			final String key = WorkUtils.getKey(record);
			if (key != null) {
				tonarten.add(key);
			}
		});
		System.out.println(StatisticUtils.map2string(tonarten));

	}

}
