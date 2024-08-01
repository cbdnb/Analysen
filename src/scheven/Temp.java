/**
 *
 */
package scheven;

import java.io.IOException;
import java.io.PrintWriter;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.mx.Library;

/**
 * Veränderbar für Standardaufgaben.
 *
 * @author baumann
 *
 */
public class Temp {

	static final String FOLDER = "D:/Analysen/scheven/";

	static final String PREFIX = "loeschungen";

	static final String IN_FILE = FOLDER + PREFIX + ".txt";

	static final String OUT_FILE = FOLDER + PREFIX + "_out" + ".txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader.getMatchingReader(IN_FILE);
		final PrintWriter out = FileUtils.outputFile(OUT_FILE, false);
		out.println(StringUtils.concatenateTab("idn", "nid", "satzart", "name",
				"verbund"));
		reader.forEach(record ->
		{
			System.out.println(record);
			System.out.println();
			final String idn = record.getId();
			final String nid = GNDUtils.getNID(record);
			final String satzart = GNDUtils.getBBG(record);
			String name;
			try {
				name = GNDUtils.getNameOfRecord(record);
			} catch (final Exception e) {
				name = null;
			}
			final Library verbund = GNDUtils.getVerbund(record);

			out.println(StringUtils.concatenateTab(idn, nid, satzart, name,
					verbund));
		});

	}

}
