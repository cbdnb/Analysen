/**
 *
 */
package scheven.expressionen;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.WorkUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 */
public class Fassung_etc {

	final static List<String> SCHLUESSELWOERTER = Arrays.asList("Version",
			"Fassung", "Ausgabe", "Edition", "Nachdruck", "Übersetzung",
			"Bearbeitung", "Umarbeitung", "Text");
	private static PrintWriter pw;

	static String folder = "D:/Analysen/scheven/Expressionen/";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		pw = FileUtils.oeffneAusgabeDatei(
				folder + "Expressionskandidaten" + "_wim_667.." + ".txt",
				false);
		pw.println(StringUtils.concatenateTab("IDN", "Name", "Syst.", "Level",
				"ISIL Verbund"));
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);
		reader.forEach(record -> bearbeite(record, pw));

	}

	/**
	 * @param record
	 * @param writer
	 *            TODO
	 */
	public static void bearbeite(final Record record,
			final PrintWriter writer) {
		if (RecordUtils.isRDA(record))
			return;

		if (!WorkUtils.isMusicalWork(record))
			return;

		// if (!WorkUtils.isMusicalWork(record))
		// return;

		final List<String> felder = RecordUtils.getContents(record,
				"667|678|680", 'a', 'b');

		boolean found = false;
		for (final String schluesselwort : SCHLUESSELWOERTER) {
			for (final String feld : felder) {
				if (StringUtils.containsWordEndTruncated(feld, schluesselwort,
						true))
					found = true;
			}

		}

		if (found) {

			String name;
			try {
				name = RDAFormatter.getRDAHeading(record);
			} catch (final IllFormattedLineException e) {
				name = GNDUtils.getNameOfRecord(record);
			}
			writer.println(StringUtils.concatenateTab(record.getId(), name,
					GNDUtils.getFirstGNDClassification(record),
					GNDUtils.getLevel(record),
					GNDUtils.getIsilVerbund(record)));

			System.err.println(StringUtils.concatenateTab(record.getId(), name,
					GNDUtils.getFirstGNDClassification(record),
					GNDUtils.getLevel(record),
					GNDUtils.getIsilVerbund(record)));
		}
	}

}
