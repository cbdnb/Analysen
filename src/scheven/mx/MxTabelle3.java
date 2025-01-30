/**
 *
 */
package scheven.mx;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.mx.Mailbox;

/**
 * @author baumann
 *
 */
public class MxTabelle3 extends DownloadWorker {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final MxTabelle3 mxTabelle = new MxTabelle3();

		String uberschr = StringUtils.concatenate("\t", "idn", "Typ");
		for (int i = 1; i < 10; i++) {
			uberschr = StringUtils.concatenate("\t", uberschr, "Datum " + i,
					"Absender " + i);
		}
		System.out.println(uberschr);

		mxTabelle.processGZipFile("D:/Analysen/scheven/mx/mx.gz");

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
		if (Mailbox.containsPseu(record) || Mailbox.containsSpio(record))
			return;

		final String idn = record.getId();
		final String nid = GNDUtils.getNID(record);
		final String bbg = GNDUtils.getBBG(record);
		String out = StringUtils.concatenate("\t", idn, bbg);
		for (final Line mxLine : GNDUtils.getMXLines(record)) {
			final String date = Mailbox.getRawDate(mxLine);
			final List<String> abs = Mailbox.getRawSenders(mxLine);
			out = StringUtils.concatenate("\t", out, date, abs);
		}
		System.out.println(out);
	}

}
