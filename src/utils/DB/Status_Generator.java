/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

import de.dnb.basics.applicationComponents.Store;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Quadruplett;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;

/**
 * @author baumann
 *
 */
public class Status_Generator extends TableGenerator {

	/**
	 * @param path
	 * @param data
	 * @param description
	 */
	public Status_Generator() {
		super(FOLDER + "status.out",
				new HashMap<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>>(),
				"IDN -> (DHS, DNS, Tiefe, $E)");
		System.err.println(
				"Status_Generator: Erzeugung kann 3,5 Stunden dauern.");
	}

	@SuppressWarnings("unchecked")
	private final Map<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> statusMap = (Map<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>>) data;

	private final Store<Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> store = new Store<>();

	private final StatusAndCodeFilter acFilter = StatusAndCodeFilter
			.filterAZc();

	@Override
	void process(final Record record) {
		if (!acFilter.test(record))
			return;
		final int ppnI = IDNUtils.ppn2int(record.getId());
		if (ppnI == -1)
			return;
		final Quadruplett<DDC_SG, DDC_SG, TIEFE, String> status = SubjectUtils
				.getErschliessungsStatus(record, null);
		if (status.first != null) {
			final Quadruplett<DDC_SG, DDC_SG, TIEFE, String> newStatus = store
					.get(status);
			statusMap.put(ppnI, newStatus);
		}
	}

	@Override
	public HashMap<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> getTable()
			throws ClassNotFoundException, IOException {
		return CollectionUtils.loadHashMap(path);
	}

	@Override
	public Predicate<String> getStreamFilter() {
		final Predicate<String> negAa = (new ContainsTag("0500", '0', "Aa",
				BibTagDB.getDB())).negate();
		final Predicate<String> negAf = (new ContainsTag("0500", '0', "Af",
				BibTagDB.getDB())).negate();
		return new ContainsTag("5050", BibTagDB.getDB()).and(negAa).and(negAf);
	}

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final Status_Generator generator = new Status_Generator();
		final HashMap<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> table = generator
				.getTable();
		final Scanner scan = new Scanner(System.in);

		System.out.println("Geben Sie eine idn ein");
		while (scan.hasNext()) {
			final String idn = StringUtils.readClipboard();
			final int id = IDNUtils.idn2int(idn);
			System.out.println(table.get(id));
			System.out.println();
			System.out.println("Geben Sie eine idn ein");
			scan.nextLine();

		}

	}

}
