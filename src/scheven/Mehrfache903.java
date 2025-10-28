package scheven;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

public class Mehrfache903 {

	public static void main(final String[] args) throws IOException {
		final PrintWriter out = MyFileUtils
				.outputFile("D:/Analysen/scheven/903_mehrfach.txt", false);
		RecordReader.getMatchingReader(Constants.GND).forEach(record ->
		{
			final List<String> urheber = RecordUtils
					.getContentsOfFirstSubfields(record, "903", 'e');
			final List<String> redaktionen = RecordUtils
					.getContentsOfFirstSubfields(record, "903", 'r');
			final int sizeUrh = urheber.size();
			final int sizeRed = redaktionen.size();
			if (sizeRed <= 1 && sizeUrh <= 1)
				return;
			final String outS = StringUtils.concatenateTab(record.getId(),
					GNDUtils.getNID(record), RecordUtils.getDatatype(record),
					GNDUtils.getNameOfRecord(record),
					GNDUtils.getGNDClassifications(record), urheber.toString(),
					redaktionen.toString());
			out.println(outS);
		});
		MyFileUtils.safeClose(out);

	}

}
