package scheven;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class DollarVin1XX {

	public static void main(final String[] args) throws IOException {

		final PrintWriter out = MyFileUtils
				.outputFile("D:/Analysen/scheven/1xx_mit_v.txt", false);

		final List<Character> guteTypen = Arrays.asList('b', 'f', 'g', 'p', 's',
				'u');

		RecordReader.getMatchingReader(Constants.GND).forEach(rec ->
		{
			if (!guteTypen.contains(GNDUtils.getRecordType(rec)))
				return;

			Line heading = null;
			try {
				heading = GNDUtils.getHeading(rec);
			} catch (final Exception e) {
				System.err.println("falsch: " + rec.getId());
			}
			if (heading != null
					&& SubfieldUtils.containsIndicator(heading, 'v'))
				out.println(rec.getId() + " / " + RecordUtils.getDatatype(rec));
		});
		MyFileUtils.safeClose(out);

	}

}
