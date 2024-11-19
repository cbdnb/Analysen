package schumann;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;

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
public class Onix2 {

	public static enum STANDORT {
		FRANKFURT, LEIPZIG
	};

	static Pattern outputP = Pattern.compile("Output: (\\d+)");

	public static void main(final String[] args)
			throws IOException, ChunkNotFoundException {
		System.out.println(
				StringUtils.concatenateTab("Datum", "L", "F", "gesamt"));

		Stream.of(new File("V:/Temp/baumann/MVB-Mails/2024").listFiles())
				.filter(file -> !file.isDirectory())
				.forEach(Onix2::printMailData);

		final File msg = new File(
				"V:/Temp/baumann/MVB-Mails/2024/Statistik MVB-Import (1).msg");
		printMailData(msg);

	}

	private static void printMailData(final File msg) {
		MAPIMessage mapimsg;
		String text;
		Calendar cal;
		try {
			mapimsg = new MAPIMessage(msg.getAbsolutePath());
			text = mapimsg.getTextBody();
			cal = mapimsg.getMessageDate();
		} catch (final IOException | ChunkNotFoundException e) {
			return;
		}

		STANDORT naechsterStandortErwartet = STANDORT.LEIPZIG;
		int zahlF = 0;
		int zahlL = 0;

		final List<String> lines = text.lines().collect(Collectors.toList());
		for (final String line : lines) {
			final Matcher outputM = outputP.matcher(line); // findet String
															// "Output"
			if (outputM.find()) {
				final int zahl = Integer.parseInt(outputM.group(1));
				if (naechsterStandortErwartet == STANDORT.LEIPZIG) {// Leipzig
																	// ist das
																	// erste
					zahlL = zahl;
					naechsterStandortErwartet = STANDORT.FRANKFURT;
				} else {
					zahlF = zahl;
				}
			}
		}
		System.out.println(StringUtils.concatenateTab(TimeUtils.toYYYYMMDD(cal),
				zahlL, zahlF, zahlL + zahlF));
	}

}
