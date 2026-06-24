package baumann.musik;

import java.io.IOException;
import java.util.HashSet;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.WorkUtils;

public class ErzeugeMusikIDs {

	static final String MUSIK_IDNS_FILE = "D:/Analysen/baumann/Musik/musikIDNs.out";

	public static void main(final String[] args) throws IOException {
		final HashSet<Integer> musikIDNs = new HashSet<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);
		reader.forEach(record ->
		{
			if (WorkUtils.isMusicalWork(record)) {
				final String idn = record.getId();
				musikIDNs.add(IDNUtils.idn2int(idn));
			}
		});
		CollectionUtils.save(musikIDNs, MUSIK_IDNS_FILE);

	}

}
