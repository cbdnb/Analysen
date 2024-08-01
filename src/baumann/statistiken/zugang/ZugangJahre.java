/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.WV;
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;

/**
 * @author baumann
 *
 */
public class ZugangJahre {

	private static CrossProductFrequency zugangsStat;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("2105", BibTagDB.getDB()));
		final StatusAndCodeFilter ismusi = StatusAndCodeFilter.filterMusikalie()
				.setIgnoreStatus(true).setIgnoreCodes(true);

		final List<Character> erlaubteReihen = Arrays.asList('A', 'B', 'G',
				'H');
		final WV pHeft = WV.create("94,P01");

		reader.forEach(record ->
		{

			if (ismusi.test(record))
				return;

			final Collection<WV> wvs = BibRecUtils.getWVs(record);
			boolean reiheKorrekt = false;
			for (final WV wv : wvs) {
				if (pHeft.equals(wv)
						|| erlaubteReihen.contains(wv.getSeries())) {
					reiheKorrekt = true;
					break;
				}
			}
			if (!reiheKorrekt)
				return;

			final STANDORT_DNB standort = RecordUtils
					.getEingebenderStandort(record);
			if (standort == STANDORT_DNB.U || standort == STANDORT_DNB.M)
				return;

			final Date zugDate = RecordUtils.getDateEntered(record);
			if (zugDate == null)
				return;
			final Calendar zugCal = TimeUtils.getCalendar(zugDate);
			final int jahr = zugCal.get(Calendar.YEAR);

			zugangsStat.addValues(jahr, standort);

		});

		for (int jahr = 1992; jahr <= 2023; jahr++) {
			printRow(jahr);
		}

	}

	private static void printRow(final int jahr) {
		String out = jahr + "";
		out += "\t" + zugangsStat.get(jahr, STANDORT_DNB.F);
		out += "\t" + (zugangsStat.get(jahr, STANDORT_DNB.L)
				+ zugangsStat.get(jahr, STANDORT_DNB.M));
		System.out.println(out);
	}

}
