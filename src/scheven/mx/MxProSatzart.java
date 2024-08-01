/**
 *
 */
package scheven.mx;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class MxProSatzart extends DownloadWorker {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final MxProSatzart anzahl = new MxProSatzart();
		anzahl.setStreamFilter(new ContainsTag("901", GNDTagDB.getDB()));
		anzahl.processGZipFile(Constants.GND);

		System.out.println(anzahlen);
	}

	private static Frequency<Character> anzahlen = new Frequency<>();

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
			final char typ = GNDUtils.getRecordType(record);
			System.err.println(typ + " / " + record.getId());
			anzahlen.add(typ);
		}

	}

}
