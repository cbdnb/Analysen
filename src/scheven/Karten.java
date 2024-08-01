/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import de.dnb.basics.collections.Frequency;
import de.dnb.basics.filtering.Between;
import de.dnb.gnd.parser.ItemParser;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;

/**
 * @author baumann
 *
 */
public class Karten {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Frequency<Between<Integer>> interv2count = new Frequency<>();
		final Set<Between<Integer>> intervals = new TreeSet<>(
				Arrays.asList(new Between<Integer>(Integer.MIN_VALUE, 1850),
						new Between<Integer>(1851, 2000),
						new Between<Integer>(2001, Integer.MAX_VALUE)));
		final RecordReader reader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/karten.dat.gz");
		reader.forEach(karte ->
		{
			final Integer jahr = BibRecUtils.getYearOfPublication(karte);
			if (jahr == null)
				return;
			final int anzahl = ItemParser.countDNB(karte);
			if (anzahl > 0) {
				for (final Between<Integer> intv : intervals) {
					if (intv.test(jahr))
						interv2count.increment(intv, anzahl);

				}
			}
		});
		intervals.forEach(
				in -> System.out.println(in + ": " + interv2count.get(in)));

	}

}
