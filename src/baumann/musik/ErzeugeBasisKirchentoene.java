package baumann.musik;

import java.util.GregorianCalendar;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.TimeUtils;

public class ErzeugeBasisKirchentoene {

	enum SPALTEN {
		DE, EN, FR, IT, GR, ALT_DE, WIKI, NUM_DE, NUM_IT, NUM_EN, NUM_FR, IDN, VB, FINALIS
	}

	static final String TEMPLATE_MODUS = """
			005 Ts1
			008 saz
			011 s
			040 $frswk
			065 14.4
			083 781.263$d2$t%s
			150 %ser Kirchenton
			450 %ser Modus
			450 %s$gMusik
			450 %s
			450 %s
			450 %s
			450 %s
			450 %s$gKirchenton
			450 %s
			450 %s$gKirchenton
			450 %s$gKirchenton
			450 %s$gKirchenton
			550 !041950585!$4obge
			550 !041703375!$4obge
			550 !%s!$4vbal
			670 Riemann$bunter Kirchentöne
			670 Wikipedia$bStand: %s$u%s
			677 %s Kirchentonart mit Finalis %s.""";

	public static void main(final String[] args) {

		StringUtils.readLinesFromClip()
				.forEach(ErzeugeBasisKirchentoene::verarbeiteDurMoll);

	}

	private static void verarbeiteDurMoll(final String line) {
		if (StringUtils.isNullOrWhitespace(line))
			return;

		final String[] excel = line.trim().split("\t");
		final String de = StringUtils
				.getArrayElement(excel, SPALTEN.DE.ordinal()).trim();
		final String en = StringUtils
				.getArrayElement(excel, SPALTEN.EN.ordinal()).trim();
		final String fr = StringUtils
				.getArrayElement(excel, SPALTEN.FR.ordinal()).trim();
		final String it = StringUtils
				.getArrayElement(excel, SPALTEN.IT.ordinal()).trim();
		final String gr = StringUtils
				.getArrayElement(excel, SPALTEN.GR.ordinal()).trim();
		String numDEverbal = StringUtils
				.getArrayElement(excel, SPALTEN.ALT_DE.ordinal()).trim();
		numDEverbal = !StringUtils.isNullOrWhitespace(numDEverbal)
				? numDEverbal + " Kirchenton"
				: "";
		final String wiki = StringUtils.getArrayElement(excel,
				SPALTEN.WIKI.ordinal());
		final String numDE = StringUtils
				.getArrayElement(excel, SPALTEN.NUM_DE.ordinal()).trim();
		final String numEN = StringUtils
				.getArrayElement(excel, SPALTEN.NUM_EN.ordinal()).trim();
		final String numFR = StringUtils.capitalize(
				StringUtils.getArrayElement(excel, SPALTEN.NUM_FR.ordinal()))
				.trim();
		final String numIT = StringUtils.capitalize(
				StringUtils.getArrayElement(excel, SPALTEN.NUM_IT.ordinal()))
				.trim();
		final String idn = StringUtils
				.getArrayElement(excel, SPALTEN.IDN.ordinal()).trim();
		final String vb = StringUtils
				.getArrayElement(excel, SPALTEN.VB.ordinal()).trim();
		final String authPlag = de.startsWith("Hypo") ? "Plagale"
				: "Authentische";
		final String finalis = StringUtils.getArrayElement(excel,
				SPALTEN.FINALIS.ordinal());
		final GregorianCalendar now = new GregorianCalendar();
		final String jjjjmmtt = TimeUtils.toYYYYMMDD(now);
		final String ttmmjjjj = TimeUtils.toDDMMYYYY(now);

		String ausgabe = "";

		ausgabe = TEMPLATE_MODUS.formatted(jjjjmmtt, de, de, de, en, fr, it, gr,
				numDE, numDEverbal, numEN, numFR, numIT, vb, ttmmjjjj, wiki,
				authPlag, finalis);

		if (!StringUtils.isNullOrWhitespace(idn))
			System.out.println(
					"############### Vorhanden: " + idn + " ##############");
		System.out.println(ausgabe);
		System.out.println();

	}

}
