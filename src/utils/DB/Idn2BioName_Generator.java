/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.function.Predicate;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * Erzeugt den RDA-Namen (Zusatz in Klammern...) und setzt ihn in
 * Kleinbuchstaben.
 *
 * @author baumann
 *
 */
public class Idn2BioName_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Idn2BioName_Generator() {
		super(FOLDER + "ppn2BioName.out", BiMultimap.createSetMap(),
				"Bio: PPN <-> Name");
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, String> ppn2names = (BiMultimap<Integer, String>) data;

	@Override
	public void process(final Record record) {
		try {
			if (!GNDUtils.containsGNDClassifications(record, "24.3", "25.3"))
				return;
			String name;
			try {
				name = RDAFormatter.getRDAHeading(record);
			} catch (final IllFormattedLineException e) {
				name = GNDUtils.getNameOfRecord(record);
			}
			if (StringUtils.isNullOrEmpty(name))
				return;
			// name = name.trim();
			// name = name.toLowerCase();
			// name = StringUtils.unicodeComposition(name);
			final int ppnI = IDNUtils.ppn2int(record.getId());
			ppn2names.add(ppnI, name);

		} catch (final IllegalStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public BiMultimap<Integer, String> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadBiMultimap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		return null;
	}

}
