/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class OB_UB_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public OB_UB_Generator() {
		super(FOLDER + "OB_UB.out", BiMultimap.createSetMap(), "UBB-OBB (n:m)");
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, Integer> ub_ob_map = (BiMultimap<Integer, Integer>) data;

	@Override
	public void process(final Record record) {
		final int ppnI = IDNUtils.ppn2int(record.getId());
		final List<Integer> obb = GNDUtils.getOBBidns(record);
		if (!obb.isEmpty()) {
			ub_ob_map.addAll(ppnI, obb);
		}
	}

	@Override
	public BiMultimap<Integer, Integer> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadBiMultimap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return null;
	}

}
