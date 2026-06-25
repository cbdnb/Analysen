/**
 *
 */
package scheven.hinweis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;

/**
 * @author baumann
 *
 */
public class HinweisDB {

	public static void main1(final String[] args) {
		final HinweisDB db = new HinweisDB();
		final Frequency<String> frequency = new Frequency<>();
		db.idnExpansionKombi2Records.flatten().forEach(rec ->
		{
			final List<Character> chars = Util.getSignature(rec);
			final String s = StringUtils.concatenate(",", chars);
			frequency.add(s);
		});
		frequency.getOrderedDistribution().forEach(System.out::println);
	}

	public static void main(final String[] args) {
		final HinweisDB db = new HinweisDB();
		System.out.println(db.getExpansion("115367918"));
		System.out.println(db.getIDN("Gatterer, Johann Christoph"));
	}

	/**
	 * Vorsicht: Expansion enthält Typ und Level in eckigen Klammern! z.B.
	 * "Südtiroler Landesarchiv [Tb1]". z.B. 118512374 -> "Böcklin, Arnold
	 * [Tp1]" aus 260.
	 */
	private Map<String, String> idn2Expansion;

	/**
	 * z.B. 118512374 <-> "Böcklin, Arnold" aus 260.
	 */
	private final BiMap<String, String> idn2Name = new BiMap<>();

	/**
	 * Zentrale quasirelationale Datenbank. Enthält zu einer Kombination von
	 * relationierten Datensätzen (IDNs) die Hinweisdatensätze zu dieser
	 * Kombination. Das können mehrere sein, da zu jeder synonymen Benennung ein
	 * eigener Hinweisdatensatz angelegt werden muss. Die Kombinationen sind als
	 * Set (idn1,idn2,...,idnN) gespeichert. <br>
	 * <br>
	 * Diese Datenbank kann gefiltert werden, z.B. nur die Kombinationen, die zu
	 * einem bestimmten Hinweistyp gehören. Damit ist sie aber irreversibel
	 * verändert!
	 *
	 */
	private Multimap<Set<Pair<String, String>>, Record> idnExpansionKombi2Records;

	/**
	 * Füllt alle Maps und Multimaps aus {@link Util#DOWNLOAD_FILE} Muss
	 * aufgerufen werden, bevor die anderen Methoden benutzt werden.
	 */
	public HinweisDB() {
		idnExpansionKombi2Records = new ListMultimap<>();
		idn2Expansion = new HashMap<>();

		RecordReader reader = null;
		try {
			reader = RecordReader.getMatchingReader(Util.DOWNLOAD_FILE);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		if (reader != null)
			reader.forEach(record ->
			{
				final Set<Pair<String, String>> kombi = Util
						.getIDNExpansionPairs(record);
				if (kombi.isEmpty())
					return;
				idnExpansionKombi2Records.add(kombi, record);

				kombi.forEach(hinweis ->
				{
					final String expansion = hinweis.second;
					if (expansion != null) {
						final String idn = hinweis.first;
						idn2Expansion.put(idn, expansion);
						idn2Name.put(idn, Util.getNameFromExpansion(expansion));
					}
				});
			});
	}

	/**
	 *
	 * @return Alle Kombinationen von SWW: {[(id1, expansion1), ...], ...}.
	 *         Menge ändert sich durch Filterung.
	 */
	public Set<Set<Pair<String, String>>> getIdnExpansionKombis() {
		return idnExpansionKombi2Records.getKeySet();
	}

	/**
	 *
	 * @return Zahl der Kombinationen
	 */
	public int getAnzahlKombinationen() {
		return idnExpansionKombi2Records.getKeyCount();
	}

	/**
	 *
	 * @return Zahl der Kombinationen
	 */
	public int getAnzahlHinweissaetze() {
		return idnExpansionKombi2Records.getValueCount();
	}

	/**
	 *
	 * @param idn
	 *            auch null
	 * @return Die Expansion aus 260, MIT " [T#1]" am Ende, wenn kein Zeit-SW.
	 */
	public String getExpansionPlusType(final String idn) {
		return idn2Expansion.get(idn);
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
	 *
	 * @param record
	 *            nicht null
	 * @return Alle Hinweissätze, die die gleiche 260-Kombination wie record
	 *         haben
	 */
	public Collection<Record> getGruppe(final Record record) {
		final Set<Pair<String, String>> kombi = Util
				.getIDNExpansionPairs(record);
		return getRecords(kombi);
	}

	/**
	 *
	 *
	 * @param idnExpansionKombi
	 *            [(id1, expansion1), ...], auch null
	 * @return Alle Datensätze zu einer SW-Kombination
	 */
	public Collection<Record> getRecords(
			final Set<Pair<String, String>> idnExpansionKombi) {
		return idnExpansionKombi2Records.get(idnExpansionKombi);
	}

	/**
	 *
	 * Verändert die zentrale Datenbank!<br>
	 *
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
				idnExpansionKombi2Records.getKeySet());
		for (final Set<Pair<String, String>> kombi : keys) {
			if (!kombiPredicate.test(kombi))
				idnExpansionKombi2Records.remove(kombi);
		}
	}

	/**
	 * Verändert die zentrale Datenbank!<br>
	 *
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
				idnExpansionKombi2Records.getKeySet());
		for (final Set<Pair<String, String>> kombi : keys) {
			final Collection<Record> records = getRecords(kombi);
			if (!recPredicate.test(records))
				idnExpansionKombi2Records.remove(kombi);
		}
	}

	/**
	 * Verändert die zentrale Datenbank!<br>
	 *
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
				idnExpansionKombi2Records.getKeySet());
		for (final Set<Pair<String, String>> kombi : keys) {
			final Collection<Record> records = getRecords(kombi);
			for (final Record rec : records) {
				if (!recPredicate.test(rec))
					idnExpansionKombi2Records.remove(kombi);
			}
		}
	}

	/**
	 * Verändert die zentrale Datenbank!<br>
	 *
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
				idnExpansionKombi2Records.getKeySet());
		for (final Set<Pair<String, String>> kombi : keys) {
			final Collection<Record> records = getRecords(kombi);
			boolean found = false;
			for (final Record rec : records) {
				if (recPredicate.test(rec))
					found = true;
			}
			if (!found)
				idnExpansionKombi2Records.remove(kombi);
		}
	}

}
