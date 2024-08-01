/**
 *
 */
package baumann.statistiken;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.StatisticUtils;
import de.dnb.basics.filtering.Between;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;

/**
 * @author baumann
 *
 */
public class DurchlaufzeitenVergleich {

	/**
	 *
	 */
	private static final String DATENBASIS = "Datenbasis: ";
	private static final String QUARTIL_3 = "3. Quartil: ";
	private static final String MEDIAN = "Median: ";
	private static final String QUARTIL_1 = "1. Quartil: ";

	private static final int JJJJ_ACTUAL = TimeUtils.getActualYear();
	private static final int JJJJ_BEGIN = 2017;
	private static final Between<Integer> VON_BIS = new Between<>(JJJJ_BEGIN,
			JJJJ_ACTUAL);

	static Map<Integer, Frequency<Long>> jahr2durchlaufIE = new TreeMap<>();
	static Map<Integer, Frequency<Long>> jahr2durchlaufFOE = new TreeMap<>();
	static Map<Integer, Frequency<Long>> jahr2durchlaufErschliessung = new TreeMap<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		for (int i = JJJJ_BEGIN; i <= JJJJ_ACTUAL; i++) {
			jahr2durchlaufErschliessung.put(i, new Frequency<>());
			jahr2durchlaufFOE.put(i, new Frequency<>());
			jahr2durchlaufIE.put(i, new Frequency<>());
		}

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_PLUS_EXEMPLAR_Z);
		reader.setStreamFilter(new StringContains("Stat"));

		reader.forEach(record ->
		{

			if (BibRecUtils.isMagazine(record))
				return;

			final Date dateFOE = BibRecUtils.getFOEstatDate(record);
			final Date dateIE = BibRecUtils.getIEstatDate(record);

			final Calendar calendarFOE = TimeUtils.getCalendar(dateFOE);
			final Calendar calendarIE = TimeUtils.getCalendar(dateIE);

			final Integer jahrFOE = calendarFOE == null ? 0
					: calendarFOE.get(Calendar.YEAR);
			final Integer jahrIE = calendarIE == null ? 0
					: calendarIE.get(Calendar.YEAR);

			final Triplett<Long, Long, Long> durchlaufzeiten = BibRecUtils
					.getDurchlaufzeiten(record);
			// System.err.println(durchlaufzeiten);

			final Long duchlaufFOE = durchlaufzeiten.first;
			final Long duchlaufIE = durchlaufzeiten.second;
			final Long duchlaufErschliessung = durchlaufzeiten.third;

			if (duchlaufFOE != null && VON_BIS.test(jahrFOE)) {
				jahr2durchlaufFOE.get(jahrFOE).add(duchlaufFOE);
			}

			if (duchlaufIE != null && VON_BIS.test(jahrIE)) {
				jahr2durchlaufIE.get(jahrIE).add(duchlaufIE);
			}

			if (duchlaufErschliessung != null && VON_BIS.test(jahrIE)) {
				jahr2durchlaufErschliessung.get(jahrIE)
						.add(duchlaufErschliessung);
			}

		});

		for (int i = JJJJ_BEGIN; i <= JJJJ_ACTUAL; i++) {
			final Frequency<Long> durchlaufzeitenErschliessung = jahr2durchlaufErschliessung
					.get(i);
			final Frequency<Long> durchlaufzeitenFOE = jahr2durchlaufFOE.get(i);
			final Frequency<Long> durchlaufzeitenIE = jahr2durchlaufIE.get(i);

			System.out.println("Jahr: " + i);
			ausdrucken(durchlaufzeitenFOE, "FE: ");
			ausdrucken(durchlaufzeitenIE, "IE: ");
			ausdrucken(durchlaufzeitenErschliessung, "Gesamt: ");
			System.out.println("--------------");
			System.out.println();
		}

	}

	/**
	 * @param durchlaufzeiten
	 */
	private static void ausdrucken(final Frequency<Long> durchlaufzeiten,
			final String typ) {
		if (!durchlaufzeiten.isEmpty()) {
			final BiMap<Long, Long> cdf = StatisticUtils
					.getCDF(durchlaufzeiten);
			System.out.println(typ);
			System.out.println(DATENBASIS + durchlaufzeiten.getSum());
			System.out
					.println(QUARTIL_1 + StatisticUtils.getQuantile(cdf, .25));
			System.out.println(MEDIAN + StatisticUtils.getQuantile(cdf, .5));
			System.out
					.println(QUARTIL_3 + StatisticUtils.getQuantile(cdf, .75));
			System.out.println();
		}
	}

}
