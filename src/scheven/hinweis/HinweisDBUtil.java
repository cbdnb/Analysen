/**
 *
 */
package scheven.hinweis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.ie.utils.DB.GND_DB_UTIL;
import de.dnb.ie.utils.DB.GUI;

/**
 * @author baumann
 *
 */
public class HinweisDBUtil {

	public static final String DOWNLOAD_FILE = "D:/Analysen/scheven/Hinweis/download_ohne_s_p.txt";

	public static Pair<String, String> extractHinweis(final Line line260) {
		final String sub9 = line260.getIdnRelated();
		final String dollarA = SubfieldUtils.getContentOfFirstSubfield(line260,
				'a');
		if (sub9 != null) {
			return new Pair<>(sub9, line260.getExpansion());
		} else {
			return new Pair<>(dollarA, null);
		}
	}

	/**
	 *
	 * @param record
	 *            nicht null
	 * @return [(idn1, expansion1), ...]. Die Expansionen enden auf " [T#1]"!!
	 *         Bei Zeitschlagwörtern ist das Tupel (zeitSW, null).
	 */
	public static Set<Pair<String, String>> getPairKombi(final Record record) {
		final Set<Pair<String, String>> pairs = new LinkedHashSet<>();

		final List<Line> hinweisLines = GNDUtils.getHinweisLines(record);
		if (hinweisLines.isEmpty())
			return pairs;

		hinweisLines.forEach(line ->
		{
			pairs.add(extractHinweis(line));
		});
		return pairs;
	}

	static final int POS_INDIKATOR = -3;

	/**
	 *
	 * @param link260
	 *            nicht null, (idn, expansion)
	 * @return liefert b aus "Südtiroler Landesarchiv [Tb1]", z bei Zeit-SWW;
	 *         dort ist expansion == null
	 */
	public static char getIndikator(final Pair<String, String> link260) {
		final String fullExpansion = link260.second;
		if (fullExpansion == null) {
			return 'z';
		}
		return getIndikator(fullExpansion);
	}

	/**
	 * @param fullExpansion
	 *            so etwas wie "Südtiroler Landesarchiv [Tb1]"
	 * @return
	 */
	public static char getIndikator(final String fullExpansion) {
		final int expanLength = fullExpansion.length();
		return fullExpansion.charAt(expanLength + POS_INDIKATOR);
	}

	/**
	 *
	 * @param record
	 *            nicht null
	 * @return Kombination von IDNs oder Zeit-SW, für die dieser
	 *         Hinweisdatensatz steht.
	 */
	public static Set<String> getIdnKombi(final Record record) {
		final Set<Pair<String, String>> pairs = getPairKombi(record);
		return new LinkedHashSet<>((pairs.stream().map(Pair::getFirst)
				.collect(Collectors.toList())));
	}

	static final int LEN_SUFFIX = " [Tp1]".length();

	/**
	 *
	 * @param link260
	 *            nicht null, (idn, expansion)
	 * @return schneidet bei expansion " [T#1]" ab.
	 */
	public static String getTrueExpansion(final Pair<String, String> link260) {
		final String fullExpansion = link260.second;
		if (fullExpansion == null) {
			return null;
		}
		return getTrueExpansion(fullExpansion);
	}

	/**
	 * @param Fullexpansion
	 *            z.B. "Südtiroler Landesarchiv [Tb1]"
	 * @return schneidet bei expansion " [Tb1]" ab.
	 */
	public static String getTrueExpansion(final String Fullexpansion) {
		final int expanLength = Fullexpansion.length();
		return Fullexpansion.substring(0, expanLength - LEN_SUFFIX);
	}

	public static void main1(final String[] args) {
		final HinweisDBUtil db = new HinweisDBUtil();
		final Frequency<String> frequency = new Frequency<>();
		db.kombi2Records.flatten().forEach(rec ->
		{
			final List<Character> chars = getSignature(rec);
			final String s = StringUtils.concatenate(",", chars);
			frequency.add(s);
		});
		frequency.getOrderedDistribution().forEach(System.out::println);
	}

	public static void main(final String[] args) {
		final HinweisDBUtil db = new HinweisDBUtil();
		System.out.println(db.getExpansion("115367918"));
		System.out.println(db.getIDN("Gatterer, Johann Christoph"));
	}

	/**
	 * @param record
	 *            nicht null
	 * @return Die Typen der SW-Kombination, z.B. [p, g, s]
	 */
	public static List<Character> getSignature(final Record record) {
		final Set<Pair<String, String>> pairs = getPairKombi(record);
		return getSignature(pairs);
	}

