package baumann;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;

public class TonartenTitel {

	public static void main(final String[] args) throws IOException {
		System.err.println("los");
		final RecordReader matchingReader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_D);
		matchingReader
				.setStreamFilter(new ContainsTag("3217", BibTagDB.getDB()));
		matchingReader.forEach(rec ->
		{
			if (RecordUtils.containsField(rec, "3217"))
				System.out.println(rec.getId());
		});
		System.err.println("feddisch");

	}

}
