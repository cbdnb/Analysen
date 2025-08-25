package baumann.musik;

import java.util.GregorianCalendar;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.TimeUtils;

public class ErzeugeAbgeleiteteKirchentoene {

	enum SPALTEN {
		DE, EN, FR, IT, GR, OB
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
			450 %s
			450 %s
			550 !%s!$4obge
			670 analog
			677 %s Kirchentonart mit Finalis %s.""";

	public static void main(final String[] args) {

		StringUtils.readLinesFromClip()
				.forEach(ErzeugeAbgeleiteteKirchentoene::verarbeiteDurMoll);

	}

	private static void verarbeiteDurMoll(final String line) {
		if (StringUtils.isNullOrWhitespace(line))
			return;

		final String[] excel = line.trim().split("\t");
		final String de = StringUtils.getArrayElement(excel,
				SPALTEN.DE.ordinal());
		final String en = StringUtils.getArrayElement(excel,
				SPALTEN.EN.ordinal());
		final String fr = StringUtils.getArrayElement(excel,
				SPALTEN.FR.ordinal());
		final String it = StringUtils.getArrayElement(excel,
				SPALTEN.IT.ordinal());
		final String gr = StringUtils.getArrayElement(excel,
				SPALTEN.GR.ordinal());

		final String authPlag = de.contains("Hypo") ? "Plagale"
				: "Authentische";
		final String finalis = de.substring(0, 1).toLowerCase();
		final GregorianCalendar now = new GregorianCalendar();
		final String jjjjmmtt = TimeUtils.toYYYYMMDD(now);

		String ausgabe = "";

		ausgabe = TEMPLATE_MODUS.formatted(jjjjmmtt, de, en, fr, it, gr,
				authPlag, finalis);

		System.out.println(ausgabe);
		System.out.println();

	}

}
