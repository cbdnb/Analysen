/**
 *
 */
package baumann.statistiken.zugang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;

/**
 * @author baumann
 *
 */
public class TabelleUmorganisieren {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final List<String> sgg = Arrays.asList(
				"000: Allgemeines, Informatik, Informationswissenschaft",
				"100: Philosophie und Psychologie", "200: Religion",
				"300: Sozialwissenschaften", "400: Sprache",
				"500: Naturwissenschaften und Mathematik",
				"600: Technik, Medizin, angewandte Wissenschaften",
				"700: Künste und Unterhaltung", "800: Literatur",
				"900: Geschichte und Geografie", "B: Belletristik",
				"K: Kinder- und Jugendliteratur", "null", "S: Schulbücher");

		final List<String> jahre = Arrays.asList("2018", "2019", "2020", "2021",
				"2022", "2023");

		final ArrayList<String> zahlen = new ArrayList<>(
				StringUtils.readLinesFromClip());

		System.out.println("\t" + StringUtils.concatenate("\t", sgg));

		for (int jahr = 0; jahr < jahre.size(); jahr++) {
			String zeile = jahre.get(jahr);
			for (int sg = 0; sg < sgg.size(); sg++) {
				zeile += "\t" + zahlen.get(jahr * sgg.size() + sg);
			}
			System.out.println(zeile);

		}

	}

}
