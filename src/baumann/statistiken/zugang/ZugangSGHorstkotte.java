/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Quadruplett;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.DDC_SG.HUNDERTER;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import de.dnb.ie.utils.DB.Status_Generator;

/**
 * @author baumann
 *
 *         Basierend auf dem Wunsch Horstkotte, die "ser"-Felder zu benutzen.
 *
 */
public class ZugangSGHorstkotte {

	private static CrossProductFrequency zugangsStat;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_Z);
		reader.setStreamFilter(
				new StringContains(Constants.MARC_SUB_SEP + "z" + "StatZUG"));
		final Status_Generator generator = new Status_Generator();
		final Map<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> table = generator
				.getTable();
		final StatusAndCodeFilter ismusi = StatusAndCodeFilter.filterMusikalie()
				.setIgnoreStatus(true);
		final PrintWriter error = FileUtils.outputFile(
				"D:/Analysen/baumann/statistik/keine_SG_Horstkotte.txt", false);

		reader.forEach(record ->
		{
			if (ismusi.test(record))
				return;

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

			final HUNDERTER hunderter = SGUtils.getHunderterDHS(record, table);

			if (hunderter == null)
				error.println(StringUtils.concatenateTab(record.getId(), jahr,
						standort, BibRecUtils.getVollstaendigenTitel(record)));
			// System.err.println(StringUtils.concatenate(", ", jahr, monatsnr,
			// monat, standort));

			zugangsStat.addValues(jahr, standort, hunderter);

		});
		System.out.println(
				"	2017		2018		2019		2020		2021		2022		2023	");
		System.out.println(
				"Hunderter	F	L	F	L	F	L	F	L	F	L	F	L	F	L");
		EnumSet.allOf(HUNDERTER.class).forEach(hund -> printRow(hund));
		printRow(null);

		FileUtils.safeClose(error);

	}

	/**
	 * @param hund
	 * @return
	 */
	private static void printRow(final HUNDERTER hund) {
		String out = hund == null ? "null" : hund.toString();
		for (int jahr = 2018; jahr <= 2023; jahr++) {
			out += "\t" + zugangsStat.get(jahr, STANDORT_DNB.F, hund);
			out += "\t" + zugangsStat.get(jahr, STANDORT_DNB.L, hund);
		}
		System.out.println(out);

	}

}
