/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class Top500 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final List<String> lines = StringUtils.readLinesFromClip();
		System.out.println("Lese Download");
		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Analysen/scheven/top500.txt");
		final Set<String> top500nids = new TreeSet<>();
		reader.forEach(record ->
		{
			top500nids.add(GNDUtils.getNID(record));
		});

		lines.forEach(line ->
		{
			final List<String> idns = IDNUtils.extractIDNs(line);
			if (idns.size() == 1) {
				final String nid = ListUtils.getFirst(idns);
				if (!top500nids.contains(nid))
					System.out.println(line);
			}

		});

	}

}
