/**
 *
 */
package baumann.musik;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class DollarV382 {

	public static void main(final String... args) throws IOException {
		final Frequency<String> dollarvFreq = new Frequency<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);
		reader.forEach(record ->
		{
			List<String> dollarvs = RecordUtils
					.getContentsOfAllSubfields(record, "382", 'v');
			dollarvs = dollarvs.stream().map(String::trim)
					// .map(String::toLowerCase)
					.map(StringUtils::unicodeComposition)
					.collect(Collectors.toList());
			dollarvFreq.addCollection(dollarvs);
		});

		final PrintWriter file = FileUtils
				.oeffneAusgabeDatei("D:/Analysen/baumann/Musik/dollarV", false);

		file.println(dollarvFreq);
		FileUtils.safeClose(file);

	}

}
