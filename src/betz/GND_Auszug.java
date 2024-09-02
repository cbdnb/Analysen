/**
 *
 */
package betz;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.Komprimierer;

/**
 * @author baumann
 *
 */
public class GND_Auszug {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Set<String> nids = new HashSet<>();
		MyFileUtils.readFileIntoCollection(NSogg.FOLDER + "nids.txt", nids);

		final Set<String> ppns = new HashSet<>();
		MyFileUtils.readFileIntoCollection(NSogg.FOLDER + "ppns.txt", ppns);

		System.err.println("tc laden");
		final RecordReader tcReader = RecordReader.getMatchingReader(NSogg.TC);
		tcReader.forEach(record ->
		{
			final List<String> idns = GNDUtils.getCrossConcordanceIDNs(record);
			ppns.addAll(idns);
		});
		final Set<Integer> ppnints = ppns.stream().map(IDNUtils::ppn2int)
				.collect(Collectors.toSet());

		System.err.println("Abzug laden");
		final PrintStream out = MyFileUtils
				.getGZipPrintStream(NSogg.FOLDER + "gnd_extrakt.gzip");
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);

		reader.forEach(record ->
		{
			final String ppn = record.getId();
			final int ppnInt = IDNUtils.ppn2int(ppn);
			// System.err.println(ppn);
			final String nid = GNDUtils.getNID(record);

			String komp = null;
			if (nids.contains(nid)) {
				komp = Komprimierer.toGZip(record);
				nids.remove(nid);
			}
			if (ppnints.contains(ppnInt)) {
				komp = Komprimierer.toGZip(record);
				ppnints.remove(ppnInt);
			}
			if (komp != null) {
				out.println(komp);
				System.err.println(record);
			}

		});

		StreamUtils.safeClose(out);

		System.out.println("ungenutzte ppns:");
		System.out.println(FilterUtils.map(ppnints, IDNUtils::int2PPN));

		System.out.println();

		System.out.println("ungenutzte nids:");
		System.out.println(nids);
	}

}
