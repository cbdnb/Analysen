/**
 *
 */
package koehn;

import java.io.IOException;
import java.util.ArrayList;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * Statistik der HDH-Felder.
 *
 * @author baumann
 *
 */
public class MDH {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Frequency<String> typNichtEHDAnz = new Frequency<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		reader.forEach(record ->
		{
			final ArrayList<Line> lines = RecordUtils.getLines(record, "5050");
			if (lines.isEmpty())
				return;
			final boolean notEHD = lines.stream()
					.anyMatch(line -> !SubfieldUtils.containsIndicators(line,
							'E', 'H', 'D'));
			if (notEHD) {
				final String typ = RecordUtils.getDatatype(record);
				typNichtEHDAnz.add(typ);
				System.err.println(record.getId() + " / " + typ);

			}
		});
		System.out.println(typNichtEHDAnz);

	}

}
