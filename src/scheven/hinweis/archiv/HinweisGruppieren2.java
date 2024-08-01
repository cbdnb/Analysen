/**
 *
 */
package scheven.hinweis.archiv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SystematikComparator;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 *         Findet über die in der 260 verküpften Schlagwörter die wirkliche
 *         voneinander verschiedenen Begriffe - also Begriffscluster. Jede Menge
 *         von SWW, die von anderen Mengen verschieden ist, repäsentiert einen
 *         eigenen Begriff.
 *
 */
public class HinweisGruppieren2 extends DownloadWorker {

	private static final int SPALTE_KOMBINATION = 0;

	private static final int SPALTE_HINW_IDNS = 1;

	private static final int SPALTE_ISIL = 2;

	private static final int SPALTE_SYSTEMATIK = 3;

	private static final int SPALTE_NAMEN = 4;

	private static final int SPALTE_ANZAHL = 5;

	static String folder = "D:/Analysen/scheven/Hinweis/";

	/*
	 * Enthält auch Zeitschlagwörter!
	 */
	static Multimap<Set<String>, Record> relationierte_1xx = new ListMultimap<>();

	static Map<String, String> idn2name = new HashedMap<>();

	@Override
	protected void processRecord(final Record record) {
		if (!GNDUtils.isUseCombination(record))
			return;
		final List<Line> hinweisLines = GNDUtils.getHinweisLines(record);
		if (hinweisLines.isEmpty())
			return;

		final List<String> list = hinweisLines.stream().map(line ->
		{
			final String sub9 = line.getIdnRelated();
			final String dollarA = SubfieldUtils.getContentOfFirstSubfield(line,
					'a');
			if (sub9 != null) {
				idn2name.put(sub9, dollarA);
				return sub9;
			} else {
				return dollarA;
			}
		}).collect(Collectors.toList());
		final LinkedHashSet<String> set = new LinkedHashSet<>(list);

		RecordUtils.retainTags(record, "150", "903", "005", "065");

		relationierte_1xx.add(set, record);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final HinweisGruppieren2 gruppieren = new HinweisGruppieren2();
		gruppieren.setStreamFilter(new ContainsTag("260", GNDTagDB.getDB()));

		gruppieren.processGZipFiles(Constants.Ts);
		System.out.println(relationierte_1xx.getKeyCount());
		gruppieren.setOutputFile(
				folder + "Hinweissätze_gruppiert" + "_Ts_1" + ".txt");
		final List<List<String>> zeilen = new ArrayList<>();
		relationierte_1xx.forEach(idnKombination ->
		{
			final List<String> spalten = Arrays.asList(new String[6]);

			final List<String> hinweisIDNs = new ArrayList<>();
			String isil = "";
			String systematik = "";
			final List<String> namen = new ArrayList<>();

			for (final Record record : relationierte_1xx.get(idnKombination)) {

				hinweisIDNs.add(record.getId());
				isil = GNDUtils.getIsilVerbund(record);
				systematik = GNDUtils.getFirstGNDClassification(record);
				String name;
				try {
					name = RDAFormatter.getRDAHeading(record);
				} catch (final IllFormattedLineException e) {
					name = GNDUtils.getNameOfRecord(record);
				}
				namen.add(name);

			}

			final List<String> idnsPlusNamen = new ArrayList<>();
			idnKombination.forEach(idn ->
			{

				final String name = idn2name.get(idn);
				String idnPlusName = idn;
				if (name != null)
					idnPlusName += ": " + name;
				idnsPlusNamen.add(idnPlusName);
				System.err.println(idnPlusName);
			});
			spalten.set(SPALTE_KOMBINATION,
					StringUtils.makeExcelCellFromCollection(idnsPlusNamen));
			spalten.set(SPALTE_HINW_IDNS,
					StringUtils.makeExcelCellFromCollection(hinweisIDNs));
			spalten.set(SPALTE_ISIL, isil);
			spalten.set(SPALTE_SYSTEMATIK, systematik);
			spalten.set(SPALTE_NAMEN,
					StringUtils.makeExcelCellFromCollection(namen));
			spalten.set(SPALTE_ANZAHL, "" + hinweisIDNs.size());

			zeilen.add(spalten);

		});
		final Comparator<List<String>> listSizeComparator = (list1,
				list2) -> Integer.parseInt(list2.get(SPALTE_ANZAHL))
						- Integer.parseInt(list1.get(SPALTE_ANZAHL));

		final SystematikComparator systcmp = new SystematikComparator();
		final Comparator<List<String>> systComparator = (list1,
				list2) -> systcmp.compare(list1.get(SPALTE_SYSTEMATIK),
						list2.get(SPALTE_SYSTEMATIK));

		final Comparator<List<String>> isilComparator = (list1, list2) -> list1
				.get(SPALTE_ISIL).compareTo(list2.get(SPALTE_ISIL));

		// Collections.sort(zeilen, isilComparator.thenComparing(systComparator)
		// .thenComparing(listSizeComparator));

		// Collections.sort(zeilen,
		// listSizeComparator.thenComparing(systComparator));

		Collections.sort(zeilen, systComparator.thenComparing(isilComparator)
				.thenComparing(listSizeComparator));

		// Collections.sort(zeilen, systComparator);

		zeilen.forEach(zeile ->
		{
			if (!zeile.get(SPALTE_ANZAHL).equals("1")) {
				gruppieren.println(StringUtils.concatenate("\t", zeile));
				// System.err.println(zeile);
			}
		});

	}

}
