/**
 *
 */
package scheven.hinweis.archiv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 */
public class Hinweise_2023_1 {

	/**
	 * 
	 */
	private static final String TYP = "Tg1e";
	static String folder = "D:/Analysen/baumann/Hinweissaetze/";
	private static HashMap<Integer, String> ppn2name;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		ppn2name = GND_DB_UTIL.getppn2name();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tg);
		reader.setStreamFilter(
				new ContainsTag("005", '0', TYP, GNDTagDB.getDB()));

		final PrintWriter syst12_12 = FileUtils.outputFile(folder + "11_12.txt",
				false);
		final PrintWriter nicht_syst12_12 = FileUtils
				.outputFile(folder + "nicht_11_12.txt", false);

		reader.forEach(record ->
		{
			if (!GNDUtils.isUseCombination(record))
				return;

			if (GNDUtils.containsGNDClassificationsTrunk(record, "11", "12")) {
				output(syst12_12, record);
			} else {
				output(nicht_syst12_12, record);
			}

		});

		FileUtils.safeClose(nicht_syst12_12);
		FileUtils.safeClose(syst12_12);

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

		final List<String> list = hinweisLines.stream().map(line ->
		{
			final String idn260 = line.getIdnRelated();

			String name = SubfieldUtils.getContentOfFirstSubfield(line, 'a');
			if (idn260 != null) {
				name = ppn2name.get(IDNUtils.idn2int(idn260));
				return idn260 + " (" + name + ")";
			} else {
				return name;
			}
		}).collect(Collectors.toList());
		final TreeSet<String> set = new TreeSet<>(list);
		final String redaktion = GNDUtils.getIsilVerbund(record);

		pw.println(StringUtils.concatenateTab(idn, nid, nameHinweis, syst,
				set.toString(), redaktion));
	}

}
