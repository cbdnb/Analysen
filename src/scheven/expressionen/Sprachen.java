/**
 *
 */
package scheven.expressionen;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.WorkUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 */
public class Sprachen {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		System.err.println("Sprachen suchen");
		// Alle Sprachen sammeln (sis):
		final Set<String> sprachen = new HashSet<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Ts);
		reader.forEach(record ->
		{
			if (!GNDUtils.containsEntityType(record, "sis"))
				return;

			String sprache;
			try {
				sprache = RDAFormatter.getRDAHeading(record);
			} catch (final IllFormattedLineException e) {
				sprache = GNDUtils.getNameOfRecord(record);
			}
			if (!sprache.isEmpty())
				sprachen.add(sprache.toLowerCase());
		});
		System.err.println("tu mit sprachen suchen");
		// nun alle $g absuchen auf Sprachen:
		final PrintWriter pw = MyFileUtils.oeffneAusgabeDatei(Fassung_etc.folder
				+ "Expressionskandidaten" + "_sprachen_in_130" + ".txt", false);
		final RecordReader tuReader = RecordReader
				.getMatchingReader(Constants.Tu);
		tuReader.forEach(record ->
		{
			if (!WorkUtils.isNormalWork(record))
				return;

			final List<String> felder = RecordUtils.getContents(record, "1..",
					'g');

			boolean found = false;

			for (final String feld : felder) {
				if (sprachen.contains(feld.toLowerCase()))
					found = true;
			}

			if (found) {

				final String name = RecordUtils.getTitle(record);

				pw.println(StringUtils.concatenateTab(record.getId(), name,
						GNDUtils.getFirstGNDClassification(record),
						GNDUtils.getLevel(record),
						GNDUtils.getIsilVerbund(record)));
			}

		});

	}

}
