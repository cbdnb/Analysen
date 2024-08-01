/**
 *
 */
package baumann.skurriles.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.function.Predicate;

import baumann.skurriles.SkurConstants;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Ukraine implements Predicate<Record> {

	static HashSet<Integer> idns = null;

	public static final String IDNS = SkurConstants.FOLDER + "Ukraine.idns";

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main1(final String[] args)
			throws IOException, ClassNotFoundException {
		make();
	}

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		final Record record = RecordUtils.readFromClip();
		System.out.println(new Ukraine().test(record));
	}

	/**
	 * @throws IOException
	 */
	public static void make() throws IOException {
		idns = new HashSet<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);
		reader.setStreamFilter(new ContainsTag("043", GNDTagDB.getDB()));

		reader.forEach(record ->
		{
			if (!GNDUtils.getCountryCodes(record).contains("XA-UA"))
				return;
			idns.add(IDNUtils.ppn2int(record.getId()));
		});
		// System.err.println(idns.size());
		CollectionUtils.save(idns, IDNS);
	}

	public static void load() {
		try {
			idns = CollectionUtils.loadHashSet(IDNS);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean test(final Record record) {
		if (idns == null)
			load();
		if (CollectionUtils.containsAny(RecordUtils.getAllIDNints(record),
				idns))
			return true;
		final String title = BibRecUtils.getHaupttitelUndZusatz(record);
		return StringUtils.contains(title, "ukrain", true)
				|| StringUtils.containsWord(title, "ukrajn", true)
				|| StringUtils.containsWord(title, "Украина", true)
				|| StringUtils.containsWord(title, "Украïна", true);
	}

}
