/**
 *
 */
package langer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import de.dnb.basics.filtering.Between;

/**
 * @author baumann
 *
 */
public class Musikepoche implements Comparable<Musikepoche> {

	private final Between<LocalDate> zeitraum;

	private String name = null;

	/**
	 * @param zeitraum
	 * @param name
	 */
	public Musikepoche(final Between<LocalDate> zeitraum, final String name) {
		super();
		this.zeitraum = zeitraum;
		this.name = name;
	}

	public Musikepoche(final int anfang, final int ende, final String name) {
		zeitraum = new Between<LocalDate>(LocalDate.ofYearDay(anfang, 1),
				LocalDate.ofYearDay(ende, 1));
		this.name = name;
	}

	public static void putEpoche(final int anfang, final int ende,
			final String name) {
		final Musikepoche musikepoche = new Musikepoche(anfang, ende, name);
		date2epoche.put(musikepoche.getAnfang(), musikepoche);
	}

	/**
	 * @param zeitraum
	 * @param name
	 */
	public Musikepoche(final Between<LocalDate> zeitraum) {
		super();
		this.zeitraum = zeitraum;

	}

	@Override
	public String toString() {
		final LocalDate lower = zeitraum.lowerBound;
		String anfang;
		if (!lower.equals(LocalDate.MIN))
			anfang = Integer.toString(lower.getYear());
		else
			anfang = "Anfänge";
		final LocalDate higher = zeitraum.higherBound;
		String ende;
		if (!higher.equals(LocalDate.MAX))
			ende = Integer.toString(higher.getYear());
		else
			ende = "";
		String s = anfang + "-" + ende;
		if (name != null)
			s = name + " (" + s + ")";
		return s;
	}

	private static TreeMap<LocalDate, Musikepoche> date2epoche = new TreeMap<>();

	static {

		final Between<LocalDate> antike = new Between<LocalDate>(LocalDate.MIN,
				LocalDate.ofYearDay(500, 1));
		final Musikepoche antEp = new Musikepoche(antike, "Antike");
		date2epoche.put(antike.lowerBound, antEp);
		putEpoche(500, 1150, "Frühmittelalter");

		putEpoche(500, 1150, "Frühmittelalter");
		putEpoche(1150, 1300, "Hochmittelalter");
		putEpoche(1300, 1400, "Spätmittelalter");
		putEpoche(1400, 1470, "Frührenaissance");
		putEpoche(1470, 1530, "Hochrenaissance");
		putEpoche(1530, 1600, "Spätrenaissance");
		putEpoche(1600, 1650, "Frühbarock");
		putEpoche(1650, 1700, "Hochbarock");
		putEpoche(1700, 1750, "Spätbarock");
		putEpoche(1750, 1775, "Frühklassik");
		putEpoche(1775, 1790, "Wiener Klassik");
		putEpoche(1790, 1820, "Spätklassik");

		for (int i = 1820; i < 1900; i += 20) {
			final Between<LocalDate> zr = new Between<LocalDate>(
					LocalDate.ofYearDay(i, 1), LocalDate.ofYearDay(i + 20, 1)); // Romantik
			final Musikepoche musikepoche = new Musikepoche(zr);
			date2epoche.put(zr.lowerBound, musikepoche);
		}

		for (int i = 1900; i < 2020; i += 10) {
			final Between<LocalDate> zr = new Between<LocalDate>(
					LocalDate.ofYearDay(i, 1), LocalDate.ofYearDay(i + 10, 1)); // Moderne
			final Musikepoche musikepoche = new Musikepoche(zr);
			date2epoche.put(zr.lowerBound, musikepoche);
		}

		final Between<LocalDate> moderne = new Between<LocalDate>(
				LocalDate.ofYearDay(2020, 1), LocalDate.MAX);
		final Musikepoche modep = new Musikepoche(moderne, "Avantgarde");
		date2epoche.put(moderne.lowerBound, modep);

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the zeitraum
	 */
	public Between<LocalDate> getZeitraum() {
		return zeitraum;
	}

	public LocalDate getAnfang() {
		return zeitraum.lowerBound;
	}

	public LocalDate getEnde() {
		return zeitraum.higherBound;
	}

	public static List<Musikepoche> getEpochen(
			final Between<LocalDate> zeitInt) {
		final ArrayList<Musikepoche> list = new ArrayList<>();
		date2epoche.values().forEach(epoche ->
		{
			if (epoche.getZeitraum().intersects(zeitInt))
				list.add(epoche);
		});
		return list;
	}

	public static List<Musikepoche> getEpochenKorrigiert(
			final Between<LocalDate> zeitInt) {
		final List<Musikepoche> list = getEpochen(zeitInt);
		if (list.size() > 2)
			list.clear();
		return list;
	}

	public static Musikepoche getEpoche(final LocalDate localDate) {
		return date2epoche.floorEntry(localDate).getValue();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Between<LocalDate> zeitInt = new Between<LocalDate>(LocalDate.MIN,
				LocalDate.MAX);
		// TimeUtils.get548dInterval("1150-");
		System.out.println(zeitInt);
		System.out.println(getEpochen(zeitInt));

	}

	@Override
	public int compareTo(final Musikepoche o) {
		return zeitraum.compareTo(o.zeitraum);
	}

}
