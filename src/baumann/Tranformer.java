/**
 *
 */
package baumann;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;

/**
 * @author baumann
 *
 */
public class Tranformer {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args) throws FileNotFoundException {
		final String[][] table = StringUtils.readTableFromClip();
		// idn, name, Koordinaten
		final List<String> orte = new ArrayList<>();
		// idn, epoche, Anzahl
		final List<String> orteEpochen = new ArrayList<>();

		// Überschrift verarbeiten
		String[] zeile = table[0];
		final List<String> epochen = Arrays.asList(zeile);

		for (int i = 1; i < table.length; i++) {
			zeile = table[i];

			final String idn = zeile[0];
			final String name = zeile[1];
			final String ost = zeile[2];
			final String nord = zeile[3];
			final String ort = StringUtils.concatenate("\t", idn, name, nord,
					ost);
			orte.add(ort);

			for (int j = 4; j < zeile.length; j++) {
				final String zahl = zeile[j];
				if (!zahl.equals("0")) {
					final String epocheZahl = StringUtils.concatenate("\t", idn,
							epochen.get(j), zahl);
					orteEpochen.add(epocheZahl);
					System.err.println(epocheZahl);
				}
			}

		}

		final PrintStream streamO = new PrintStream(
				"D:\\Analysen\\baumann\\orte_2.csv");
		streamO.println("idn\tname\tlat\tlon");
		orte.forEach(streamO::println);
		StreamUtils.safeClose(streamO);

		final PrintStream streamE = new PrintStream(
				"D:\\Analysen\\baumann\\zahlen_2.csv");
		streamE.println("idn\tepoche\tcount");
		orteEpochen.forEach(streamE::println);
		StreamUtils.safeClose(streamE);

	}

}
