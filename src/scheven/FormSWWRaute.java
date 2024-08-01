/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.Collection;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class FormSWWRaute {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("5100", BibTagDB.getDB()));

		final Multimap<String, String> id2forms = new ListMultimap<>();

		reader.forEach(rec ->
		{
			final Collection<String> forms = SubjectUtils.getFormSWW(rec);
			if (forms.isEmpty())
				return;
			final String id = rec.getId();
			forms.forEach(f ->
			{
				if (f.contains("#"))
					id2forms.add(id, f);
			});
		});

		System.out.println(id2forms);

	}

}
