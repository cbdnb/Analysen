/**
 *
 */
package scheven.mx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.mx.MXAddress;
import de.dnb.gnd.utils.mx.Mailbox;

/**
 * @author baumann
 *
 */
public class MxTabelle3 extends DownloadWorker {

	private static PrintWriter out;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final MxTabelle3 mxTabelle = new MxTabelle3();

		out = MyFileUtils.outputFile("D:/Analysen/scheven/mx/Mailboxen.txt",
				false);
		final String uberschr = StringUtils.concatenateTab("Datum erste MX",
				"Absender erste", "Datum letzte MX", "Absender letzte", "idn",
				"nid", "Satzart", "Redaktion");
		out.println(uberschr);
		mxTabelle.processGZipFile("D:/Analysen/scheven/mx/mx.gz");
		MyFileUtils.safeClose(out);
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

		Mailbox first = Mailbox.getFirstMx(record);
		Mailbox last = Mailbox.getLastMx(record);
		Collection<Mailbox> nullMxx = Collections.emptyList();
		if (first == null && last == null) {
			nullMxx = Mailbox.getNullMx(record);
			first = ListUtils.getFirst(nullMxx);
			last = ListUtils.getLast(nullMxx);
		}

		final String text = last.getText();
		if (StringUtils.contains(text, "Arbeitsnotiz", true))
			return;

		final String idn = record.getId();
		final String nid = GNDUtils.getNID(record);
		final String bbg = GNDUtils.getBBG(record);
		if (StringUtils.contains(bbg, "Tc", true))
			return;
		final String dateFirst = TimeUtils.toYYYYMMDD(first.getDate());
		final MXAddress absenderFirst = first.getAbsender();
		final String absFirstStr = absenderFirst != null
				? absenderFirst.getLibrary().nameKurz
				: first.getRawAdresses();
		final String dateLast = TimeUtils.toYYYYMMDD(last.getDate());
		final MXAddress absenderLast = last.getAbsender();
		final String absLastStr = absenderLast != null
				? absenderLast.getLibrary().nameKurz
				: last.getRawAdresses();
		final String outS = StringUtils.concatenate("\t", dateFirst,
				absFirstStr, dateLast, absLastStr, idn, nid, bbg,
				GNDUtils.getIsilVerbund(record));

		out.println(outS);
	}

}
