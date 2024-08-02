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
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class Werk_Instrument_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Werk_Instrument_Generator() {
		super(FOLDER + "Work_INSTR.out", BiMultimap.createSetMap(),
				"Werk-Instrument (n:m)");
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, Integer> work_instr_map = (BiMultimap<Integer, Integer>) data;

	@Override
	public void process(final Record record) {
		final int ppnI = IDNUtils.ppn2int(record.getId());
		final List<Integer> instr = WorkUtils.getIstrIdns(record);
		if (!instr.isEmpty()) {
			work_instr_map.addAll(ppnI, instr);
		}
	}

	@Override
	public BiMultimap<Integer, Integer> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadBiMultimap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return new ContainsTag("382", GNDTagDB.getDB());
	}
}
