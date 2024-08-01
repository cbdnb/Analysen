/**
 *
 */
package steiger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.WorkUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 */
public class TonartenGezaehlt {

	static List<String> tongeschlechter = Arrays.asList("Phrygisch",
			"Hypophrygisch", "Lydisch", "Hypolydisch", "Mixolydisch",
			"Hypomixolydisch", "Ionisch", "Hypoionisch", "Dorisch",
			"Hypodorisch", "äolisch", "Hypoäolisch", "\\-Dur", "\\-moll",
			"\\. Ton");

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final String zahl = "\\W+\\d";
		final List<String> list = tongeschlechter.stream()
				.map(gs -> "(" + gs + zahl + ")").collect(Collectors.toList());
		final String regexp = StringUtils.concatenate("|", list);
		// System.err.println(regexp);
		final Pattern pattern = Pattern.compile(regexp,
				Pattern.CASE_INSENSITIVE);

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);
		reader.forEach(record ->
		{
			if (!WorkUtils.isMusicalWork(record))
				return;

			final String titel = GNDUtils.getSimpleName(record);

			final Matcher matcher = pattern.matcher(titel);
			if (matcher.find()) {
				String out = GNDUtils.getNID(record) + ": ";
				try {
					final String rda = RDAFormatter.getRDAHeading(record);
					out += rda;
				} catch (final IllFormattedLineException e) {
					out += titel;
				}
				System.out.println(out);
				System.out.println(matcher.group());
			}

		});

	}

}
