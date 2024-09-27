package scheven;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * Such nach Latvija / Latvijskaja SSR in $a / $g von 1XX und 4XX.
 */
public class Latvija {

	private static final String FOLDER = "D:/Analysen/scheven/";
	private static PrintWriter latvija;
	private static PrintWriter ssr;

	public static void main(final String[] args) throws IOException {

		latvija = MyFileUtils.outputFile(FOLDER + "latvija.txt", false);
		ssr = MyFileUtils.outputFile(FOLDER + "ssr.txt", false);

		final RecordReader reader = RecordReader
				.getMatchingReader(FOLDER + "latvij.txt");

		for (final Record record : reader) {
			final List<String> dollarAG = RecordUtils.getContents(record,
					"[14]..", 'a', 'g');
			if (dollarAG.contains("Latvija"))
				ausgabe(record, latvija);
			if (dollarAG.contains("Latvijskaja SSR"))
				ausgabe(record, ssr);
		}

		MyFileUtils.safeClose(reader);
		MyFileUtils.safeClose(latvija);
		MyFileUtils.safeClose(ssr);

	}

	private static void ausgabe(final Record record, final PrintWriter pw) {
		final String idn = record.getId();
		final String nid = GNDUtils.getNID(record);
		final String satzart = RecordUtils.getDatatype(record);
		final String feld1XX = GNDUtils.getNameOfRecord(record);
		final String redaktion = GNDUtils.getIsilVerbund(record);
		pw.println(StringUtils.concatenateTab(idn, nid, satzart, feld1XX,
				redaktion));

	}

}
