package scheven;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

public class FalscheSatzart {

	private static final String FOLDER = "D:/Analysen/scheven/FalscheSatzart/";

	public static void main(final String[] args) throws IOException {

		final Map<Character, PrintWriter> typ2File = new HashMap<>();
		Constants.SATZ_TYPEN.keySet().forEach(satztyp ->
		{
			final char typ = satztyp.charAt(1);
			try {
				typ2File.put(typ, MyFileUtils
						.outputFile(FOLDER + "T" + typ + ".txt", false));
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		final RecordReader reader = RecordReader
				.getMatchingReader(FOLDER + "download.txt");
		reader.forEach(record ->
		{
			final char typ = GNDUtils.getRecordType(record);
			final String idn = record.getId();
			final String nid = GNDUtils.getNID(record);
			final String satzart = RecordUtils.getDatatype(record);
			final String red = GNDUtils.getIsilVerbund(record);
			final List<String> feld008 = GNDUtils.getEntityTypes(record);
			String name;
			try {
				name = GNDUtils.getNameOfRecord(record);
			} catch (final Exception e) {
				name = RecordUtils.getLines(record, "1..").toString();
			}
			final List<String> feld667 = GNDUtils
					.getNonpublicGeneralNotes(record);
			final String outS = StringUtils.concatenateTab(idn, nid, satzart,
					red, feld008, name, feld667);
			final PrintWriter outFile = typ2File.get(typ);
			outFile.println(outS);
		});

		typ2File.values().forEach(MyFileUtils::safeClose);

	}

}
