/**
 *
 */
package scheven;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.utils.NumberUtils;
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
public class Statistik1131 {

	/**
	 *
	 */
	private static final String OUT_FILE = "D:/Analysen/scheven/Statistik1131.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("1131", BibTagDB.getDB()));

		final Frequency<String> frequency = new Frequency<>();

		reader.forEach(rec ->
		{
			final Field field1131 = RecordUtils.getFieldGivenAsString(rec,
					"1131");

			field1131.forEach(line ->
			{
				if (SubfieldUtils.containsIndicator(line, '9')) {

				} else {
					final String lineS = RecordUtils.toPicaWithoutTag(line);

					if (lineS.contains("!")
							|| NumberUtils.containsArabicInts(lineS)) {
						System.err.println(line + " / " + rec.getId());
					} else {
						final List<String> codes = SubfieldUtils
								.getContentsOfSubfields(line, 'a');
						codes.forEach(frequency::add);
					}
				}
			});
		});

		final PrintStream outputStream = new PrintStream(OUT_FILE);
		outputStream.println(frequency);
		MyFileUtils.safeClose(outputStream);

	}

}
