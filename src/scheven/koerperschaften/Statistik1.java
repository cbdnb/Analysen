package scheven.koerperschaften;

import java.io.IOException;
import java.util.ArrayList;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class Statistik1 {

	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tb);
		int dollarb110 = 0;
		int dollarb410 = 0;
		int dollarb110_410 = 0;
		for (final Record record : reader) {
			Line line110;
			boolean found = false;
			try {
				line110 = GNDUtils.getHeading(record);
			} catch (final Exception e) {
				continue;
			}
			if (SubfieldUtils.containsIndicator(line110, 'b')) {
				dollarb110++;
				found = true;
			}
			final ArrayList<Line> lines4xx = GNDUtils.getLines4XX(record);
			for (final Line line410 : lines4xx) {
				if (SubfieldUtils.containsIndicator(line410, 'b')) {
					dollarb410++;
					if (found)
						dollarb110_410++;
					break;
				}
			}
		}
		System.out.println("110 mit $b: " + dollarb110);
		System.out.println("410 mit $b: " + dollarb410);
		System.out.println("110 und 410 mit $b: " + dollarb110_410);
	}

}
