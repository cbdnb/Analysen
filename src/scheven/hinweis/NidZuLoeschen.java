package scheven.hinweis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

public class NidZuLoeschen {

	public static void main(final String[] args) throws IOException {
		final String fileNameNIDZuLoeschen = Util.FOLDER + "nids_zu_loeschen_"
				+ ".txt";
		final PrintWriter outNidZuLoeschen = MyFileUtils
				.outputFile(fileNameNIDZuLoeschen, false);
		outNidZuLoeschen
				.println(StringUtils.concatenateTab("PPN", "NID", "Name"));
		// Alle idns einsammeln:
		final Map<String, String> idn2nid = new HashMap<>();
		final Map<String, String> idn2name = new HashMap<>();
		final RecordReader recordReaderHinweis = RecordReader
				.getMatchingReader(Util.DOWNLOAD_FILE);

		recordReaderHinweis.forEach(rec ->
		{
			final String idn = rec.getId();
			final String nid = GNDUtils.getNID(rec);
			final String name = GNDUtils.getNameOfRecord(rec);
			idn2nid.put(idn, nid);
			idn2name.put(idn, name);
		});

		final List<String> idns = StringUtils
				.readLinesFromFile(TransformerSammlung.fileNameIDNsZuLoeschen);
		idns.forEach(idn ->
		{
			final String nid = idn2nid.get(idn);
			final String name = idn2name.get(idn);
			outNidZuLoeschen
					.println(StringUtils.concatenateTab(idn, nid, name));
		});
		MyFileUtils.safeClose(outNidZuLoeschen);

	}

}
