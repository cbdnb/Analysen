/**
 *
 */
package baumann.alteSWW;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.basics.tries.TrieMultimap;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 */
public class GND_DB2 {

	public static final String FOLDER = "D:/analysen/baumann/ttt";
	public static final String NAME_2_IDNS = FOLDER + "name2idns.out";
	public static final String IDN_2_RECORD = FOLDER + "idn2record.out";
	public static final String VWW_2_IDNS = FOLDER + "vww2idns.out";
	public static final String F_2_IDNS = FOLDER + "f2idns.out";

	static TrieMultimap<String> name2idns = new TrieMultimap<>();
	static TrieMultimap<String> vww2idns = new TrieMultimap<>();
	static Map<String, MyRecord> idn2record = new HashMap<>();
	static Multimap<String, String> f2idns = new ListMultimap<>();

	public static void save() {
		System.err.println("save");
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(NAME_2_IDNS));
			out.writeObject(name2idns);
			FileUtils.safeClose(out);

			out = new ObjectOutputStream(new FileOutputStream(IDN_2_RECORD));
			out.writeObject(idn2record);
			FileUtils.safeClose(out);

			out = new ObjectOutputStream(new FileOutputStream(VWW_2_IDNS));
			out.writeObject(vww2idns);
			FileUtils.safeClose(out);

			out = new ObjectOutputStream(new FileOutputStream(F_2_IDNS));
			out.writeObject(f2idns);
			FileUtils.safeClose(out);
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void bearbeiteStream(final Stream<Record> stream,
			final boolean trunkSuche) {
		final AtomicInteger ai = new AtomicInteger();
		stream.forEach(record ->
		{
			final int level = GNDUtils.getLevel(record);
			if (level > 1)
				return;

			if (!GNDUtils.isTeilbestandIE(record))
				return;
			if (!GNDUtils.isNutzungIE(record))
				return;

			String name;
			try {
				name = RDAFormatter.getRDAHeading(record);
			} catch (final IllFormattedLineException e) {
				return;
			}
			ai.incrementAndGet();

			final String idn = record.getId();
			final List<String> sys = GNDUtils.getGNDClassifications(record);
			final String satzart = RecordUtils.getDatatype(record);
			final MyRecord myRecord = new MyRecord();
			myRecord.idn = idn;
			myRecord.satzart = satzart;
			myRecord.name = StringUtils.unicodeComposition(name);
			myRecord.systs.addAll(sys);
			ai.incrementAndGet();
			if (ai.get() % 50_000 == 0)
				System.err.println(ai + ": " + myRecord);
			// System.err.println(myRecord);
			idn2record.put(idn, myRecord);
			final String nameDecomposed = StringUtils
					.unicodeDecomposition(name);
			if (trunkSuche)
				name2idns.addValue(nameDecomposed, idn);
			else
				f2idns.add(nameDecomposed, idn);
			// Verweisungen:
			final ArrayList<Line> vww = GNDUtils.getLines4XX(record);
			final ArrayList<Line> vwfilter = FilterUtils.map(vww,
					GNDUtils::remove_4_v);
			final HashSet<Line> vwSet = new HashSet<>(vwfilter);
			if (!vwSet.isEmpty()) {
				final RDAFormatter rdaFormatter = new RDAFormatter(record);
				vwSet.forEach(vw ->
				{
					try {
						final String vwS = rdaFormatter.format(vw);
						final String vwDecomposed = StringUtils
								.unicodeDecomposition(vwS);
						if (trunkSuche)
							vww2idns.addValue(vwDecomposed, idn);
						else
							f2idns.add(vwDecomposed, idn);
					} catch (final IllFormattedLineException e) {
					}
				});
			}

		});

	}

