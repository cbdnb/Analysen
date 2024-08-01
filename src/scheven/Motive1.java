/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Motive1 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		RecordReader.getMatchingReader(Constants.GND).forEach(record ->
		{
			boolean isMotiv = false;
			Line heading;
			try {
				heading = GNDUtils.getHeading(record);
			} catch (final Exception e) {

				return;
			}
			final List<Subfield> subs = heading.getSubfields();
			for (final Subfield sub : subs) {
				if (sub.getIndicator().indicatorChar == 'g'
						&& sub.getContent().equalsIgnoreCase("motiv"))
					isMotiv = true;
			}
			if (isMotiv && GNDUtils.contains4XX(record)) {
				final String idn = record.getId();
				final String bbg = RecordUtils.getDatatype(record);
				final String name = GNDUtils.getNameOfRecord(record);
				final List<String> syst = GNDUtils
						.getGNDClassifications(record);
				final ArrayList<String> verw = FilterUtils.map(
						GNDUtils.getLines4XX(record),
						RecordUtils::toPicaWithoutTag);
				final String out = StringUtils.concatenate("\t", idn, bbg, name,
						syst, StringUtils.concatenate(", ", verw));
				System.out.println(out);
			}
		});

	}

}
