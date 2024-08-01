/**
 *
 */
package scheven.hinweis.archiv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class HinweisDatensaetzeCKW {

	public static HashMap<String, String> idn2hinw = new HashMap<>();

	public static DownloadWorker gndWorker = new DownloadWorker() {

		@Override
		protected void processRecord(final Record record) {
			if (!GNDUtils.isUseCombination(record))
				return;
			final String idn = record.getId();
			final String name = GNDUtils.getNameOfRecord(record) + " ["
					+ RecordUtils.getDatatype(record) + "]";

			idn2hinw.put(idn, name);

		}
	};

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		gndWorker.gzipSettings();
		gndWorker.setFilePrefix("DNBGND_");
		gndWorker.setInputFolder("D:/Normdaten");
		gndWorker.processAllFiles();

		// System.err.println(idn2hinw);
		System.err.println("Titeldaten:");

		final RecordReader ckwReader = RecordReader.getMatchingReader(
				"D:/Analysen/baumann/thesaurus_wirtschaft.txt");

		ckwReader.forEach(record ->
		{

			final ArrayList<Line> lines = RecordUtils.getLines(record, "190");
			lines.forEach(line ->
			{
				final String id190 = line.getIdnRelated();
				if (!idn2hinw.containsKey(id190))
					return;

				System.out.println(record);
				System.out.println();

			});

		});

	}

}
