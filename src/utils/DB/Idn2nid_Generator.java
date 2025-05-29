/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.function.Predicate;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;

/**
 * Erzeugt eine Zuordnung idn (int) <-> nid
 *
 * @author baumann
 *
 */
public class Idn2nid_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Idn2nid_Generator() {
		super(FOLDER + "ppn2nid.out", new BiMap<>(), "PPN <-> nid");
	}

	@SuppressWarnings("unchecked")
	final BiMap<Integer, String> ppn2names = (BiMap<Integer, String>) data;

	@Override
	public void process(final Record record) {
		final String nid = GNDUtils.getNID(record);
		if (StringUtils.isNullOrEmpty(nid))
			return;

		final int ppnI = IDNUtils.ppn2int(record.getId());
		ppn2names.add(ppnI, nid);
	}

	@Override
	public BiMap<Integer, String> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadBimap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return null;
	}

}
