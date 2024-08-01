/**
 *
 */
package scheven;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class DollarAOhneD {

	private static PrintStream printStream;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tp);
		// reader.setStreamFilter(
		// new ContainsTag("005", '0', "Tp", GNDTagDB.getDB()));
		printStream = new PrintStream("D:/Analysen/scheven/DollarAOhneD.txt");

		reader.forEach(record ->
		{
			analyze(record);
		});

	}

	/**
	 * @param record
	 */
	public static void analyze(final Record record) {
		final ArrayList<Line> lines14XX = GNDUtils.getLines1XX(record);
		lines14XX.addAll(GNDUtils.getLines4XX(record));

		for (final Line line : lines14XX) {
			final boolean found = dollarAOhneD(line);

			if (found) {
				ausgeben(record, line);
			}
		}
	}

	/**
	 * @param line
	 * @return
	 */
	private static boolean dollarAOhneD(final Line line) {
		return SubfieldUtils.containsIndicator(line, 'a')
				&& !SubfieldUtils.containsIndicator(line, 'd');
	}

	private static void ausgeben(final Record record, final Line line) {
		final String idn = record.getId();
		final String bbg = RecordUtils.getDatatype(record);
		String name;
		try {
			name = GNDUtils.getNameOfRecord(record);
		} catch (final Exception e) {
			return;
		}
		final String syst = GNDUtils.getGNDClassifications(record).toString();
		final String line2Str = RecordUtils.toPica(line, Format.PICA3, true,
				'$');
		final String out = StringUtils.concatenate("\t", idn, bbg, syst, name,
				line2Str);
		System.out.println(out);
		printStream.println(out);

	}

}
