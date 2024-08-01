/**
 *
 */
package schmidt;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.Mutable;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class AuslOrtsteile {

	static Pattern pattern = Pattern.compile("[^-]+\\-[^-]+");

	static boolean contains1dash(final String s) {
		final Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tg);

		reader.stream().forEach(record ->
		{
			final List<String> ents = GNDUtils.getEntityTypes(record);
			if (!ents.isEmpty())
				return;
			final String dollarA = RecordUtils.getContentOfSubfield(record,
					"151", 'a');
			if (dollarA == null)
				return;
			if (!contains1dash(dollarA))
				return;
			// if (dollarA.contains("-sur-"))
			// return;
			// if (dollarA.contains("-le-"))
			// return;
			// if (dollarA.contains("-les-"))
			// return;
			// if (dollarA.contains("-la-"))
			// return;
			// if (dollarA.contains("-en-"))
			// return;
			if (dollarA.contains("Saint-"))
				return;
			if (dollarA.contains("isch-"))
				return;
			if (dollarA.startsWith("al-"))
				return;
			if (dollarA.startsWith("el-"))
				return;
			if (dollarA.startsWith("ar-"))
				return;
			if (dollarA.startsWith("ad-"))
				return;
			if (dollarA.startsWith("Ober-"))
				return;
			if (dollarA.startsWith("Unter-"))
				return;
			if (dollarA.startsWith("Haut-"))
				return;
			if (dollarA.startsWith("Haute-"))
				return;
			if (dollarA.startsWith("Bas-"))
				return;
			if (dollarA.startsWith("Neu-"))
				return;
			if (dollarA.startsWith("Alt-"))
				return;
			if (dollarA.startsWith("Fort-"))
				return;
			if (dollarA.startsWith("Port-"))
				return;
			// if (dollarA.contains("-la-"))
			// return;
			if (dollarA.contains("-l'"))
				return;
			if (dollarA.contains("-d'"))
				return;
			if (dollarA.contains("-Gebiet"))
				return;
			if (dollarA.contains("-Kreis"))
				return;
			if (dollarA.contains("-Departement"))
				return;
			// if (dollarA.contains("-du-"))
			// return;
			// if (dollarA.contains("-du-"))
			// return;
			// if (dollarA.contains("-du-"))
			// return;

			//
			final Mutable<Boolean> abbruch = new Mutable<>(false);

			final List<String> countryCodes = GNDUtils.getCountryCodes(record);
			countryCodes.forEach(cc ->
			{
				final String upperCase = cc.toUpperCase();
				if (upperCase.startsWith("XA-DE")
						|| upperCase.startsWith("XA-AT"))
					abbruch.setValue(true);
			});
			if (abbruch.getValue())
				return;
			GNDUtils.getBemerkungen(record).forEach(bem ->
			{
				if (bem.toLowerCase().contains("doppelort"))
					abbruch.setValue(true);
				if (bem.toLowerCase().contains("zusammenleg"))
					abbruch.setValue(true);
			});
			if (abbruch.getValue())
				return;

			final String name = GNDUtils.getNameOfRecord(record);
			final String id = record.getId();
			final List<String> lcs = countryCodes;
			final String out = StringUtils.concatenate("\t", id, name, ents,
					lcs);

			System.out.println(out);

		});

	}

}
