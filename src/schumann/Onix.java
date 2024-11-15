package schumann;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.TimeUtils;

/**
 * Liest die MVB-Einspielungen. Zuerst kommt ein String wie:
 *
 * ** Tue Nov 5 11:00:02 CET 2024: Neue MVB-Daten
 *
 * Das ergibt das Datum. Der Java-Parser kann mit CET seltsamerweise nichts
 * anfangen. Das wird daher entfernt. Danach kommen zunächst die Leipziger, dann
 * die Frankfurter Werte. Diese sind an dem Schnipsel (z.B.)
 *
 * Output: 197
 *
 * erkennbar.
 */
public class Onix {

	static SimpleDateFormat parser = new SimpleDateFormat(
			"LLL dd HH:mm:ss yyyy");
	static Pattern zahlP = Pattern.compile("Output: (\\d+)");

	public static void main(final String[] args) throws IOException {
		Date dateAktuell = null;
		int zahlF = 0;
		int zahlL = 0;
		boolean leipzigFound = false;
		System.out.println(StringUtils.concatenateTab("Datum", "L", "F"));

		final List<String> lines = StringUtils
				.readLinesFromFile("D:/Temp/AW Frage zu MVB 1.txt");
		for (final String line : lines) {
			final Date date = getDate(line);
			// Neuer Datensatz:
			if (date != null) {
				if (dateAktuell != null) {
					// Ausgabe:
					System.out.println(StringUtils.concatenateTab(
							TimeUtils.toYYYYMMDD(dateAktuell), zahlL, zahlF));
				}
				dateAktuell = date;
				leipzigFound = false;
			}
			final Matcher matcher = zahlP.matcher(line);
			if (matcher.find()) {
				final int zahl = Integer.parseInt(matcher.group(1));
				if (!leipzigFound) {
					leipzigFound = true;
					zahlL = zahl;
				} else
					zahlF = zahl;
			}

		}
		// Letzte Ausgabe:
		if (dateAktuell != null)
			System.out.println(StringUtils.concatenateTab(
					TimeUtils.toYYYYMMDD(dateAktuell), zahlL, zahlF));

	}

	static Date getDate(String s) {
		if (s == null)
			return null;
		// Date date = null;
		s = s.replace("CET", "");
		return parser.parse(s, new ParsePosition(7));
		// return date;
	}

}
