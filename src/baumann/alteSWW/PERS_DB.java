/**
 *
 */
package baumann.alteSWW;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.collections.TreeMultimap;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 */
public class PERS_DB {

	public static final String FOLDER_AND_PREFIX = "D:/analysen/baumann/ppp";
	public static final String NAME_2_IDN = FOLDER_AND_PREFIX + "name2idns.out";
	public static final String IDN_2_RECORD = FOLDER_AND_PREFIX
			+ "idn2record.out";

	static TreeMultimap<String, String> pndName2idns = new TreeMultimap<>();
	static HashMap<String, MyRecord> idn2record = new HashMap<>();

	public static void save() {
		System.err.println("save");
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(NAME_2_IDN));
			out.writeObject(pndName2idns);
			MyFileUtils.safeClose(out);

			out = new ObjectOutputStream(new FileOutputStream(IDN_2_RECORD));
			out.writeObject(idn2record);
			MyFileUtils.safeClose(out);

		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void bearbeiteStream(final Stream<Record> stream) {
		final AtomicInteger ai = new AtomicInteger();
		stream.forEach(record ->
		{
			final int level = GNDUtils.getLevel(record);
			if (level > 1)
				return;

			if (!GNDUtils.isTeilbestandIE(record))
				return;

			final List<String> sys = GNDUtils.getGNDClassifications(record);
			if (sys.isEmpty())
				return;

			String name;
			try {
				name = RDAFormatter.getRDAHeading(record);
			} catch (final IllFormattedLineException e) {
				return;
			}
			ai.incrementAndGet();

			final String idn = record.getId();

			final String satzart = RecordUtils.getDatatype(record);
			final MyRecord myRecord = new MyRecord();
			myRecord.idn = idn;
			myRecord.satzart = satzart;
			myRecord.name = StringUtils.unicodeComposition(name);
			myRecord.systs.addAll(sys);
			ai.incrementAndGet();
			if (ai.get() % 1_000 == 0)
				System.err.println(ai + ": " + myRecord);

			idn2record.put(idn, myRecord);

			final ArrayList<Line> lines1xx4xx = GNDUtils.getLines4XX(record);
			lines1xx4xx.addAll(GNDUtils.getLines1XX(record));
			lines1xx4xx.forEach(line ->
			{
				final String bereinigt = bereinige1XX4XX(line);
				// if (record.getId().equals("118634771"))
				// System.err.println(bereinigt);
				pndName2idns.add(bereinigt, idn);

			});

			final List<String> swdSWW = GNDUtils.getOriginalHeadings(record);
			swdSWW.forEach(swd -> pndName2idns.add(normalisiere(swd), idn));
		});

	}

	/**
	 * Entfernt $g, $x, $v und stellt $n nach.
	 *
	 * @param personLine
	 * @return Aneinanderreichung der Unterfelder ohne Blank
	 */
	private static String bereinige1XX4XX(final Line personLine) {
		final List<Subfield> subs = SubfieldUtils.retainSubfields(personLine,
				'P', 'a', 'd', 'c', 'n', 'l');
		final Subfield dollarN = SubfieldUtils.getFirstSubfield(personLine,
				'n');
		if (dollarN != null) {
			subs.remove(dollarN);
			subs.add(dollarN);
		}

		final String concatenated = StringUtils.concatenate("",
				FilterUtils.map(subs, Subfield::getContent));
		return normalisiere(concatenated);

	}

	/**
	 * Entfernt Blanks, Klammern und Kommas.
	 *
	 * @param s
	 * @return Unicode-Normalisierung
	 */
	private static String normalisiere(String s) {
		s = s.trim();
		s = StringUtils.unicodeComposition(s);
		s = s.replaceAll("[\\s,;\\(\\)\\<\\>\\[\\]{}\\/]", "");
		// s = s.replaceAll("[\\(\\)\\<\\>\\[\\]{}]", "");
		return s;

	}

	public static void create() {
		System.err.println("create");
		try {
			System.err.println("Tp");
			final RecordReader reader = RecordReader
					.getMatchingReader(Constants.Tp);
			final Stream<Record> stream = reader.stream();
			bearbeiteStream(stream);

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
					new FileInputStream(NAME_2_IDN));
			pndName2idns = (TreeMultimap<String, String>) objectInputStream
					.readObject();
			MyFileUtils.safeClose(objectInputStream);

			objectInputStream = new ObjectInputStream(
					new FileInputStream(IDN_2_RECORD));
			idn2record = (HashMap<String, MyRecord>) objectInputStream
					.readObject();
			MyFileUtils.safeClose(objectInputStream);

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
	public static List<MyRecord> getMyRecordsByName(final String name) {
		final Collection<String> idns = pndName2idns
				.getNullSafe(normalisiere(name));
		final Comparator<String> myComparator = new Comparator<String>() {

			@Override
			public int compare(final String s1, final String s2) {
				final int i1 = Integer
						.parseInt(s1.substring(0, s1.length() - 1));
				final int i2 = Integer
						.parseInt(s2.substring(0, s1.length() - 1));
				return Integer.compare(i1, i2);
			}
		};
		final List<String> idL = ListUtils.convertToModifiableList(idns);
		Collections.sort(idL);
		return idns2records(idL);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main1(final String[] args) throws IOException {

		load();
		while (true) {
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(System.in));
			reader.readLine();
			final String name = StringUtils.readClipboard().trim();
			System.out.println(getMyRecordsByName(name));
		}

	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		create();
		save();
		// load();
		System.out.println(getMyRecord("118634771"));
		System.out.println(getMyRecordsByName("Wolff, Christian"));
		System.out.println(getMyRecordsByName("Wolf, Chrétien"));

	}

	public static void main2(final String[] args) {
		final Record record = RecordUtils.readFromClip();
		final ArrayList<Line> l4 = GNDUtils.getLines4XX(record);
		l4.forEach(line -> System.out.println(bereinige1XX4XX(line)));
	}

}
