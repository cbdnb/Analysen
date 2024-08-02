/**
 *
 */
package scheven.hinweis.archiv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 */
public class Hinweise_2023_2 {

	static final String TYP = "Tb1e";
	static final String folder = "D:/Analysen/baumann/Hinweissaetze/";
	private static HashMap<Integer, String> ppn2name;
	static final String QUELLE = Constants.Tb;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		ppn2name = GND_DB_UTIL.getppn2name();
		final RecordReader reader = RecordReader.getMatchingReader(QUELLE);
		reader.setStreamFilter(
				new ContainsTag("005", '0', TYP, GNDTagDB.getDB()));

		final PrintWriter pw = FileUtils.outputFile(folder + TYP + ".txt",
				false);

		reader.forEach(record ->
		{
			if (!GNDUtils.isUseCombination(record))
				return;

			output(pw, record);

		});

		FileUtils.safeClose(pw);

	}

	/**
	 * @param syst12_12
	 * @param record
	 */
	private static void output(final PrintWriter pw, final Record record) {
		final String idn = record.getId();
		final String nid = GNDUtils.getNID(record);
		final String nameHinweis = GNDUtils.getNameOfRecord(record);
		final String syst = GNDUtils.getFirstGNDClassification(record);

		final List<Line> hinweisLines = GNDUtils.getHinweisLines(record);

		final List<String> list = Util.get260IdnsNames(hinweisLines, ppn2name);
		final TreeSet<String> set = new TreeSet<>(list);
		final String redaktion = GNDUtils.getIsilVerbund(record);

		pw.println(StringUtils.concatenateTab(idn, nid, nameHinweis, syst,
				set.toString(), redaktion));
	}

}
