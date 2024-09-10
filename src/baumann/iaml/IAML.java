package baumann.iaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.dnb.basics.applicationComponents.strings.StringUtils;

public class IAML {

	private static final char EMAIL = 'M';
	private static final char ANREDE = 'D';
	private static final char TITEL = 'E';
	private static final char NAME = 'F';
	private static final char VORNAME = 'G';

	public static void main(final String[] args) {
		final String[][] excel = StringUtils.readTableFromClip();
		System.out.println("Email-Liste Kopieren");
		final Scanner scanner = new Scanner(System.in);
		scanner.next();
		final String emailstr = StringUtils.readClipboard();
		final Set<String> emails = Stream.of(emailstr.split(" *; *"))
				.map(String::trim).collect(Collectors.toSet());
		for (int i = 1; i <= excel.length; i++) {
			final String email = StringUtils.getExcelCellAt(excel, EMAIL, i)
					.trim();
			if (emails.contains(email)) {
				ausgabe(excel, i);
			}
		}

	}

	static void ausgabe(final String[][] excel, final int i) {
		final List<String> teilnehmner = new ArrayList<String>(5);
		teilnehmner.add(StringUtils.getExcelCellAt(excel, ANREDE, i));
		final String titel = StringUtils.getExcelCellAt(excel, TITEL, i);
		if (!StringUtils.isNullOrEmpty(titel))
			teilnehmner.add(titel);
		teilnehmner.add(StringUtils.getExcelCellAt(excel, VORNAME, i));
		teilnehmner.add(StringUtils.getExcelCellAt(excel, NAME, i));

		System.out.println(StringUtils.concatenate(" ", teilnehmner));
	}

}
