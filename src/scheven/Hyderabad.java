package scheven;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class Hyderabad {

	private static final String FOLDER = "D:/Analysen/scheven/";

	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(FOLDER + "hayderabad.txt");
		final PrintWriter out = MyFileUtils.outputFile(FOLDER + "hay_out.txt",
				false);
		out.println(StringUtils.concatenateTab("005", "IDN", "NID", "LC", "1XX",
				"Falsche Felder", "551", "Verbund"));
		reader.forEach(rec ->
		{
			final ArrayList<Line> lines = RecordUtils.getLines(rec, "[14]..");
			FilterUtils.filter(lines, Hyderabad::istFalsch);
			if (lines.isEmpty())
				return;
			// Ab jetzt sind falsche drin. Also ausgeben:
			final String feld005 = RecordUtils.getDatatype(rec);
			final String idn = rec.getId();
			final String nid = GNDUtils.getNID(rec);
			final String feld043 = StringUtils.concatenate(",",
					GNDUtils.getCountryCodes(rec));
			final String name = GNDUtils.getSimpleName(rec);
			final String falsche = StringUtils.makeExcelCellFromCollection(
					FilterUtils.map(lines, line -> RecordUtils.toPica(line,
							Format.PICA3, true, '$')));
			final String feld551 = StringUtils.makeExcelCellFromCollection(
					FilterUtils.map(RecordUtils.getLines(rec, "551"),
							line -> RecordUtils.toPica(line, Format.PICA3, true,
									'$')));
			final String red = GNDUtils.getIsilVerbund(rec);
			out.println(StringUtils.concatenateTab(feld005, idn, nid, feld043,
					name, falsche, feld551, red));
		});
		MyFileUtils.safeClose(out);

	}

	public static boolean istFalsch(final Line line14XX) {
		final List<String> sublist = FilterUtils.mapNullFiltered(
				SubfieldUtils.getSubfields(line14XX, 'c', 'g'),
				Subfield::getContent);
		for (final String sub : sublist) {
			if (!sub.contains("yderaba"))
				continue;
			// also wird so was wie [Hh]yderaba[td] drin sein:
			if (!sub.equals("Hyderabad, Indien"))
				return true;
		}
		return false;
	}

}
