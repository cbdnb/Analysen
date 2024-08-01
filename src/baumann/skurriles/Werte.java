/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import de.dnb.basics.Constants;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.basics.utils.WaehrungsRechner;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;

/**
 * @author baumann
 *
 */
public class Werte extends DownloadWorker {

	private static final int JJJJ = 2022;

	private double wert = 0;

	WaehrungsRechner rechner = new WaehrungsRechner();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Werte werte = new Werte();
		werte.setStreamFilter(StringContains.containsSubfield('z', "StatZUG")
				.and(StringContains.containsSubfield('D',
						Integer.toString(JJJJ))));

		werte.processGZipFile(Constants.TITEL_PLUS_EXEMPLAR_Z);

		System.out.println(werte.wert);

	}

	@Override
	protected void processRecord(final Record record) {
		final ArrayList<Line> zugStatistik = BibRecUtils
				.getZUGStatistik(record);
		if (zugStatistik.isEmpty())
			return;

		final Date date = BibRecUtils.getDateFromStatLines(zugStatistik);
		final Calendar calendar = TimeUtils.getCalendar(date);
		final int year = calendar.get(Calendar.YEAR);
		if (year != JJJJ)
			return;

		final double dieserWert = rechner.wertAllerExemplare(record);
		wert += dieserWert;
		// System.err.println(wert);

	}

}
