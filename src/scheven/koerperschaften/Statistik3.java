package scheven.koerperschaften;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class Statistik3 {

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		final RecordReader readerKoe = RecordReader
				.getMatchingReader(Constants.Tb);
		final Set<Integer> idnsKoe = new HashSet<>();
		readerKoe.forEach(koe -> idnsKoe.add(IDNUtils.ppn2int(koe.getId())));

		System.err.println("idns Koe geladen: " + idnsKoe.size());

		final RecordReader readerTitle = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz");

		int relationierte = 0;
		for (final Record record : readerTitle) {
			final Set<Integer> idns = new HashSet<Integer>();
			idns.addAll(IDNUtils.ppns2ints(
					BibRecUtils.getAlleKoerperschaftIDsDerFE(record)));
			idns.addAll(IDNUtils.ppns2ints(SubjectUtils.getRSWKids(record)));

			if (CollectionUtils.intersects(idnsKoe, idns)) {
				relationierte++;
				// System.err.println(record.getId());
			}

		}

		System.out.println("Relationiert Körperschaft: " + relationierte);

	}

}