	/**
	 * @param kombi
	 *            nicht null
	 * @return Die Typen der SW-Kombination, z.B. [p, g, s]
	 */
	public static List<Character> getSignature(
			final Set<Pair<String, String>> kombi) {
		return FilterUtils.map(kombi, pair -> getIndikator(pair));
	}

	/**
	 *
	 * @param pairs
	 * @return
	 */
	public static Multimap<Character, Pair<String, String>> getSignatureMap(
			final Collection<Pair<String, String>> pairs) {
		final Multimap<Character, Pair<String, String>> map = new ListMultimap<>();
		pairs.forEach(pair -> map.add(getIndikator(pair), pair));
		return map;
	}

	/**
	 * z.B. 118512374 -> "Böcklin, Arnold [Tp1]" aus 260.
	 */
	private Map<String, String> idn2NamePlusType;

	/**
	 * z.B. 118512374 <-> "Böcklin, Arnold" aus 260.
	 */
	private final BiMap<String, String> idn2Name = new BiMap<>();

	/**
	 * Zentrale Datenbank. Diese kann gefiltert werden.
	 */
	private Multimap<Set<Pair<String, String>>, Record> kombi2Records;

	/**
	 * Wird extern geladen, kann etwas dauern-
	 */
	private static HashMap<Integer, Integer> tb2tgId;

	/**
	 * Wird extern geladen, kann etwas dauern-
	 */
	private static HashMap<Integer, String> tgId2name;

