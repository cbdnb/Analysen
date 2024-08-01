/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class MO {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		System.out.println(">");
		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Analysen/scheven/MO.txt");
		reader.forEach(rec ->
		{
			final String id = rec.getId();
			final String nameORec = GNDUtils.getNameOfRecord(rec);
			final String syst = GNDUtils.getFirstGNDClassification(rec);
			final List<String> bems = GNDUtils.getBemerkungen(rec);
			bems.forEach(bem ->
			{
				if (StringUtils.containsWord(bem, "MO", false)) {
					bem = bem.replaceFirst(" ", "\t");
					System.out.println(StringUtils.concatenateTab(id, nameORec,
							syst, bem));
				}
			});
		});

	}

}
