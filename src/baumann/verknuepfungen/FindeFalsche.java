package baumann.verknuepfungen;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SG;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class FindeFalsche {

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		final String outFileName = Utils.FOLDER + "falsche.txt";
		System.out.println(outFileName);
		final PrintWriter out = MyFileUtils.outputFile(outFileName, false);
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		reader.setStreamFilter(new ContainsTag("5100", BibTagDB.getDB()));
		for (final Record record : reader) {
			if (Utils.isDBSM(record))
				continue;
			final List<Line> rswkLines = SubjectUtils.getAllRSWKLines(record);
			for (final Line rswkLine : rswkLines) {
				final String rswkID = rswkLine.getIdnRelated();
				if (rswkID == null)
					continue;
				if (Utils.isGueltigesSW(rswkID))
					continue;
				final String typ = SubfieldUtils
						.getContentOfFirstSubfield(rswkLine, '7');
				final String entit = SubfieldUtils
						.getContentOfFirstSubfield(rswkLine, 'V');
				String name = SubfieldUtils.getContentOfFirstSubfield(rswkLine,
						'a');
				// wenigstens etwas:
				if (name == null)
					name = SubfieldUtils.getContentOfFirstSubfield(rswkLine,
							't');

				final SG dhs = SGUtils.getDHS(record);
				final String dhsS = dhs != null ? dhs.getDDCString() : "";
				String title = BibRecUtils.getVollstaendigenTitel(record);
				// wenigstens etwas
				if (title == null)
					title = BibRecUtils.getTitelDesTeils(record);
				final String treffer = StringUtils.concatenateTab(rswkID, typ,
						entit, name, record.getId(),
						RecordUtils.getDatatype(record), title, dhsS);
				System.out.println(treffer);
				out.println(treffer);
			}

		}
		MyFileUtils.safeClose(out);

	}

}
