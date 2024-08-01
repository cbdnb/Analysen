/**
 *
 */
package scheven.hinweis.archiv;

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
public class HinweisDatensaetze2 {

	static class MyRecord {
		@Override
		public String toString() {
			final String syst = StringUtils.concatenate(";", sys);
			final String rswk = inRswk ? "x" : "-";
			final String in5540s = in5540 ? "x" : "-";
			final String in5550s = in5550 ? "x" : "-";
			final String out = StringUtils.concatenate("\t", syst, idn, name,
					typ, rswk, in5540s, in5550s);
			return out;
		}

		String idn;
		String name;
		String typ;
		List<String> sys;
		boolean inRswk = false;
		boolean in5540 = false;
		boolean in5550 = false;
	}

	public static HashMap<String, MyRecord> idn2hinw = new HashMap<>();

	public static DownloadWorker gndWorker = new DownloadWorker() {

		@Override
		protected void processRecord(final Record record) {
			if (!GNDUtils.isUseCombination(record))
				return;
			final String idn = record.getId();
			final MyRecord myRecord = new MyRecord();
			myRecord.idn = idn;
			myRecord.typ = RecordUtils.getDatatype(record);
			myRecord.name = GNDUtils.getNameOfRecord(record);
			final List<String> sys = GNDUtils.getGNDClassifications(record);
			if (sys.isEmpty())
				sys.add("");
			myRecord.sys = sys;
			idn2hinw.put(idn, myRecord);
		}
	};

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		gndWorker.gzipSettings();
		gndWorker.setFilePrefix("DNBGND_");
		gndWorker.setInputFolder("D:/Normdaten");
		gndWorker.processAllFiles();

		System.err.println("Titeldaten:");

		final RecordReader treader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);

		final TagDB db = BibTagDB.getDB();
		final Predicate<String> pred = new ContainsTag("5100", db)
				.or(new ContainsTag("5540", db))
				.or(new ContainsTag("5550", db));
		treader.setStreamFilter(pred);

		final Set<String> hinwIDs = idn2hinw.keySet();

		treader.forEach(record ->
		{

			final Collection<String> rswkIDs = SubjectUtils.getRSWKidsSet(record);
			for (final String id : rswkIDs) {
				if (hinwIDs.contains(id)) {
					final MyRecord myRecord = idn2hinw.get(id);
					myRecord.inRswk = true;
				}
			}

			final Collection<String> automID5540 = new HashSet<>(
					RecordUtils.getContentsOfFirstSubfield(record, '9', "5540"));
			for (final String id : automID5540) {
				if (hinwIDs.contains(id)) {
					final MyRecord myRecord = idn2hinw.get(id);
					myRecord.in5540 = true;
				}
			}

			final Collection<String> automID5550 = new HashSet<>(
					RecordUtils.getContentsOfFirstSubfield(record, '9', "5550"));
			for (final String id : automID5550) {
				if (hinwIDs.contains(id)) {
					final MyRecord myRecord = idn2hinw.get(id);
					myRecord.in5550 = true;
					// System.err.println(myRecord);
				}
			}

		});
		final ArrayList<MyRecord> myRecords = new ArrayList<>();
		idn2hinw.values().forEach(myRecord ->
		{
			if (myRecord.inRswk || myRecord.in5540 || myRecord.in5550) {
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
