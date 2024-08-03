/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.parser.tag.Tag;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class HB {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Predicate<Line> predicateHB = line -> SubfieldUtils
				.getContentsOfSubfields(line, 'f').contains("HB/Sach");
		final Tag tag = BibTagDB.getDB().getPica3("7109");

		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Analysen/baumann/HB.txt");

		reader.forEach(record ->
		{

			final String shortTitle = BibRecUtils.createShortTitle(record);
			final String idn = record.getId();
			final DDC_SG sg = utils.AcDatabase.getSG(record);
			final String dhs = sg != null ? sg.getDDCString() : null;

			final ArrayList<Line> lines = RecordUtils.getLines(record,
					predicateHB, tag);
			final String basisKl = lines.isEmpty() ? null
					: SubfieldUtils.getContentOfFirstSubfield(lines.get(0),
							'g');

			final String s = StringUtils.concatenate("\t", shortTitle, idn, dhs,
					basisKl);
			System.out.println(s);

		});

	}

}
