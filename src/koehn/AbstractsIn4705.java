/**
 *
 */
package koehn;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class AbstractsIn4705 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		reader.setStreamFilter(new ContainsTag("4705", BibTagDB.getDB()));
		final PrintWriter out = FileUtils
				.outputFile("D:/Analysen/koehn/4705.txt", false);
		out.println(StringUtils.concatenateTab("idn", "bbg", "maximale Länge",
				"Publikationsjahr", "Jahr der Ersterfassung"));

		reader.forEach(record ->
		{

			final List<String> abstracts = RecordUtils
					.getContentsOfFirstSubfield(record, 'a', "4705");
			if (abstracts.isEmpty())
				return;
			int maxLen = 0;
			for (final String abst : abstracts) {
				maxLen = Integer.max(maxLen, abst.length());
			}

			final String bbg = RecordUtils.getDatatype(record);

			final String idn = record.getId();

			final String jahr = BibRecUtils.getYearOfPublicationString(record);

			final Date dateErsterfassung = RecordUtils.getDateEntered(record);

			final String ersterfassung = TimeUtils
					.toYYYYMMDD(dateErsterfassung);

			out.println(StringUtils.concatenateTab(idn, bbg, maxLen, jahr,
					ersterfassung));

		});

		FileUtils.safeClose(out);

	}

}
