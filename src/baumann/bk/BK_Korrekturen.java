/**
 *
 */
package baumann.bk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.utils.HTMLUtils;
import de.dnb.basics.utils.OutputUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.formatter.Pica3Formatter;

/**
 * @author baumann
 *
 */
public class BK_Korrekturen {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final TreeMap<String, Record> bk2gnd = new TreeMap<>();
		final TreeMap<String, Record> bk2druck = new TreeMap<>();
		final RecordReader downloadReader = RecordReader
				.getMatchingReader("D:\\Analysen\\baumann\\BK.dow");
		downloadReader.forEach(record ->
		{
			retain(record);
			final String bk = GNDUtils.getClassificationNumber(record);
			bk2gnd.put(bk, record);
		});

		final String bkStr = StringUtils
				.readIntoString("D:\\Analysen\\baumann\\bk.txt");
		final List<BKRecord> bkDruckRecs = BKUtil.getRecords(bkStr);
		bkDruckRecs.forEach(record ->
		{
			final Record gndVorschlag = BKUtil.toDNB_Tc(record);
			retain(gndVorschlag);
			final String bk = GNDUtils.getClassificationNumber(gndVorschlag);
			bk2druck.put(bk, gndVorschlag);
		});

		final Set<String> schnittmenge = CollectionUtils
				.intersection(bk2gnd.keySet(), bk2druck.keySet());
		int i = 0;
		final Collection<Collection<String>> diffs = new ArrayList<>();
		for (final String bk : schnittmenge) {
			final Record gndRecord = bk2gnd.get(bk);
			final Record druckRecord = bk2druck.get(bk);
			if (!gndRecord.contentEquals(druckRecord)) {
				i++;
				System.out.println(gndRecord);
				System.out.println(druckRecord);
				System.out.println("------------");
				final List<String> diff = Arrays.asList(
						Pica3Formatter.toHTML(gndRecord),
						Pica3Formatter.toHTML(druckRecord));
				diffs.add(diff);
			}
		}
		System.out.println("Anzahl: " + i);
		final String tabelle = HTMLUtils.tableFromCells(diffs, 0, 0, 0, 16);
		OutputUtils.show(tabelle);
	}

	/**
	 * @param record
	 */
	private static void retain(final Record record) {
		RecordUtils.retainTags(record, "153", "453", "553", "900");
	}

}
