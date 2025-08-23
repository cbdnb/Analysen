package baumann.musik;

import java.util.GregorianCalendar;

import de.dnb.basics.utils.TimeUtils;

public class ErzeugeTonarten {
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

	static final String TEMPLATE_MODUS = """
			005 Ts1
			008 saz
			011 s
			040 $frswk
			065 14.4
			083 781.262$d2$t%s
			150 %ser Kirchenton
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
	final static int MODUS = 5;

	public static void main(final String[] args) {
		final String[] excel = """
				A-Dur	A major	la majeur	la maggiore	https://de.wikipedia.org/wiki/A-Dur	Dur
				"""
				.split("\t");
		final String de = excel[DE];
		final String en = excel[EN];
		final String fr = excel[FR];
		final String it = excel[IT];
		final String wiki = excel[WIKI];
		final GregorianCalendar now = new GregorianCalendar();
		final String jjjjmmtt = TimeUtils.toYYYYMMDD(now);
		final String ttmmjjjj = TimeUtils.toDDMMYYYY(now);
		final String grundton = de.toLowerCase().substring(0, 1);
		final String dur = TEMPLATE_DUR.formatted(jjjjmmtt, de, en, fr, it,
				ttmmjjjj, wiki, grundton);

		final String moll = TEMPLATE_MOLL.formatted(jjjjmmtt, de, en, fr, it,
				ttmmjjjj, wiki, grundton);

		System.out.println(dur);
		System.out.println();
		System.out.println(moll);

	}

}
