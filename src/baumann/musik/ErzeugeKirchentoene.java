package baumann.musik;

import java.util.GregorianCalendar;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.TimeUtils;

public class ErzeugeKirchentoene {

	static final String TEMPLATE_MODUS = """
			005 Ts1
			008 saz
			011 s
			040 $frswk
			065 14.4
			083 781.263$d2$t%s
			150 %s
			450 %s
			450 %s
			450 %s
			450 %s
			450 %s
			550 !041950585!$4obge
			550 %s$4vbal
			670 Riemann$bunter Kirchentöne
			670 Wikipedia$bStand: %s$u%s""";

	final static int DE = 0;
	final static int EN = 1;
	final static int FR = 2;
	final static int IT = 3;
	final static int GR = 4;
	final static int ALT = 5;
	final static int WIKI = 6;
	final static int MODUS_REL = 7;
	final static int IDN = 8;

	public static void main(final String[] args) {
		StringUtils.readLinesFromClip()
				.forEach(ErzeugeKirchentoene::verarbeiteDurMoll);

	}

	private static void verarbeiteDurMoll(final String line) {
		if (StringUtils.isNullOrWhitespace(line))
			return;

		final String[] excel = line.trim().split("\t");
		final String de = StringUtils.getArrayElement(excel, DE);
		final String en = StringUtils.getArrayElement(excel, EN) + " mode";
		final String fr = "Mode " + StringUtils.getArrayElement(excel, FR);
		final String it = "Modo " + StringUtils.getArrayElement(excel, IT);
		final String gr = StringUtils.getArrayElement(excel, GR);
		String alt = StringUtils.getArrayElement(excel, ALT);
		alt = !StringUtils.isNullOrWhitespace(alt) ? alt + " Kirchenton" : "";
		final String wiki = StringUtils.getArrayElement(excel, WIKI);
		final String modus_rel = StringUtils.getArrayElement(excel, MODUS_REL);
		final String idn = StringUtils.getArrayElement(excel, IDN);
		final GregorianCalendar now = new GregorianCalendar();
		final String jjjjmmtt = TimeUtils.toYYYYMMDD(now);
		final String ttmmjjjj = TimeUtils.toDDMMYYYY(now);

		String ausgabe = "";

		ausgabe = TEMPLATE_MODUS.formatted(jjjjmmtt, de, gr, alt, en, fr, it,
				modus_rel, ttmmjjjj, wiki);

		if (idn != null)
			System.out.println(
					"############### Vorhanden: " + idn + " ##############");
		System.out.println(ausgabe);
		System.out.println();

	}

}
