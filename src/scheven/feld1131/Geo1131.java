/**
 *
 */
package scheven.feld1131;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.ie.utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 */
public class Geo1131 {

	static String path = "D:/Analysen/scheven/1131_geo_falsch";

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		final Frequency<String> falscheGeos = new Frequency<>();
		final PrintWriter out = FileUtils.outputFile(path + "/falsche.txt",
				false);
		final PrintWriter statistik = FileUtils
				.outputFile(path + "/statistik.txt", false);
		final BiMultimap<Integer, String> table = GND_DB_UTIL.getppn2RDAName();
		final RecordReader reader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz");
		for (final Record bibrec : reader) {

			final ArrayList<Line> lines1131 = RecordUtils.getLines(bibrec,
					"1131");
			lines1131.forEach(line ->
			{
				final String dollarZ = SubfieldUtils
						.getContentOfFirstSubfield(line, 'z');
				if (dollarZ == null)
					return;
				final String geo = StringUtils.unicodeComposition(dollarZ);
				if (table.searchKeys(geo).isEmpty()) {
					falscheGeos.add(geo);
					final String dollarA = SubfieldUtils
							.getContentOfFirstSubfield(line, 'a');
					final String outLine = StringUtils
							.concatenateTab(bibrec.getId(), dollarA, geo);
					out.println(outLine);
					out.flush();
					System.err.println(outLine);
				}
			});

		}

		FileUtils.safeClose(out);
		statistik.println(falscheGeos);
		FileUtils.safeClose(statistik);

	}

}
