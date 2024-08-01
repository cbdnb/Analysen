/**
 *
 */
package baumann.alteSWW;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class MacheKonkordanz {

	final static String path = "V:/03_FB_EE/14_IE/07_Projekte/Interne_Projekte/Altdaten1972_1985/Arbeitsordner_ba/";

	final static Map<String, String> term2Umsetzung = new LinkedHashMap<>();

	final static String geschiS = ":z.+";
	final static Pattern geschiPattern = Pattern.compile(geschiS);

	private static final String out = "altdaten_konkordanz.txt";

	private static final String ERROR = "error.txt";

	private static PrintWriter pwError;

	private final static Pair<String, List<String>> emptyPair = new Pair<String, List<String>>(
			"", Collections.emptyList());

	static List<String> extrahiereAusZelle(String zelle) {
		if (zelle == null)
			return Collections.emptyList();
		zelle = zelle.trim();
		final List<String> idns = IDNUtils.extractPPNs(zelle);
		final Matcher matcher = geschiPattern.matcher(zelle);
		if (matcher.find()) {
			idns.add(matcher.group());
		}
		return idns;
	}

	static void leseDatei(final String filename, final boolean sammleAlle)
			throws FileNotFoundException {
		pwError.println("------- Datei: " + filename);
		final Scanner scanner = new Scanner(new File(path + filename));
		scanner.nextLine();
		int i = 2;
		while (scanner.hasNext()) {
			final String nextLine = scanner.nextLine();
			final Pair<String, List<String>> pair = analysiereZeile(nextLine,
					sammleAlle);
			final List<String> idns = pair.second;
			if (!idns.isEmpty()) {
				final String term = pair.first;
				final String newV = StringUtils.concatenate("\t", idns);
				final String oldV = term2Umsetzung.put(term, newV);
				if (oldV != null && !StringUtils.equals(oldV, newV)) {
					pwError.println("Zeile " + i + ": " + term + " " + oldV
							+ " ersetzt durch " + newV);
					pwError.println(".........");
				}
			} else {
				pwError.println("-> Zeile " + i + ": Keine idn zu ermitteln: "
						+ nextLine);
				pwError.println("...............");
			}
			i++;
		}
		StreamUtils.safeClose(scanner);
	}

	public static Pair<String, List<String>> analysiereZeile(final String zeile,
			final boolean sammleAlle) {

		if (StringUtils.isNullOrWhitespace(zeile)) {
			// pwError.println("zeile leer oder null");
			return emptyPair;
		}
		// größe>0:
		final String[] zellen = zeile.split("\t");
		if (zellen.length == 0) {
			pwError.println("leere Zeile");
			return emptyPair;
		}
		if (zellen.length == 1) {
			pwError.println("Nur eine Spalte: " + zeile);
			return emptyPair;
		}
		final String term = zellen[0].trim();
		if (term.isEmpty()) {
			pwError.println("Kein Term: " + zeile);
			return emptyPair;
		}
		if (extrahiereAusZelle(term).size() != 0) {
			pwError.println("idn in der ersten Spalte: " + zeile);
			return emptyPair;
		}
		final List<String> idns = new ArrayList<>();
		final Pair<String, List<String>> pair = new Pair<String, List<String>>(
				term, idns);

		for (int i = 1; i < zellen.length; i++) {
			final List<String> actualList = extrahiereAusZelle(zellen[i]);
			idns.addAll(actualList);
			// erster Treffer und nicht mehr weitersuchen:
			if (!actualList.isEmpty() & !sammleAlle)
				break;
		}
		return pair;
	}

	static void ausgeben() {

		try (PrintWriter pw = new PrintWriter(path + out)) {
			term2Umsetzung.keySet().forEach(key ->
			{
				pw.println(key + "\t" + term2Umsetzung.get(key));
			});
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args) throws FileNotFoundException {

		pwError = new PrintWriter(path + ERROR);

		leseDatei("rest.txt", true);
		leseDatei("personen.txt", false);
		leseDatei("null.txt", true);
		leseDatei("ts.txt", true);
		leseDatei("tg.txt", true);

		ausgeben();

		StreamUtils.safeClose(pwError);

	}

}