	/**
	 *
	 */
	public HinweisDBUtil() {
		kombi2Records = new ListMultimap<>();
		idn2NamePlusType = new HashMap<>();

		RecordReader reader = null;
		try {
			reader = RecordReader.getMatchingReader(DOWNLOAD_FILE);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		if (reader != null)
			reader.forEach(record ->
			{
				final Set<Pair<String, String>> hinweise = getPairKombi(record);
				if (hinweise.isEmpty())
					return;
				kombi2Records.add(getPairKombi(record), record);

				hinweise.forEach(hinweis ->
				{
					final String expansion = hinweis.second;
					if (expansion != null) {
						final String idn = hinweis.first;
						idn2NamePlusType.put(idn, expansion);
						idn2Name.put(idn, getTrueExpansion(expansion));
					}
				});
			});
	}

	/**
	 *
	 * @return Alle Kombinationen von SWW: {[(id1, expansion1), ...], ...}.
	 *         Menge ändert sich durch Filterung.
	 */
	public Set<Set<Pair<String, String>>> getKombis() {
		return kombi2Records.getKeySet();
	}

	/**
	 *
	 * @return Zahl der Kombinationen
	 */
	public int getAnzahlKombinationen() {
		return kombi2Records.getKeyCount();
	}

	/**
	 *
	 * @return Zahl der Kombinationen
	 */
	public int getAnzahlHinweissaetze() {
		return kombi2Records.getValueCount();
	}

	/**
	 *
	 * @param idn
	 *            auch null
	 * @return Die Expansion aus 260, MIT " [T#1]" am Ende, wenn kein Zeit-SW.
	 */
	public String getExpansionPlusType(final String idn) {
		return idn2NamePlusType.get(idn);
	}

	/**
	 *
	 * @param idn
	 *            Die idn aus 260, wenn kein Zeit-SW.
	 * @return Die reine Expansion (ohne " [Tp1]") aus 260, wenn kein Zeit-SW.
	 */
	public String getExpansion(final String idn) {
		return idn2Name.get(idn);
	}

	/**
	 *
	 * @param swName,
	 *            die reine Expansion (ohne " [Tp1]") aus 260, wenn kein
	 *            Zeit-SW; auch null.
	 * @return Die idn aus 260, wenn kein Zeit-SW.
	 */
	public String getIDN(final String swName) {
		return idn2Name.getKey(swName);
	}

	/**
	 * Ermittelt zu einer Körperschaft den Namen des zugehörige Geografikums
	 * ($4geoa). Aufpassen: Die verwendeteten Datenbanken
	 * {@link GND_DB_UTIL#getppn2name()} und {@link GND_DB_UTIL#getTb2Ort()}
	 * müssen über {@link GUI} so geladen werden, dass sie auch die nötigen
	 * Geografika enthalten.
	 *
	 * @param tbIDN
	 *            Der Einfachheit halber als String, obwohl die zugrundeliegende
	 *            Datei die Zuordnung int->String hat.
	 * @return
	 */
	public static String getGeoName(final String tbIDN) {
		final Integer geoIDN = getGeoIdInt(tbIDN);
		if (tgId2name == null)
			try {
				tgId2name = GND_DB_UTIL.getppn2name();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				return null;
			}
		return tgId2name.get(geoIDN);
	}

	/**
	 * Ermittelt zu einer Körperschaft die IDN des zugehörige Geografikums
	 * ($4geoa). Aufpassen: Die verwendetete Datenbank
	 * {@link GND_DB_UTIL#getTb2Ort()} muss über {@link GUI} so geladen werden,
	 * dass sie auch die nötigen Geografika enthält.
	 *
	 * @param tbIDN
	 * @return
	 * @throws NumberFormatException
	 */
	public static Integer getGeoIdInt(final String tbIDN)
			throws NumberFormatException {
		if (tb2tgId == null)
			try {
				tb2tgId = GND_DB_UTIL.getTb2Ort();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				return null;
			}
		final Integer tbInt = IDNUtils.ppn2int(tbIDN);
		return tb2tgId.get(tbInt);
	}

	/**
	 *
	 * @param record
	 *            nicht null
	 * @return Alle Hinweissätze, die die gleiche 260-Kombination wie record
	 *         haben
	 */
	public Collection<Record> getGruppe(final Record record) {
		final Set<Pair<String, String>> kombi = getPairKombi(record);
		return getRecords(kombi);
	}

	/**
	 * Menge ändert sich durch Filterung.
	 *
	 * @param kombi
	 *            [(id1, expansion1), ...], auch null
	 * @return Alle Datensätze zu einer SW-Kombination
	 */
	public Collection<Record> getRecords(
			final Set<Pair<String, String>> kombi) {
		return kombi2Records.get(kombi);
	}

	/**
	 * Behält nur die Gruppen bei, deren Kombination die Bedingung
	 * kombiPredicate erfüllen, z.B.: <br>
	 * <code>db.retainIfKombi(kombi -> kombi.size() == 1);</code>
	 *
	 * @param kombiPredicate
	 *            nicht null
	 */
	public void retainIfKombi(
			final Predicate<Set<Pair<String, String>>> kombiPredicate) {
		// Trick, um eine ConcurrentModificationException zu vermeiden:
		// keySet umkopieren.
		final List<Set<Pair<String, String>>> keys = new ArrayList<>(
				kombi2Records.getKeySet());
		for (final Set<Pair<String, String>> kombi : keys) {
			if (!kombiPredicate.test(kombi))
				kombi2Records.remove(kombi);
		}
	}

	/**
	 * Behält nur die Gruppen bei, die die Bedingung recPredicate erfüllen, z.B.
	 * <br>
	 * <code>db.retainIfRecords(recs -> recs.size() == 1);</code>
	 *
	 * @param recPredicate
	 *            nicht null
	 */
	public void retainIfRecords(
			final Predicate<Collection<Record>> recPredicate) {
		// Trick, um eine ConcurrentModificationException zu vermeiden:
		// keySet umkopieren.
		final List<Set<Pair<String, String>>> keys = new ArrayList<>(
				kombi2Records.getKeySet());
		for (final Set<Pair<String, String>> kombi : keys) {
			final Collection<Record> records = getRecords(kombi);
			if (!recPredicate.test(records))
				kombi2Records.remove(kombi);
		}
	}

	/**
	 * Behält nur die Gruppen bei, in denen alle Datensätze die Bedingung
	 * recPredicate erfüllen.
	 *
	 * @param recPredicate
	 *            nicht null
	 */
	public void retainIfAllRecords(final Predicate<Record> recPredicate) {
		// Trick, um eine ConcurrentModificationException zu vermeiden:
		// keySet umkopieren.
		final List<Set<Pair<String, String>>> keys = new ArrayList<>(
				kombi2Records.getKeySet());
		for (final Set<Pair<String, String>> kombi : keys) {
			final Collection<Record> records = getRecords(kombi);
			for (final Record rec : records) {
				if (!recPredicate.test(rec))
					kombi2Records.remove(kombi);
			}
		}
	}

	/**
	 * Behält nur die Gruppen bei, in denen mindestens einer der Datensätze die
	 * Bedingung recPredicate erfüllt.
	 *
	 * @param recPredicate
	 *            nicht null
	 */
	public void retainIfAnyRecord(final Predicate<Record> recPredicate) {
		// Trick, um eine ConcurrentModificationException zu vermeiden:
		// keySet umkopieren.
		final List<Set<Pair<String, String>>> keys = new ArrayList<>(
				kombi2Records.getKeySet());
		for (final Set<Pair<String, String>> kombi : keys) {
			final Collection<Record> records = getRecords(kombi);
			boolean found = false;
			for (final Record rec : records) {
				if (recPredicate.test(rec))
					found = true;
			}
			if (!found)
				kombi2Records.remove(kombi);
		}
	}

}
