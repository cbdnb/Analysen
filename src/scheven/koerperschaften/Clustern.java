package scheven.koerperschaften;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.UnionFind;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;

public class Clustern {

	public static final String FOLDER = "D:/Analysen/scheven/Tb/";
	public static final String PARTITIONEN = FOLDER + "partitionen.txt";
	public static final String STAT = FOLDER + "statistik.txt";
	public static final String SET = FOLDER + "gesplittete.set";

	public static void main(final String[] args) throws IOException {
		final PrintWriter outPartitions = MyFileUtils.outputFile(PARTITIONEN,
				false);
		final PrintWriter outStat = MyFileUtils.outputFile(STAT, false);
		final UnionFind<Integer> uf = new UnionFind<Integer>(null);
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tb);
		reader.forEach(rec ->
		{
			final Collection<Integer> ids = GNDUtils.getVorgNachf(rec);
			if (ids.isEmpty())
				return;
			final int thisID = IDNUtils.ppn2int(rec.getId());
			ids.forEach(id -> uf.union(id, thisID));
		});

		final Frequency<Integer> statLaenge = new Frequency<Integer>();

		final Set<Integer> roots = uf.getRoots();
		for (final Integer root : roots) {
			final Set<Integer> cluster = uf.getCluster(root);
			outPartitions.println(IDNUtils.ints2ppns(cluster));
			statLaenge.add(cluster.size());
		}
		MyFileUtils.safeClose(outPartitions);
		outStat.println(statLaenge);
		MyFileUtils.safeClose(outStat);

		final HashSet<Integer> gesplittete = new HashSet<Integer>(
				uf.getElements());
		CollectionUtils.save(gesplittete, SET);

	}

}
