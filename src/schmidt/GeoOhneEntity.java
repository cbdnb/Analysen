/**
 *
 */
package schmidt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class GeoOhneEntity {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tg);
		reader.setStreamFilter(new ContainsTag("043", GNDTagDB.getDB()));
		final List<String> kontinente = Arrays.asList("XA", "XB", "XC", "XD",
				"XE", "XH", "XI", "XK", "XL", "XM");
		// System.err.println(kontinente.contains("XM"));

		final String path = "D:/Analysen/schmidt/";
		final Map<String, PrintWriter> kont2pw = new LinkedHashMap<>();
		kontinente.forEach(kont ->
		{
			try {
				final PrintWriter writer = new PrintWriter(
						path + kont + ".txt");
				kont2pw.put(kont, writer);
			} catch (final FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		reader.stream().forEach(record ->
		{
			if (GNDUtils.getLevel(record) != 1)
				return;
			if (GNDUtils.containsEntityTypes(record))
				return;
			if (GNDUtils.containsGNDClassification(record))
				return;

			if (RecordUtils.containsField(record, "010"))
				return;
			final List<String> lcs = GNDUtils.getCountryCodes(record);

			final String lcStr = RecordUtils.toPicaWithoutTag(RecordUtils
					.getFirstLineTagGivenAsString(record, "043").first);

			// Schnittmenge:
			lcs.retainAll(kontinente);

			final String name = GNDUtils.getNameOfRecord(record);
			// final List<String> ddcs =
			// GNDUtils.getAllDDCLines(record).stream()
			// .map(RecordUtils::toPicaWithoutTag)
			// .collect(Collectors.toList());

			lcs.forEach(lc ->
			{
				final PrintWriter pw = kont2pw.get(lc);
				final String id = record.getId();
				final String out = StringUtils.concatenate("\t", id, name,
						lcStr);
				System.err.println(out);
				pw.println(out);
			});
		});

		kontinente.forEach(kont ->
		{
			MyFileUtils.safeClose(kont2pw.get(kont));
		});

	}

}
