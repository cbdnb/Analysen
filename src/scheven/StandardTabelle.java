package scheven;

import java.io.IOException;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.parser.Record;

public class StandardTabelle {

	public static void main(final String[] args) throws IOException {
		final String path = StringUtils.readClipboard();
		final RecordReader reader = RecordReader.getMatchingReader(path);
		String out = StringUtils.concatenateTab("Satzart", "1XX", "nid",
				"Redaktion");

		for (final Record record : reader) {

			final String zeile = StringUtils.concatenateTab(
					RecordUtils.getDatatype(record),
					GNDUtils.getSimpleName(record), GNDUtils.getNID(record),
					GNDUtils.getIsilVerbund(record));
			System.out.println(zeile);
			out += "\n" + zeile;
		}
		StringUtils.writeToClipboard(out);

	}

}
