/**
 *
 */
package henze;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.function.Predicate;

import de.dnb.basics.collections.Frequency;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class StatistikGNDJahrXXX extends DownloadWorker {

	private static final String FRANKFURT = "1250";

	private static final String LEIPZIG = "1150";

	private static final String JAHR_STR = "17";

	private static final int JAHR = 2017;

	private static final Frequency<Character> frequency = new Frequency<>();

	private static Collection<String> abteilungen = Arrays.asList(LEIPZIG,
			FRANKFURT);

	private static Predicate<String> abtFilter;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {

		if (GNDUtils.containsChangeCode(record))
			return;

		final Date eingabeDatum = RecordUtils.getDateEntered(record);
		if (eingabeDatum == null)
			return;
		final Calendar calendar = TimeUtils.getCalendar(eingabeDatum);
		final int jahr = calendar.get(Calendar.YEAR);

		if (jahr != JAHR)
			return;

		final String abteilung = RecordUtils.getSourceEntered(record);
		if (!abteilungen.contains(abteilung))
			return;

		final char type = GNDUtils.getRecordType(record);

		if (type == 0)
			return;

		frequency.add(type);
		// System.err.println(record.getId());

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		TimeUtils.startStopWatch();

		final StatistikGNDJahrXXX statistik = new StatistikGNDJahrXXX();

		statistik.setInputFolder("D:/Normdaten");
		statistik.setFilePrefix("DNBGND_s");
		statistik.gzipSettings();

		final Predicate<String> lFilter = new ContainsTag("001", '0', LEIPZIG,
				GNDTagDB.getDB());
		final Predicate<String> fFilter = new ContainsTag("001", '0', FRANKFURT,
				GNDTagDB.getDB());
		abtFilter = lFilter.or(fFilter);
		statistik.setStreamFilter(abtFilter);

		statistik.processAllFiles();

		System.out.println(TimeUtils.delta_t_millis());

		System.out.println(frequency);

	}

}
