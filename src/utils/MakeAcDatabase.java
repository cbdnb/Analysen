package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;

/**
 * Speichert zu den idns übergeordneter Datensätze (Ac ...), den
 * Erschließungslevel, die Sachgruppe und den Verlag.
 *
 * @author baumann
 *
 */
public class MakeAcDatabase extends DownloadWorker {

	public static final String FOLDER = "D:/Normdaten/";
	public static final String FILE_AC_TO_LEVEL = FOLDER + "Ac2Level.out";
	public static final String FILE_AC_TO_PUB = FOLDER + "Ac2Verlag.out";
	public static final String FILE_AC_TO_TITLE = FOLDER + "Ac2Titel.out";

	private static HashMap<String, Pair<String, TIEFE>> MAP_AC_LEVEL = new HashMap<>();
	private static HashMap<String, String> MAP_AC_TITLE = new HashMap<>();
	private static HashMap<String, String> MAP_AC_PUB = new HashMap<>();

	private final StatusAndCodeFilter acFilter = StatusAndCodeFilter
			.filterAZc();

	@Override
	protected void processRecord(final Record record) {

		if (!acFilter.test(record))
			return;

		final String dhs = SGUtils.getFullDHSString(record, null);
		if (dhs == null)
			return;

		final String title = BibRecUtils.getMainTitle(record);
		final String pub = BibRecUtils.getVerlageUndOrte(record);

		final TIEFE status = SubjectUtils.getErschliessungsTiefe(record);
		final Pair<String, TIEFE> pair = new Pair<String, SubjectUtils.TIEFE>(
				dhs, status);
		final String id = record.getId();
		MAP_AC_LEVEL.put(id, pair);
		MAP_AC_PUB.put(id, pub);
		MAP_AC_TITLE.put(id, title);

		System.err.println(id + " " + RecordUtils.getDatatype(record));

	}

	public static void main(final String[] args) throws IOException {

		final MakeAcDatabase ac2Level = new MakeAcDatabase();

		Predicate<String> titleFilter = new ContainsTag("0500", '0', "Ac",
				BibTagDB.getDB());
		titleFilter = titleFilter
				.or(new ContainsTag("0500", '0', "Zc", BibTagDB.getDB()));
		titleFilter = titleFilter
				.or(new ContainsTag("0500", '0', "AE", BibTagDB.getDB()));
		titleFilter = titleFilter
				.or(new ContainsTag("0500", '0', "Abvz", BibTagDB.getDB()));
		titleFilter = titleFilter
				.or(new ContainsTag("0500", '0', "Advz", BibTagDB.getDB()));

		ac2Level.setStreamFilter(titleFilter);
		ac2Level.gzipSettings();

		System.err.println("Titeldaten flöhen:");

		try {
			ac2Level.processFile(Constants.GND_TITEL_GESAMT_Z);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(FILE_AC_TO_LEVEL));
		out.writeObject(MAP_AC_LEVEL);
		MyFileUtils.safeClose(out);

		out = new ObjectOutputStream(new FileOutputStream(FILE_AC_TO_PUB));
		out.writeObject(MAP_AC_PUB);
		MyFileUtils.safeClose(out);

		out = new ObjectOutputStream(new FileOutputStream(FILE_AC_TO_TITLE));
		out.writeObject(MAP_AC_TITLE);
		MyFileUtils.safeClose(out);

	}

}
