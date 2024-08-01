/**
 *
 */
package weigand;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Stemmer {

	public static final boolean INSTRUMENTE = false;
	private static TreeSet<String> stemmata;
	private static Collection<String> descriptors;
	private static Collection<String> ausnahmen = Arrays.asList("Blues",
			"Choräle", "Choros", "Concertone", "Tänze", "Dreher", "Friauler",
			"Gesänge", "Glees", "Jodler", "Konzertsätze", "Kuhreigen", "Kyrie",
			"Ländler", "Zwiefacher", "Liriche", "Lullabies", "Märsche",
			"Masques", "Miserere", "Nachtänze", "Pisen", "Piven",
			"Quartettsätze", "Quintettsätze", "Reigen", "Rheinländer",
			"Rutscher", "Sätze", "Schwerttänze", "Sonatensätze", "Stückchen",
			"Walzer");
	private static NavigableSet<String> descendingStemmata;

	public static boolean isVowel(final char c) {

		switch (c) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
		case 'y':
		case 'A':
		case 'E':
		case 'I':
		case 'O':
		case 'U':
		case 'Y':
			return true;
		}
		return false;
	}

	public static boolean isMultiWord(final String s) {
		return s.trim().matches(".*\\s.*");
	}

	public static String stem(final String s) {
		// sind alle Singular
		if (INSTRUMENTE)
			return s;
		if (ausnahmen.contains(s))
			return s;
		if (isMultiWord(s))
			return s;
		if (s.equals("Antiphonen"))
			return "Antiphon";
		if (s.equals("Battaglien"))
			return "Battagli";
		if (s.equals("Bergamasken"))
			return "Bergamask";
		if (s.equals("Bicinien"))
			return "Bicini";
		if (s.equals("Bossa novas"))
			return "Bossa nova";
		if (s.equals("Kassationen"))
			return "Kassation";
		if (s.equals("Castle walks"))
			return "Castle walk";
		if (s.equals("Choralbearbeitungen"))
			return "Choralbearbeitung";
		if (s.equals("Choralpartiten"))
			return "Choralpartit";
		if (s.equals("Church sonatas"))
			return "Church sonata";
		if (s.equals("Kompositionen"))
			return "Komposition";
		if (s.equals("Kontertänze"))
			return "Kontert";
		if (s.equals("Country dances"))
			return "Country dance";
		if (s.equals("Dirges"))
			return "Dirge";
		if (s.equals("Fantasy pieces"))
			return "Fantasy piece";
		if (s.equals("Gradualien"))
			return "Gradual";
		if (s.equals("Graves"))
			return "Grave";
		if (s.equals("Intermedien"))
			return "Intermedi";
		if (s.equals("Litaneien"))
			return "Litanei";
		if (s.equals("Masques"))
			return "Masque";
		if (s.equals("Mazurken"))
			return "Mazurk";
		if (s.equals("Merengues"))
			return "Merengue";
		if (s.equals("Morceaux"))
			return "Morceau";
		if (s.equals("Morisken"))
			return "Morisk";
		if (s.equals("Nocturnes"))
			return "Nocturne";
		if (s.equals("Partiten"))
			return "Partit";
		if (s.equals("Passacaglien"))
			return "Passacagli";
		if (s.equals("Pieces"))
			return "Piece";
		if (s.equals("Pièces"))
			return "Pièce";
		if (s.equals("Potpourris"))
			return "Potpourri";
		if (s.equals("Ragtimes"))
			return "Ragtime";
		if (s.equals("Sainetes"))
			return "Sainete";
		if (s.equals("Sainetes"))
			return "Sainete";
		if (s.equals("Sinfonietten"))
			return "Sinfoniett";
		if (s.equals("Solfeggien"))
			return "Solfeggi";
		if (s.equals("Squaredances"))
			return "Squaredance";
		if (s.equals("Strathspeys"))
			return "Strathspey";
		if (s.equals("Tokkaten"))
			return "Tokkat";
		if (s.equals("Tokkatinen"))
			return "Tokkatin";
		if (s.equals("Tombeaux"))
			return "Tombeau";
		if (s.equals("Trenchmores"))
			return "Trenchmore";
		if (s.equals("Tricinien"))
			return "Tricini";
		if (s.equals("Übungen"))
			return "Übung";
		if (s.equals("Vaudevilles"))
			return "Vaudeville";
		if (s.equals("Vivaces"))
			return "Vivace";
		if (s.equals("Voluntaries"))
			return "Voluntar";

		if (s.endsWith("ionen")) {
			return chopTwice(s);
		}
		if (s.endsWith("orien")) {
			return chopTwice(s);
		}
		if (s.endsWith("musiken")) {
			return chopTwice(s);
		}
		if (s.endsWith("ludien")) {
			return chopTwice(s);
		}

		if (s.endsWith("e")) {
			return StringUtils.chop(s);
		}
		if (s.endsWith("i")) {
			return StringUtils.chop(s);
		}
		if (s.endsWith("en")) {
			return StringUtils.chop(s);
		}
		if (s.endsWith("er")) {
			return chopTwice(s);
		}
		if (s.endsWith("es")) {
			return chopTwice(s);
		}
		if (s.endsWith("as")) {
			return StringUtils.chop(s);
		}
		if (s.endsWith("os")) {
			return StringUtils.chop(s);
		}
		if (s.endsWith("ys")) {
			return StringUtils.chop(s);
		}
		if (s.endsWith("n") || s.endsWith("s")) {
			final char c = StringUtils.penultimateChar(s);
			if (!isVowel(c))
				return StringUtils.chop(s);
		}
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	public static String chopTwice(final String s) {
		return StringUtils.chop(StringUtils.chop(s));
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		loadDescriptors();

		final Multimap<String, String> stemma2idns = new ListMultimap<>();
		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Normdaten/DNBGND_u.dat.gz");
		reader.forEach(record ->
		{
			final Collection<String> list = foundDescriptors(record);
			list.forEach(stemma -> stemma2idns.add(stemma, record.getId()));
		});
		stemmata.forEach(stemma ->
		{
			System.out.println(StringUtils.concatenate("\t", stemma,
					stemma2idns.getNullSafe(stemma).size(),
					stemma2idns.get(stemma)));
		});

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main2(final String[] args) throws IOException {
		loadDescriptors();

		System.out.println("Record eingeben");
		final String tt = StringUtils.readConsole();

		final Record record = RecordUtils.readFromClip();

		final Pair<String, String> pair = foundDescriptor(record);
		if (pair != null) {
			System.out.println(pair.first);
			System.out.println(pair.second);
		} else
			System.out.println("nix");
	}

	/**
	 *
	 * @param record
	 *            nicht null
	 * @return Paar aus Stemma und Titel
	 */
	public static Pair<String, String> foundDescriptor(final Record record) {
		final List<String> ents = GNDUtils.getEntityTypes(record);
		if (!ents.contains("wim"))
			return null;
		String titel = "";
		try {
			titel = GNDUtils.getNameOfRecord(record);
		} catch (final IllegalStateException e) {
			return null;
		}

		for (final String stemma : descendingStemmata) {

			if (StringUtils.contains(titel, stemma, false)) {
				return new Pair<String, String>(stemma, titel);
			}
		}
		return null;
	}

	/**
	 *
	 * @param record
	 *            nicht null
	 * @return Liste der gefundenen Stemmata
	 */
	public static Collection<String> foundDescriptors(final Record record) {
		final List<String> ents = GNDUtils.getEntityTypes(record);
		if (!ents.contains("wim"))
			return Collections.emptyList();
		String titel = "";
		try {
			titel = GNDUtils.getNameOfRecord(record);
		} catch (final IllegalStateException e) {
			return Collections.emptyList();
		}

		final List<String> list = new ArrayList<>();

		for (final String stemma : descendingStemmata) {

			if (StringUtils.contains(titel, stemma, false)) {
				list.add(stemma);
			}
		}
		return list;
	}

	/**
	 * @throws IOException
	 *
	 */
	private static void loadDescriptors() throws IOException {
		final Path path = FileSystems.getDefault()
				.getPath("D:/Normdaten/Gattungen.txt");
		descriptors = Files.readAllLines(path);
		stemmata = new TreeSet<>();

		// Verkürzen und vorne einfügen:
		descriptors.forEach(line ->
		{
			final String lineTrim = line.trim();
			if (!lineTrim.isEmpty()) {
				final String stemmma = stem(lineTrim);
				stemmata.add(stemmma);
				// System.err.println(lineTrim + " / " + stemmma);
			}
		});
		descendingStemmata = stemmata.descendingSet();

	}
}
