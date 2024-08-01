/**
 *
 */
package junger;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;

/**
 * @author baumann
 *
 */
public class EKZ1 {

	public static final String DNB = "nur DNB";
	public static final String DNB_EKZ = "DNB und EKZ";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Path path = FileSystems.getDefault()
				.getPath("D:/texte/id-titel-2018.txt");
		final Map<String, Boolean> isbn2Used = new TreeMap<>();
		final Set<String> lines = new HashSet<>(Files.readAllLines(path));
		lines.forEach(line ->
		{
			isbn2Used.put(line, false);
		});

		final Frequency<String> frequency = new Frequency<>();

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		// 2000-2018:
		final Predicate<String> titleFilter = new ContainsTag("2105", '0', "1",
				BibTagDB.getDB()).or(
						new ContainsTag("2105", '0', "0", BibTagDB.getDB()));
		reader.setStreamFilter(titleFilter);

		reader.forEach(record ->
		{
			if (!BibRecUtils.isRA(record))
				return;
			final List<String> isbns = BibRecUtils.getValidRawISBNs(record);
			if (isbns.isEmpty())
				return;
			System.err.println(isbns);
			for (final String isb : isbns) {
				if (lines.contains(isb)) {
					isbn2Used.put(isb, true);
					frequency.add(DNB_EKZ);
					return;
				}
			}
			frequency.add(DNB);
		});

		int nurEKZ = 0;

		for (final String isbn : lines) {
			if (!isbn2Used.get(isbn))
				nurEKZ++;
		}

		System.out.println("nur EKZ: " + nurEKZ);
		System.out.println(DNB + ": " + frequency.get(DNB));
		System.out.println(DNB_EKZ + ": " + frequency.get(DNB_EKZ));

	}

}
