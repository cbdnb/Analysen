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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 */
public class GND_DB {

	public static final String FOLDER = "D:/analysen/baumann/";
	public static final String NAME_2_IDNS = FOLDER + "name2idns.out";
	public static final String IDN_2_RECORD = FOLDER + "idn2record.out";
	public static final String VWW_2_IDNS = FOLDER + "vww2idns.out";

	static Multimap<String, String> name2idns = new ListMultimap<>();
	static Multimap<String, String> vww2idns = new ListMultimap<>();
	static Map<String, MyRecord> idn2record = new HashMap<>();

	public static void save() {
		try {
			final ObjectOutputStream out1 = new ObjectOutputStream(
					new FileOutputStream(NAME_2_IDNS));
			out1.writeObject(name2idns);
			MyFileUtils.safeClose(out1);

			final ObjectOutputStream out2 = new ObjectOutputStream(
					new FileOutputStream(IDN_2_RECORD));
			out2.writeObject(idn2record);
			MyFileUtils.safeClose(out2);

			final ObjectOutputStream out3 = new ObjectOutputStream(
					new FileOutputStream(VWW_2_IDNS));
			out3.writeObject(vww2idns);
			MyFileUtils.safeClose(out3);
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void bearbeiteStream(final Stream<Record> stream) {
		stream.forEach(record ->
		{
			final int level = GNDUtils.getLevel(record);
			if (level > 1)
				return;
			final List<String> k011 = GNDUtils.getTbs(record);
			if (!k011.contains("s"))
				return;
			String name;
			try {
				name = RDAFormatter.getRDAHeading(record);
			} catch (final IllFormattedLineException e) {
				return;
			}
			final String idn = record.getId();
			final List<String> sys = GNDUtils.getGNDClassifications(record);
			final String satzart = RecordUtils.getDatatype(record);
			final MyRecord myRecord = new MyRecord();
			myRecord.idn = idn;
			myRecord.satzart = satzart;
			myRecord.name = name;
			myRecord.systs.addAll(sys);
			System.err.println(myRecord);
			idn2record.put(idn, myRecord);
			name2idns.add(name, idn);
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
						vww2idns.add(vwS, idn);
					} catch (final IllFormattedLineException e) {
					}
				});
			}

		});

	}

	public static void create() {
		try {
			final RecordReader readerS = RecordReader
					.getMatchingReader(Constants.Ts);

			final Stream<Record> streamS = readerS.stream();
			bearbeiteStream(streamS);

			final RecordReader readerG = RecordReader
					.getMatchingReader(Constants.Tg);
			final Stream<Record> streamG = readerG.stream();
			bearbeiteStream(streamG);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public static void load() {
		System.err.println("laden");
		System.err.println(TimeUtils.getActualTimehhMM());
		ObjectInputStream objectInputStream;
		try {
			objectInputStream = new ObjectInputStream(
					new FileInputStream(NAME_2_IDNS));
			name2idns = (Multimap<String, String>) objectInputStream
					.readObject();
			MyFileUtils.safeClose(objectInputStream);

			objectInputStream = new ObjectInputStream(
					new FileInputStream(VWW_2_IDNS));
			vww2idns = (Multimap<String, String>) objectInputStream
					.readObject();
			MyFileUtils.safeClose(objectInputStream);

			objectInputStream = new ObjectInputStream(
					new FileInputStream(IDN_2_RECORD));
			idn2record = (Map<String, MyRecord>) objectInputStream.readObject();
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

	public static List<MyRecord> getMyRecordsByName(final String name) {
		final Collection<String> idns = name2idns.getNullSafe(name);
		final List<MyRecord> records = new LinkedList<>();
		idns.forEach(idn ->
		{
			final Optional<MyRecord> opt = getMyRecord(idn);
			opt.ifPresent(records::add);
		});
		return records;
	}

	public static List<MyRecord> getMyRecordsByVWW(final String name) {
		final Collection<String> idns = vww2idns.getNullSafe(name);
		final List<MyRecord> records = new LinkedList<>();
		idns.forEach(idn ->
		{
			final Optional<MyRecord> opt = getMyRecord(idn);
			opt.ifPresent(records::add);
		});
		return records;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// create();
		// save();
		load();
		System.out.println(getMyRecordsByVWW("RDA"));
	}

}
