/**
 *
 */
package baumann.skurriles.filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import baumann.skurriles.SkurConstants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class FIFA_WM_Katar implements Predicate<Record> {

	private static final String IDNS = SkurConstants.FOLDER + "KatarWM.idns";
	private static HashSet<Integer> idns = null;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main1(final String[] args) throws IOException {
		make();
	}

	public static void main(final String[] args) {
		final Record record = RecordUtils.readFromClip();
		System.out.println(new FIFA_WM_Katar().test(record));
	}

	@Override
	public boolean test(final Record record) {
		if (idns == null)
			load();
		if (CollectionUtils.intersects(RecordUtils.getAllIDNints(record),
				idns))
			return true;
		final String title = BibRecUtils.getHaupttitelUndZusatz(record);
		// System.err.println(title);
		return StringUtils.contains(title, "fussballwelt", true)
				|| (StringUtils.containsWord(title, "WM", true)
						|| StringUtils.contains(title, "weltmeist", true))
						&& StringUtils.contains(title, "2022", true)
				|| StringUtils.containsWord(title, "fifa", true)
				|| StringUtils.contains(title, "qatar", true)
				|| StringUtils.containsWord(title, "katar", true);
	}

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void load() {
		try {
			idns = CollectionUtils.loadHashSet(IDNS);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @throws IOException
	 */
	public static void make() throws IOException {
		final Path path = Paths.get(SkurConstants.FOLDER,
				"IDN-Liste_WM_2022.txt");
		final List<String> lines = Files.readAllLines(path);
		idns = new HashSet<>(lines.stream().map(IDNUtils::ppn2int)
				.collect(Collectors.toList()));
		CollectionUtils.save(idns, IDNS);
	}

}
