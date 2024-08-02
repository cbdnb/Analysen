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
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class Titel_Werke_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Titel_Werke_Generator() {
		super(FOLDER + "Titel_Werke.out", BiMultimap.createSetMap(),
				"Titel-Werke / früher EST (n:m)");
		System.err.println("Erzeugung kann mehrere Stunden dauern!");
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, Integer> tit_work_map = (BiMultimap<Integer, Integer>) data;

	@Override
	public void process(final Record record) {
		final int ppnI = IDNUtils.ppn2int(record.getId());
		final List<Integer> werke = IDNUtils
				.ppns2ints(BibRecUtils.getWorkIds(record));
		if (!werke.isEmpty()) {
			tit_work_map.addAll(ppnI, werke);
		}
	}

	@Override
	public BiMultimap<Integer, Integer> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadBiMultimap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return new ContainsTag("3210", BibTagDB.getDB())
				.or(new ContainsTag("3211", BibTagDB.getDB()));
	}

}
