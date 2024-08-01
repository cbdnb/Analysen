/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Bemerkungsfelder extends DownloadWorker {

	static final Frequency<String> BEM_STAT = new Frequency<>();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {
		final int lev = GNDUtils.getLevel(record);
		if (lev != 1)
			return;
		final List<Line> bems = GNDUtils.getBemerkungsFelder(record);
		final String idn = record.getId();
		final List<String> systs = GNDUtils.getGNDClassifications(record);
		final String syst = StringUtils.concatenate(";", systs);
		bems.forEach(bem ->
		{
			final String picaWithoutTag = RecordUtils.toPicaWithoutTag(bem);
			if (!picaWithoutTag.contains("RSWK"))
				return;

			final String concatenated = StringUtils.concatenate("\t", idn, syst,
					bem.getTag().pica3, picaWithoutTag);
			println(concatenated);
			// System.err.println(concatenated);

		});

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Bemerkungsfelder bem = new Bemerkungsfelder();
		bem.gzipSettings();
		bem.setOutputFile("D:/Analysen/scheven/bemerkungen 1.txt");
		bem.setStreamFilter(new StringContains("RSWK"));
		// bem.processFile(Constants.GND_GEO);
		// bem.processFile(Constants.GND_WERKE);
		// bem.processFile(Constants.GND_KOE);
		bem.processFile(Constants.Ts);

	}

}
