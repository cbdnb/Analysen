/**
 *
 */
package scheven.hinweis.archiv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.parser.tag.TagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class HinweisDatensaetze {

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

		final RecordReader treader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);

		final TagDB db = BibTagDB.getDB();
		final Predicate<String> pred = new ContainsTag("5100", db)
				.or(new ContainsTag("5540", db))
				.or(new ContainsTag("5550", db));
		treader.setStreamFilter(pred);

		final Set<String> hinwIDs = idn2hinw.keySet();

		treader.forEach(record ->
		{
			final String idn = record.getId();
			final List<String> rswkNames = new ArrayList<>();
			final List<String> automNames5540 = new ArrayList<>();
			final List<String> automNames5550 = new ArrayList<>();
			boolean found = false;

			final Collection<String> rswkIDs = SubjectUtils.getRSWKidsSet(record);
			for (final String id : rswkIDs) {
				if (hinwIDs.contains(id)) {
					rswkNames.add(id + ": " + idn2hinw.get(id));
					found = true;
				}
			}

			final Collection<String> automID5540 = new HashSet<>(
					RecordUtils.getContentsOfFirstSubfield(record, '9', "5540"));
			for (final String id : automID5540) {
				if (hinwIDs.contains(id)) {
					automNames5540.add(id + ": " + idn2hinw.get(id));
					found = true;
				}
			}

			final Collection<String> automID5550 = new HashSet<>(
					RecordUtils.getContentsOfFirstSubfield(record, '9', "5550"));
			for (final String id : automID5550) {
				if (hinwIDs.contains(id)) {
					automNames5550.add(id + ": " + idn2hinw.get(id));
					found = true;
				}
			}

			if (found) {
				final String dhs = SGUtils.getFullDHSString(record, null);

				final String rswOut = StringUtils.concatenate(" // ",
						rswkNames);
				final String out5540 = StringUtils.concatenate(" // ",
						automNames5540);
				final String out5550 = StringUtils.concatenate(" // ",
						automNames5550);
				System.out.println(StringUtils.concatenate("\t", dhs, idn,
						rswOut, out5540, out5550));
			}

		});

	}

}
