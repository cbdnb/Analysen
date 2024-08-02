/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.function.Predicate;

import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class Werke_Autor_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Werke_Autor_Generator() {
		super(FOLDER + "Werke_Autor.out", BiMultimap.createSetMap(),
				"Werk-Autor (1:n)");
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, Integer> werke_autor_map = (BiMultimap<Integer, Integer>) data;

	@Override
	public void process(final Record record) {
		final int ppnI = IDNUtils.ppn2int(record.getId());
		final Integer autorID = IDNUtils.ppn2int(WorkUtils.getAuthorID(record));
		if (autorID != -1) {
			werke_autor_map.add(ppnI, autorID);
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
