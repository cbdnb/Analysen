/**
 *
 */
package scheven.Feld7XX;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Vergleiche1XX7XX {

	public static final String FOLDER = "D:/Analysen/scheven/7XX";

	public static final List<String> FALSCHE_TAGS = Arrays.asList("700", "710",
			"711", "730", "750", "751");

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final List<String> falscheTags = new ArrayList<>(FALSCHE_TAGS);

		final String richtigerTag = "700";
		falscheTags.remove(richtigerTag);
		final String[] falschArr = falscheTags.toArray(new String[] {});

		final Map<String, PrintWriter> falsche2out = new TreeMap<>();
		for (final String falscher : falscheTags) {
			final PrintWriter out = FileUtils.outputFile(
					FOLDER + "/" + falscher + "_statt_" + richtigerTag + ".txt",
					false);
			falsche2out.put(falscher, out);
		}

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tp);

		for (final de.dnb.gnd.parser.Record record : reader) {
			// if (!RecordUtils.containsFields(record, falschArr))
			// continue;
			for (final String falscher : falscheTags) {
				final ArrayList<Line> lines = RecordUtils.getLines(record,
						falscher);
				if (lines.isEmpty())
					continue;
				lines.forEach(line ->
				{
					final PrintWriter out = falsche2out.get(falscher);
					out.println(macheAusgabeZeile(record, line));
				});
			}
		}

		for (final String falscher : falscheTags) {
			FileUtils.safeClose(falsche2out.get(falscher));
		}

	}

	public static String macheAusgabeZeile(final Record record,
			final Line line) {

		final String nid = GNDUtils.getNID(record);
		final String kat005 = RecordUtils.getDatatype(record);
		final String verbund = GNDUtils.getIsilVerbund(record);
		final String name = GNDUtils.getNameOfRecord(record);
		final String feld7XX = RecordUtils.toPica(line, Format.PICA3, true,
				Constants.DOLLAR);

		return StringUtils.concatenateTab(nid, kat005, verbund, name, feld7XX);
	}

}
