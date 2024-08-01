/**
 *
 */
package baumann;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.StatisticUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.ie.utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 */
public class SWHistogram {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		final HashMap<Integer, String> ppn2name = GND_DB_UTIL.getppn2name();

		final Frequency<Integer> swHist = new Frequency<>();

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		reader.setStreamFilter(ContainsTag.getContainsRSWK());

		System.err.println("flöhen");
		reader.forEach(record ->
		{
			final Collection<String> idStrs = SubjectUtils.getRSWKids(record);
			swHist.addCollection(IDNUtils.idns2ints(idStrs));
		});

		System.err.println("Rangfolge:");
		// IDNs sortieren: Idn mit den meisten Treffern zuerst:
		final Integer[] keys = swHist.keySet().toArray(new Integer[0]);
		Arrays.sort(keys, Comparator.comparingLong(swHist::get).reversed());

		System.err.println(CollectionUtils.shortView(Arrays.asList(keys)));

		System.err.println();
		System.err.println("Neues Histogramm nach Rangfolge");
		// Dann in die Häufigkeiten nach ihrer Rangfolge in ein neues Histogramm
		// eingeben:
		final Frequency<Integer> newHist = new Frequency<>();
		for (int i = 0; i < keys.length; i++) {
			final int idn = keys[i];
			final long count = swHist.get(idn);
			newHist.put(i, count);
		}

		System.err.println("cdf");
		// Dann eine Verteilungsfunktion (streng monoton) erzeugen:
		final BiMap<Integer, Long> cdf = StatisticUtils.getCDF(newHist);

		System.err.println("speichern");
		final PrintWriter out = FileUtils
				.outputFile("D:/Analysen/baumann/SWW-Haeufigkeit.txt", false);
		// und alles ausgeben:
		for (int i = 0; i < keys.length; i++) {

			final Integer idnInt = keys[i];
			final String name = ppn2name.get(idnInt);
			final String idnStr = IDNUtils.int2PPN(idnInt);
			final long count = newHist.get(i);
			final long sum = cdf.get(i);

			out.println(StringUtils.concatenateTab(idnStr, name, i + 1, count,
					sum));
		}

		FileUtils.safeClose(out);
		FileUtils.safeClose(reader);

	}

}
