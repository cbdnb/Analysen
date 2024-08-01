/**
 *
 */
package scheven.hinweis.archiv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;
import de.dnb.ie.utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 *         Findet über die in der 190 verküpften Schlagwörter die wirklich
 *         voneinander verschiedenen Begriffe - also Begriffscluster. Jede Menge
 *         von SWW, die von anderen Mengen verschieden ist, repäsentiert einen
 *         eigenen Begriff.
 *
 */
public class HinweisGruppieren extends DownloadWorker {

	static final String folder = "D:/Analysen/baumann/Hinweissaetze/";
	private static HashMap<Integer, String> ppn2name;

	static Multimap<Set<String>, String> relationierte_1xx = new ListMultimap<>();

	@Override
	protected void processRecord(final Record record) {
		if (!GNDUtils.isUseCombination(record))
			return;
		final List<Line> hinweisLines = GNDUtils.getHinweisLines(record);
		if (hinweisLines.isEmpty())
			return;
		final String nid = GNDUtils.getNID(record);

		final LinkedHashSet<String> hinweisSet = new LinkedHashSet<>(
				Util.get260IdnsNames(hinweisLines, ppn2name));
		String zelle;
		try {
			zelle = RDAFormatter.getRDAHeading(record);
		} catch (final IllFormattedLineException e) {
			zelle = GNDUtils.getNameOfRecord(record);
		}
		zelle += "\n" + nid;
		final String syst = GNDUtils.getFirstGNDClassification(record);
		zelle += "\n" + syst;
		zelle += "\n" + GNDUtils.getIsilVerbund(record);
		System.err.println(zelle);
		relationierte_1xx.add(hinweisSet, zelle);

	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args) throws IOException {
		try {
			ppn2name = GND_DB_UTIL.getppn2name();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final HinweisGruppieren gruppieren = new HinweisGruppieren();
		gruppieren.setStreamFilter(new ContainsTag("260", GNDTagDB.getDB()));
		gruppieren.processGZipFiles(Constants.Tb, Constants.Tg, Constants.Tp,
				Constants.Tu);
		// gruppieren.processGZipFiles(Constants.GND);
		System.out.println(relationierte_1xx.getKeyCount());
		gruppieren.setOutputFile(folder + "Hinweissätze_gruppiert" + ".txt");
		final List<List<String>> zeilen = new ArrayList<>();
		relationierte_1xx.forEach(kombination ->
		{
			final List<String> zellen = new ArrayList<>();
			zellen.add(kombination.toString());
			zellen.addAll(relationierte_1xx.get(kombination));
			zeilen.add(zellen);

		});
		final Comparator<List<String>> myComparator = (list1,
				list2) -> list2.size() - list1.size();
		Collections.sort(zeilen, myComparator);

		zeilen.forEach(zeile ->
		{
			gruppieren.println(StringUtils.makeExcelLine(zeile));
			System.err.println(zeile);
		});

	}

}
