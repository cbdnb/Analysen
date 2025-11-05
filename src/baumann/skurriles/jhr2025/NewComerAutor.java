package baumann.skurriles.jhr2025;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubjectUtils;
import utils.DB.GND_DB_UTIL;

public class NewComerAutor {

	private static final List<String> LISTE_TYPEN = Arrays.asList("Tb", "Tp");
	private static CrossProductFrequency idnJhr2Count;
	private static HashMap<String, Set<Integer>> typ2beruecksichtigende = new HashMap<>();
	private static HashMap<String, Set<Integer>> typ2tatsaechliche = new HashMap<>();

	static {

		LISTE_TYPEN.forEach(typ -> typ2tatsaechliche.put(typ, new HashSet<>()));
	}

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		for (final String typ : LISTE_TYPEN) {
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

		System.err.println("Autoren laden");
		idnJhr2Count = new CrossProductFrequency();
		final RecordReader titleReader = RecordReader
				.getMatchingReader(Utils.INPUT_FILE);
		Predicate<String> ab1990 = new ContainsTag("1100", 'a', "20",
				BibTagDB.getDB());
		ab1990 = ab1990
				.or(new ContainsTag("1100", 'a', "199", BibTagDB.getDB()));
		Predicate<String> filter = ab1990;
		final Predicate<String> bbgAO = new ContainsTag("0500", '0', "A",
				BibTagDB.getDB());
		// bbgAO = bbgAO.or(new ContainsTag("0500", '0', "O",
		// BibTagDB.getDB()));
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

			RecordUtils.getContentsOfFirstSubfield(record, '9', "3000", "3100")
					.forEach(id ->
					{
						final int intID = IDNUtils.idn2int(id);
						LISTE_TYPEN.forEach(typ ->
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

		GND_DB_UTIL.getppn2name();
		final PrintWriter out = MyFileUtils
				.outputFile(Utils.makeOutputFile("AU"), false);

		Utils.ausgeben(out, LISTE_TYPEN, typ2tatsaechliche, idnJhr2Count);

		MyFileUtils.safeClose(out);

	}

}
