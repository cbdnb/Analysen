/**
 *
 */
package scheven.mx;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
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
public class MxTabelle1 extends DownloadWorker {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final MxTabelle1 anzahl = new MxTabelle1();
		anzahl.setStreamFilter(new ContainsTag("901", GNDTagDB.getDB()));
		anzahl.processGZipFile(Constants.GND);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {
		if (!GNDUtils.containsMX(record))
			return;
		GNDUtils.getMXLines(record).forEach(line ->
		{
			final String idn = record.getId();
			final String date = Mailbox.getRawDate(line);
			final char typ = GNDUtils.getRecordType(record);
			final List<String> abs = Mailbox.getRawSenders(line);
			System.out.println(
					StringUtils.concatenate("\t", idn, date, typ, abs));

		});

	}

}
