/**
 *
 */
package baumann;

import java.io.IOException;
import java.util.Date;

import de.dnb.basics.collections.Frequency;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class MatheStatistik {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Frequency<Integer> dets = new Frequency<>();
		final Frequency<String> dates = new Frequency<>();

		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Normdaten/DNBGND_s.dat.gz");

		//@formatter:off
		reader
			.stream()
			.filter(rec -> GNDUtils.containsGNDClassifications(rec, "28", "29"))
			.forEach(rec ->{
				final Integer det = GNDUtils.getMaxDet(rec);
				dets.add(det);

				final Date date = RecordUtils.getDateEntered(rec);
				if (date!= null){
					final String dateS = TimeUtils.toYYYYMM(date);
					dates.add(dateS);
				}
				else
					System.out.println(rec.getId());
			});
		//@formatter:on
		System.out.println(dets);
		System.out.println("---");
		System.out.println(dates);

	}

}
