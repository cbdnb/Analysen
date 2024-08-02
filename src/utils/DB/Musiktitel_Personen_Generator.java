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
import de.dnb.gnd.utils.StatusAndCodeFilter;

/**
 * @author baumann
 *
 */
public class Musiktitel_Personen_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Musiktitel_Personen_Generator() {
		super(FOLDER + "Musiktitel_Personen.out", BiMultimap.createSetMap(),
				"Musiktitel-Personen (n:m)");
		System.err.println("Erzeugung kann mehrere Stunden dauern!");
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, Integer> tit_pers_map = (BiMultimap<Integer, Integer>) data;

	StatusAndCodeFilter statusAndCodeFilter = StatusAndCodeFilter
			.filterMusikalie();

	@Override
	public void process(final Record record) {
		if (!statusAndCodeFilter.test(record))
			return;
		final int ppnI = IDNUtils.ppn2int(record.getId());

		final List<Integer> personen = IDNUtils
				.ppns2ints(BibRecUtils.getPersonIds(record));
		if (!personen.isEmpty()) {
			System.err.println(ppnI);
			tit_pers_map.addAll(ppnI, personen);
		}
	}

	@Override
	public BiMultimap<Integer, Integer> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadBiMultimap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return new ContainsTag("3000", BibTagDB.getDB())
				.or(new ContainsTag("3010", BibTagDB.getDB())
						.or(new ContainsTag("3019", BibTagDB.getDB())));
	}

}
