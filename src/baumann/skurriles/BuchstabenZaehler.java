/**
 *
 */
package baumann.skurriles;

import java.util.Comparator;

import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.BoundedPriorityQueue;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.PriorityMultimap;
import de.dnb.gnd.utils.DownloadWorker;

/**
 * @author baumann
 *
 */
public class BuchstabenZaehler {

	final static int MAX_ENTRIES = 5;

	final static int MIN_COUNT = 3;

	final static Comparator<Triplett<String, String, Long>> zahlComparator = (Comparator
			.comparingLong(t -> t.third));

	/**
	 * zahl1 > zahl2; name1 kürzer name2
	 */
	final static Comparator<Triplett<String, String, Long>> maxComparator = zahlComparator
			.thenComparing(Triplett::getSecond,
					Comparator.comparing(String::length).reversed());

	/**
	 * zahl1 < zahl2; name1 kürzer name2
	 */
	final static Comparator<Triplett<String, String, Long>> minComparator = zahlComparator
			.reversed().thenComparing(Triplett::getSecond,
					Comparator.comparing(String::length).reversed());

	final static Comparator<Triplett<String, String, Long>> anteilComparator = Comparator
			.comparingDouble(t -> t.third / (t.second.length() + 1));

	final Multimap<Character, Triplett<String, String, Long>> selteneBuchstaben = new PriorityMultimap<>(
			MAX_ENTRIES, BuchstabenZaehler.maxComparator);

