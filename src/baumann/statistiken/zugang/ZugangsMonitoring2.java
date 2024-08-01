/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.tuples.Quadruplett;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.filtering.Between;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.DDC_SG.HUNDERTER;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import de.dnb.ie.utils.DB.Status_Generator;

/**
 * Deutschland mit Musik.
 *
 * <a href=
 * "https://wiki.dnb.de/display/ERSCHLIESSUNG/Zugangsmonitoring">wiki</a>
 *
 *
 *
 * @author baumann
 *
 */
public class ZugangsMonitoring2 {

	private static String outFileName;

	static final String FOLDER = "D:/Analysen/baumann/statistik/";

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		outFileName = MethodHandles.lookup().lookupClass().getSimpleName()
				+ ".txt";
		System.err.println(outFileName);

		final CrossProductFrequency zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_D);
		reader.setStreamFilter(
				new StringContains(Constants.MARC_SUB_SEP + "z" + "StatZUG"));
		final Status_Generator generator = new Status_Generator();
		final Map<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> table = generator
				.getTable();

		final PrintWriter out = FileUtils.outputFile(FOLDER + outFileName,
				false);

		System.err.println("flöhen");

		/**
		 * ra/rb/rc/rh/rm/rt
		 */
		final List<String> erlaubteReihen = Arrays.asList("ra", "rb", "rc",
				"rh", "rm", "rt");

		/**
		 * rg/ru
		 */
		final List<String> verboteneReihen = Arrays.asList("rg", "ru");

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

			// 1700 /1XA-DE unabhängig von der Position
			// Deutschland kann an erster oder zweiter Stelle stehen:
			final List<String> erscheinunglaender = RecordUtils
					.getContentsOfAllSubfields(record, "1700", 'a');
			final List<String> ersteZwei = ListUtils.subList(erscheinunglaender,
					0, 2);
			boolean istDeutschland = false;
			for (final String land : ersteZwei) {
				if (land.startsWith("XA-DE"))
					istDeutschland = true;
			}
			if (!istDeutschland)
				return;

			// 0600 ohne rg/ru
			final List<String> reihen = BibRecUtils.getCodes(record);
			if (!CollectionUtils.intersection(verboteneReihen, reihen)
					.isEmpty())
				return;
			// 0600 ra/rb/rc/rh/rm/rt
			if (CollectionUtils.intersection(erlaubteReihen, reihen).isEmpty())
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
			final Between<Integer> erlaubteJahre = new Between<>(2018, 2023);
			if (!erlaubteJahre.test(jahrPub))
				return;

			// 5050 Sachgruppe
			final HUNDERTER hunderter = SGUtils.getHunderterDHS(record, table);

			zugangsStat.addValues(jahrPub, hunderter);

		});

		out.println(zugangsStat.toString());
		FileUtils.safeClose(out);

	}

}
