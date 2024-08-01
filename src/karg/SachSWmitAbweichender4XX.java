/**
 *
 */
package karg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.parser.tag.Tag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SystematikUtils;

/**
 * @author baumann
 *
 */
public class SachSWmitAbweichender4XX {

	private static ArrayList<Record> records = new ArrayList<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final GNDTagDB db = GNDTagDB.getDB();
		final Collection<? extends Tag> tag4XX = new LinkedHashSet<>(
				db.getTag4XX());
		tag4XX.remove(GNDTagDB.TAG_450);
		RecordReader.getMatchingReader(Constants.Ts).forEach(record ->
		{
			final ArrayList<Line> lines = RecordUtils.getLines(record, tag4XX);
			if (!lines.isEmpty()) {
				records.add(record);
			}
		});

		Collections.sort(records, SystematikUtils.recordSysComparator);

		records.forEach(record ->
		{
			final ArrayList<Line> lines = RecordUtils.getLines(record, tag4XX);
			final String id = record.getId();
			final String name = GNDUtils.getNameOfRecord(record);
			final String sys = StringUtils.concatenate(";",
					GNDUtils.getGNDClassifications(record));
			final String abweichend = RecordUtils.toPica(lines, Format.PICA3,
					true, " // ", '$');

			System.out.println(
					StringUtils.concatenateTab(id, name, sys, abweichend));

		});
	}
}
