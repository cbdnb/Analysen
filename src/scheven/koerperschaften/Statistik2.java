package scheven.koerperschaften;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class Statistik2 {

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);

		final BibTagDB db = BibTagDB.getDB();
		reader.setStreamFilter(new ContainsTag("3100", db)
				.or(new ContainsTag("3110", db)).or(new ContainsTag("3119", db))
				.or(new ContainsTag("5100", db)));
		final HashSet<Integer> idnsSplit = CollectionUtils
				.loadHashSet(Clustern.SET);
		int relationierte = 0;
		for (final Record record : reader) {
			final Set<Integer> idns = new HashSet<Integer>();
			idns.addAll(IDNUtils.ppns2ints(
					BibRecUtils.getAlleKoerperschaftIDsDerFE(record)));
			idns.addAll(IDNUtils.ppns2ints(SubjectUtils.getRSWKids(record)));

			if (!CollectionUtils.intersection(idns, idnsSplit).isEmpty()) {
				relationierte++;
				// System.err.println(record.getId());
			}

		}

		System.out.println("Relationiert mit Splits: " + relationierte);

	}

}
