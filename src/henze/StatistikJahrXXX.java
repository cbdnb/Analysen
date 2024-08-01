/**
 *
 */
package henze;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.BibRecUtils.REIHE;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;

/**
 * @author baumann
 *
 */
public class StatistikJahrXXX extends DownloadWorker {

	private static final String JAHR_STR = "17";

	private static final int JAHR = 2017;

	private static final CrossProductFrequency frequency = new CrossProductFrequency();

	private static final Collection<REIHE> REIHEN = Arrays.asList(REIHE.A,
			REIHE.B, REIHE.H);

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		final StatistikJahrXXX statXXX = new StatistikJahrXXX();

		// final Predicate<String> titleFilter = new ContainsTag("2105", '0',
		// JAHR_STR, BibTagDB.getDB());

		final Predicate<String> titleFilter = new ContainsTag("2105",
				BibTagDB.getDB());

		statXXX.setStreamFilter(titleFilter);
		statXXX.gzipSettings();

		System.err.println("Titeldaten flöhen:");

		try {
			statXXX.processFile(Constants.TITEL_STICHPROBE);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		System.out.println(frequency);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {

		final Collection<Integer> wvJahre = BibRecUtils.getWVYears(record);
		if (!wvJahre.contains(JAHR))
			return;

		final REIHE reihe = BibRecUtils.getReihe(record);
		if (!REIHEN.contains(reihe))
			return;

		final TIEFE status = SubjectUtils.getErschliessungsTiefe(record);
		if (status == null)
			return;

		frequency.addValues(reihe, status.verbal);

	}

}
