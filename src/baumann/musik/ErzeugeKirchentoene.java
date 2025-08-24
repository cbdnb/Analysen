package baumann.musik;

import java.util.GregorianCalendar;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.TimeUtils;

public class ErzeugeKirchentoene {

	enum SPALTEN {
		DE, EN, FR, IT, GR, ALT_DE, WIKI, NUM_DE, NUM_IT, NUM_EN, NUM_FR, IDN
	}

	static final String TEMPLATE_MODUS = """
			005 Ts1
			008 saz
			011 s
			040 $frswk
			065 14.4
			083 781.263$d2$t%s
			150 %s
			450 %s
			450 %s$gMusik
			450 %s
			450 %s
			450 %s
			450 %s
			450 %s
			450 %s
			450 %s
			450 %s
			450 %s
			550 !041950585!$4obge
			550 !041703375!$4obge
			670 Riemann$bunter Kirchentöne
			670 Wikipedia$bStand: %s$u%s""";

	public static void main(final String[] args) {

		StringUtils.readLinesFromClip()
				.forEach(ErzeugeKirchentoene::verarbeiteDurMoll);

	}

	private static void verarbeiteDurMoll(final String line) {
		if (StringUtils.isNullOrWhitespace(line))
			return;

		final String[] excel = line.trim().split("\t");
		final String de = StringUtils.getArrayElement(excel,
				SPALTEN.DE.ordinal());
		final String wurzelDE = de.substring(0,
				de.length() - "er Kirchenton".length());
		final String de450_1 = wurzelDE + "er Modus";
		final String en = StringUtils.getArrayElement(excel,
				SPALTEN.EN.ordinal());
		final String fr = StringUtils.getArrayElement(excel,
				SPALTEN.FR.ordinal());
		final String it = StringUtils.getArrayElement(excel,
				SPALTEN.IT.ordinal());
		final String gr = StringUtils.getArrayElement(excel,
				SPALTEN.GR.ordinal());
		String numDEverbal = StringUtils.getArrayElement(excel,
				SPALTEN.ALT_DE.ordinal());
		numDEverbal = !StringUtils.isNullOrWhitespace(numDEverbal)
				? numDEverbal + " Kirchenton"
				: "";
		final String wiki = StringUtils.getArrayElement(excel,
				SPALTEN.WIKI.ordinal());
		final String numDE = StringUtils.getArrayElement(excel,
				SPALTEN.NUM_DE.ordinal());
		final String numEN = StringUtils.getArrayElement(excel,
				SPALTEN.NUM_EN.ordinal());
		final String numFR = StringUtils.capitalize(
				StringUtils.getArrayElement(excel, SPALTEN.NUM_FR.ordinal()));
		final String numIT = StringUtils.capitalize(
				StringUtils.getArrayElement(excel, SPALTEN.NUM_IT.ordinal()));
		final String idn = StringUtils.getArrayElement(excel,
				SPALTEN.IDN.ordinal());
		final GregorianCalendar now = new GregorianCalendar();
		final String jjjjmmtt = TimeUtils.toYYYYMMDD(now);
		final String ttmmjjjj = TimeUtils.toDDMMYYYY(now);

		String ausgabe = "";

		ausgabe = TEMPLATE_MODUS.formatted(jjjjmmtt, de, de450_1, wurzelDE, en,
				fr, it, gr, numDE, numDEverbal, numEN, numFR, numIT, ttmmjjjj,
				wiki);

		if (idn != null)
			System.out.println(
					"############### Vorhanden: " + idn + " ##############");
		System.out.println(ausgabe);
		System.out.println();

	}

}
