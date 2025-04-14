package scheven.hinweis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

public class Idn2Nid {

	public static void main(final String[] args) throws IOException {
		final Set<String> idns = new HashSet<>(StringUtils.readLinesFromFile(
				"D:/Analysen/scheven/Hinweis/zu_loeschen_31.3ab_gib_2024-07-18.txt"));
		final RecordReader recordReader = RecordReader
				.getMatchingReader(HinweisDBUtil.DOWNLOAD_FILE);
		System.err.println("Anfang");
		recordReader.forEach(rec ->
		{
			final String idn = rec.getId();
			if (!idns.contains(idn))
				return;
			idns.remove(idn);
			final String nid = GNDUtils.getNID(rec);
			System.out.println(StringUtils.concatenateTab(idn, nid));
		});
		System.err.println(idns.size());

	}

}
