/**
 *
 */
package jahns;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.filtering.Between;
import de.dnb.basics.filtering.StringContains;
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
public class SachgruppeDollarAI {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final PrintWriter out = FileUtils
				.outputFile("D:/Analysen/jahns/doppelte5050.txt", false);
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_Z);
		reader.setStreamFilter(new StringContains(Constants.US + "D" + "2023-0")
				.and(new ContainsTag("5050", BibTagDB.getDB()))
				.and(new StringContains(Constants.MARC_LINE_SEP + "220C")));
		final Calendar first = new GregorianCalendar(2023, Calendar.MARCH, 23);
		final Calendar last = new GregorianCalendar(2023, Calendar.JUNE, 30);
		final Between<Calendar> interval = new Between<Calendar>(first, last);
		reader.forEach(record ->
		{
			final List<String> dollarE = RecordUtils
					.getContentsOfAllSubfields(record, "5050", 'E');
			if (dollarE.size() <= 1)
				return;
			if (!dollarE.contains("i"))
				return;
			final Date dateIE = BibRecUtils.getIEstatDate(record);
			final Calendar calIE = TimeUtils.getCalendar(dateIE);
			if (!interval.test(calIE))
				return;
			out.println(record.getId());
		});

		FileUtils.safeClose(out);

	}

}
