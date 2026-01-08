package baumann.musik;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.RecordUtils;

public class Orgel {

	public static void main(final String[] args) throws IOException {
		RecordReader.getMatchingReader(Constants.Tu).forEach(r ->
		{
			final Integer anz1 = anzahlInstrumente(r);
			final int anz2 = anzahlVerknuepfteInstrumente(r);
			if (anz1 == null && anz2 == 1)
				System.out.println(r.getId());
		});

	}

	/**
	 *
	 * @param record
	 *            nicht null
	 *
	 * @return Anzahl der Instrumente aus 382 $2, sofern ermittelbar, sonst null
	 */
	public static Integer anzahlInstrumente(final Record record) {
		final Set<String> anzahlen = new HashSet<>(
				RecordUtils.getContentsOfAllSubfields(record, "382", 's'));
		if (anzahlen.isEmpty())
			return null;
		if (anzahlen.size() > 1) {
			return null;
		}
		final String anzStr = ListUtils.getFirst(anzahlen);
		try {
			return Integer.parseInt(anzStr);
		} catch (final NumberFormatException e) {
			System.err.println("Falsches Format: " + record.getId());
			return null;
		}
	}

	public static int anzahlVerknuepfteInstrumente(final Record r) {
		final Set<String> anzahlen = new HashSet<>(
				RecordUtils.getContentsOfAllSubfields(r, "382", '9'));
		return anzahlen.size();
	}

}
