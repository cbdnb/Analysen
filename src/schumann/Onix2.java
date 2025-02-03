package schumann;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class Onix2 {//

	public static enum STANDORT {
		FRANKFURT("1245"), LEIPZIG("1145");

		String code;

		/**
		 * @param code
		 */
		private STANDORT(final String code) {
			this.code = code;
		}

	};

	static Pattern outputP = Pattern.compile("Output: +(\\d+)");
	static Pattern sourceP = Pattern.compile("Source code +: +(\\d+)");
	static Pattern dateP = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}) ");

	static int fgesamt = 0;
	static int lgesamt = 0;

	public static void main(final String[] args)
			throws IOException, ChunkNotFoundException {
		System.out.println(
				StringUtils.concatenateTab("Datum", "L", "F", "gesamt"));

		Stream.of(new File(
				"//dnbf-fs01/DNB-Gesamt/03_FB_EE/13_BF/_Bestandsaufbau/MVB-Zahlen/2024_Statistikmails")
						.listFiles())
				.filter(file -> !file.isDirectory())
				.forEach(Onix2::printMailData);

		System.out.println("Frankfurt insgesamt: " + fgesamt);
		System.out.println("Leipzig insgesamt: " + lgesamt);

	}

	private static void printMailData(final File msg) {
		MAPIMessage mapimsg;
		String text;
		Calendar cal;
		String datS;
		try {
			mapimsg = new MAPIMessage(msg.getAbsolutePath());
			text = mapimsg.getTextBody();
			cal = mapimsg.getMessageDate();
		} catch (final IOException | ChunkNotFoundException e) {
			return;
		}
		final Matcher dateM = dateP.matcher(text);
		if (dateM.find())
			datS = dateM.group(1);
		else
			datS = TimeUtils.toYYYYMMDD(cal);

		final String[] teile = text
				.split("Read |Inserted | Updated | Deleted | Skipped", 2);
		if (teile.length != 2) {
			System.err.println("hä?");
			return;
		}

		int zahlF = 0;
		int zahlL = 0;

		for (final String teil : teile) {
			STANDORT standort = null;
			final Matcher sourceM = sourceP.matcher(teil);
			if (sourceM.find()) {
				final String source = sourceM.group(1);
				if (source.equals(STANDORT.LEIPZIG.code))
					standort = STANDORT.LEIPZIG;
				else if (source.equals(STANDORT.FRANKFURT.code))
					standort = STANDORT.FRANKFURT;
			}
			final Matcher outputM = outputP.matcher(teil);
			if (outputM.find()) {
				final int zahl = Integer.parseInt(outputM.group(1));
				if (standort == STANDORT.LEIPZIG)
					zahlL = zahl;
				else
					zahlF = zahl;
			}
		}
		System.out.println(
				StringUtils.concatenateTab(datS, zahlL, zahlF, zahlL + zahlF));
		lgesamt += zahlL;
		fgesamt += zahlF;

	}

}
