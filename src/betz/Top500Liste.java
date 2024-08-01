/**
 *
 */
package betz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class Top500Liste {

	private static HashSet<String> top500ppn;
	private static HashSet<String> top500nid;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		// createHashmap();
		analyzeList();
	}

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void analyzeList()
			throws IOException, ClassNotFoundException {
		top500ppn = CollectionUtils.loadHashSet(NSogg.TOP_500_PPN);
		top500nid = CollectionUtils.loadHashSet(NSogg.TOP_500_NID);
		final Collection<String> input = StringUtils.readLinesFromClip();
		final ArrayList<String> output = new ArrayList<>();
		input.forEach(line ->
		{
			if (StringUtils.isNullOrWhitespace(line))
				output.add("");
			else if (top500ppn.contains(line.trim())
					|| top500nid.contains(line.trim())) {
				output.add("+");
			} else
				output.add("-");
		});
		System.out.println(StringUtils.concatenate(output));
	}

	/**
	 * @throws IOException
	 */
	public static void createHashmap() throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(NSogg.FOLDER + "top500.txt");
		top500ppn = new HashSet<>();
		top500nid = new HashSet<>();
		reader.forEach(record ->
		{
			top500ppn.add(record.getId());
			top500nid.add(GNDUtils.getNID(record));
		});
		CollectionUtils.save(top500ppn, NSogg.TOP_500_PPN);
		CollectionUtils.save(top500nid, NSogg.TOP_500_NID);
	}

}
