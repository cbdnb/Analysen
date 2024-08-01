/**
 *
 */
package baumann.statistiken;

import java.io.IOException;
import java.util.ArrayList;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SGUtils;

/**
 * @author baumann
 *
 */
public class DoppelteIE4821 extends DownloadWorker {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final DoppelteIE4821 doppelte = new DoppelteIE4821();
		doppelte.setStreamFilter(new StringContains("StatIE"));

		doppelte.processGZipFile(Constants.TITEL_PLUS_EXEMPLAR_Z);

	}

	@Override
	protected void processRecord(final Record record) {
		final ArrayList<Line> statLines = BibRecUtils.getIEStatistik(record);
		if (statLines.size() > 1) {
			final DDC_SG sg = DDC_SG.getSG(SGUtils.getDhsStringPair(record).first);
			final String id = record.getId();
			System.out.println(StringUtils.concatenateTab(id, sg));
		}

	}

}
