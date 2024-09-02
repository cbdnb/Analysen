/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.StatisticUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;

/**
 * @author baumann
 *
 */
public class Stichwoerter {

	private static final int JJJJ = 2023;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Set<String> stpw = new HashSet<>(StringUtils.readLinesFromFile(
				"D:/Analysen/baumann/skurriles/stop_words_de_complete.txt"));
		stpw.addAll(StringUtils.readLinesFromFile(
				"D:/Analysen/baumann/skurriles/stop_words_en_complete.txt"));
		final Frequency<String> stichwoerter = new Frequency<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_D);
		reader.setStreamFilter(StringContains.containsSubfield('z', "StatZUG")
				.and(StringContains.containsSubfield('D',
						Integer.toString(JJJJ))));

		reader.forEach(record ->
		{
			final ArrayList<Line> zugStatistik = BibRecUtils
					.getZUGStatistik(record);
			if (zugStatistik.isEmpty())
				return;

			final Date date = BibRecUtils.getDateFromStatLines(zugStatistik);
			final Calendar calendar = TimeUtils.getCalendar(date);
			final int year = calendar.get(Calendar.YEAR);
			if (year != JJJJ)
				return;

			final String titel = BibRecUtils.getMainTitle(record);
			final List<String> stichWW = StringUtils.stichwortListe(titel,
					stpw);
			// if (stichWW.contains(""))
			// System.out.println(record.getId());
			// System.err.println(stichWW);
			stichwoerter.addCollection(stichWW);
		});

		System.err.println(stichwoerter.size());

		final Map<Integer, Pair<String, Long>> rankmap = StatisticUtils
				.rankMap(stichwoerter);

		final PrintWriter out = MyFileUtils.outputFile(
				"D:/Analysen/baumann/skurriles/stichwörter.txt", false);
		for (int i = 0; i < rankmap.size(); i++) {
			final Pair<String, Long> pair = rankmap.get(i);
			out.println(
					StringUtils.concatenateTab(i + 1, pair.first, pair.second));
		}
		MyFileUtils.safeClose(out);

	}

}
