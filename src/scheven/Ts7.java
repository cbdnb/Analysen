/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.parser.tag.TagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SystematikComparator;

/**
 * @author baumann
 *
 */
public class Ts7 {

	static class MyRecord {
		@Override
		public String toString() {
			final String syst = StringUtils.concatenate(";", sys);

			final String out = StringUtils.concatenate("\t", syst, idn, name,
					typ, StringUtils.concatenate("\t", titels));
			return out;
		}

		String idn;
		String name;
		String typ;
		List<String> sys;
		List<String> titels = new ArrayList<>();

	}

	public static HashMap<String, MyRecord> idn2Ts7 = new HashMap<>();

	public static DownloadWorker gndWorker = new DownloadWorker() {

		@Override
		protected void processRecord(final Record record) {
			final String typ = RecordUtils.getDatatype(record);
			if (!"Ts7".equals(typ))
				return;

			final String idn = record.getId();
			final MyRecord myRecord = new MyRecord();
			myRecord.idn = idn;
			myRecord.typ = typ;
			myRecord.name = GNDUtils.getNameOfRecord(record);
			final List<String> sys = GNDUtils.getGNDClassifications(record);
			if (sys.isEmpty())
				sys.add("");
			myRecord.sys = sys;
			idn2Ts7.put(idn, myRecord);
		}
	};

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		gndWorker.gzipSettings();
		gndWorker.setFilePrefix("DNBGND_s");
		gndWorker.setInputFolder("D:/Normdaten");
		gndWorker.processAllFiles();

		System.err.println(idn2Ts7.size());

		System.err.println("Titeldaten:");

		final RecordReader treader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);

		final TagDB db = BibTagDB.getDB();
		final Predicate<String> pred = ContainsTag.getContainsRSWK()
				.or(new ContainsTag("5540", db))
				.or(new ContainsTag("5550", db));
		treader.setStreamFilter(pred);

		final Set<String> hinwIDs = idn2Ts7.keySet();

		treader.forEach(record ->
		{
			if (record.getId().equals("1128652986"))
				System.out.println(record);

			final String recID = record.getId();

			final Collection<String> rswkIDs = SubjectUtils.getRSWKidsSet(record);
			for (final String id : rswkIDs) {
				if (hinwIDs.contains(id)) {
					final MyRecord myRecord = idn2Ts7.get(id);
					myRecord.titels.add(recID + "\t" + "51XX");
				}
			}

			final Collection<String> automID5540 = new HashSet<>(
					RecordUtils.getContentsOfFirstSubfield(record, '9', "5540"));
			for (final String id : automID5540) {
				if (hinwIDs.contains(id)) {
					final MyRecord myRecord = idn2Ts7.get(id);
					myRecord.titels.add(recID + "\t" + "5540");
				}
			}

			final Collection<String> automID5550 = new HashSet<>(
					RecordUtils.getContentsOfFirstSubfield(record, '9', "5550"));
			for (final String id : automID5550) {
				if (hinwIDs.contains(id)) {
					final MyRecord myRecord = idn2Ts7.get(id);
					myRecord.titels.add(recID + "\t" + "5550");
				}
			}

		});
		final ArrayList<MyRecord> myRecords = new ArrayList<>();
		idn2Ts7.values().forEach(myRecord ->
		{
			if (!myRecord.titels.isEmpty()) {
				myRecords.add(myRecord);
			}
		});
		myRecords.sort(new Comparator<MyRecord>() {
			SystematikComparator sysComparator = new SystematikComparator();

			@Override
			public int compare(final MyRecord m1, final MyRecord m2) {
				if (!m1.sys.get(0).equals(m2.sys.get(0)))
					return sysComparator.compare(m1.sys.get(0), m2.sys.get(0));
				if (!m1.typ.equals(m2.typ))
					return m1.typ.compareTo(m2.typ);
				return m1.name.compareTo(m2.name);
			}
		});

		myRecords.forEach(myRecord ->
		{
			System.out.println(myRecord);
		});

	}

}
