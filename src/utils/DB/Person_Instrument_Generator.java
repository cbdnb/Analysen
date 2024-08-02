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
import de.dnb.gnd.utils.PersonUtils;

/**
 * @author baumann
 *
 *         Extrahiert die Instrumente aus 550 $4 istr.
 *
 */
public class Person_Instrument_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Person_Instrument_Generator() {
		super(FOLDER + "P_INSTR.out", BiMultimap.createSetMap(),
				"Person-Instrument (n:m)");
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, Integer> p_instr_map = (BiMultimap<Integer, Integer>) data;

	@Override
	public void process(final Record record) {
		final int ppnI = IDNUtils.ppn2int(record.getId());
		final List<Integer> instr = PersonUtils.getIstrIdns(record);
		if (!instr.isEmpty()) {
			p_instr_map.addAll(ppnI, instr);
		}
	}

	@Override
	public BiMultimap<Integer, Integer> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadBiMultimap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return new ContainsTag("550", GNDTagDB.getDB());
	}

}
