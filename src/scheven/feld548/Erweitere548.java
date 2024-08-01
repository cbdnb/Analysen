/**
 *
 */
package scheven.feld548;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 *         Zu Listen mit IDNs sollen die idn / nid / 005 Satzart+Level / 903 r /
 *         1XX / 548 Unterfeld a(b,c,d) in eine Exceltabelle geschrieben werden.
 *
 */
public class Erweitere548 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final String idnListe = StringUtils.readClipboard();
		final Set<String> idns = new HashSet<>(IDNUtils.extractIDNs(idnListe));

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);

		String out = "";

		for (final Record record : reader) {
			final String idn = record.getId();
			if (!idns.contains(idn))
				continue;

			final List<String> unterfelder548 = RecordUtils
					.getContentsOfAllSubfields(record, "548", 'a');

			final ArrayList<String> testListe = new ArrayList<>(unterfelder548);
			FilterUtils.filter(testListe, Util.istZulaessig.negate());
			if (testListe.isEmpty())
				continue;

			final String nid = GNDUtils.getNID(record);
			final String kat005 = RecordUtils.getDatatype(record);
			final String verbund = GNDUtils.getIsilVerbund(record);
			final String name = GNDUtils.getNameOfRecord(record);

			final String spalten548 = StringUtils.concatenate("\t",
					unterfelder548);
			final String zeile =

					StringUtils.concatenateTab(idn, nid, kat005, verbund, name,
							spalten548);

			out += "\n" + zeile;
			System.err.println(zeile);
		}

		StringUtils.writeToClipboard(out);

	}

}
