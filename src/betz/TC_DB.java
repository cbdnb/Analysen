/**
 *
 */
package betz;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class TC_DB {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader.getMatchingReader(NSogg.TC);

		final BiMultimap<String, String> mid2ppn = BiMultimap.createListMap();
		final BiMultimap<String, String> mid2lcsh = BiMultimap.createListMap();
		final BiMultimap<String, String> lcsh2name = BiMultimap.createListMap();
		final BiMultimap<String, Pair<String, String>> mid2lcshP = BiMultimap.createListMap();
		final Map<String, String> mid2TcId = new HashMap<>();

		reader.forEach(record ->
		{
			final String id = record.getId();
			final List<String> mids = GNDUtils.getMACSids(record);
			final List<String> ppns = GNDUtils.getTc_IDNs_Labels(record);
			// restriktiv: nur 1:1 beachten:
			if (ppns.size() != 1)
				return;
			final Set<Pair<String, String>> lcshs = GNDUtils
					.getLCSH_ID_Label(record);
			// restriktiv: nur Datensätze mit LCSH behandeln:
			// if (!lcshs.isEmpty())
			mids.forEach(mid ->
			{
				final String macs = "MACS" + mid.trim();
				mid2ppn.addAll(macs, ppns);
				mid2lcshP.addAll(macs, lcshs);
				lcshs.forEach(pair ->
				{
					final String lcshId = pair.first;
					final String lcshName = pair.second;
					mid2lcsh.add(mid, lcshId);
					lcsh2name.add(lcshId, lcshName);
				});
				mid2TcId.put(mid, id);
			});

		});

		CollectionUtils.save(mid2ppn, NSogg.MID2PPN);
		CollectionUtils.save(mid2lcshP, NSogg.MID2LCSH_P);
		CollectionUtils.save(mid2lcsh, NSogg.MID2LCSH);
		CollectionUtils.save(lcsh2name, NSogg.LCSH2NAME);
		CollectionUtils.save(mid2TcId, NSogg.MID2TC_ID);
	}

}
