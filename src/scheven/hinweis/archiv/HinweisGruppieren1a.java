/**
 *
 */
package scheven.hinweis.archiv;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;
import utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 *         Findet über die in der 190 verküpften Schlagwörter die wirklich
 *         voneinander verschiedenen Begriffe - also Begriffscluster. Jede Menge
 *         von SWW, die von anderen Mengen verschieden ist, repäsentiert einen
 *         eigenen Begriff.
 *
 *         Es werden nur die Kombinationen ausgegeben, zu denen es genau einen
 *         Hinweissatz gibt, wiel diese leichter in einen normalen Datensatz
 *         umgewandelt werden können.
 *
 */
public class HinweisGruppieren1a extends DownloadWorker {

	static final String folder = "D:/Analysen/baumann/Hinweissaetze/";
	private static HashMap<Integer, String> ppn2name;

	static ListMultimap<Set<String>, String> relationierte_1xx = new ListMultimap<>();

	@Override
	protected void processRecord(final Record record) {
		if (!GNDUtils.isUseCombination(record))
			return;
		final List<Line> hinweisLines = GNDUtils.getHinweisLines(record);
		if (hinweisLines.isEmpty())
			return;

		final LinkedHashSet<String> hinweisSet = new LinkedHashSet<>(
				Util.get260IdnsNames(hinweisLines, ppn2name));

		final String nid = GNDUtils.getNID(record);

		final char typ = GNDUtils.getRecordType(record);

		String name;
		try {
			name = RDAFormatter.getRDAHeading(record);
		} catch (final IllFormattedLineException e) {
			name = GNDUtils.getNameOfRecord(record);
		}

		final String sys = GNDUtils.getFirstGNDClassification(record);

		final String isilVerbund = GNDUtils.getIsilVerbund(record);

		relationierte_1xx.add(hinweisSet,
				StringUtils.concatenateTab(typ, name, nid, sys, isilVerbund));

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
		final HinweisGruppieren1a gruppieren = new HinweisGruppieren1a();
		gruppieren.setStreamFilter(new ContainsTag("260", GNDTagDB.getDB()));
		gruppieren.processGZipFiles(Constants.Tb, Constants.Tg, Constants.Tp,
				Constants.Tu);
		// gruppieren.processGZipFiles(Constants.GND);
		System.out.println(relationierte_1xx.getKeyCount());
		gruppieren.setOutputFile(
				folder + "Hinweissätze_einmal_vorkommend" + ".txt");

		relationierte_1xx.forEach(kombination ->
		{
			final List<String> hinweise = (List<String>) relationierte_1xx
					.get(kombination);
			if (hinweise.size() != 1)
				return;
			gruppieren.println(kombination + "\t" + hinweise.get(0));
		});

	}

}
