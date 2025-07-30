package schlicht;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.formatter.ExcelFormatter;
import de.dnb.gnd.utils.formatter.Pica3Formatter;

/**
 * Bitte CSV-Datei erstellen mit folgenden Normdaten:
 *
 * Set 1: f sn 2.1 und bbg ts* und ent sip =2400 Datensätze
 *
 * Plus: Von Set 1: alle 550-Einträge mit Entitätencode saz
 *
 * Plus: Von Set 1: alle 500 Einträge
 *
 * Plus: Von Set 1: alle 510 Einträge
 *
 * Also 4 verschiedene Dateien: sip saz Tp Tb
 *
 * Ablage: V:\Projekte\Schriftproben\Treffen\Normdatenworkshop\Datendump
 *
 */
public class Sip {

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final List<Record> sipList = new ArrayList<>();
		final List<Record> sazList = new ArrayList<>();
		final List<Record> tpList = new ArrayList<>();
		final List<Record> tbList = new ArrayList<>();
		final Set<Record> relRecords = CollectionUtils
				.loadHashSet(SipDB.RECORDS_RELATED);
		RecordReader.getMatchingReader(SipDB.SIP_RECORDS).forEach(sipList::add);

		relRecords.forEach(rec ->
		{
			final char type = GNDUtils.getRecordType(rec);
			switch (type) {
			case 'p':
				tpList.add(rec);
				break;
			case 'b':
				tbList.add(rec);
				break;
			case 's':
				if (!GNDUtils.containsEntityType(rec, "sip"))
					sazList.add(rec);
				break;
			default:
				System.err.println(
						"Unerwarteter Typ:" + rec.getId() + "/" + type);
			}
		});

		final String sipHtml = Pica3Formatter.toHTML(sipList, "Schriften");
		final PrintWriter sip = MyFileUtils
				.outputFile(SipDB.FOLDER + "sip.html", false);
		sip.print(sipHtml);
		MyFileUtils.safeClose(sip);

		final String sazHtml = Pica3Formatter.toHTML(sazList, "Typ saz");
		final PrintWriter saz = MyFileUtils
				.outputFile(SipDB.FOLDER + "saz.html", false);
		saz.print(sazHtml);
		MyFileUtils.safeClose(saz);

		final String tpHtml = Pica3Formatter.toHTML(tpList, "Typ Tp");
		final PrintWriter tp = MyFileUtils.outputFile(SipDB.FOLDER + "tp.html",
				false);
		tp.print(tpHtml);
		MyFileUtils.safeClose(tp);

		final String tbHtml = Pica3Formatter.toHTML(tbList, "Typ Tb");
		final PrintWriter tb = MyFileUtils.outputFile(SipDB.FOLDER + "tb.html",
				false);
		tb.print(tbHtml);
		MyFileUtils.safeClose(tb);

		// -------------------------

		final String sipTab = ExcelFormatter.format(sipList, false);
		final PrintWriter siptabOut = MyFileUtils
				.outputFile(SipDB.FOLDER + "sipTab.txt", false);
		siptabOut.print(sipTab);
		MyFileUtils.safeClose(siptabOut);

		final String sazTab = ExcelFormatter.format(sazList, false);
		final PrintWriter sazTabOut = MyFileUtils
				.outputFile(SipDB.FOLDER + "sazTab.txt", false);
		sazTabOut.print(sazTab);
		MyFileUtils.safeClose(sazTabOut);

		final String tpTab = ExcelFormatter.format(tpList, false);
		final PrintWriter tpTabOut = MyFileUtils
				.outputFile(SipDB.FOLDER + "tpTab.txt", false);
		tpTabOut.print(tpTab);
		MyFileUtils.safeClose(tpTabOut);

		final String tbTab = ExcelFormatter.format(tbList, false);
		final PrintWriter tbTabOut = MyFileUtils
				.outputFile(SipDB.FOLDER + "tbTab.txt", false);
		tbTabOut.print(tbTab);
		MyFileUtils.safeClose(tbTabOut);

	}

}
