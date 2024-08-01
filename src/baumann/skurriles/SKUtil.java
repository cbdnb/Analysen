/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.utils.WaehrungsRechner;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class SKUtil {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) {

		final WaehrungsRechner rechner = new WaehrungsRechner();

		final Scanner scanner = new Scanner(System.in);

		System.out.println("Record kopieren");
		System.out.println("Buchstabe eingeben und Enter drücken");
		while (scanner.hasNext()) {

			final String s = scanner.next();
			final Record record = RecordUtils.readFromClip();
			final List<String> infos = RecordUtils
					.getContentsOfAllSubfields(record, "2000", 'f');

			System.out.println(rechner.findePreis(infos));

			System.out.println("Record kopieren");
			System.out.println("Buchstabe eingeben und Enter drücken");
		}

	}

	/**
	 * @param infos
	 * @throws NumberFormatException
	 */
	static void de_ch(final List<String> infos) throws NumberFormatException {
		final String zahl = " +(\\d+\\.\\d\\d)";
		final Pattern preisPatternDE = Pattern.compile("EUR" + zahl);
		final Pattern preisPatternCH = Pattern.compile("CHF" + zahl);

		double preis = 0;
		for (final String info : infos) {
			final Matcher deMatcher = preisPatternDE.matcher(info);
			if (deMatcher.find()) {
				final String strDE = deMatcher.group(1);
				preis = Double.parseDouble(strDE);
				System.out.println(preis);
				return;
			}
			final Matcher chMatcher = preisPatternCH.matcher(info);
			if (chMatcher.find()) {
				final String strCH = chMatcher.group(1);
				final double franken2Euro = 1.0566;
				preis = Double.parseDouble(strCH) * franken2Euro;
				System.out.println(preis);
			}
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	static Set<String> loadStopwords() throws IOException {
		final Set<String> stopWords = new HashSet<>();
		final List<String> de = StringUtils.readLinesFromFile(
				"D:/Analysen/baumann/skurriles/stop_words_de_complete.txt");
		stopWords.addAll(de);
		final List<String> en = StringUtils.readLinesFromFile(
				"D:/Analysen/baumann/skurriles/stop_words_en_complete.txt");
		stopWords.addAll(en);
		return stopWords;
	}

	public static int zahlVerschiedenerZeichen(String s) {
		if (s != null)
			s = s.toLowerCase();
		final HashSet<Character> hashSet = new HashSet<>(
				StringUtils.string2charList(s));
		// System.err.println(hashSet);
		return hashSet.size();
	}

	/**
	 *
	 * @param s
	 *            auch null
	 * @return [a-z]
	 */
	public static boolean istPangramm(String s) {
		if (s != null)
			s = s.toLowerCase();
		final HashSet<Character> hashSet = new HashSet<>(
				StringUtils.string2charList(s));
		return hashSet.containsAll(normaleBuchstaben);
	}

	/**
	 *
	 * @param s
	 *            auch null
	 * @return
	 */
	public static int zahlVerschiedenerSonderzeichen(String s) {
		if (s != null)
			s = s.toLowerCase();
		final HashSet<Character> hashSet = new HashSet<>(
				StringUtils.string2charList(s));
		hashSet.removeAll(normaleZeichen);
		// System.err.println(hashSet);
		return hashSet.size();
	}

	/**
	 * aeiouyäöüáàâéèêíìêóòôúùûØāī.
	 */
	public static final List<Character> vowels = StringUtils.string2charList(
			"aeiouy" + "äöü" + "áàâåãāǎąăạæằȧ" + "éèêëęėẹěēẽ" + "íìîīïịı"
					+ "óòôõøœőōọốộờ" + "úùûŭůūűųưứ" + "ýÿ".toLowerCase());

	/**
	 * ZKVPÜÄÖẞJXQY nach Wikipedia deutsch.
	 */
	public static final List<Character> selteneBuchstaben = StringUtils
			.string2charList("ZKPÜÄÖẞJXY._".toLowerCase());

	public static final List<Character> xyz = StringUtils
			.string2charList("XYZýÿźžżẓ".toLowerCase());

	/**
	 * "$[a-z1-0]äöü, ()"
	 */
	public static final List<Character> normaleZeichen = StringUtils
			.string2charList("abcdefghijklmnopqrstuvwxyzäöü1234567890 ,$()"
					.toLowerCase());

	/**
	 * "[a-z]"
	 */
	public static final List<Character> normaleBuchstaben = StringUtils
			.string2charList("abcdefghijklmnopqrstuvwxyz".toLowerCase());

	/**
	 * mit Y!
	 */
	public static final List<Character> umlaute = StringUtils
			.string2charList("äöüy".toLowerCase());

	public static int umlautZahl(final String s) {
		final List<Character> list = StringUtils
				.string2charList(s.toLowerCase());
		list.retainAll(umlaute);
		return list.size();
	}

	public static int xyzZahl(final String s) {
		final List<Character> list = StringUtils
				.string2charList(s.toLowerCase());
		list.retainAll(xyz);
		return list.size();
	}

	public static boolean onlyOneVowel(final String s) {
		final List<Character> list = StringUtils
				.string2charList(s.toLowerCase());
		final Set<Character> set = new HashSet<>(list);
		set.retainAll(vowels);
		return set.size() == 1;
	}

	public static int vowelCount(final String s) {
		final List<Character> list = StringUtils
				.string2charList(s.toLowerCase());
		list.retainAll(vowels);
		return list.size();
	}

	public static int characterCount(final String s, final char c) {
		final List<Character> list = StringUtils
				.string2charList(s.toLowerCase());
		return Collections.frequency(list, c);
	}

	/**
	 *
	 * @param s
	 *            auch null
	 * @return Histogramm von allen Buchstaben
	 */
	public static Frequency<Character> getCharacterFrequency(final String s) {
		final List<Character> list = StringUtils
				.string2charList(s.toLowerCase());
		return ListUtils.toFrequency(list);
	}

	/**
	 * Sprachen mit verwertbaren Zeichen.
	 */
	public static final List<String> languages = Arrays.asList("eng", "fre",
			"ger", "ita", "spa", "por", "dut");

	/**
	 *
	 * @param record
	 *            nicht null
	 * @return Sprache == "eng", "fre","ger", "ita", "spa", "por", "dut"
	 */
	public static boolean spracheErlaubt(final Record record) {
		final List<String> sprachen = BibRecUtils.getLanguagesOfText(record);
		sprachen.retainAll(languages);
		return !sprachen.isEmpty();
	}

	/**
	 *
	 * @param s
	 *            auch null
	 * @return enthält nur lateinische Buchstaben (<U+00FF)
	 */
	public static boolean spracheErlaubt(final String s) {
		final List<Character> list = StringUtils.string2charList(s);
		return list.stream().noneMatch(c -> c > '\u00ff');
	}

	/**
	 *
	 * @param s
	 *            beliebig. Wird auf Unicode-Composition, Kleinbuchstaben und
	 *            ohne Leerzeichen oder Satzzeichen normiert. Akzente werden,
	 *            soweit sinnvoll, entfernt.
	 * @return false, wenn null oder leer. True, wenn Palindrom.
	 */
	public static boolean isPalindrome(String s) {
		if (s == null || s.isEmpty())
			return false;
		s = getRelevantChars(s);
		// System.err.println(s);
		return isPalindrome(s.toCharArray(), 0, s.length() - 1);
	}

	/**
	 * @param s
	 *            auch null, dann ""
	 * @return alle Zeichen, die keine Leerzeichen oder Interpunktionszeichen
	 *         sind
	 */
	public static String getRelevantChars(String s) {
		if (s == null)
			return "";
		s = StringUtils.unicodeComposition(s);
		s = s.toLowerCase();
		s = s.replaceAll("\\s", "");
		s = s.replaceAll("\\p{Punct}", "");
		s = s.replaceAll("–", "");
		s = s.replaceAll("’", "");
		s = StringUtils.removeAccents(s);
		return s;
	}

	private static boolean isPalindrome(final char[] arr, final int start,
			final int end) {
		if (end <= start)
			return true;
		return arr[start] == arr[end] && isPalindrome(arr, start + 1, end - 1);
	}

	static boolean isPalindrome(final int i) {
		return isPalindrome(Integer.toString(i));
	}

}
