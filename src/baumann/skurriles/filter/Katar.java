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
public class Katar implements Predicate<Record> {

	public static final String IDNS = SkurConstants.FOLDER + "Katar.idns";

	public static HashSet<Integer> idns = null;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args) {
		final Record record = RecordUtils.readFromClip();
		System.out.println(new Katar().test(record));
	}

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void make() throws IOException, ClassNotFoundException {
		idns = new HashSet<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);
		reader.setStreamFilter(new ContainsTag("043", GNDTagDB.getDB()));

		reader.forEach(record ->
		{
			if (!GNDUtils.getCountryCodes(record).contains("XB-QA"))
				return;
			Katar.idns.add(IDNUtils.ppn2int(record.getId()));
		});
		// System.err.println(Katar.idns.size());
		CollectionUtils.save(Katar.idns, IDNS);

	}

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void load() {
		try {
			Katar.idns = CollectionUtils.loadHashSet(IDNS);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean test(final Record record) {
		if (idns == null)
			load();
		if (CollectionUtils.intersects(RecordUtils.getAllIDNints(record),
				idns))
			return true;
		final String title = BibRecUtils.getHaupttitelUndZusatz(record);
		return StringUtils.contains(title, "qatar", true)
				|| StringUtils.containsWord(title, "katar", true);
	}

}
