/**
 *
 */
package alex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 *
 *
 * @author baumann
 *
 */
public class DDC5400Fehlt {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final List<String> ausgaben = Arrays.asList("DDC22ger", "DDC23ger");
		final PrintWriter schreiber = null;
		// FileUtils
		// .oeffneAusgabeDatei("D:/Analysen/alex/DDC5400Fehlt.txt", false);

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		reader.setStreamFilter(new ContainsTag("5410", BibTagDB.getDB()));
		reader.forEach(record ->
		{
			if (BibRecUtils.isMagazine(record))
				return;

			if (!SubjectUtils.getDDCSegment(record, 0).isEmpty())
				return;

			if (SubjectUtils.getDDCSegment(record, 1).isEmpty())
				return;

			final Line ddc5410line = RecordUtils.getTheOnlyLine(record, "5410");
			final String ausgabe = SubfieldUtils
					.getContentOfFirstSubfield(ddc5410line, 'e');

			if (!ausgaben.contains(ausgabe))
				return;

			final String ddc = SubfieldUtils
					.getContentOfFirstSubfield(ddc5410line, 'a');

			final DDC_SG dhs = SGUtils.getDDCDHS(record);
			final DDC_SG dhsAusDDC = SGUtils.ddc2sg(ddc);
			if (dhs != dhsAusDDC)
				return;
			if (!SubjectUtils.getDDCSegment(record, 2).isEmpty())
				System.out.println(record.getId());
			// schreiber.println(record.getId());
		});
		MyFileUtils.safeClose(schreiber);
	}

}
