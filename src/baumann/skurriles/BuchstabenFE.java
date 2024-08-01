/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.util.ArrayList;
import de.dnb.basics.Constants;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class BuchstabenFE extends DownloadWorker {

	private static final String JJJJ = "2022";
	private static BuchstabenZaehler zaehler = new BuchstabenZaehler();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final BuchstabenFE fe = new BuchstabenFE();
		fe.setStreamFilter(new StringContains("StatZUG")
				.and(new StringContains(JJJJ + "-")));
		fe.setOutputFile("D:/Analysen/baumann/skurriles/buchstaben_titel_"
				+ JJJJ + ".txt");
		// fe.processFile(Constants.TITEL_PLUS_EXEMPLAR_Z);
		fe.processGZipFile(Constants.TITEL_PLUS_EXEMPLAR_Z);

		zaehler.ausgabe(fe);

	}

	@Override
	protected void processRecord(final Record record) {
		final ArrayList<Line> zugStatLines = BibRecUtils
				.getZUGStatistik(record);
		boolean found = false;
		for (final Line line : zugStatLines) {
			final String date = SubfieldUtils.getDollarD(line);
			if (date.startsWith(JJJJ))
				found = true;
		}
		if (!found)
			return;

		if (!SKUtil.spracheErlaubt(record))
			return;
		final String tit = BibRecUtils.getMainTitle(record);
		if (!SKUtil.spracheErlaubt(tit))
			return;
		final String id = record.getId();

		zaehler.buchstabenStatistik(tit, id);

	}

}
