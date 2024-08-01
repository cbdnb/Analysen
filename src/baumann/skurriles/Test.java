/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Predicate;

import baumann.skurriles.filter.Ukraine;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Predicate<Record> isUkraine = new Ukraine();

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);

		final PrintWriter pw = FileUtils.oeffneAusgabeDatei(
				SkurConstants.FOLDER + "ukraine_idns.txt", false);

		reader.forEach(record ->
		{
			final Date date = RecordUtils.getDateEntered(record);
			if (date == null)
				return;
			final Calendar zugCal = TimeUtils.getCalendar(date);
			final int jahr = zugCal.get(Calendar.YEAR);
			final String id = record.getId();

			if (isUkraine.test(record))
				pw.println(StringUtils.concatenateTab(jahr, id));
		});

		FileUtils.safeClose(pw);

	}

}
