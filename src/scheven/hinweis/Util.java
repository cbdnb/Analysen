package scheven.hinweis;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import utils.DB.GND_DB_UTIL;
import utils.DB.GUI;

public class Util {

	public static final String FILE_NAME = "download_ohne_s_p.txt";
	public static final String FOLDER = "D:/Analysen/scheven/Hinweis/";
	public static final String DOWNLOAD_FILE = FOLDER + FILE_NAME;

	public static void main(final String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Körperschaft zu IDN. Muss extern erstellt werden, z.B. aus der
	 * GND-Datenbank. Laden kann etwas dauern-
	 */
	static HashMap<Integer, Integer> tb2tgId = null;
	/**
	 * Geografikum zu IDN. Muss extern erstellt werden, z.B. aus der
	 * GND-Datenbank. Laden kann etwas dauern-
	 */
	static HashMap<Integer, String> tgId2name = null;

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
	 * Extrahiert IDN und Expansion. Die Expansion endet auf " [T#1]"!!
	 *
	 * @param line260
	 *            Im Hinweissatz einer der relationierten (zu verwendenden)
	 *            Datensätze.
	 * @return Entweder (IDN, Expansion) oder (Zeitschlagwort, null) oder (null,
	 *         null).
	 */
	public static Pair<String, String> extractIdnExpansion(final Line line260) {
		final String sub9 = line260.getIdnRelated();
		final String dollarA = SubfieldUtils.getContentOfFirstSubfield(line260,
				'a'); // Nur für Zeitschlagwörter.
		if (sub9 != null) {
			return new Pair<>(sub9, line260.getExpansion());
		} else {
			return new Pair<>(dollarA, null);
		}
	}

	/**
	 * Extrahiert die Paare mit IDN und Expansion aus allen relationierten DSS
	 * eines Hinweissatzes.
	 *
	 * @param record
	 *            nicht null
	 * @return [(idn1, expansion1), ...]. Die Expansionen enden auf " [T#1]"!!
	 *         Bei Zeitschlagwörtern ist das Tupel (zeitSW, null). Kann auch
	 *         leer sein, wenn der Hinweissatz keine relationierten DSS hat.
	 */
	public static Set<Pair<String, String>> getIDNExpansionPairs(
			final Record record) {
		final Set<Pair<String, String>> pairs = new LinkedHashSet<>();

		final List<Line> hinweisLines = GNDUtils.getHinweisLines(record);
		if (hinweisLines.isEmpty())
			return pairs;

		hinweisLines.forEach(line ->
		{
			pairs.add(extractIdnExpansion(line));
		});
		return pairs;
	}

	/**
	 *
	 * @param idnExpansionPair
	 *            nicht null, (idn, expansion)
	 * @return liefert b aus der Expansion "Südtiroler Landesarchiv [Tb1]", z
	 *         bei Zeit-SWW; dort ist expansion == null
	 */
	public static char getIndikator(
			final Pair<String, String> idnExpansionPair) {
		final String fullExpansion = idnExpansionPair.second;
		if (fullExpansion == null) {
			return 'z';
		}
		return Util.getIndikator(fullExpansion);
	}

	/**
	 * @param expansion
	 *            so etwas wie "Südtiroler Landesarchiv [Tb1]"
	 * @return
	 */
	public static char getIndikator(final String expansion) {
		final int POS_INDIKATOR = -3; // Position des Indikators in der
										// Expansion, z.B. "Südtiroler
										// Landesarchiv [Tb1]" von hinten
										// gezählt. -3 = 'b'
		final int expanLength = expansion.length();
		return expansion.charAt(expanLength + POS_INDIKATOR);
	}

	/**
	 *
	 * @param record
	 *            nicht null
	 * @return Kombination von IDNs oder Zeit-SW, für die dieser
	 *         Hinweisdatensatz steht.
	 */
	public static Set<String> getIdnKombis(final Record record) {
		final Set<Pair<String, String>> pairs = getIDNExpansionPairs(record);
		return new LinkedHashSet<>((pairs.stream().map(Pair::getFirst)
				.collect(Collectors.toList())));
	}

	/**
	 *
	 * @param idnExpansionPair
	 *            nicht null, (idn, expansion)
	 * @return schneidet bei expansion " [T#1]" ab. null bei Zeit-SWW, dort ist
	 *         expansion == null
	 */
	public static String getName(final Pair<String, String> idnExpansionPair) {
		final String fullExpansion = idnExpansionPair.second;
		if (fullExpansion == null) {
			return null;
		}
		return Util.getNameFromExpansion(fullExpansion);
	}

	/**
	 * @param expansion
	 *            z.B. "Südtiroler Landesarchiv [Tb1]"
	 * @return schneidet bei expansion " [Tb1]" ab.
	 */
	public static String getNameFromExpansion(final String expansion) {
		final int expanLength = expansion.length();
		final int LEN_SUFFIX = " [Tp1]".length();
		return expansion.substring(0, expanLength - LEN_SUFFIX);
	}

	/**
	 * Die Signatur ist die Liste der Indikatoren (Typen) der Hinweisdatensätze.
	 *
	 * @param record
	 *            nicht null
	 * @return Die Typen der SW-Kombination, z.B. [p, g, s]
	 */
	public static List<Character> getSignature(final Record record) {
		final Set<Pair<String, String>> pairs = getIDNExpansionPairs(record);
		return getSignature(pairs);
	}

	/**
	 * Die Signatur ist die Liste der Indikatoren (Typen) der Hinweisdatensätze.
	 *
	 * @param kombi
	 *            nicht null
	 * @return Die Typen der SW-Kombination, z.B. [p, g, s]
	 */
	public static List<Character> getSignature(
			final Set<Pair<String, String>> kombi) {
		return FilterUtils.map(kombi, pair -> getIndikator(pair));
	}

	/**
	 * Hilfsfunktion, die eine sehr kleine Map liefert. Die enhält in der Regel
	 *
	 *
	 * <li>'s' -> Alle Sachschlagwörter (idn, Expansion),
	 * <li>'p' -> Alle Personen (idn, Expansion),
	 * <li>'g' -> Alle Geografika (idn, Expansion),
	 * <li>'z' -> Alle Zeitschlagwörter
	 *
	 * @param idnExpansionKombi
	 *            nicht null, [(idn1, expansion1), ...]. Für Zeitschlagwörter
	 *            ist das Tupel: (zeitSW, null).
	 * @return z.B. 's' -> Alle Sachschlagwörter, 'p' -> Alle Personen, 'g' ->
	 *         Alle Geografika, 'z' -> Alle Zeitschlagwörter
	 *
	 */
	public static Multimap<Character, Pair<String, String>> getSignatureMap(
			final Collection<Pair<String, String>> idnExpansionKombi) {
		final Multimap<Character, Pair<String, String>> map = new ListMultimap<>();
		idnExpansionKombi.forEach(idnExpansionPair -> map
				.add(getIndikator(idnExpansionPair), idnExpansionPair));
		return map;
	}

	/*
	 * Hilfsfunktionen zur Verarbeitung der Unterfelder.
	 */

	/**
	 *
	 * @param line
	 *            nicht null
	 * @return Nur relevante Unterfelder (ohne $v..). Alle verbunden durch
	 *         Komma.
	 */
	static String mitKomma(final Line line) {
		final List<Subfield> relevantsubs = SubfieldUtils
				.getNamingRelevantSubfields(line);
		return relevantsubs.stream().map(Subfield::getContent)
				.collect(Collectors.joining(", "));
	}

	/**
	 *
	 * @param line
	 *            nicht null
	 * @return Nur relevante Unterfelder (ohne $v..). Alle verbunden durch
	 *         Blank.
	 */
	static String mitBlank(final Line line) {
		final List<Subfield> relevantsubs = SubfieldUtils
				.getNamingRelevantSubfields(line);
		return relevantsubs.stream().map(Subfield::getContent)
				.collect(Collectors.joining(" "));
	}

	/**
	 *
	 * @param line
	 *            nicht null
	 * @return Nur relevante Unterfelder (ohne $v..), ohne $g. Alle verbunden
	 *         durch Komma.
	 */
	static String ohneDollarGmitKomma(final Line line) {
		final List<Subfield> relevantsubs = SubfieldUtils
				.getNamingRelevantSubfields(line);
		return relevantsubs.stream()
				.filter(sub -> sub.getIndicator().indicatorChar != 'g')
				.map(Subfield::getContent).collect(Collectors.joining(", "));
	}

	/**
	 *
	 * @param line
	 *            nicht null
	 * @return Nur relevante Unterfelder (ohne $v..), ohne $g. Alle verbunden
	 *         durch Blank.
	 */
	static String ohneDollarGmitBlank(final Line line) {
		final List<Subfield> relevantsubs = SubfieldUtils
				.getNamingRelevantSubfields(line);
		return relevantsubs.stream()
				.filter(sub -> sub.getIndicator().indicatorChar != 'g')
				.map(Subfield::getContent).collect(Collectors.joining(" "));
	}

}
