/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.Between;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * <a href=
 * "https://wiki.dnb.de/display/ERSCHLIESSUNG/Zugangsmonitoring">wiki</a>
 *
 *
 *
 * @author baumann
 *
 */
public class ZugangsMonitoring5 {

	static final String FOLDER = "D:/Analysen/baumann/statistik/";

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		final String outFileName = MethodHandles.lookup().lookupClass()
				.getSimpleName() + ".txt";
		System.err.println(outFileName);

		final CrossProductFrequency zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_D);
		reader.setStreamFilter(
				new StringContains(Constants.MARC_SUB_SEP + "z" + "StatZUG"));
		final PrintWriter out = MyFileUtils.outputFile(FOLDER + outFileName,
				false);

		final Between<Integer> erlaubteJahre = new Between<>(2018, 2023);
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
			if (!erlaubteJahre.test(jahrZugang))
				return;

			// 0500 G/M***
			final String type = RecordUtils.getDatatype(record);
			if (type == null || !(type.startsWith("G") || type.startsWith("M")))
				return;

			// 1100 Jahr (2018-2023)
			final String jahrStr = BibRecUtils.getYearOfPublicationString(record);
			if (jahrStr == null)
				return;
			int jahrPub;
			try {
				jahrPub = Integer.parseInt(jahrStr);
			} catch (final NumberFormatException e) {
				jahrPub = -1;
			}
			if (!erlaubteJahre.test(jahrPub))
				return;
			zugangsStat.addValues(jahrPub);
		});

		out.println(zugangsStat.toString());
		MyFileUtils.safeClose(out);

	}

}
