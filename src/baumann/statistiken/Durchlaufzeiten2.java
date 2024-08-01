/**
 *
 */
package baumann.statistiken;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.StatisticUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Durchlaufzeiten2 extends DownloadWorker {

	/**
	 *
	 */
	private static final String PREFIX = "vollabzug";

	private static final String INPUT_FOLDER = "Z:/cbs/stages/prod/vollabzug/"
			+ "230213";

	private static final String QUARTIL_3 = "3. Quartil: ";

	static final String MEDIAN = "Median: ";

	private static final String QUARTIL_1 = "1. Quartil: ";

	private static final String TITEL = " Titel";

	private static final String DATENBASIS = "Datenbasis: ";

	private static final int JJJJ = 2022;

	private static final String FOLDER_OUT = "D:/Analysen/baumann/statistik/";

	static Frequency<Long> durchlaufIE = new Frequency<>();
	static Frequency<Long> durchlaufFE = new Frequency<>();
	static Frequency<Long> durchlaufErschliessung = new Frequency<>();

	private static AtomicInteger countErschliessung = new AtomicInteger();;

	private static AtomicInteger countIE = new AtomicInteger();

	private static AtomicInteger countFE = new AtomicInteger();

	private static PrintWriter alle;

	private static PrintWriter histogram;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Durchlaufzeiten2 durchlauf = new Durchlaufzeiten2();
		durchlauf.gzipSettings();
		durchlauf.setInputFolder(INPUT_FOLDER);
		durchlauf.setStreamFilter(new StringContains("StatIE")
				.and(new StringContains(JJJJ + "-")));
		durchlauf.setFilePrefix(PREFIX);

		alle = FileUtils.oeffneAusgabeDatei(FOLDER_OUT + "alle_test.txt",
				false);

		durchlauf.processAllFiles();

		histogram = FileUtils.oeffneAusgabeDatei(
				FOLDER_OUT + "häufigkeiten_test.txt", false);

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

		System.out.println("FE:");
		System.out.println(DATENBASIS + countFE + TITEL);
		System.out.println(QUARTIL_1 + StatisticUtils.getQuantile(cdfFE, .25));
		System.out.println(MEDIAN + StatisticUtils.getQuantile(cdfFE, .5));
		System.out.println(QUARTIL_3 + StatisticUtils.getQuantile(cdfFE, .75));
		System.out.println();

		System.out.println("IE:");
		System.out.println(DATENBASIS + countIE + TITEL);
		System.out.println(QUARTIL_1 + StatisticUtils.getQuantile(cdfIE, .25));
		System.out.println(MEDIAN + StatisticUtils.getQuantile(cdfIE, .5));
		System.out.println(QUARTIL_3 + StatisticUtils.getQuantile(cdfIE, .75));
		System.out.println();
		System.out.println("Gesamt:");
		System.out.println(DATENBASIS + countErschliessung + TITEL);
		System.out.println(
				QUARTIL_1 + StatisticUtils.getQuantile(cdfErschliessung, .25));
		System.out.println(
				MEDIAN + StatisticUtils.getQuantile(cdfErschliessung, .5));
		System.out.println(
				QUARTIL_3 + StatisticUtils.getQuantile(cdfErschliessung, .75));

	}

	@Override
	protected void processRecord(final Record record) {

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
		final ArrayList<Line> lines4821 = RecordUtils.getLines(record, "4821");
		final String out = StringUtils.concatenateTab(id,
				dFE == null ? "" : dFE, dIE == null ? "" : dIE,
				dErschliessung == null ? "" : dErschliessung, lines4821);
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

	}

}
