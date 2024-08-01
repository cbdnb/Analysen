/**
 *
 */
package scheven.feld548;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;

/**
 * Unterfelder a, b und c von Feld 548 bei Tp* auswerten: welche Datensätze
 * haben in den genannten Unterfeldern neben Ziffern noch Buchstaben außer einem
 * führendem v oder X (an verschiedenen Positionen möglich).
 *
 * @author baumann
 *
 */
public class StatistikAbweichend {

	private static Frequency<Character> falsche = new Frequency<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tp);
		reader.setStreamFilter(new ContainsTag("548", GNDTagDB.getDB()));

		reader.forEach(record ->
		{
			final List<String> aa = RecordUtils
					.getContentsOfAllSubfields(record, "548", 'a');
			final List<String> bb = RecordUtils
					.getContentsOfAllSubfields(record, "548", 'b');
			final List<String> cc = RecordUtils
					.getContentsOfAllSubfields(record, "548", 'c');
			final List<String> dd = RecordUtils
					.getContentsOfAllSubfields(record, "548", 'd');

			zaehle(aa, 'a');
			zaehle(bb, 'b');
			zaehle(cc, 'c');
			zaehle(dd, 'd');
		});

		System.out.println(falsche);
	}

	/**
	 * @param subs
	 * @param ind
	 */
	private static void zaehle(final List<String> subs, final char ind) {
		subs.forEach(sub ->
		{
			if (!Util.istZulaessig.test(sub))
				falsche.add(ind);
		});

	}

}
