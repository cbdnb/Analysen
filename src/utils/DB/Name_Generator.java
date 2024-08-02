/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Predicate;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class Name_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Name_Generator() {
		super(FOLDER + "ppn2name.out", new HashMap<Integer, String>(),
				"PPN -> Name");
	}

	@SuppressWarnings("unchecked")
	final HashMap<Integer, String> ppn2names = (HashMap<Integer, String>) data;

	@Override
	public void process(final Record record) {
		try {
			String name = GNDUtils.getNameOfRecord(record);
			name = StringUtils.unicodeComposition(name);
			final int ppnI = IDNUtils.ppn2int(record.getId());
			ppn2names.put(ppnI, name);

		} catch (final IllegalStateException e) {
			// nix
		}
	}

	@Override
	public HashMap<Integer, String> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadHashMap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return null;
	}

}
