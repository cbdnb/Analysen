/**
 *
 */
package baumann.bk;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class BK_Anzahl {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader downloadReader = RecordReader
				.getMatchingReader("D:\\Analysen\\baumann\\BK.dow");
		final Set<String> dowNmmern = downloadReader.stream()
				.map(record -> GNDUtils.getClassificationNumber(record))
				.collect(Collectors.toSet());

		final String bkStr = StringUtils
				.readIntoString("D:\\Analysen\\baumann\\bk.txt");
		final List<BKRecord> bkDruckRecs = BKUtil.getRecords(bkStr);
		final Set<String> drucknnr = bkDruckRecs.stream().map(rec -> rec.nummer)
				.collect(Collectors.toSet());
		System.out.println("Druck \\ DB:");
		System.out.println(CollectionUtils.difference(drucknnr, dowNmmern));
		System.out.println();
		System.out.println("DB \\ Druck:");
		System.out.println(CollectionUtils.difference(dowNmmern, drucknnr));
	}

}
