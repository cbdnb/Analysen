/**
 *
 */
package koehn.bioNames;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.gnd.utils.IDNFinderLeven;
import utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final IDNFinderLeven finder = new IDNFinderLeven(
				GND_DB_UTIL.getppn2bioName(),
				GND_DB_UTIL.getppn2bioVerweisungen());
		final List<String> lines = StringUtils.readLinesFromClip();
		System.err.println("Eingabe gelesen");
		String out = "";
		for (final String line : lines) {
			final Triplett<Collection<String>, Collection<String>, Integer> tripel = finder
					.find(line);
			out += StringUtils.makeExcelLine(tripel.first, tripel.second,
					tripel.third) + "\n";
		}
		System.err.println("Eingabe verarbeitet");
		StringUtils.writeToClipboard(out);
		System.err.println("fertig");

	}

}
