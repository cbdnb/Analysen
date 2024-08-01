package hofmann;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

public class AutorAlsOBPA extends DownloadWorker {

	Frequency<String> frequency5XX = new Frequency<String>();

	@Override
	protected void processRecord(final Record record) {

		if (RecordUtils.isBibliographic(record))
			return;

		final List<Line> lines500 = RecordUtils.getLines(record, "500");
		if (lines500.isEmpty())
			return;

		final List<Line> filteredLines = GNDUtils.getLinesWithDollar4(lines500,
				"obpa");
		if (filteredLines.isEmpty())
			return;

		System.out.println(record);
		System.out.println();

	}

	public static void main(final String[] args) throws IOException {

		final AutorAlsOBPA aut = new AutorAlsOBPA();

		try {
			aut.processGZipFile("D:/Normdaten/DNBGND_u.dat.gz");
		} catch (final Exception e) {

		}

	}
}
