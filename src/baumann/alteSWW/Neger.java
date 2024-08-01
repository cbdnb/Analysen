/**
 *
 */
package baumann.alteSWW;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 *
 *
 *
 * @author baumann
 *
 */
public class Neger {

	/**
	 *
	 */
	public static final String FOLDER = "D:/Analysen/baumann/";

	private static PrintStream stream_hsw_neger;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		stream_hsw_neger = new PrintStream(FOLDER + "HSW_Neger1.txt");

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("5530", BibTagDB.getDB()));

		reader.forEach(record ->
		{

			final ArrayList<Line> lines5530 = RecordUtils.getLines(record,
					"5530");

			for (final Line line : lines5530) {
				final String f = SubfieldUtils.getContentOfFirstSubfield(line,
						'a');
				if (f != null && StringUtils.contains(f, "neger", true)) {
					ausgeben(line, record);
				}
				final String l = SubfieldUtils.getContentOfFirstSubfield(line,
						'g');
				if (l != null && StringUtils.contains(l, "neger", true)) {
					ausgeben(line, record);
					System.err.println(line);
				}

			}

		});

		StreamUtils.safeClose(stream_hsw_neger);
	}

	/**
	 * @param stream_hsw_f
	 * @param stream_hsw_l
	 */
	public static void ausgeben(final Line line, final Record record) {
		stream_hsw_neger.println(StringUtils.concatenate("\t", record.getId(),
				BibRecUtils.createShortTitle(record),
				RecordUtils.toPicaWithoutTag(line)));
	}

}
