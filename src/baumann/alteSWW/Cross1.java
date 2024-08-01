/**
 *
 */
package baumann.alteSWW;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class Cross1 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Frequency<String> frequency = new Frequency<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("5530", BibTagDB.getDB()));
		reader.forEach(record ->
		{
			final boolean containsRswk = SubjectUtils.containsRSWK(record);
			final boolean contains5530 = RecordUtils.containsField(record,
					"5530");
			if (contains5530 && containsRswk) {
				frequency.add("RSWK und 5530");
			}
			if (contains5530 && !containsRswk) {
				frequency.add("nur 5530");
			}
		});
		System.out.println(frequency);

	}

}
