package scheven.hinweis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordParser;

public class GZip {

	public static void main(final String[] args) throws IOException {
		final List<String> idnsZuLoeschenL = StringUtils.readLinesFromFile(
				"D:/Analysen/scheven/Hinweis/idns_zu_loeschen_ps_Sammlung.txt");
		final Set<String> idnsZuLoeschen = new HashSet<>(idnsZuLoeschenL);
		final String dateiName = Util.FOLDER + "Zu_loeschende_Hinweissaetze"
				+ ".dat.gzip";
		final File file = new File(dateiName);
		final PrintStream outGND = MyFileUtils.getGZipPrintStream(file);

		final BufferedReader in = MyFileUtils.getGZipReader(Constants.GND);
		String line = in.readLine();
		while (line != null) {
			final RecordParser parser = new RecordParser();
			final Record record = parser.parse(line);
			final String idn = record.getId();
			// System.err.println(idn);
			if (idnsZuLoeschen.contains(idn))
				outGND.print(line + Constants.LF);
			line = in.readLine();
		}
		MyFileUtils.safeClose(outGND);
	}

}
