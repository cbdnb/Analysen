/**
 *
 */
package scheven.feld548;

import java.io.IOException;
import java.io.PrintWriter;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.gnd.parser.RecordReader;

/**
 * @author baumann
 *
 */
public class GestGeb {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final String flag = "geb";
		final PrintWriter out = FileUtils
				.outputFile("D:/Analysen/scheven/" + flag + "_tab.txt", false);
		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Analysen/scheven/" + flag + ".txt");
		reader.forEach(record ->
		{
			out.println(Util.macheAusgabeZeile(record));
		});

		FileUtils.safeClose(out);
	}

}
