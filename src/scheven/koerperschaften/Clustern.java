package scheven.koerperschaften;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.UnionFind;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;

public class Test {

	public static final String FOLDER = "D:/Analysen/scheven/Tb/";

	public static void main(final String[] args) throws IOException {
		final PrintWriter out = de.dnb.basics.applicationComponents.MyFileUtils
				.outputFile(FOLDER + "test.txt", false);
		final UnionFind<Integer> uf = new UnionFind<Integer>(null);
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tb);
		reader.forEach(rec ->
		{
			final Collection<Integer> ids = GNDUtils.getPrePostIDs(rec);
			if (ids.isEmpty())
				return;
			final int thisID = IDNUtils.ppn2int(rec.getId());
			ids.forEach(id -> uf.union(id, thisID));
		});

		final Frequency<Integer> statLaenge = new Frequency<Integer>();

		final Set<Integer> roots = uf.getRoots();
		for (final Integer root : roots) {
			final Set<Integer> cluster = uf.getCluster(root);
			out.println(IDNUtils.ints2ppns(cluster));
			statLaenge.add(cluster.size());
		}
		de.dnb.basics.applicationComponents.MyFileUtils.safeClose(out);
		System.out.println(statLaenge);

	}

}
