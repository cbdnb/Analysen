/**
 *
 */
package alex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;

/**
 * Findet Nebensachgruppen B, die dort nicht hingehören (150;B).
 *
 * @author baumann
 *
 */
public class FalscheB {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz");
		final PrintWriter out = MyFileUtils
				.outputFile("D:/Analysen/alex/Falsche_NSG_B.txt", false);
		reader.forEach(rec ->
		{
			if (!BibRecUtils.isPrintedPublication(rec))
				return;
			if (BibRecUtils.isMagazine(rec))
				return;

			final Line line = SGUtils.getDHSLine(rec);
			if (line == null)
				return;
			final List<String> dnss = SGUtils.getDNSstrings(line);
			if (!dnss.contains("B"))
				return;
			final String dhs = SGUtils.getDhsStringPair(line).first;
			if (StringUtils.charAt(dhs, 0) == '8')
				return;
			final String typ = RecordUtils.getDatatype(rec);
			final String idn = rec.getId();
			final Integer jahr = BibRecUtils.getYearOfPublication(rec);
			final String sgg = SGUtils.getSGGSemicola(line);
			final String titel = BibRecUtils.getMainTitle(rec);
			out.println(StringUtils.concatenateTab(idn, typ, jahr, sgg, titel));
		});

		MyFileUtils.safeClose(out);

	}

}
