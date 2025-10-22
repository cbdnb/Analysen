package baumann.skurriles.jhr2025;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.collections.PriorityMultimap;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubjectUtils;
import utils.DB.GND_DB_UTIL;

public class NewComer {

	static CrossProductFrequency idnJhr2Count;
	static Set<Integer> beruecksichtigteIDNs;
	static Set<Integer> tatsaechlicheIDNs;
	static Comparator<Pair<Integer, Double>> idnRangComparator;
	static PriorityMultimap<Integer, Pair<Integer, Double>> jahr2idnRang;
	static BiMultimap<Integer, String> ppn2name;

	static {
		idnRangComparator = (Comparator.comparingDouble(t -> t.second));
		// idnRangComparator = idnRangComparator.reversed();
		jahr2idnRang = new PriorityMultimap<>(5, idnRangComparator);
	}

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		ppn2name = GND_DB_UTIL.getppn2RDAName();
		System.err.println("Ts laden");
		beruecksichtigteIDNs = new HashSet<>();
		tatsaechlicheIDNs = new HashSet<>();

		RecordReader.getMatchingReader(Constants.Ts).forEach(rec ->
		{
			if (GNDUtils.getLevel(rec) <= 1)
				beruecksichtigteIDNs.add(IDNUtils.idn2int(rec.getId()));
		});

		System.err.println("RSWK-Ketten laden");
		idnJhr2Count = new CrossProductFrequency();
		final RecordReader titleReader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		final Predicate<String> ab2000 = new ContainsTag("1100", 'a', "20",
				BibTagDB.getDB());
		Predicate<String> filter = ab2000
				.and(new ContainsTag("5100", BibTagDB.getDB()));
		filter = filter
				.and(new ContainsTag("0500", '0', "A", BibTagDB.getDB()));
		titleReader.setStreamFilter(filter);

		titleReader.forEach(record ->
		{
			final Integer jahr = BibRecUtils.getYearOfPublication(record);
			if (jahr == null)
				return;

			SubjectUtils.getRSWKidsSet(record).forEach(id ->
			{
				final int intID = IDNUtils.idn2int(id);
				if (beruecksichtigteIDNs.contains(intID)) {
					idnJhr2Count.addValues(intID, jahr);
					tatsaechlicheIDNs.add(intID);
				}
			});
		});

		System.err.println("Rang feststellen");
		tatsaechlicheIDNs.forEach(idn ->
		{
			for (int jahr = 2000; jahr < 2025; jahr++) {
				final long alt = idnJhr2Count.getCount(idn, jahr);
				final long neu = idnJhr2Count.getCount(idn, jahr + 1);
				final double rang = toRang(alt, neu);
				final Pair<Integer, Double> idnRang = new Pair<>(idn, rang);
				jahr2idnRang.add(jahr + 1, idnRang);
			}
		});

		System.err.println("Ausgeben");
		for (int jahr = 2001; jahr <= 2025; jahr++) {
			final Collection<Pair<Integer, Double>> values = jahr2idnRang
					.getNullSafe(jahr);
			final ArrayList<Pair<Integer, Double>> pairs = new ArrayList<>(
					values);
			Collections.sort(pairs, idnRangComparator.reversed());

			final Function<Pair<Integer, Double>, String> idn2nameFun = pair ->
			{
				final Collection<String> names = ppn2name.get(pair.first);
				if (names.isEmpty())
					return "";
				final String name = names.iterator().next();
				final Double second = pair.second;
				return name + " (" + second.intValue() + ")";
			};
			final ArrayList<String> namen = FilterUtils.map(pairs, idn2nameFun);
			System.out.println(jahr + ": " + namen);
		}

	}

	public static double toRang(final long alt, final long neu) {
		if (alt == 0 && neu == 0)
			return 0;

		final double max = Long.max(alt, neu);
		final double delta = neu - alt;
		final double gewicht = Math.abs(delta) / max;
		return delta * gewicht;
	}

	public static void main2(final String[] args) throws IOException {
		System.out.println(toRang(10, 30));

	}
}
