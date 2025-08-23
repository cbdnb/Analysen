package baumann.musik;

import java.util.GregorianCalendar;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.TimeUtils;

public class ErzeugeDurMoll {
	private static final String TEMPLATE_MOLL = """
			005 Ts1
			008 saz
			011 s
			040 $frswk
			065 14.4
			083 781.262$d2$t%s
			150 %s
			450 %s
			450 %s
			450 %s
			550 !947815856!$4obge
			550 !041856562!$4obge
			670 Wikipedia$bStand: %s$u%s
			677 Tonart des Tongeschlechts Moll, die auf dem Grundton %s aufbaut.""";

	static final String TEMPLATE_DUR = """
			005 Ts1
			008 saz
			011 s
			040 $frswk
			065 14.4
			083 781.262$d2$t%s
			150 %s
			450 %s
			450 %s
			450 %s
			550 !947815562!$4obge
			550 !041856562!$4obge
			670 Wikipedia$bStand: %s$u%s
			677 Tonart des Tongeschlechts Dur, die auf dem Grundton %s aufbaut.""";

	final static int DE = 0;
	final static int EN = 1;
	final static int FR = 2;
	final static int IT = 3;
	final static int WIKI = 4;
	final static int GESCHLECHT = 5;
	final static int IDN = 6;

	public static void main(final String[] args) {
		StringUtils.readLinesFromClip()
				.forEach(ErzeugeDurMoll::verarbeiteDurMoll);

	}

	private static void verarbeiteDurMoll(final String line) {
		if (StringUtils.isNullOrWhitespace(line))
			return;

		final String[] excel = line.trim().split("\t");
		final String de = StringUtils.getArrayElement(excel, DE);
		final String en = StringUtils.getArrayElement(excel, EN);
		final String fr = StringUtils.getArrayElement(excel, FR);
		final String it = StringUtils.getArrayElement(excel, IT);
		final String wiki = StringUtils.getArrayElement(excel, WIKI);
		final String geschlecht = StringUtils.getArrayElement(excel,
				GESCHLECHT);
		final String idn = StringUtils.getArrayElement(excel, IDN);
		final GregorianCalendar now = new GregorianCalendar();
		final String jjjjmmtt = TimeUtils.toYYYYMMDD(now);
		final String ttmmjjjj = TimeUtils.toDDMMYYYY(now);
		final String grundton = de.split("-")[0].toLowerCase();
		String ausgabe = "";

		if (geschlecht.equals("Dur")) {
			ausgabe = TEMPLATE_DUR.formatted(jjjjmmtt, de, en, fr, it, ttmmjjjj,
					wiki, grundton);
		} else if (geschlecht.equals("Moll")) {
			ausgabe = TEMPLATE_MOLL.formatted(jjjjmmtt, de, en, fr, it,
					ttmmjjjj, wiki, grundton);
		}

		if (idn != null)
			System.out.println(
					"############### Vorhanden: " + idn + " ##############");
		System.out.println(ausgabe);
		System.out.println();

	}

}
