/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
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
public class Idn2BioVerweisung_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Idn2BioVerweisung_Generator() {
		super(FOLDER + "ppn2BioVerweisung.out", BiMultimap.createSetMap(),
				"Bio: PPN <-> Verweisungen (n:m)");
	}

	@SuppressWarnings("unchecked")
	final BiMultimap<Integer, String> ppn2names = (BiMultimap<Integer, String>) data;

	@Override
	public void process(final Record record) {
		if (!GNDUtils.containsGNDClassifications(record, "24.3", "25.3"))
			return;
		final int ppnI = IDNUtils.ppn2int(record.getId());
		try {
			final Set<String> verweisungen = RDAFormatter
					.getReineRDAVerweise(record);
			verweisungen.forEach(vw ->
			{
				if (StringUtils.isNullOrEmpty(vw))
					return;
				// vw = vw.trim();
				// vw = vw.toLowerCase();
				// vw = StringUtils.unicodeComposition(vw);
				ppn2names.add(ppnI, vw);
			});

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
