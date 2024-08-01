/**
 *
 */
package baumann.alteSWW;

import java.io.IOException;
import java.util.ArrayList;
import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 *
 * Um welche Mengen handelt es sich? – bezogen auf Titel Welche haben nur F,
 * welche nur L und welche haben von beiden die Erschließung? Wie viele sind
 * O*-Sätze, d.h. durch spätere Verfahren maschinell erzeugt? Wie viele Titel
 * haben nur Hauptschlagwörter?
 *
 * @author baumann
 *
 */
public class ZahlenAllgemein {

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
			boolean isFFM = false;
			boolean isL = false;
			boolean isO = false;
			boolean onlyHSW = false;
			boolean isVerweisung = false;
			final ArrayList<Line> lines5530 = RecordUtils.getLines(record,
					"5530");
			if (lines5530.isEmpty())
				return;
			if (BibRecUtils.isOnline(record))
				isO = true;
			for (final Line line : lines5530) {
				if (SubfieldUtils.containsIndicator(line, 'a'))
					isFFM = true;
				if (SubfieldUtils.containsIndicator(line, 'g'))
					isL = true;
				if (SubfieldUtils.containsIndicator(line, 'v'))
					isVerweisung = true;
				if (!SubfieldUtils.containsIndicator(line, 'f')
						&& !SubfieldUtils.containsIndicator(line, 'h'))
					onlyHSW = true;

			}

			if (isFFM && !isL)
				frequency.add("nur Frankfurt");
			if (isL && !isFFM)
				frequency.add("nur Leipzig");
			if (isL && isFFM)
				frequency.add("F und L");
			if (isVerweisung)
				frequency.add("Verweisung");
			if (!isL && !isFFM && !isVerweisung)
				System.err.println("Fehler idn " + record.getId());
			if (isO)
				frequency.add("Online-Publikationen");
			if (onlyHSW)
				frequency.add("Nur Hauptschlagwort");

		});

		System.out.println(frequency);

	}

}
