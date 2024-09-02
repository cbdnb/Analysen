/**
 *
 */
package baumann.statistiken;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.StatisticUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;

/**
 * @author baumann
 *
 */
public class Durchlaufzeiten {

	private static final String QUARTIL_3 = "3. Quartil: ";

	private static final String MEDIAN = "Median: ";

	private static final String QUARTIL_1 = "1. Quartil: ";

	private static final String TITEL = " Titel";

	private static final String DATENBASIS = "Datenbasis: ";

	private static final int JJJJ = TimeUtils.getActualYear() - 1;

	private static final String FOLDER = "D:/Analysen/baumann/statistik/";

	static Frequency<Long> durchlaufIE = new Frequency<>();
	static Frequency<Long> durchlaufFE = new Frequency<>();
	static Frequency<Long> durchlaufErschliessung = new Frequency<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final RecordReader reader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz"
		/* Constants.TITEL_PLUS_EXEMPLAR_Z */);
		reader.setStreamFilter(new StringContains("StatIE")
				.and(new StringContains(JJJJ + "-")));
		final PrintWriter alle = MyFileUtils.oeffneAusgabeDatei(
				FOLDER + "alle_idns_nicht_ZDB " + JJJJ + ".txt", false);
		final PrintWriter histogram = MyFileUtils.oeffneAusgabeDatei(
				FOLDER + "histogramm_nicht_ZDB " + JJJJ + ".txt", false);
		final PrintWriter auswertung = MyFileUtils.oeffneAusgabeDatei(
				FOLDER + "auswertung_nicht_ZDB " + JJJJ + ".txt", false);

		final AtomicInteger countErschliessung = new AtomicInteger();
		final AtomicInteger countIE = new AtomicInteger();
		final AtomicInteger countFE = new AtomicInteger();

		reader.forEach(record ->
		{

			final Date dateIE = BibRecUtils.getIEstatDate(record);
			if (dateIE == null)
				return;
			if (BibRecUtils.isMagazine(record))
				return;
			final Calendar calendar = TimeUtils.getCalendar(dateIE);
			if (calendar.get(Calendar.YEAR) != JJJJ)
				return;

			final Triplett<Long, Long, Long> durchlaufzeiten = BibRecUtils
					.getDurchlaufzeiten(record);
			final String id = record.getId();

			final Long dFE = durchlaufzeiten.first;
			final Long dIE = durchlaufzeiten.second;
			final Long dErschliessung = durchlaufzeiten.third;
			final String out = StringUtils.concatenateTab(id,
					dFE == null ? "" : dFE, dIE == null ? "" : dIE,
					dErschliessung == null ? "" : dErschliessung);
			alle.println(out);

			if (dFE != null) {
				durchlaufFE.add(dFE);
				countFE.incrementAndGet();
			}
			if (dIE != null) {
				durchlaufIE.add(dIE);
				countIE.incrementAndGet();
			}
			if (dErschliessung != null) {
				durchlaufErschliessung.add(dErschliessung);
				countErschliessung.incrementAndGet();
			}
		});
		histogram.println("--FE--");
		histogram.println(StatisticUtils.map2string(durchlaufFE));
		histogram.println("--IE--");
		histogram.println(StatisticUtils.map2string(durchlaufIE));
		histogram.println("--Erschließung--");
		histogram.println(StatisticUtils.map2string(durchlaufErschliessung));

		StreamUtils.safeClose(alle);
		StreamUtils.safeClose(histogram);

		final BiMap<Long, Long> cdfFE = StatisticUtils.getCDF(durchlaufFE);
		final BiMap<Long, Long> cdfIE = StatisticUtils.getCDF(durchlaufIE);
		final BiMap<Long, Long> cdfErschliessung = StatisticUtils
				.getCDF(durchlaufErschliessung);

		auswertung.println("FE:");
		auswertung.println(DATENBASIS + countFE + TITEL);
		auswertung.println(QUARTIL_1 + StatisticUtils.getQuantile(cdfFE, .25));
		auswertung.println(MEDIAN + StatisticUtils.getQuantile(cdfFE, .5));
		auswertung.println(QUARTIL_3 + StatisticUtils.getQuantile(cdfFE, .75));
		auswertung.println();

		auswertung.println("IE:");
		auswertung.println(DATENBASIS + countIE + TITEL);
		auswertung.println(QUARTIL_1 + StatisticUtils.getQuantile(cdfIE, .25));
		auswertung.println(MEDIAN + StatisticUtils.getQuantile(cdfIE, .5));
		auswertung.println(QUARTIL_3 + StatisticUtils.getQuantile(cdfIE, .75));
		auswertung.println();
		auswertung.println("Gesamt:");
		auswertung.println(DATENBASIS + countErschliessung + TITEL);
		auswertung.println(
				QUARTIL_1 + StatisticUtils.getQuantile(cdfErschliessung, .25));
		auswertung.println(
				MEDIAN + StatisticUtils.getQuantile(cdfErschliessung, .5));
		auswertung.println(
				QUARTIL_3 + StatisticUtils.getQuantile(cdfErschliessung, .75));

	}

}
