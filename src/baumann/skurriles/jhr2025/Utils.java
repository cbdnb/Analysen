package baumann.skurriles.jhr2025;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.collections.PriorityMultimap;
import de.dnb.basics.filtering.FilterUtils;
import utils.DB.GND_DB_UTIL;

public class Utils {

	private static final int LETZTES_JAHR = 2025;
	private static final int ERSTES_JAHR = 2001;
	// ----Anpassen: ----
	/**
	 * Das Gewicht des Sprungs ist (delta / max)**EXPONENT
	 */
	static final List<Double> EXPONENTEN = Arrays.asList(0., 1., 2.);
	static final List<Pair<Integer, Integer>> VOR_NACH_KOMBIS = Arrays
			.asList(new Pair<>(1, 1), new Pair<>(2, 1), new Pair<>(2, 2));

	static final String FOLDER = "D:/Analysen/baumann/skurriles/2025/";
	static final int RANGLISTE_GROESSE = 5;

	static final String INPUT_FILE = Constants.TITEL_PLUS_EXEMPLAR_D;

	// ----fix ------------
	static final Comparator<Pair<Integer, Double>> IDN_RANG_COMP = (Comparator
			.comparingDouble(t -> t.second));

	/**
	 *
	 * @param jahreVor
	 *            >0
	 * @param jahr
	 *            betrachtetes Jahr
	 * @param jahreNach
	 *            >0
	 * @param idnJhr2Count
	 *            nicht null
	 * @param idn
	 *            nicht null
	 * @param exponent
	 *            >0
	 *
	 * @return Differenz des Durchschnitts der Vorjahre (ohne das aktuelle Jahr)
	 *         und des Durchschnitts der Nachjahre (einschließlich des aktuellen
	 *         Jahrs), gewichtet mit dem Faktor
	 *         <code>(diff / max)**exponent</code> <br>
	 *         Je größer der Exponent ist, umso höher werden die selteneren
	 *         Titel (max ist klein) gewichtet.
	 */
	public static double toRang(final int jahreVor, final int jahr,
			final int jahreNach, final CrossProductFrequency idnJhr2Count,
			final int idn, final double exponent) {
		double avrPre = 0;
		for (int i = jahr - jahreVor; i < jahr; i++) {
			avrPre += idnJhr2Count.getCount(idn, i);
		}
		avrPre /= jahreVor;
		double avrPost = 0;
		for (int i = jahr; i < jahr + jahreNach; i++) {
			avrPost += idnJhr2Count.getCount(idn, i);
		}
		avrPost /= jahreNach;
		if (avrPre == 0 && avrPost == 0)
			return 0;

		final double max = Double.max(avrPre, avrPost);
		final double delta = avrPost - avrPre;
		final double gewicht = Math.pow(Math.abs(delta) / max, exponent);
		return delta * gewicht;
	}

	public static double toRang(final long alt, final long neu,
			final double exponent) {
		if (alt == 0 && neu == 0)
			return 0;

		final double max = Long.max(alt, neu);
		final double delta = neu - alt;
		final double gewicht = Math.pow(Math.abs(delta) / max, exponent);
		return delta * gewicht;
	}

	public static String makeOutputFile(final String typ) {
		return FOLDER + typ + ".txt";
	}

	public static void ausgeben(final PrintWriter out,
			final List<String> listeTypen,
			final HashMap<String, Set<Integer>> typ2idns,
			final CrossProductFrequency idnJhr2Count)
			throws ClassNotFoundException, IOException {
		final HashMap<Integer, String> ppn2name = GND_DB_UTIL.getppn2name();
		System.err.println("ausgeben");

		listeTypen.forEach(typ ->
		{

			out.println(typ + ":");
			final Set<Integer> tatsaechlicheIDNs = typ2idns.get(typ);

			;
			for (final Pair<Integer, Integer> vorNach : VOR_NACH_KOMBIS) {
				for (final Double exponent : EXPONENTEN) {
					out.println("Berücksichtigte Jahre vor/nach: " + vorNach
							+ ", Exponent: " + exponent);

					final PriorityMultimap<Integer, Pair<Integer, Double>> jahr2idnRang = new PriorityMultimap<>(
							RANGLISTE_GROESSE, IDN_RANG_COMP);

					// Rangliste einsammeln:
					tatsaechlicheIDNs.forEach(idn ->
					{
						for (int jahr = ERSTES_JAHR; jahr <= LETZTES_JAHR; jahr++) {
							final double rang = toRang(vorNach.first, jahr,
									vorNach.second, idnJhr2Count, idn,
									exponent);
							final Pair<Integer, Double> idnRang = new Pair<>(
									idn, rang);
							jahr2idnRang.add(jahr, idnRang);
						}
					});

					// Rangliste ausgeben:
					for (int jahr = ERSTES_JAHR; jahr <= LETZTES_JAHR; jahr++) {
						final Collection<Pair<Integer, Double>> values = jahr2idnRang
								.getNullSafe(jahr);
						final ArrayList<Pair<Integer, Double>> pairs = new ArrayList<>(
								values);
						Collections.sort(pairs, IDN_RANG_COMP.reversed());

						final Function<Pair<Integer, Double>, String> idn2nameFun = pair ->
						{
							final Integer idn = pair.first;
							final String name = ppn2name.get(idn);
							if (StringUtils.isNullOrEmpty(name))
								return idn + "";

							final Double second = pair.second;
							return name + " (" + second.intValue() + ")";
						};
						final ArrayList<String> namen = FilterUtils.map(pairs,
								idn2nameFun);
						out.println(jahr + ":\t" + namen);
					}
					out.println();
				}
			}
		});
	}

}
