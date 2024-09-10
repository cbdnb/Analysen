package baumann.iaml;

import de.dnb.basics.applicationComponents.strings.StringUtils;

public class Output {

	public static void main(final String[] args) {
		final String[][] excel = StringUtils.readTableFromClip();
		for (int i = 1; i <= excel.length; i++) {
			IAML.ausgabe(excel, i);
		}

	}

}
