/**
 *
 */
package baumann;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class NichtSamTitel {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final PrintWriter pw = MyFileUtils.oeffneAusgabeDatei(
				"D:/Analysen/baumann/tbs_s/nicht_s_trotzdem_verknüpft.txt",
				false);

		final RecordReader titelReader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		titelReader.setStreamFilter(new ContainsTag("5100", BibTagDB.getDB()));
		final Set<Integer> mitTitelverlinkt = new HashSet<>();
		System.err.println("titel");
		titelReader.forEach(record ->
		{
			mitTitelverlinkt.addAll(
					IDNUtils.ppns2ints(SubjectUtils.getRSWKidsSet(record)));
		});

		System.err.println("gnd");

		final DownloadWorker gndWorker = new DownloadWorker() {
			@Override
			protected void processRecord(final Record record) {
				// System.err.println(record);
				if (!GNDUtils.isTeilbestandIE(record)) {
					final String id = record.getId();
					final int ppn2int = IDNUtils.ppn2int(id);
					if (mitTitelverlinkt.contains(ppn2int))
						pw.println(id);

				}
			}
		};

		gndWorker.setStreamFilter(new ContainsTag("011", GNDTagDB.getDB()));
		gndWorker.processGZipFiles(Constants.Tb, Constants.Tf, Constants.Tg,
				Constants.Tp, Constants.Ts, Constants.Tu);

	}

}
