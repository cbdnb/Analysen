/**
 *
 */
package henze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.DDC_SG.REFERATE;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import utils.AcDatabase;

/**
 * Wieviele Bücher wurden im Jahr 2010-2019 in den MINT-Fächern gedruckt?
 *
 * @author baumann
 *
 */
public class Buchproduktion1 extends DownloadWorker {

	private static List<Integer> jahre = IntStream.rangeClosed(2010, 2019)
			.boxed().collect(Collectors.toList());

	private static final CrossProductFrequency frequency = new CrossProductFrequency();

	private static final StatusAndCodeFilter statusAndCodeFilter = StatusAndCodeFilter
			.reiheA_Gedruckt_ImBestand();

	static {
		statusAndCodeFilter.auchNichtImBestand();
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		final Buchproduktion1 buchprod = new Buchproduktion1();

		final Predicate<String> titleFilter = new ContainsTag("2105", '0', "1",
				BibTagDB.getDB());

		buchprod.setStreamFilter(titleFilter);
		buchprod.gzipSettings();

		System.err.println("Titeldaten flöhen:");

		try {
			buchprod.processFile(Constants.GND_TITEL_GESAMT_Z);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		// Titelzeile
		System.out.println("\t" + StringUtils.concatenate("\t", jahre));
		DDC_SG.getSTM().forEach(sg ->
		{
			final List<String> zellen = new ArrayList<>();
			final String sgstr = sg.getDDCString();
			zellen.add(sgstr);
			jahre.forEach(jahr ->
			{
				final Long count = frequency.get(sg, jahr);
				zellen.add(Long.toString(count));
			});
			System.out.println(StringUtils.concatenate("\t", zellen));
		});

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {

		// final String idn = record.getId();

		if (!statusAndCodeFilter.test(record))
			return;

		final Collection<Integer> wvJahre = BibRecUtils.getWVYears(record);
		if (BibRecUtils.isPHeft(record))
			return;

		final Set<Integer> schnitt = new LinkedHashSet<>(jahre);
		schnitt.retainAll(wvJahre);
		if (schnitt.isEmpty())
			return;

		// nimm erstes
		final int jahr = schnitt.iterator().next();

		TIEFE status = SubjectUtils.getErschliessungsTiefe(record);
		if (status == null) { // übergeordneten versuchen
			final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
			final Pair<String, TIEFE> pair = AcDatabase.getStatus(idnBroader);
			// System.err.println(pair);
			if (pair != null) {
				status = pair.second;
			}
		}

		DDC_SG dhs = SGUtils.getDDCDHS(record);
		if (dhs == null) {
			final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
			final Pair<String, TIEFE> pair = AcDatabase.getStatus(idnBroader);
			if (pair != null) {
				final String ddcStr = pair.first;
				dhs = SGUtils.getSG(ddcStr);
				System.err.println(pair);
			}
		}
		if (dhs == null || dhs.getReferat() != REFERATE.STM)
			return;

		frequency.addValues(dhs, jahr);

	}

}