	final Multimap<Character, Triplett<String, String, Long>> alleBuchstaben = new PriorityMultimap<>(
			4, BuchstabenZaehler.maxComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> nurEinVokal = new BoundedPriorityQueue<>(
			MAX_ENTRIES, maxComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> umlaute = new BoundedPriorityQueue<>(
			MAX_ENTRIES, maxComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> alleVokale = new BoundedPriorityQueue<>(
			MAX_ENTRIES, maxComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> laengster = new BoundedPriorityQueue<>(
			MAX_ENTRIES, maxComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> kuerzester = new BoundedPriorityQueue<>(
			MAX_ENTRIES, minComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> xyz = new BoundedPriorityQueue<>(
			MAX_ENTRIES, maxComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> meisteVerschiedeneZeichen = new BoundedPriorityQueue<>(
			MAX_ENTRIES, maxComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> meisteVerschiedeneSonderzeichen = new BoundedPriorityQueue<>(
			MAX_ENTRIES, maxComparator);

	final BoundedPriorityQueue<Triplett<String, String, Long>> pangramme = new BoundedPriorityQueue<>(
			MAX_ENTRIES, zahlComparator.reversed());

	/**
	 * zahl1 < zahl2; name1 länger name2
	 */
	final static Comparator<Triplett<String, String, Long>> wenigLangComparator = zahlComparator
			.reversed().thenComparing(Triplett::getSecond,
					Comparator.comparing(String::length));

	final BoundedPriorityQueue<Triplett<String, String, Long>> wenigsteVerschiedeneZeichen = new BoundedPriorityQueue<>(
			MAX_ENTRIES * 10, wenigLangComparator);

	public static <T> void debug(final Iterable<T> it) {
		it.forEach(el -> System.err.println("\t" + el));
	}

	/**
	 * @param ansetzung
	 * @param id
	 */
	public void buchstabenStatistik(final String ansetzung, final String id) {
		// Länge:
		final int len = ansetzung.length();
		final Triplett<String, String, Long> lenTriplett = new Triplett<String, String, Long>(
				id, ansetzung, (long) len);
		laengster.add(lenTriplett);
		kuerzester.add(lenTriplett);

		// nur ein Vokal:
		if (SKUtil.onlyOneVowel(ansetzung)) {
			final int vokZahl = SKUtil.vowelCount(ansetzung);
			if (vokZahl >= MIN_COUNT) {
				final Triplett<String, String, Long> newTriplett = new Triplett<String, String, Long>(
						id, ansetzung, (long) vokZahl);
				nurEinVokal.add(newTriplett);
			}
		}

		// Umlaute:
		final int umlautZ = SKUtil.umlautZahl(ansetzung);
		if (umlautZ >= MIN_COUNT) {
			final Triplett<String, String, Long> newTriplett = new Triplett<String, String, Long>(
					id, ansetzung, (long) umlautZ);
			umlaute.add(newTriplett);
		}

		// alle Vokale
		final int vokalZ = SKUtil.vowelCount(ansetzung);
		if (vokalZ >= MIN_COUNT) {
			final Triplett<String, String, Long> newTriplett = new Triplett<String, String, Long>(
					id, ansetzung, (long) vokalZ);
			alleVokale.add(newTriplett);
		}

		// XYZ:
		final int xyzZ = SKUtil.xyzZahl(ansetzung);
		if (xyzZ >= MIN_COUNT) {
			final Triplett<String, String, Long> newTriplett = new Triplett<String, String, Long>(
					id, ansetzung, (long) xyzZ);
			xyz.add(newTriplett);
		}

		// Seltene Buchstaben zählen:
		final Frequency<Character> freq = SKUtil
				.getCharacterFrequency(ansetzung);
		SKUtil.selteneBuchstaben.forEach(c ->
		{
			final long count = freq.get(c);
			if (count < MIN_COUNT)
				return;
			final Triplett<String, String, Long> newTriplett = new Triplett<String, String, Long>(
					id, ansetzung, count);
			selteneBuchstaben.add(c, newTriplett);

		});

		// Verschiedene Zeichen:
		final int verschiedeneZ = SKUtil.zahlVerschiedenerZeichen(ansetzung);

		Triplett<String, String, Long> newTriplett = new Triplett<String, String, Long>(
				id, ansetzung, (long) verschiedeneZ);
		meisteVerschiedeneZeichen.add(newTriplett);
		wenigsteVerschiedeneZeichen.add(newTriplett);

		// Verschiedene Sonderzeichen:
		final int verschiedeneSonderZ = SKUtil
				.zahlVerschiedenerSonderzeichen(ansetzung);
		newTriplett = new Triplett<String, String, Long>(id, ansetzung,
				(long) verschiedeneSonderZ);
		meisteVerschiedeneSonderzeichen.add(newTriplett);

		// Pangramme (Vogel Quax zwickt Johnys Pferd Bim):
		if (SKUtil.istPangramm(ansetzung)) {
			System.err.println(ansetzung);
			newTriplett = new Triplett<String, String, Long>(id, ansetzung,
					(long) ansetzung.length());
			pangramme.add(newTriplett);
		}

		// alle Buchstaben:

		final Frequency<Character> f = SKUtil.getCharacterFrequency(ansetzung);
		f.forEach(c ->
		{
			final long count = f.get(c);
			if (count < MIN_COUNT)
				return;
			final Triplett<String, String, Long> tr = new Triplett<String, String, Long>(
					id, ansetzung, count);
			alleBuchstaben.add(c, tr);
		});

	}

	/**
	 * @param fe
	 */
	public void ausgabe(final DownloadWorker fe) {
		fe.println("Längster:");
		fe.printIterable(laengster.ordered());
		fe.println();

		fe.println("Kürzester:");
		fe.printIterable(kuerzester.ordered());
		fe.println();

		fe.println("Ein einziger Vokal:");
		fe.printIterable(nurEinVokal.ordered());
		fe.println();

		fe.println("Umlaute:");
		fe.printIterable(umlaute.ordered());
		fe.println();

		fe.println("Alle Vokale:");
		fe.printIterable(alleVokale.ordered());
		fe.println();

		fe.println("xyz:");
		fe.printIterable(xyz.ordered());
		fe.println();

		fe.println("meiste verschiedene Zeichen:");
		fe.printIterable(meisteVerschiedeneZeichen.ordered());
		fe.println();

		fe.println("meiste verschiedene Sonderzeichen:");
		fe.printIterable(meisteVerschiedeneSonderzeichen.ordered());
		fe.println();

		fe.println("wenigste verschiedene Zeichen:");
		fe.printIterable(wenigsteVerschiedeneZeichen.ordered());
		fe.println();

		fe.println("Pangramme:");
		fe.printIterable(pangramme.ordered());
		fe.println();

		fe.println("Seltene Buchstaben");
		selteneBuchstaben.forEach(c ->
		{
			fe.println(c + ": ");
			fe.printIterable(((BoundedPriorityQueue) selteneBuchstaben.get(c))
					.ordered());
		});

		fe.println();
		fe.println("Alle Buchstaben");
		alleBuchstaben.forEach(c ->
		{
			fe.println(c + ": ");
			fe.printIterable(
					((BoundedPriorityQueue) alleBuchstaben.get(c)).ordered());
		});
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub

	}

}
