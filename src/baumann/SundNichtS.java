/**
 *
 */
package baumann;

import java.io.IOException;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;

/**
 * Analysiert, ob Schlagwörter mit 011 s mit solchen verknüpft sind, die nicht s
 * sind.
 *
 * @author baumann
 *
 *
 *
 */
public class SundNichtS extends DownloadWorker {

	private static final BiMultimap<Integer, Integer> s = BiMultimap
			.createSetMap();
	/**
	 * alle nicht s
	 */
	private static BiMultimap<Integer, Integer> f = BiMultimap.createSetMap();

	@Override
	protected void processRecord(final Record record) {
		final boolean tbsie = GNDUtils.isTeilbestandIE(record);
		if (tbsie) {
			final STANDORT_DNB urheber = RecordUtils
					.getEingebenderStandort(record);
			// nur von uns eingegeben:
			if (urheber != STANDORT_DNB.U)
				fillgraph(record, s);
		} else
			fillgraph(record, f);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final SundNichtS sundNichtS = new SundNichtS();
		sundNichtS.processGZipFiles(Constants.Tg, Constants.Ts, Constants.Tu);
		// sundNichtS.processGZipFiles(Constants.Tg);

		// final Set<Integer> s_ziel_von_f = CollectionUtils
		// .intersection(s.getKeySet(), f.getValueSet());
		// s_ziel_von_f.forEach(s_ ->
		// {
		// System.out.println(IDNUtils.int2PPN(s_) + ": "
		// + IDNUtils.ints2ppns(f.searchKeys(s_)));
		// });

		// System.out.println("------");
		final Set<Integer> f_ziel_von_s = CollectionUtils
				.intersection(f.getKeySet(), s.getValueSet());
		f_ziel_von_s.forEach(f_ ->
		{
			System.out.println(IDNUtils.int2PPN(f_) + ": "
					+ IDNUtils.ints2ppns(s.searchKeys(f_)));
		});

		System.out.println(f_ziel_von_s.size());

	}

	/**
	 * @param record
	 * @param graph
	 */
	private void fillgraph(final Record record,
			final BiMultimap<Integer, Integer> graph) {
		final int id = IDNUtils.idn2int(record.getId());
		graph.add(id);
		GNDUtils.getRelatedLines5XX(record).forEach(line ->
		{
			final String idnRelated = line.getIdnRelated();
			if (idnRelated != null)
				graph.add(id, IDNUtils.idn2int(idnRelated));
		});
	}

}
