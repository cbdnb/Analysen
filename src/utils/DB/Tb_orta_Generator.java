/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Tb_orta_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Tb_orta_Generator() {
		super(FOLDER + "Tb_Ort.out", new HashMap<Integer, Integer>(),
				"Tb-idn -> Ort-idn");
	}

	@SuppressWarnings("unchecked")
	final HashMap<Integer, Integer> tb2ort = (HashMap<Integer, Integer>) data;

	@Override
	public void process(final Record record) {
		try {
			List<Line> orte = RecordUtils.getLinesWithSubfield(record, "551",
					'4', "orta");
			if (orte.isEmpty()) {
				orte = RecordUtils.getLinesWithSubfield(record, "551", '4',
						"adue");
				if (orte.isEmpty()) {
					orte = RecordUtils.getLinesWithSubfield(record, "551", '4',
							"geow");
				}
				if (orte.isEmpty()) {
					return;
				}
			}
			// gefährlich, da nicht immer das erste gültig ist!
			final Line ortaLine = orte.get(0);
			final int ortaID = IDNUtils.ppn2int(ortaLine.getIdnRelated());

			final int tbID = IDNUtils.ppn2int(record.getId());
			tb2ort.put(tbID, ortaID);

		} catch (final IllegalStateException e) {
			// nix
		}
	}

	@Override
	public HashMap<Integer, Integer> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadHashMap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return new ContainsTag("551", GNDTagDB.getDB());
	}

}
