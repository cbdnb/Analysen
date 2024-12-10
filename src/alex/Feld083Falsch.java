package alex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.DDC_Utils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.parser.Record;

public class Feld083Falsch {

	public static void main(final String[] args) throws IOException {
		final PrintWriter out = MyFileUtils
				.outputFile("D:/Analysen/alex/Falsche_083.txt", false);
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);
		reader.setStreamFilter(new ContainsTag("083", GNDTagDB.getDB()));

		for (final Record record : reader) {
			final ArrayList<Line> lines083 = GNDUtils.getValidDDCLines(record);
			final List<String> falscheDDCs = new ArrayList<String>();
			for (final Line line083 : lines083) {
				final Subfield ddcSub = SubfieldUtils.getFirstSubfield(line083,
						'c');
				final String ddc = (ddcSub == null) ? "" : ddcSub.getContent();

				if (!DDC_Utils.isDDC(ddc)) {
					falscheDDCs.add(ddc);
				}
			}
			if (!falscheDDCs.isEmpty()) {
				out.print(record.getId());
				out.print("\t");
				out.println(StringUtils.concatenateTab(falscheDDCs));
			}
		}

	}

}
