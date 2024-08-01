/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.mx.Library;

/**
 * @author baumann
 *
 */
public class ZweiDollarG extends DownloadWorker {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final ZweiDollarG zweiDollarG = new ZweiDollarG();
		zweiDollarG.gzipSettings();
		zweiDollarG.setOutputFile("D:/Analysen/scheven/2_Dollar_g2_1xx4xx.txt");
		zweiDollarG.processFile(Constants.GND);

	}

	public static boolean enthaelt2g(final Line line) {
		final StringBuilder indicators = new StringBuilder();
		line.getSubfields().stream().map(Subfield::getIndicator)
				.map(ind -> ind.indicatorChar).forEach(indicators::append);
		final Matcher gMatcher = gPattern.matcher(indicators);
		return gMatcher.find();
	}

	private static final Pattern gPattern = Pattern.compile("gg");

	@Override
	protected void processRecord(final Record record) {
		Line line1xx = null;
		try {
			line1xx = GNDUtils.getHeading(record);
		} catch (final IllegalStateException e) {
			return;
		}

		final String name = GNDUtils.getNameOfRecord(record);
		final String level = RecordUtils.getDatatype(record);
		final String idn = record.getId();

		final ArrayList<String> falsche4xx = new ArrayList<>();
		final ArrayList<Line> lines4xx = GNDUtils.getLines4XX(record);
		lines4xx.forEach(line ->
		{
			if (enthaelt2g(line)) {
				final String falschesFeld = RecordUtils.toPica(line,
						Format.PICA3, false, '$');
				falsche4xx.add(falschesFeld);
			}
		});

		if (!falsche4xx.isEmpty() || enthaelt2g(line1xx)) {
			final String falscheOut = StringUtils.concatenate("\t", falsche4xx);
			final Library urheber = GNDUtils.getVerbund(record);
			final String out = StringUtils.concatenate("\t", level, idn,
					urheber, name, falscheOut);
			System.out.println(out);
			println(out);
		}

	}

}
