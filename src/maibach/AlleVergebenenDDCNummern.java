package maibach;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.TreeMultimap;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SubjectUtils;

/**
 *
 *
 * @author baumann
 *
 */
public class AlleVergebenenDDCNummern {

	private static final String FILENAME_DDC_2_Titles = "D:/Normdaten/ddc2Titles.out";

	public static void main(final String[] args) throws IOException {

		final TreeMultimap<String, String> ddc2Titles = new TreeMultimap<String, String>();

		final DownloadWorker worker = new DownloadWorker() {

			int i = 0;

			@Override
			protected void processRecord(final Record record) {
				final Set<String> ddcs = SubjectUtils
						.getAllDDCNotations(record);
				for (final String ddc : ddcs) {
					ddc2Titles.add(ddc, record.getId());
				}
				i++;
				System.err.println(i);

			}
		};

		// vorab nach DDC filtern (045F):
		final Predicate<String> titleFilter = new StringContains(
				Constants.RS + "045F " + Constants.US);

		worker.setStreamFilter(titleFilter);
		System.err.println("Titeldaten flöhen:");
		try {
			worker.processGZipFile("D:/Normdaten/DNBtitelgesamt.dat.gz");
		} catch (final Exception e) {
			e.printStackTrace();
		}

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(FILENAME_DDC_2_Titles));
		out.writeObject(ddc2Titles);
		MyFileUtils.safeClose(out);
		System.out.println(ddc2Titles);

		for (final String string : ddc2Titles) {
			System.out.println(ddc2Titles.get(string));
		}
	}

}
