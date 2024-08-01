/**
 *
 */
package horstkotte;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class DoppelteSWW {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		final Predicate<String> filter5100 = new ContainsTag("5100",
				BibTagDB.getDB());

		reader.setStreamFilter(filter5100);

		reader.forEach(record ->
		{
			if (BibRecUtils.isMagazine(record) || BibRecUtils.isOnline(record))
				return;
			SubjectUtils.getRswkSequencesOfContent(record).forEach(seq ->
			{
				final List<String> multipleElements = ListUtils
						.getMultipleElements(seq);
				if (!multipleElements.isEmpty()) {
					final String out = StringUtils.concatenate("\t",
							record.getId(), SGUtils.getDDCDHS(record),
							RecordUtils.getDatatype(record), multipleElements);
					System.out.println(out);
				}
			});
		});

	}

}
