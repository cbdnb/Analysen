/**
 *
 */
package scheven.mx;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class AnzahlMX extends DownloadWorker {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final AnzahlMX anzahl = new AnzahlMX();
		anzahl.setStreamFilter(new ContainsTag("901", GNDTagDB.getDB()));
		anzahl.processGZipFile(Constants.GND);

		System.out.println(anzahlMx);
	}

	private static int anzahlMx = 0;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {
		// System.out.println(record.getId());
		if (GNDUtils.containsMX(record))
			anzahlMx++;

	}

}
