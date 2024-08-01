/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class BuchstabenGND extends DownloadWorker {

	private static final int JJ = 22;
	private static final int JAHR = JJ + 2000;
	private static List<Character> typen = Arrays.asList('b', 'f', 'g', 'p',
			's', 'u');
	private static BuchstabenZaehler zaehler;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		typen.forEach(typ ->
		{
			System.out.println("--------: " + typ);
			final BuchstabenGND bGND = new BuchstabenGND();
			zaehler = new BuchstabenZaehler();
			bGND.setStreamFilter(new StringContains("-" + JJ));
			try {
				bGND.setOutputFile(
						"D:/Analysen/baumann/skurriles/buchstaben_gnd_" + typ
								+ "_" + JAHR + ".txt");
				bGND.processGZipFile("D:/Normdaten/DNBGND_" + typ + ".dat.gz");

				zaehler.ausgabe(bGND);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

	}

	@Override
	protected void processRecord(final Record record) {

		final String id = record.getId();

		final Date date = RecordUtils.getDateEntered(record);
		if (date == null)
			return;
		final int level = GNDUtils.getLevel(record);
		if (level > 3)
			return;
		final Calendar calendar = TimeUtils.getCalendar(date);
		final int year = calendar.get(Calendar.YEAR);
		if (year != JAHR)
			return;
		final String sw = GNDUtils.getSimpleName(record);
		System.out.println(sw);

		zaehler.buchstabenStatistik(sw, id);

	}

}
