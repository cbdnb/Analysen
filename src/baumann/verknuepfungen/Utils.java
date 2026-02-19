package baumann.verknuepfungen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * SWW mit Level 1/z und Tbs s.
 */
public class Utils {

	static final String GUELTIGE_SWW_NAME = "gueltige.out";
	static final String PATH = "D:/Analysen/baumann/zulaessige/";
	static final String GUELTIGE_PATH = PATH + GUELTIGE_SWW_NAME;
	static HashSet<Integer> gueltigeSWW = null;
	static HashSet<Integer> bestandsgliederung = null;
	static HashSet<Integer> dbsmKlassifik = null;

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		final Record record = RecordUtils.readFromClip();
		System.out.println(isDBSM(record));
	}

	public static void load() throws ClassNotFoundException, IOException {
		loadBestandsgliederung();
		loadDbsmKlassifik();
		loadGueltigeSWW();
	}

	public static void loadBestandsgliederung() throws IOException {
		if (bestandsgliederung == null) {
			bestandsgliederung = new HashSet<>();
			final BufferedReader bufferedReader = new BufferedReader(
					new FileReader(PATH + "DBSM-Bestandsgliederung.txt"));
			bufferedReader.lines().forEach(line ->
			{
				final int idn = IDNUtils.idn2int(line);
				if (idn != -1)
					bestandsgliederung.add(idn);
			});
			MyFileUtils.safeClose(bufferedReader);
		}
	}

	public static void loadDbsmKlassifik() throws IOException {
		if (dbsmKlassifik == null) {
			dbsmKlassifik = new HashSet<>();
			final BufferedReader bufferedReader = new BufferedReader(
					new FileReader(PATH + "DBSMKlassIDN.txt"));
			bufferedReader.lines().forEach(line ->
			{
				final int idn = IDNUtils.idn2int(line);
				if (idn != -1)
					dbsmKlassifik.add(idn);
			});
			MyFileUtils.safeClose(bufferedReader);
		}
	}

	public static void loadGueltigeSWW()
			throws IOException, ClassNotFoundException {
		if (gueltigeSWW == null)
			gueltigeSWW = CollectionUtils.loadHashSet(GUELTIGE_PATH);
	}

	public static void makeGueltigeSWW() throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);
		final Set<Integer> gueltigeSWW = new HashSet<>();
		reader.forEach(rec ->
		{
			if (!GNDUtils.isTeilbestandIE(rec))
				return;
			if (GNDUtils.isUseCombination(rec))
				return;
			final int level = GNDUtils.getLevel(rec);
			if (level > 1)
				return;
			final int idn = IDNUtils.idn2int(rec.getId());
			if (idn != -1)
				gueltigeSWW.add(idn);
		});
		CollectionUtils.save(gueltigeSWW, GUELTIGE_PATH);
	}

	public static boolean isDBSM(final Record record)
			throws ClassNotFoundException, IOException {
		// shortcut:
		final String raw = record.getRawData();
		if (raw.contains("DBSM"))
			return true;

		System.err.println("1");

		final List<String> subs = RecordUtils.getContentsOfAllSubfields(record,
				"0600", 'a');
		if (subs.contains("yy"))
			return true;

		System.err.println(2);

		if (RecordUtils.containsField(record, "4105")) // Zugehörigkeit zu einer
														// Sammlung
			return true;

		if (RecordUtils.containsField(record, "4227")) // Herkunftsangaben
			return true;

		if (RecordUtils.containsField(record, "4232")) // Redaktionelle
														// Bemerkungen
			return true;

		System.err.println(3);

		final List<String> dollarBs = RecordUtils
				.getContentsOfAllSubfields(record, "5550", 'b');
		for (final String dollarB : dollarBs) {
			if (dollarB.startsWith("DBSM"))
				return true;
		}

		System.err.println(4);

		// Gestaltungsmerkmale auf bibliografischer Ebene
		if (!RecordUtils.getLines(record, "559.").isEmpty())
			return true;

		System.err.println(5);

		// die aufwendigeren Tests:
		loadDbsmKlassifik();
		List<String> idns = RecordUtils.getContentsOfAllSubfields(record,
				"5320", '9');
		List<Integer> intIDS = IDNUtils.idns2ints(idns);
		if (CollectionUtils.intersects(intIDS, dbsmKlassifik))
			return true;

		System.err.println("q");

		loadBestandsgliederung();
		// Aufstellung innerhalb / Zugehörigkeit zu einer Sammlung
		idns = RecordUtils.getContentsOfAllSubfields(record, "6710", '9');
		intIDS = IDNUtils.idns2ints(idns);
		if (CollectionUtils.intersects(intIDS, bestandsgliederung))
			return true;

		return false;
	}

}
