/**
 *
 */
package scheven;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Field;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class Statistik5530 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("5530", BibTagDB.getDB()));

		final Frequency<List<String>> frequency = new Frequency<>();

		reader.forEach(rec ->
		{
			final Field field5530 = RecordUtils.getFieldGivenAsString(rec,
					"5530");

			field5530.forEach(line ->
			{
				final List<String> subs = new ArrayList<>();
				final String sub_a = SubfieldUtils
						.getContentOfFirstSubfield(line, 'a');
				final List<String> sub_f = SubfieldUtils
						.getContentsOfSubfields(line, 'f');
				final String sub_v = SubfieldUtils
						.getContentOfFirstSubfield(line, 'v');
				if (sub_a != null || !sub_f.isEmpty() || sub_v != null) {
					subs.add(sub_a);
					subs.add(sub_v);
					// f ist wiederholbar, daher am Ende
					subs.addAll(sub_f);
					frequency.add(subs);
				}

			});
		});

		final PrintStream outputStream = new PrintStream(
				"D:/Analysen/scheven/Statistik5530.txt");

		frequency.forEach(list ->
		{
			String s = "" + frequency.get(list);
			s += "\t" + StringUtils.concatenate("\t", list);
			outputStream.println(s);
		});

		MyFileUtils.safeClose(outputStream);

	}

}
