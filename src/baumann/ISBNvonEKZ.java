/**
 *
 */
package baumann;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import de.dnb.basics.applicationComponents.FileUtils;

/**
 * @author baumann
 *
 */
public class ISBNvonEKZ {

	static String pathprefix = "D:/texte/id-titel-";

	static Map<Integer, Set<String>> year2isbns = new HashMap<>();

	static List<Integer> years = Arrays.asList(2013, 2014, 2015, 2016, 2017,
			2018);

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		for (int y = 2013; y < 2019; y++) {
			final Set<String> isbns = new HashSet<>();
			final String file = pathprefix + y + ".txt";
			try {
				FileUtils.readFileIntoCollection(file, isbns);
			} catch (final FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			year2isbns.put(y, isbns);
		}

		for (int y = 2014; y < 2019; y++) {
			final Set<String> previous = year2isbns.get(y - 1);
			final Set<String> intermediate = year2isbns.get(y);
			final Set<String> actual = new HashSet<>(intermediate);
			actual.removeAll(previous);
			System.out.println(y + ": " + actual.size());
		}

	}

}
