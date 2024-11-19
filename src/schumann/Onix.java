package schumann;

import java.io.IOException;
import java.text.ParseException;
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

	public static enum STANDORT {
		FRANKFURT, LEIPZIG
	};

	static SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
	static Pattern outputP = Pattern.compile("Output: (\\d+)");

	public static void main(final String[] args) throws IOException {
		Date dateAktuell = getDate("1900-01-01");
		STANDORT naechsterStandortErwartet = STANDORT.LEIPZIG;
		int zahlF = 0;
		int zahlL = 0;
		boolean leipzigFound = false;
		System.out.println(StringUtils.concatenateTab("Datum", "L", "F"));

		final List<String> lines = StringUtils
				.readLinesFromFile("D:/MVB/2024.txt");
		for (final String line : lines) {
			final Date date = getDate(line);
			// Neuer Datensatz?
			if (date != null) {
				if (!dateAktuell.equals(date)
						&& !dateAktuell.equals(getDate("1900-01-01"))) {
					// Ausgabe:
					System.out.println(StringUtils.concatenateTab(
							TimeUtils.toYYYYMMDD(dateAktuell), zahlL, zahlF));
				}
				dateAktuell = date;
			}
			final Matcher outputM = outputP.matcher(line); // findet String
															// "Output"
			if (outputM.find()) {
				final int zahl = Integer.parseInt(outputM.group(1));
				if (!leipzigFound) {// Leipzig ist das erste
					leipzigFound = true;
					zahlL = zahl;
					naechsterStandortErwartet = STANDORT.FRANKFURT;
				} else {
					zahlF = zahl;
					leipzigFound = false;
					naechsterStandortErwartet = STANDORT.LEIPZIG;
				}
			}

		}
		// Letzte Ausgabe:
		if (dateAktuell != null)
			System.out.println(StringUtils.concatenateTab(
					TimeUtils.toYYYYMMDD(dateAktuell), zahlL, zahlF));

	}

	static Date getDate(final String s) {
		Date date = null;
		try {
			date = parser.parse(s);
		} catch (final ParseException e) {
		}
		return date;

	}

	public static void main1(final String[] args) {
		final String s = StringUtils.readClipboard();
		System.out.println(getDate(s));
	}

}
