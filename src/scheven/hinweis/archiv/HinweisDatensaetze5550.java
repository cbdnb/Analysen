/**
 *
 */
package scheven.hinweis.archiv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.parser.tag.TagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class HinweisDatensaetze5550 {

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
		final Predicate<String> pred = new ContainsTag("5550", db);
		treader.setStreamFilter(pred);

		final Set<String> verwendeteHinweisSaetzeCKW = new HashSet<>();
		final Set<String> verwendeteHinweisSaetzeGND = new HashSet<>();

		treader.forEach(record ->
		{

			final List<String> automSWW5550 = new ArrayList<>();

			final ArrayList<Line> lines = RecordUtils.getLines(record, "5550");
			lines.forEach(line ->
			{
				String id5550 = line.getIdnRelated();
				if (!idn2hinw.containsKey(id5550))
					return;
				// Keine Verbalisierung gesucht:
				final String dollarH = SubfieldUtils
						.getContentOfFirstSubfield(line, 'H');
				if (StringUtils.equals(dollarH, "gnd-ddc"))
					return;

				// andere quelle als Verbalisierung:
				final String name = idn2hinw.get(id5550);
				id5550 = "!" + id5550 + "!";
				final String quelle = "["
						+ SubfieldUtils.getContentOfFirstSubfield(line, 'b')
						+ "]";

				if (StringUtils.contains(quelle, "ckw", true))
					verwendeteHinweisSaetzeCKW.add(id5550 + "\t" + name);
				else
					verwendeteHinweisSaetzeGND.add(id5550 + "\t" + name);

				final String automSW5550 = StringUtils.concatenate("", quelle,
						id5550, name);
				automSWW5550.add(automSW5550);
			});

			if (automSWW5550.isEmpty())
				return;

			final String idn = record.getId();
			final String dhs = SGUtils.getFullDHSString(record, null);
			final String out5550 = StringUtils.concatenate("\t", automSWW5550);
			final String recordInfo = StringUtils.concatenate("\t", dhs, idn,
					out5550);
			System.out.println(recordInfo);

		});

		System.out.println();
		System.out.println("---------");
		System.out.println("ckw:");
		verwendeteHinweisSaetzeCKW.forEach(System.out::println);
		System.out.println("gnd:");
		verwendeteHinweisSaetzeGND.forEach(System.out::println);

	}

}
