/**
 *
 */
package scheven.feld1131;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.ListUtils;
import de.dnb.ie.utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 */
public class RateNamen {

	private static BiMultimap<Integer, String> ppn2rda;
	private static BiMultimap<Integer, String> ppn2rdaNorm;
	private static BiMultimap<Integer, String> ppn2verw;
	private static BiMultimap<Integer, String> ppn2verwNorm;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		initialisiere();
		final PrintWriter out = FileUtils
				.outputFile(Geo1131.path + "/Korrektur.txt", false);
		final List<String> falscheGeos = new ArrayList<>();
		FileUtils.readFileIntoCollection(Geo1131.path + "/liste.txt",
				falscheGeos);
		falscheGeos.forEach(geoFalsch ->
		{
			final Pair<String, Integer> pair = getRealName(geoFalsch);
			out.println(StringUtils.concatenateTab(pair.first, pair.second));
		});

		FileUtils.safeClose(out);

	}

	/**
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void initialisiere()
			throws ClassNotFoundException, IOException {
		System.err.println("ppn2rda");
		ppn2rda = GND_DB_UTIL.getppn2RDAName();

		System.err.println("ppn2rdaNorm");
		ppn2rdaNorm = BiMultimap.createSetMap();
		ppn2rda.getKeySet().forEach(ppn ->
		{
			// In dieser Richtung gibt es nur eines!
			final Collection<String> geos = ppn2rda.get(ppn);
			geos.forEach(geo ->
			{
				final String normalisiert = normalisiert(geo);
				ppn2rdaNorm.add(ppn, normalisiert);
			});
		});

		System.err.println("ppn2verw");
		ppn2verw = GND_DB_UTIL.getppn2RDAVerweisungen();

		System.err.println("ppn2verwNorm");
		ppn2verwNorm = BiMultimap.createSetMap();
		ppn2verw.getKeySet().forEach(ppn ->
		{
			final Collection<String> geos = ppn2verw.get(ppn);
			geos.forEach(geo -> ppn2verwNorm.add(ppn, normalisiert(geo)));
		});
	}

	/**
	 *
	 * @param geo
	 *            auch null
	 * @return ohne Sonderzeichen, Umlaute ä->ae ...
	 */
	public static String normalisiert(final String geo) {
		if (geo == null)
			return "";
		final List<String> stichworte = StringUtils.stichwortListe(geo, null);
		String concatenated = StringUtils.concatenate("", stichworte);

		// Umlaute ersetzen:
		concatenated = concatenated.replaceAll("ä", "ae");
		concatenated = concatenated.replaceAll("ö", "oe");
		concatenated = concatenated.replaceAll("ü", "ue");
		concatenated = concatenated.replaceAll("ß", "ss");
		return concatenated;
	}

	/**
	 *
	 * @param geo
	 *            auch null
	 * @return (geratener Name, level) oder (null, null)
	 */
	public static Pair<String, Integer> getRealName(String geo) {
		geo = normalisiert(geo);
		String realname = null;

		Collection<Integer> ppns = ppn2rdaNorm.getKeys(geo);
		int size = ppns.size();
		if (size == 1) {
			final int ppn = ListUtils.getFirst(ppns);
			realname = ListUtils.getFirst(ppn2rda.getValueSet(ppn));
			return new Pair<>(realname, 1);
		}

		ppns = ppn2verwNorm.getKeys(geo);
		size = ppns.size();
		if (size == 1) {
			final int ppn = ListUtils.getFirst(ppns);
			realname = ListUtils.getFirst(ppn2rda.getValueSet(ppn));
			return new Pair<>(realname, 2);
		}

		ppns = ppn2rdaNorm.getKeys(geo);
		size = ppns.size();
		if (size > 1) {
			realname = ppn2rda.searchValues(ppns).toString();
			return new Pair<>(realname, 10 + size);
		}

		ppns = ppn2verwNorm.getKeys(geo);
		size = ppns.size();
		if (size > 1) {
			realname = ppn2rda.searchValues(ppns).toString();
			return new Pair<>(realname, 20 + size);
		}

		return Pair.getNullPair();

	}

}
