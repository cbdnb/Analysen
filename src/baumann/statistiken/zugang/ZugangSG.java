/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import de.dnb.ie.utils.DB.Status_Generator;

/**
 * @author baumann
 *
 */
public class ZugangSG {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		final CrossProductFrequency zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_Z);
		reader.setStreamFilter(
				new StringContains(Constants.MARC_SUB_SEP + "z" + "StatZUG"));
		final Status_Generator generator = new Status_Generator();
		final HashMap<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> table = generator
				.getTable();
		final StatusAndCodeFilter ismusi = StatusAndCodeFilter.filterMusikalie()
				.setIgnoreStatus(true);
		final PrintWriter error = FileUtils.outputFile(
				"D:/Analysen/baumann/statistik/keine_SG.txt", false);

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

			final STANDORT_DNB standort = BibRecUtils.getStandort(zugLines);
			if (standort == STANDORT_DNB.U)
				return;

			final Date zugDate = BibRecUtils.getDateFromStatLines(zugLines);
			if (zugDate == null)
				return;
			final Calendar zugCal = TimeUtils.getCalendar(zugDate);
			final int jahr = zugCal.get(Calendar.YEAR);

			final HUNDERTER hunderter = SGUtils.getHunderterDHS(record, table);
			if (hunderter == null)
				error.println(StringUtils.concatenateTab(record.getId(), jahr,
						standort, BibRecUtils.getVollstaendigenTitel(record)));

			zugangsStat.addValues(jahr, standort, hunderter);

		});

		System.out.println(zugangsStat.toString());
		FileUtils.safeClose(error);

	}

}
