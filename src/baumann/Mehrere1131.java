/**
 *
 */
package baumann;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class Mehrere1131 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_D);
		reader.setStreamFilter(
				new ContainsTag("1131", '9', "1071861417", BibTagDB.getDB()));

		reader.forEach(record ->
		{
			final ArrayList<Line> lines = RecordUtils.getLines(record, "1131");
			lines.forEach(line ->
			{
				final String dollar9 = SubfieldUtils
						.getContentOfFirstSubfield(line, '9');
				if (!"1071861417".equals(dollar9))
					return;
				final List<String> dollarZ = SubfieldUtils
						.getContentsOfSubfields(line, 'z');
				if (dollarZ.size() > 2)
					System.out.println(record.getId() + ": " + line);
			});
		});

	}

}
