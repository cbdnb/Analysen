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
public class Klima implements Predicate<Record> {

	private static final String IDNS = SkurConstants.FOLDER + "klima.idns";
	private static HashSet<Integer> idns = null;

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
		System.out.println(new Klima().test(record));
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
		if (CollectionUtils.intersects(RecordUtils.getAllIDNints(record),
				idns))
			return true;
		final String title = BibRecUtils.getHaupttitelUndZusatz(record);
		// System.err.println(title);
		return StringUtils.contains(title, "climate", true)
				&& StringUtils.contains(title, "change", true)
				|| StringUtils.containsWord(title, "klima", true)
				|| StringUtils.contains(title, "klimawan", true)
				|| StringUtils.contains(title, "klimaänd", true)
				|| StringUtils.contains(title, "klimakata", true)
				|| StringUtils.contains(title, "klimaschut", true)
				|| StringUtils.contains(title, "klimaerwär", true)
				|| StringUtils.contains(title, "global warming", true);
	}

	/**
	 * @throws IOException
	 */
	public static void make() throws IOException {
		final Path path = Paths.get(SkurConstants.FOLDER, "klima.txt");
		final List<String> lines = Files.readAllLines(path);
		idns = new HashSet<>(lines.stream().map(IDNUtils::ppn2int)
				.collect(Collectors.toList()));
		CollectionUtils.save(idns, IDNS);
	}

}
