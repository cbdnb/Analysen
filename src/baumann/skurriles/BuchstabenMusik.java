/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class BuchstabenMusik extends DownloadWorker {

	private static List<Character> typen = Arrays.asList('u');
	private static BuchstabenZaehler zaehler;
	private static int jahr = 2022;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		typen.forEach(typ ->
		{
			System.out.println("--------: " + typ);
			final BuchstabenMusik bGND = new BuchstabenMusik();
			zaehler = new BuchstabenZaehler();
			bGND.setStreamFilter(
					new ContainsTag("008", 'a', "wim", GNDTagDB.getDB()));
			try {
				bGND.setOutputFile(
						"D:/Analysen/baumann/skurriles/buchstaben_musik_" + jahr
								+ ".txt");
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
		if (!WorkUtils.isMusicalWork(record))
			return;

		final Calendar calendar = TimeUtils.getCalendar(date);
		final int year = calendar.get(Calendar.YEAR);
		if (jahr != year)
			return;

		final String sw = GNDUtils.getSimpleName(record);
		System.out.println(sw);

		zaehler.buchstabenStatistik(sw, id);

	}

}
