/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;

/**
 * @author baumann
 *
 */
public class ZugangMonate {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final CrossProductFrequency zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_Z);
		reader.setStreamFilter(
				new StringContains(Constants.MARC_SUB_SEP + "z" + "StatZUG"));

		reader.forEach(record ->
		{

			final Date zugDate = BibRecUtils.getZUGstatDate(record);
			if (zugDate == null)
				return;
			final STANDORT_DNB standort = BibRecUtils.getStandortZugang(record);
			if (standort == STANDORT_DNB.U)
				return;
			final Calendar zugCal = TimeUtils.getCalendar(zugDate);
			final int jahr = zugCal.get(Calendar.YEAR);
			final int monatsnr = zugCal.get(Calendar.MONTH);
			final String monat = new SimpleDateFormat("MMMM", Locale.GERMAN)
					.format(zugCal.getTime());

			// System.err.println(StringUtils.concatenate(", ", jahr, monatsnr,
			// monat, standort));

			zugangsStat.addValues(jahr, monatsnr, monat, standort);

		});

		System.out.println(zugangsStat.toString());

	}

}
