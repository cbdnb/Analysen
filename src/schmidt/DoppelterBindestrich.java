/**
 *
 */
package schmidt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.Mutable;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class DoppelterBindestrich {

	static Pattern pattern = Pattern.compile("[^-]+\\-[^-]+\\-[^-]+");

	static boolean contains2dashes(final String s) {
		final Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tg);

		final String path = "D:/Analysen/schmidt/";
		final PrintWriter writerDE = new PrintWriter(path + "de.txt");
		final PrintWriter writerFR = new PrintWriter(path + "fr.txt");
		final PrintWriter writerAT = new PrintWriter(path + "at.txt");
		final PrintWriter writerHU = new PrintWriter(path + "hu.txt");
		final PrintWriter writerRest = new PrintWriter(path + "rest.txt");

		reader.stream().forEach(record ->
		{
			final List<String> ents = GNDUtils.getEntityTypes(record);
			if (!ents.contains("gik"))
				return;
			final String dollarA = RecordUtils.getContentOfSubfield(record,
					"151", 'a');
			if (dollarA == null)
				return;
			if (contains2dashes(dollarA)) {
				final String name = GNDUtils.getNameOfRecord(record);
				final String id = record.getId();
				final List<String> lcs = GNDUtils.getCountryCodes(record);
				final String line = StringUtils.concatenate("\t", id, name,
						ents, lcs);

				writerRest.println(line);

				final Mutable<Boolean> lcUsed = new Mutable<>(false);
				lcs.forEach(lc ->
				{
					if (lc.contains("DE")) {
						writerDE.println(line);
						lcUsed.setValue(true);
					}
					if (lc.contains("FR")) {
						writerFR.println(line);
						lcUsed.setValue(true);
					}
					if (lc.contains("AT")) {
						writerAT.println(line);
						lcUsed.setValue(true);
					}
					if (lc.contains("HU")) {
						writerHU.println(line);
						lcUsed.setValue(true);
					}

				});
				if (!lcUsed.getValue()) {

				}

			}

		});
		FileUtils.safeClose(writerDE);
		FileUtils.safeClose(writerFR);
		FileUtils.safeClose(writerAT);
		FileUtils.safeClose(writerHU);
		FileUtils.safeClose(writerRest);
	}

}
