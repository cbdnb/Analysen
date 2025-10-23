package baumann.skurriles.jhr2025;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;

public class Test {

	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		reader.setStreamFilter(new ContainsTag("5590", BibTagDB.getDB()));
		reader.forEach(record ->
		{
			if (RecordUtils.containsField(record, "5590"))
				System.err.println(record);
		});

	}

}
