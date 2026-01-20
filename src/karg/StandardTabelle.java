package karg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.parser.Record;

/**
 * Bildet aus einem Download von Normdaten eine Exceltabelle der Felder <br>
 *
 * "Satzart","008", "1XX", "idn", "nid", "065", "Urheber", "Redaktion"
 *
 * <br>
 * sortiert nach Systematiknummern (065)
 */
public class StandardTabelle {

	private static final int SN = 4;

	public static void main(final String[] args) throws IOException {

		final String path = StringUtils.readClipboard();
		final RecordReader reader = RecordReader.getMatchingReader(path);

		final List<List<String>> table = new ArrayList<>();

		for (final Record record : reader) {
			final List<String> zeile = new ArrayList<>(
					Arrays.asList(GNDUtils.getSimpleName(record),
							StringUtils.makeExcelCellFromCollection(
									GNDUtils.getNonpublicGeneralNotes(record)),
							StringUtils.makeExcelCellFromCollection(
									GNDUtils.getDefinitions(record)),
							StringUtils.makeExcelCellFromCollection(
									GNDUtils.getPublicGeneralNotes(record)),
							StringUtils.makeExcelCellFromCollection(
									GNDUtils.getURIs(record))));
			table.add(zeile);
		}

		// final Comparator<List<String>> myComparator = Comparator
		// .comparing(l -> l.get(SN), new SystematikComparator());
		// table.sort(myComparator);

		final StringBuilder outputBuilder = new StringBuilder(
				StringUtils.concatenateTab("150", "667", "677", "680", "Link"));
		table.forEach(zeile -> outputBuilder.append("\n")
				.append(StringUtils.concatenateTab(zeile)));
		StringUtils.writeToClipboard(outputBuilder.toString());
	}
}
