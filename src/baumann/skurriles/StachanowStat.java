/**
 *
 */
package baumann.skurriles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.utils.TimeUtils;

/**
 * @author baumann
 *
 */
public class StachanowStat {

	final static List<Calendar> dates = new ArrayList<>();
	final static List<Integer> fs = new ArrayList<>();
	final static List<Integer> ls = new ArrayList<>();
	final static List<Integer> gesamte = new ArrayList<>();
	final static List<String> standorte = Arrays.asList("Frankfurt", "Leipzig",
			"Gesamt");
	final static List[] standortZahlen = { fs, ls, gesamte };
	final static List<String> wochentage = Arrays.asList("So", "Mo", "Di", "Mi",
			"Do", "Fr", "Sa");

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Collection<String> zeilen = StringUtils.readLinesFromClip();
		zeilen.forEach(zeile ->
		{
			if (zeile.startsWith("Datum"))
				return;
			final String[] strings = zeile.split("\t");
			final Date date = TimeUtils.parseMxDate(strings[0]);
			final Calendar cal = TimeUtils.getCalendar(date);
			final int f = Integer.parseInt(strings[1]);
			final int l = Integer.parseInt(strings[2]);
			final int ges = Integer.parseInt(strings[3]);
			dates.add(cal);
			fs.add(f);
			ls.add(l);
			gesamte.add(ges);

		});
		System.out.println("\t" + StringUtils.concatenate("\t", standorte));
		for (int tag = Calendar.SUNDAY; tag <= Calendar.SATURDAY; tag++) {
			String out = wochentage.get(tag - 1);
			for (int standort = 0; standort < standorte.size(); standort++) {
				final double schnitt = durchschnitt(tag, standort);
				out += "\t" + schnitt;
			}
			System.out.println(out);
		}
	}

	/**
	 * @param i
	 * @return
	 */
	private static double durchschnitt(final int wochentag,
			final int standort) {
		final List<Integer> produktion = standortZahlen[standort];
		final List<Integer> temp = new ArrayList<>();
		for (int produktionstag = 0; produktionstag < dates
				.size(); produktionstag++) {
			final Calendar cal = dates.get(produktionstag);
			final int tag = cal.get(Calendar.DAY_OF_WEEK);
			if (tag == wochentag) {
				temp.add(produktion.get(produktionstag));
			}
		}
		if (temp.isEmpty())
			return 0;
		else
			return ListUtils.average(temp);

	}

}
