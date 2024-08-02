/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Quadruplett;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import utils.DB.Status_Generator;
import de.dnb.gnd.utils.DDC_SG.HUNDERTER;

/**
 * Alle Monografien (einschließlich Musik und Ausland) mit IE 100er Sachgruppe.
 * <a href=
 * "https://wiki.dnb.de/display/ERSCHLIESSUNG/Zugangsmonitoring">wiki</a>
 *
 *
 *
 * @author baumann
 *
 */
public class ZugangsMonitoring1 {

	/**
	 *
	 */
	static final String FOLDER = "D:/Analysen/baumann/statistik/";

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		final CrossProductFrequency zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_D);
		reader.setStreamFilter(
				new StringContains(Constants.MARC_SUB_SEP + "z" + "StatZUG"));
		final Status_Generator generator = new Status_Generator();
		final Map<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> table = generator
				.getTable();

		final PrintWriter error = FileUtils.outputFile(FOLDER + "keine_SG.txt",
				false);
		final PrintWriter out = FileUtils
				.outputFile(FOLDER + "ZugangsMonitoring1.txt", false);

		System.err.println("flöhen");

		reader.forEach(record ->
		{

			// 4821 $z StatZug
			final ArrayList<Line> zugLines = BibRecUtils
					.getZUGStatistik(record);
			if (zugLines.isEmpty())
				return;

			// 4821 $D 2018-2023
			final Date zugDate = BibRecUtils.getDateFromStatLines(zugLines);
			if (zugDate == null)
				return;
			final Calendar zugCal = TimeUtils.getCalendar(zugDate);
			final int jahrZugang = zugCal.get(Calendar.YEAR);
			if (jahrZugang < 2018 || jahrZugang > 2023)
				return;

			// 1100 Jahr (2018-2023)
			final String jahrStr = BibRecUtils
					.getYearOfPublicationString(record);
			if (jahrStr == null)
				return;
			int jahrPub;
			try {
				jahrPub = Integer.parseInt(jahrStr);
			} catch (final NumberFormatException e) {
				jahrPub = -1;
			}
			if (jahrPub < 2018 || jahrPub > 2023)
				return;

			// 5050 Sachgruppe
			final HUNDERTER hunderter = SGUtils.getHunderterDHS(record, table);
			if (hunderter == null)
				error.println(
						StringUtils.concatenateTab(record.getId(), jahrZugang,
								BibRecUtils.getVollstaendigenTitel(record)));

			zugangsStat.addValues(jahrPub, hunderter);

		});

		out.println(zugangsStat.toString());
		FileUtils.safeClose(error);
		FileUtils.safeClose(out);

	}

}
