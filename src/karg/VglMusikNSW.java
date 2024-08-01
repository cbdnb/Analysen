/**
 *
 */
package karg;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;

/**
 * @author baumann
 *
 *         Vergleicht die Abkürzungen der Musik-NSW mit der Liste aus AH-014.
 *
 */
public class VglMusikNSW {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Collection<String> abkuAH014 = StringUtils.readLinesFromClip();
		final Set<String> abkuerzungenGND = new LinkedHashSet<>();
		final RecordReader recordReader = RecordReader
				.getMatchingReader("D:/Analysen/karg/Musik_NSW.txt");
		recordReader.forEach(record ->
		{
			final String abkuerz = BibRecUtils.getAbkuerzungNSW(record);
			abkuerzungenGND.add(abkuerz);
			System.out.println(abkuerz);
		});
		System.out.println("----");
		abkuAH014.forEach(s ->
		{
			if (!abkuerzungenGND.contains(s))
				System.out.println(s);
		});

	}

}
