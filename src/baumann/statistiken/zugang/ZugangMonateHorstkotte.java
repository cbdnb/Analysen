/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import de.dnb.basics.Constants;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;

/**
 * @author baumann
 *
 */
public class ZugangMonateHorstkotte {

	private static CrossProductFrequency zugangsStat;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_Z);
		reader.setStreamFilter(
				new StringContains(Constants.MARC_SUB_SEP + "z" + "StatZUG"));
		final StatusAndCodeFilter ismusi = StatusAndCodeFilter.filterMusikalie()
				.setIgnoreStatus(true);

		reader.forEach(record ->
		{
			// if (ismusi.test(record))
			// return;

			final ArrayList<Line> zugLines = BibRecUtils
					.getZUGStatistik(record);
			FilterUtils.filter(zugLines, line -> SubfieldUtils
					.getActionscode(line).equalsIgnoreCase("NEU"));
			if (zugLines.isEmpty())
				return;

			final STANDORT_DNB standort = RecordUtils
					.getEingebenderStandort(record);
			if (standort == STANDORT_DNB.U)
				return;

			final Date zugDate = RecordUtils.getDateEntered(record);
			if (zugDate == null)
				return;
			final Calendar zugCal = TimeUtils.getCalendar(zugDate);
			final int jahr = zugCal.get(Calendar.YEAR);
			final int monatsnr = zugCal.get(Calendar.MONTH);

			// System.err.println(StringUtils.concatenate(", ", jahr, monatsnr,
			// monat, standort));

			zugangsStat.addValues(jahr, monatsnr, standort);

		});

		for (int jahr = 2017; jahr <= 2023; jahr++) {
			for (int Monat = 0; Monat < 12; Monat++) {
				printRow(jahr, Monat);
			}
		}

	}

	static List<String> monatsnamen = Arrays.asList("Januar", "Februar", "März",
			"April", "Mai", "Juni", "Juli", "August", "September", "Oktober",
			"November", "Dezember");

	/**
	 * @param hund
	 * @return
	 */
	private static void printRow(final int jahr, final int monatsnr) {
		String out = jahr + ", " + monatsnamen.get(monatsnr);
		out += "\t" + zugangsStat.get(jahr, monatsnr, STANDORT_DNB.F);
		out += "\t" + zugangsStat.get(jahr, monatsnr, STANDORT_DNB.L);
		System.out.println(out);
	}

}
