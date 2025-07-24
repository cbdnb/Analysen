package scheven;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;

/**
 * Alle Formangaben: F ent saf not bbg tc* = 217 Davon nur die, die in der 550
 * erneut eine Formangabe haben.
 *
 * Spalten:
 *
 * Idn/nid | 150 | 550 | 667 | 677 | 680 |
 *
 */
public class Formangabe {

	static String folder = "D:/Analysen/scheven";
	static String fileNameIn = "formangabe.txt";
	static String fileIn = new File(folder, fileNameIn).getPath();
	static Set<String> idns = new HashSet<>();
	static Map<String, String> idn2name = new HashMap<>();
	private static PrintWriter out;

	public static void main(final String[] args) throws IOException {

		out = MyFileUtils.outputFile(
				new File(folder, "formangabeMitOB.txt").getPath(), false);
		out.println(StringUtils.concatenateTab("Idn", "nid", "150", "550",
				"667", "677", "680"));

		RecordReader.getMatchingReader(fileIn).forEach(record ->
		{
			idn2name.put(record.getId(), GNDUtils.getNameOfRecord(record));
		});

		RecordReader.getMatchingReader(fileIn).forEach(record ->
		{
			final List<Line> obb = GNDUtils.getOBB(record);
			final Set<String> schnitt = CollectionUtils.intersection(
					idn2name.keySet(),
					FilterUtils.mapNullFiltered(obb, Line::getIdnRelated));
			if (!schnitt.isEmpty()) {
				ausgeben(record, schnitt);
			}
		});

	}

	private static void ausgeben(final Record record,
			final Set<String> schnitt) {
		final String idn = record.getId();
		final String nid = GNDUtils.getNID(record);
		final String name = idn2name.get(idn);
		final Set<String> idns2ob = new LinkedHashSet<>();
		schnitt.forEach(idob ->
		{
			final String out550 = idob + " " + idn2name.get(idob);
			idns2ob.add(out550);
		});
		final List<String> feld667 = GNDUtils.getNonpublicGeneralNotes(record);
		final List<String> feld677 = GNDUtils.getDefinitions(record);
		final List<String> feld680 = GNDUtils.getPublicGeneralNotes(record);

		out.println(StringUtils.concatenateTab(idn, nid, name,
				StringUtils.makeExcelCellFromCollection(idns2ob),
				StringUtils.makeExcelCellFromCollection(feld667),
				StringUtils.makeExcelCellFromCollection(feld677),
				StringUtils.makeExcelCellFromCollection(feld680)));

	}

}
