/**
 *
 */
package baumann.alteSWW;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class Korrektur {

	private static Map<String, String> konkordanz;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		konkordanz = new TreeMap<>();
		final Map<String, String> neu = new TreeMap<>();
		final Map<String, String> aenderung = new TreeMap<>();

		System.err.println("Konkordanz laden");
		lade(konkordanz,
				"V:/03_FB_EE/14_IE/_intern/00_Arbeitsordner/Baumann/Alte_SWW/altdaten_konkordanz.txt");
		System.err.println("neu laden");
		lade(neu,
				"V:/03_FB_EE/14_IE/_intern/00_Arbeitsordner/Baumann/Alte_SWW/neu.txt");
		System.err.println("Änderung laden");
		lade(aenderung,
				"V:/03_FB_EE/14_IE/_intern/00_Arbeitsordner/Baumann/Alte_SWW/aenderung.txt");

		putAll(aenderung);
		putAll(neu);

		System.err.println("Löschen");
		final List<String> loeschStr = StringUtils.readLinesFromFile(
				"V:/03_FB_EE/14_IE/_intern/00_Arbeitsordner/Baumann/Alte_SWW/loeschung.txt");
		loeschStr.forEach(loe ->
		{
			if (konkordanz.remove(
					StringUtils.unicodeDecomposition(loe.trim())) == null)
				System.err.println("Wurde nicht entfernt: " + loe);
		});

		final PrintWriter out = FileUtils.outputFile(
				"V:/03_FB_EE/14_IE/_intern/00_Arbeitsordner/Baumann/Alte_SWW/konkordanz_neu.txt",
				false);
		konkordanz.forEach((a, b) -> out.println(a + "\t" + b));
		FileUtils.safeClose(out);
	}

	/**
	 * @param aenderung
	 */
	private static void putAll(final Map<String, String> otherMap) {
		otherMap.keySet().forEach(key ->
		{
			final String value = otherMap.get(key);
			final String ret = konkordanz.put(key, value);
			if (ret != null)
				System.err.println("Ersetzt wurde:" + key + "/" + value);
		});

	}

	/**
	 * @param map
	 * @param string
	 * @throws IOException
	 */
	private static void lade(final Map<String, String> map, final String string)
			throws IOException {
		final List<String> zeilen = StringUtils.readLinesFromFile(string);
		zeilen.forEach(zeile ->
		{
			final String[] split = zeile.split("\t", 2);
			String altSW = split[0].trim();
			altSW = StringUtils.unicodeDecomposition(altSW);
			String rest = split[1].trim();
			rest = StringUtils.unicodeDecomposition(rest);

			final List<String> idns = IDNUtils.extractIDNs(rest);
			final int posGeschichte = rest.indexOf(":z");
			if (posGeschichte > -1) {
				idns.add(rest.substring(posGeschichte));
			}

			if (!idns.isEmpty()) {
				final String idnsString = StringUtils.concatenateTab(idns);
				if (map.put(altSW, idnsString) != null)
					System.err.println(
							"Seltsam. Ersetzt wurde schon beim Laden:" + zeile);
			} else
				System.err
						.println("Oops, something went wrong. Couldn't extract "
								+ zeile);

		});

	}

}
