/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class FehlendeEntitaeten {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final RecordReader swReader = RecordReader
				.getMatchingReader(Constants.Ts);
		final AtomicInteger integer = new AtomicInteger();

		swReader.forEach(record ->
		{
			final int level = GNDUtils.getLevel(record);
			if (level != 1)
				return;
			if (!GNDUtils.containsEntityTypes(record)) {

				final String idn = record.getId();
				final List<String> systL = GNDUtils
						.getGNDClassifications(record);
				final String syst = StringUtils.concatenate(";", systL);
				final String name = GNDUtils.getNameOfRecord(record);
				System.out.println(
						StringUtils.concatenate("\t", idn, name, syst));
				integer.incrementAndGet();
			}

		});

		System.err.println(integer.get());

	}

}
