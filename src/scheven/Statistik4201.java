/**
 *
 */
package scheven;

import java.io.IOException;
import java.io.PrintStream;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Field;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class Statistik4201 {

	/**
	 *
	 */
	private static final String OUT_FILE = "D:/Analysen/scheven/Statistik4201.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		reader.setStreamFilter(new ContainsTag("4201", BibTagDB.getDB()));

		final Frequency<String> frequency = new Frequency<>();

		reader.forEach(rec ->
		{

			final Field field4201 = RecordUtils.getFieldGivenAsString(rec,
					"4201");

			if (field4201 != null)
				field4201.forEach(line ->
				{
					if (line != null) {
						final String dollarA = SubfieldUtils
								.getContentOfFirstSubfield(line, 'a');
						if (dollarA != null) {
							frequency.add(dollarA);

						}
					}
				});

		});

		final PrintStream outputStream = new PrintStream(OUT_FILE);
		outputStream.println(frequency);
		FileUtils.safeClose(outputStream);

	}

}
