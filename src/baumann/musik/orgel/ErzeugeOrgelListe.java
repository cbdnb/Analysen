package baumann.musik.orgel;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

public class ErzeugeOrgelListe {

	private static final String IDN_TASTENINSTR = "040591034";
	private static final String IDN_ORGEL = "040438449";
	private static final String IDN_DREHORGEL = "041293924";
	private static final String IDN_ORCHESTRION = "041727142";
	private static final String IDN_SPIELUHR = "041822870";
	private static final String IDN_WELTE = "041895789";
	private static final String IDN_AUTOMAT = "041945182";
	private static final String IDN_FLOETENUHR = "042654459";
	private static final String IDN_KARUSSELORG = "957913737";
	private static final String IDN_HARFENUHR = "960225668";
	private static final String IDN_MECHORG = "96239324X";
	private static final String IDN_LICHTTON = "950090824";
	private static final String IDN_KINO = "041903293";
	private static final String IDN_EORGEL = "041518802";
	private static final String IDN_KLEINORG = "954168917";
	private static final String IDN_CLAVIORG = "994895577";
	private static final String IDN_THEATERORG = "042800072";
	private static final String IDN_FUNKORG = "991127277";
	private static final String IDN_HARMONIUM = "041591356";
	private static final String IDN_KLAVIER = "040309827";
	private static final String IDN_CEMBALO = "04009667X";
	private static final String IDN_KINDERKLAVIER = "950280984";

	private static final List<String> ECHTE_ORGELN = Arrays.asList(IDN_ORGEL,
			IDN_DREHORGEL, IDN_WELTE, IDN_FLOETENUHR, IDN_KARUSSELORG,
			IDN_MECHORG, IDN_LICHTTON, IDN_KINO, IDN_EORGEL, IDN_KLEINORG,
			IDN_CLAVIORG, IDN_THEATERORG, IDN_FUNKORG);

	public static final String IDN_FILE_NAME = "Orgel_idns.txt";
	public static final String FOLDER = "D:/Analysen/baumann/Musik/Orgel/";
	public static final String IDN_FILE_PATH = FOLDER + IDN_FILE_NAME;
	private static PrintWriter out;
	private static Pattern orgPattern = Pattern.compile("orgel",
			Pattern.CASE_INSENSITIVE);

	public static void main1(final String[] args) {
		final Record record = RecordUtils.readFromClip();
		System.out.println(check(record));

	}

	public static void main(final String[] args) throws IOException {
		final RecordReader matchingReader = RecordReader
				.getMatchingReader(Constants.Tu);
		final StringContains contOrgel = new StringContains(IDN_ORGEL);
		final StringContains contClavier = new StringContains(IDN_TASTENINSTR);
		Predicate<String> alle = contOrgel.or(contClavier);
		alle = alle.or(new StringContains(IDN_AUTOMAT));
		alle = alle.or(new StringContains(IDN_CLAVIORG));
		alle = alle.or(new StringContains(IDN_DREHORGEL));
		alle = alle.or(new StringContains(IDN_EORGEL));
		alle = alle.or(new StringContains(IDN_FLOETENUHR));
		alle = alle.or(new StringContains(IDN_FUNKORG));
		alle = alle.or(new StringContains(IDN_HARFENUHR));
		alle = alle.or(new StringContains(IDN_KARUSSELORG));
		alle = alle.or(new StringContains(IDN_KINO));
		alle = alle.or(new StringContains(IDN_KLEINORG));
		alle = alle.or(new StringContains(IDN_LICHTTON));
		alle = alle.or(new StringContains(IDN_MECHORG));
		alle = alle.or(new StringContains(IDN_ORCHESTRION));
		alle = alle.or(new StringContains(IDN_SPIELUHR));
		alle = alle.or(new StringContains(IDN_THEATERORG));
		alle = alle.or(new StringContains(IDN_WELTE));
		alle = alle.or(new StringContains(IDN_HARMONIUM));
		alle = alle.or(new StringContains(IDN_KLAVIER));
		alle = alle.or(new StringContains(IDN_CEMBALO));
		alle = alle.or(new StringContains(IDN_KINDERKLAVIER));

		matchingReader.setStreamFilter(alle);
		out = MyFileUtils.outputFile(IDN_FILE_PATH, false);

		matchingReader.forEach(r ->
		{
			if (check(r))
				out.println(r.getId());
		});

	}

	public static boolean check(final Record r) {
		if (!GNDUtils.isEntityType(r, "wim"))
			return false;
		final String name = GNDUtils.getSimpleName(r);
		if (name.startsWith("Musik für"))
			return false;
		if (name.startsWith("Werke$m"))
			return false;
		final List<String> formids = RecordUtils.getContentsOfFirstSubfields(r,
				"380", '9');
		if (formids.contains("1130357813"))// Zusammenstellung
			return false;
		if (!genauEinInstrument(r))
			return false;
		final String raw = StringUtils.unicodeComposition(r.getRawData());
		if (raw.contains("ufgeführte Musik"))
			return false;
		final boolean orgelIn382 = CollectionUtils.intersects(
				RecordUtils.getContentsOfAllSubfields(r, "382", '9'),
				ECHTE_ORGELN);

		final List<String> dollar_ms = RecordUtils.getContents(r, "[14]30",
				'm');
		if (dollar_ms.contains("Org") || dollar_ms.contains("Orgel")
				|| orgelIn382) {
			// nix zu tun

		} else { // anderweitig suchen
			final Matcher matcher = orgPattern.matcher(raw);
			if (!matcher.find())
				return false;
		}
		return true;

	}

	public static boolean genauEinInstrument(final Record r) {
		// Ensemble beteiligt?
		final Set<String> anzahlen_t = new HashSet<>(
				RecordUtils.getContentsOfAllSubfields(r, "382", 't'));
		// System.err.println(anzahlen_t);
		if (!anzahlen_t.isEmpty())
			return false;

		Integer anzDollar = anzahlAusDollar_s(r);
		if (anzDollar == null)
			anzDollar = 0;
		final int anzVerkn = anzahlVerknuepfteInstrumente(r);
		return 1 == anzDollar || 1 == anzVerkn;

	}

	/**
	 *
	 * @param record
	 *            nicht null
	 *
	 * @return Anzahl der Instrumente aus 382 $s, sofern ermittelbar, sonst
	 *         null. Wenn auch ein $t beteiligt ist, dann wird Integer.MAX_VALUE
	 *         zurückgegeben.
	 */
	public static Integer anzahlAusDollar_s(final Record record) {
		final Set<String> anzahlen = new HashSet<>(
				RecordUtils.getContentsOfAllSubfields(record, "382", 's'));
		// System.err.println(anzahlen);
		if (anzahlen.isEmpty())
			return null;
		if (anzahlen.size() > 1) {
			return null;
		}
		final String anzStr = ListUtils.getFirst(anzahlen);
		try {
			return Integer.parseInt(anzStr);
		} catch (final NumberFormatException e) {
			// System.err.println("Falsches Format: " + record.getId());
			return null;
		}
	}

	public static int anzahlVerknuepfteInstrumente(final Record r) {
		final Set<String> anzahlen = new HashSet<>(
				RecordUtils.getContentsOfAllSubfields(r, "382", '9'));
		return anzahlen.size();
	}

}
