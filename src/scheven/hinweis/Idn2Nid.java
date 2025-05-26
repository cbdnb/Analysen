package scheven.hinweis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class Idn2Nid {

	public static void main(final String[] args) throws IOException {
		final PrintWriter out = MyFileUtils
				.outputFile(HinweisDBUtil.FOLDER + "nids.txt", false);
		// Alle 260-idns einsammeln:
		final Set<String> idns260 = new HashSet<>();
		final RecordReader recordReader260 = RecordReader
				.getMatchingReader(HinweisDBUtil.DOWNLOAD_FILE);
		System.err.println("260 einlesen");
		recordReader260.forEach(rec ->
		{
			GNDUtils.getHinweisLines(rec).forEach(line ->
			{
				final String id = line.getIdnRelated();
				if (id != null)
					idns260.add(id);
			});
		});
		// Alle SWW durchgehen, um nid zu idn zu finden:
		System.err.println("idn -> nid");
		final Map<String, String> idn2nid = new HashMap<>();
		final RecordReader recordReaderNID = RecordReader
				.getMatchingReader(Constants.GND);
		recordReaderNID.forEach(rec ->
		{
			final String idn = rec.getId();
			final String nid = GNDUtils.getNID(rec);
			if (idns260.contains(idn)) {
				// System.err.println(StringUtils.concatenateTab(idn, nid));
				idn2nid.put(idn, nid);
			}
		});

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
			out.print(StringUtils.concatenateTab(idn, nid));
			final List<String> nids260 = new ArrayList<>();
			GNDUtils.getHinweisLines(rec).forEach(line ->
			{
				final String id = line.getIdnRelated();
				if (id != null)
					nids260.add(idn2nid.get(id));
				else
					nids260.add(
							SubfieldUtils.getContentOfFirstSubfield(line, 'a'));
			});
			out.println("\t" + StringUtils.concatenateTab(nids260));
		});
		System.err.println(idns.size());
	}

}
