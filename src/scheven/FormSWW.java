package scheven;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

public class FormSWW {

	public static void main(final String[] args) throws IOException {
		final PrintWriter out = MyFileUtils
				.outputFile("D:/Analysen/scheven/formbegriffe.tab", false);
		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Analysen/scheven/formbegriffe.txt");
		reader.forEach(formSW ->
		{
			final String satzart = GNDUtils.getBBG(formSW);
			final String nid = GNDUtils.getNID(formSW);
			String feld150 = "";
			try {
				feld150 = RDAFormatter.getRDAHeading(formSW);

			} catch (final IllFormattedLineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final List<String> felder667 = GNDUtils
					.getNonpublicGeneralNotes(formSW);
			final String feld667 = ListUtils.getFirst(felder667);

			final List<String> felder670 = GNDUtils.getSourcesDataFound(formSW);
			final String feld670 = ListUtils.getFirst(felder670);

			final List<String> felder677 = GNDUtils.getDefinitions(formSW);
			final String feld677 = ListUtils.getFirst(felder677);

			final List<String> felder680 = GNDUtils
					.getPublicGeneralNotes(formSW);
			final String feld680 = ListUtils.getFirst(felder680);

			final List<Line> sachOBBL = GNDUtils.getGenerischeSachOBB(formSW);
			String sachOBB = sachOBBL.stream()
					.map(RecordUtils::toPicaWithoutTag)
					.collect(Collectors.joining(" / "));
			if (sachOBB.isEmpty())
				sachOBB = "-";

			out.println(StringUtils.concatenateTab(satzart, nid, feld150,
					feld667, feld670, feld677, feld680, sachOBB));
		});

		MyFileUtils.safeClose(out);

	}

}