	public static void create() {
		System.err.println("create");
		try {
			System.err.println("Ts");
			RecordReader reader = RecordReader
					.getMatchingReader(Constants.Ts);
			Stream<Record> stream = reader.stream();
			bearbeiteStream(stream, true);

			System.err.println("Tb");
			reader = RecordReader.getMatchingReader(Constants.Tb);
			reader.setStreamFilter(
					new ContainsTag("005", '0', "Tb1", GNDTagDB.getDB()));
			stream = reader.stream();
			bearbeiteStream(stream, false);

			System.err.println("Tg");
			reader = RecordReader.getMatchingReader(Constants.Tg);
			stream = reader.stream();
			bearbeiteStream(stream, true);

			// System.err.println("Tu");
			// reader = RecordReader.getMatchingReader(Constants.GND_WERKE);
			// reader.setStreamFilter(
			// new ContainsTag("005", '0', "Tu1", GNDTagDB.getDB()));
			// stream = reader.stream();
			// bearbeiteStream(stream, false);

		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("create fertig");

	}

	@SuppressWarnings("unchecked")
	public static void load() {
		System.err.println("laden");
		System.err.println(TimeUtils.getActualTimehhMM());
		ObjectInputStream objectInputStream;
		try {
			objectInputStream = new ObjectInputStream(
					new FileInputStream(NAME_2_IDNS));
			name2idns = (TrieMultimap<String>) objectInputStream.readObject();
			FileUtils.safeClose(objectInputStream);

			objectInputStream = new ObjectInputStream(
					new FileInputStream(VWW_2_IDNS));
			vww2idns = (TrieMultimap<String>) objectInputStream.readObject();
			FileUtils.safeClose(objectInputStream);

			objectInputStream = new ObjectInputStream(
					new FileInputStream(IDN_2_RECORD));
			idn2record = (Map<String, MyRecord>) objectInputStream.readObject();
			FileUtils.safeClose(objectInputStream);

			objectInputStream = new ObjectInputStream(
					new FileInputStream(F_2_IDNS));
			f2idns = (Multimap<String, String>) objectInputStream.readObject();
			FileUtils.safeClose(objectInputStream);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("laden fertig");
		System.err.println(TimeUtils.getActualTimehhMM());

	}

	public static Optional<MyRecord> getMyRecord(final String idn) {
		return Optional.ofNullable(idn2record.get(idn));
	}

	/**
	 *
	 * @param name
	 * @return nicht null
	 */
	public static List<MyRecord> getMyRecordsByName(final String name) {
		final Collection<String> idns = name2idns
				.getNullSafe(StringUtils.unicodeDecomposition(name));
		final List<MyRecord> records = idns2records(idns);
		return records;
	}

	public static List<MyRecord> getRecordsByNamePrefix(final String name) {
		final Collection<String> idns = name2idns
				.flatValuesWithPrefix(StringUtils.unicodeDecomposition(name));
		return idns2records(idns);
	}

	public static List<MyRecord> getRecordsByVWPrefix(final String name) {
		final Collection<String> idns = vww2idns
				.flatValuesWithPrefix(StringUtils.unicodeDecomposition(name));
		return idns2records(idns);
	}

	/**
	 *
	 * @param name
	 * @return Körperschaften in Ansetzung und Verweisung
	 */
	public static List<MyRecord> getKoeRecords(final String name) {
		final Collection<String> idns = f2idns
				.getNullSafe(StringUtils.unicodeDecomposition(name));
		return idns2records(idns);
	}

	/**
	 * @param idns
	 * @return
	 */
	public static List<MyRecord> idns2records(final Collection<String> idns) {
		final List<MyRecord> records = new LinkedList<>();
		idns.forEach(idn ->
		{
			final Optional<MyRecord> opt = getMyRecord(idn);
			opt.ifPresent(records::add);
		});
		return records;
	}

	/**
	 *
	 * @param name
	 * @return nicht null
	 */
	public static List<MyRecord> getMyRecordsByVWW(final String name) {
		final Collection<String> idns = vww2idns
				.getNullSafe(StringUtils.unicodeDecomposition(name));
		final List<MyRecord> records = idns2records(idns);
		return records;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		create();
		save();
		// load();
		System.out.println(getRecordsByVWPrefix("Körpererz"));
		System.out.println(getKoeRecords("Altes Testament"));
		System.out.println(getKoeRecords("SPD"));
	}

}
