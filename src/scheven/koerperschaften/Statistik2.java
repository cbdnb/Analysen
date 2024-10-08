package scheven.koerperschaften;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class Statistik2 {

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		final RecordReader reader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz");

		final HashSet<Integer> idnsSplit = CollectionUtils
				.loadHashSet(Clustern.SET);
		int relationierte = 0;
		for (final Record record : reader) {
			final Set<Integer> idns = new HashSet<Integer>();
			idns.addAll(IDNUtils.ppns2ints(
					BibRecUtils.getAlleKoerperschaftIDsDerFE(record)));
			idns.addAll(IDNUtils.ppns2ints(SubjectUtils.getRSWKids(record)));

			if (!CollectionUtils.intersects(idnsSplit, idns)) {
				relationierte++;
				// System.err.println(record.getId());
			}

		}

		System.out.println("Relationiert mit Splits: " + relationierte);

	}

}
