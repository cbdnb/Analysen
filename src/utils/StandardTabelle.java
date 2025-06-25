package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SystematikComparator;
import de.dnb.gnd.parser.Record;

/**
 * Bildet aus einem Download von Normdaten eine Exceltabelle der Felder <br>
 *
 * "Satzart", "1XX", "idn", "nid", "065", "Redaktion"
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
					Arrays.asList(RecordUtils.getDatatype(record),
							GNDUtils.getSimpleName(record), record.getId(),
							GNDUtils.getNID(record),
							GNDUtils.getFirstGNDClassification(record),
							GNDUtils.getIsilVerbund(record)));
			table.add(zeile);
		}

		final Comparator<List<String>> myComparator = Comparator
				.comparing(l -> l.get(SN), new SystematikComparator());
		table.sort(myComparator);

		final StringBuilder outputBuilder = new StringBuilder(
				StringUtils.concatenateTab("Satzart", "1XX", "idn", "nid",
						"065", "Redaktion"));
		table.forEach(zeile -> outputBuilder.append("\n")
				.append(StringUtils.concatenateTab(zeile)));
		StringUtils.writeToClipboard(outputBuilder.toString());
	}
}
