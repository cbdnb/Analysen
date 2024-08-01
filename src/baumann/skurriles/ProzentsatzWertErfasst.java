/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import de.dnb.basics.Constants;
import de.dnb.basics.utils.WaehrungsRechner;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.StatusAndCodeFilter;

/**
 * @author baumann
 *
 */
public class ProzentsatzWertErfasst extends DownloadWorker {

	WaehrungsRechner rechner = new WaehrungsRechner();

	int zahlMitPreis = 0;
	int zahlOhnePreis = 0;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final ProzentsatzWertErfasst werte = new ProzentsatzWertErfasst();

		werte.processGZipFile(Constants.TITEL_STICHPROBE);

		System.out.println("Zahl der Titel mit Preis: " + werte.zahlMitPreis);
		System.out.println("Zahl der Titel ohne Preis: " + werte.zahlOhnePreis);

		final double prozentsatz = (double) werte.zahlMitPreis
				/ (werte.zahlMitPreis + werte.zahlOhnePreis) * 100.0;
		System.out.println("Prozentsatz mit Preis: " + prozentsatz);

	}

	StatusAndCodeFilter imBestand = StatusAndCodeFilter.imBestand();

	@Override
	protected void processRecord(final Record record) {
		final boolean kannPassieren = imBestand.test(record);
		if (!kannPassieren)
			return;

		final double preis = rechner.wertAllerExemplare(record);
		if (preis > 0) {
			zahlMitPreis++;
		} else {
			zahlOhnePreis++;
		}

	}

}
