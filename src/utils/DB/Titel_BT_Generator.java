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
 *
 * @author baumann
 *
 */
public class Titel_BT_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Titel_BT_Generator() {
		super(FOLDER + "Titel_BT.out", BiMultimap.createSetMap(),
				"Titel-Übergeordneter Titel");
		System.err.println("Erzeugung kann mehrere Stunden dauern!");
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, Integer> tit_bt_map = (BiMultimap<Integer, Integer>) data;

	@Override
	public void process(final Record record) {
		final int ppnI = IDNUtils.ppn2int(record.getId());

		final List<Integer> btt = IDNUtils
				.ppns2ints(BibRecUtils.getBroaderTitleIDNs(record));
		if (!btt.isEmpty()) {
			tit_bt_map.addAll(ppnI, btt);
		}
	}

	@Override
	public BiMultimap<Integer, Integer> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadBiMultimap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return new ContainsTag("4000", BibTagDB.getDB())
				.or(new ContainsTag("4140", BibTagDB.getDB()))
				.or(new ContainsTag("4160", BibTagDB.getDB()))
				.or(new ContainsTag("4180", BibTagDB.getDB()))
				.or(new ContainsTag("4181", BibTagDB.getDB()))
				.or(new ContainsTag("4182", BibTagDB.getDB()));
	}

}
