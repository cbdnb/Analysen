package baumann;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;

public class TgOhneDet extends DownloadWorker {

	static int i = 0;

	static Collection<String> dets = Arrays.asList("3", "4");

	@Override
	protected void processRecord(final Record record) {

		final ArrayList<Line> lines = GNDUtils.getValidDDCLines(record);

		for (final Line line : lines) {
			final String det = GNDUtils.getDDCDeterminacy(line);
			// if (det != null) {
			if (dets.contains(det)) {
				i++;
				// System.err.println(record);
				return;
			}
		}

	}

	public static void main(final String[] args) throws IOException {

		final TgOhneDet dlw = new TgOhneDet();

		dlw.setStreamFilter(new ContainsTag("083", GNDTagDB.getDB()));

		System.err.println("Ts flöhen:");
		try {
			dlw.processGZipFile("D:/Normdaten/DNBGND_s.dat.gz");

		} catch (final Exception e) {
			e.printStackTrace();
		}

		System.out.println(i);

	}

}
