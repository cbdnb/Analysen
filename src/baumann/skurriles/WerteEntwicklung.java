/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.StatisticUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.basics.utils.WaehrungsRechner;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;

/**
 * @author baumann
 *
 */
public class WerteEntwicklung extends DownloadWorker {

	private final HashMap<Integer, Double> eingabeJahr2wert = new HashMap<>();
	private final Frequency<Integer> eingabeJahr2zahl = new Frequency<>();
	private final HashMap<Integer, Double> publikationsJahr2wert = new HashMap<>();
	private final Frequency<Integer> publikationsJahr2zahl = new Frequency<>();

	WaehrungsRechner rechner = new WaehrungsRechner();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final WerteEntwicklung werte = new WerteEntwicklung();
		// werte.setStreamFilter(new StringContains(Constants.RS + "004"));

		werte.processGZipFile(Constants.TITEL_GESAMT_D);

		final PrintWriter out = FileUtils.outputFile(
				"D:/Analysen/baumann/skurriles/wertentwicklung.txt", false);

		out.println(StringUtils.concatenateTab("Jahr",
				"Wert pro Publikationsjahr", "Zahl pro Publikationsjahr",
				"Wert pro Eingabejahr", "Zahl pro Eingabejahr"));

		for (int jahr = 1900; jahr <= 2023; jahr++) {
			final Double wertProPubjahr = werte.publikationsJahr2wert.get(jahr);
			final Long zahlProPubjahr = werte.publikationsJahr2zahl.get(jahr);
			final Double wertProEingjahr = werte.eingabeJahr2wert.get(jahr);
			final Long zahlProEingjahr = werte.eingabeJahr2zahl.get(jahr);
			out.println(StringUtils.concatenateTab(jahr, wertProPubjahr,
					zahlProPubjahr, wertProEingjahr, zahlProEingjahr));
		}

		FileUtils.safeClose(out);

	}

	StatusAndCodeFilter imBestand = StatusAndCodeFilter.imBestand();

	@Override
	protected void processRecord(final Record record) {
		final boolean kannPassieren = imBestand.test(record);
		if (!kannPassieren)
			return;

		// zum Vergleich: Die Zahl der Bücher:
		int eingabeJahr = -1;
		try {
			final Date date = RecordUtils.getDateEntered(record);
			final Calendar calendar = TimeUtils.getCalendar(date);
			eingabeJahr = calendar.get(Calendar.YEAR);
		} catch (final Exception e) {
		}
		eingabeJahr2zahl.add(eingabeJahr);
		final Integer pubJahr = BibRecUtils.getYearOfPublication(record);
		publikationsJahr2zahl.add(pubJahr);

		final double preis = rechner.wertAllerExemplare(record);
		StatisticUtils.increment(eingabeJahr2wert, eingabeJahr, preis);
		StatisticUtils.increment(publikationsJahr2wert, pubJahr, preis);

	}

}
