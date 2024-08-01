/**
 *
 */
package baumann.musik;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class SAB {

	/**
	 *
	 */
	private static final int spalteSucheinstieg = 3;
	static String folder = "D:/Analysen/baumann/Musik/";
	private static int spalteNID = 2;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Set<String> listeStevanovic = new LinkedHashSet<>();
		/*
		 * Annahme, das Stevanovic-Liste NIDs enthält (was so nicht stimmt!)
		 */
		final Map<String, String> nid2name = new HashMap<>();
		final String instr = StringUtils
				.readIntoString(folder + "Instrumente.txt");
		final String[][] table = StringUtils.makeTable(instr);
		final int tableLength = table.length;
		for (int row = 0; row < tableLength; row++) {
			String nid = StringUtils.getCellAt(table, row, spalteNID);
			if (nid == null)
				nid = "";
			nid = nid.trim();
			final String name = StringUtils.getCellAt(table, row,
					spalteSucheinstieg);
			if (IDNUtils.isKorrekteIDN(nid)) {
				listeStevanovic.add(nid);
				nid2name.put(nid, name);
			}
		}

		final PrintWriter printWriter = new PrintWriter(
				folder + "fehlerInstr.txt");

		final RecordReader reader = RecordReader
				.getMatchingReader(folder + "sab.txt");
		final Map<String, String> normnummer2idn = new LinkedHashMap<>();
		printWriter.println("Nicht in Stevanovic-Liste enthalten:");
		reader.forEach(record ->
		{
			if (GNDUtils.getRecordType(record) == 'c')
				return;
			final String idn = record.getId();

			final Set<String> nummern = GNDUtils.getAlleNormnummer(record);
			nummern.forEach(nummer -> normnummer2idn.put(nummer, idn));

			// nummern nicht in Stevanovic-Liste enthalten:
			if (!nummern.removeAll(listeStevanovic)) {
				printWriter
						.println(idn + "\t" + GNDUtils.getNameOfRecord(record));
			}
		});

		printWriter.println();
		printWriter.println("Kein sab:");
		listeStevanovic.forEach(nid ->
		{
			final String idn = normnummer2idn.get(nid);
			if (idn == null) {
				final String name = nid2name.get(nid);
				printWriter.println(nid + "\t" + name);
			}
		});

		StreamUtils.safeClose(printWriter);

	}

}
