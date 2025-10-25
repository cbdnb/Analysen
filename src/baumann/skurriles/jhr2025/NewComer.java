package baumann.skurriles.jhr2025;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.collections.PriorityMultimap;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubjectUtils;
import utils.DB.GND_DB_UTIL;

public class NewComer {

	private static final int RANGLISTE_GROESSE = 5;
	private static final int JAHRE_VOR = 1;
	private static final int JAHRE_NACH = 1;

	// static List<String> listeTypen = Arrays.asList("Ts", "Tg", "Tp");
	static List<String> listeTypen = Arrays.asList("Tg");

	private static CrossProductFrequency idnJhr2Count;
	private static HashMap<String, Set<Integer>> typ2beruecksichtigende = new HashMap<>();
	private static HashMap<String, Set<Integer>> typ2tatsaechliche = new HashMap<>();

	private static Comparator<Pair<Integer, Double>> idnRangComparator;
	private static HashMap<Integer, String> ppn2name;

	static {
		idnRangComparator = (Comparator.comparingDouble(t -> t.second));
		// new PriorityMultimap<>(RANGLISTE_GROESSE,
		// idnRangComparator);
		listeTypen.forEach(typ -> typ2tatsaechliche.put(typ, new HashSet<>()));
	}

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		for (final String typ : listeTypen) {
			System.err.println(typ + " laden");
			final Set<Integer> zuBeruecksichtigendeIDNs = new HashSet<>();
			RecordReader.getMatchingReader(Constants.SATZ_TYPEN.get(typ))
					.forEach(rec ->
					{
						if (GNDUtils.getLevel(rec) <= 1)
							zuBeruecksichtigendeIDNs
									.add(IDNUtils.idn2int(rec.getId()));
					});
			typ2beruecksichtigende.put(typ, zuBeruecksichtigendeIDNs);
		}

		System.err.println("RSWK-Ketten laden");
		idnJhr2Count = new CrossProductFrequency();
		final RecordReader titleReader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		Predicate<String> ab1990 = new ContainsTag("1100", 'a', "20",
				BibTagDB.getDB());
		ab1990 = ab1990
				.or(new ContainsTag("1100", 'a', "199", BibTagDB.getDB()));
		Predicate<String> filter = ab1990
				.and(new ContainsTag("5100", BibTagDB.getDB()));
		Predicate<String> bbgAO = new ContainsTag("0500", '0', "A",
				BibTagDB.getDB());
		bbgAO = bbgAO.or(new ContainsTag("0500", '0', "O", BibTagDB.getDB()));
		filter = filter.and(bbgAO);
		titleReader.setStreamFilter(filter);

		titleReader.forEach(record ->
		{
			final Integer jahr = BibRecUtils.getYearOfPublication(record);
			if (jahr == null)
				return;
			// DBSM-Titel haben oft keine SG
			if (!SubjectUtils.containsDHS(record))
				return;
			// DBSM haben meist 5590:
			if (RecordUtils.containsField(record, "5590"))
				return;

			// Einziger Besitzer DBSM ausschließen:
			final List<String> besitzer = RecordUtils
					.getContentsOfAllSubfields(record, "4800", '9');
			if (besitzer.size() == 1
					&& besitzer.get(0).equalsIgnoreCase("009033645"))
				return;

			SubjectUtils.getRSWKidsSet(record).forEach(id ->
			{
				final int intID = IDNUtils.idn2int(id);
				listeTypen.forEach(typ ->
				{
					final Set<Integer> zuBeruecksichtigendeIDNs = typ2beruecksichtigende
							.get(typ);
					if (zuBeruecksichtigendeIDNs.contains(intID)) {
						idnJhr2Count.addValues(intID, jahr);
						final Set<Integer> tatsaechlicheIDNs = typ2tatsaechliche
								.get(typ);
						tatsaechlicheIDNs.add(intID);
					}
				});

			});
		});

		typ2beruecksichtigende = null;

		System.err.println("Ränge feststellen und ausgeben");

		ppn2name = GND_DB_UTIL.getppn2name();

		listeTypen.forEach(typ ->
		{

			final Set<Integer> tatsaechlicheIDNs = typ2tatsaechliche.get(typ);
			final PriorityMultimap<Integer, Pair<Integer, Double>> jahr2idnRang = new PriorityMultimap<>(
					RANGLISTE_GROESSE, idnRangComparator);
			tatsaechlicheIDNs.forEach(idn ->
			{
				for (int jahr = 2000; jahr <= 2025; jahr++) {
					final double rang = toRang(JAHRE_VOR, jahr, JAHRE_NACH, idn,
							idnJhr2Count);
					final Pair<Integer, Double> idnRang = new Pair<>(idn, rang);
					jahr2idnRang.add(jahr, idnRang);
				}
			});

			System.out.println(typ + ":");
			for (int jahr = 2001; jahr <= 2025; jahr++) {
				final Collection<Pair<Integer, Double>> values = jahr2idnRang
						.getNullSafe(jahr);
				final ArrayList<Pair<Integer, Double>> pairs = new ArrayList<>(
						values);
				Collections.sort(pairs, idnRangComparator.reversed());

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
				System.out.println(jahr + ": " + namen);
			}
		});

	}

	public static double toRang(final long alt, final long neu) {
		if (alt == 0 && neu == 0)
			return 0;

		final double max = Long.max(alt, neu);
		final double delta = neu - alt;
		final double gewicht = Math.abs(delta) / max;
		return delta * gewicht;
	}

	/**
	 *
	 * @param yearsPre
	 *            Vorjahre, >0
	 * @param year
	 *            betrachtetes Jahr
	 * @param yearsPost
	 *            Nachkahre >0
	 * @param idn
	 * @param idnJhr2Count
	 * @return Differenz des Durchschnitts der yearsPre Vorjahre (ohne das
	 *         aktuelle Jahr) und des Durchschnitts der yearsPost Nachjahre
	 *         (einschließlich des aktuellen Jahrs), gewichtet mit dem Faktor
	 *         <code>diff / max</code>
	 */
	public static double toRang(final int yearsPre, final int year,
			final int yearsPost, final int idn,
			final CrossProductFrequency idnJhr2Count) {
		double avrPre = 0;
		for (int i = year - yearsPre; i < year; i++) {
			avrPre += idnJhr2Count.getCount(idn, i);
		}
		avrPre /= yearsPre;
		double avrPost = 0;
		for (int i = year; i < year + yearsPost; i++) {
			avrPost += idnJhr2Count.getCount(idn, i);
		}
		avrPost /= yearsPost;
		if (avrPre == 0 && avrPost == 0)
			return 0;

		final double max = Double.max(avrPre, avrPost);
		final double delta = avrPost - avrPre;
		final double gewicht = Math.abs(delta) / max;
		return delta * gewicht;
	}

	public static void main2(final String[] args) throws IOException {
		System.out.println(toRang(10, 30));

	}
}
