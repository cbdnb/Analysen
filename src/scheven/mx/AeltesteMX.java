/**
 *
 */
package scheven.mx;

import java.io.IOException;
import java.util.Date;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.mx.Mailbox;

/**
 * @author baumann
 *
 */
public class AeltesteMX extends DownloadWorker {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final AeltesteMX anzahl = new AeltesteMX();
		anzahl.setStreamFilter(new ContainsTag("901", GNDTagDB.getDB()));
		anzahl.processGZipFile(Constants.GND);

		System.out.println(oldestdate);
	}

	private static Date oldestdate = new Date();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {
		// System.out.println(record.getId());
		if (GNDUtils.containsMX(record)) {
			final Date date = Mailbox.getFirstDate(record).orElse(oldestdate);

			if (oldestdate.compareTo(date) > 0)
				oldestdate = date;
		}

	}

}
