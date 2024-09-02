/**
 *
 */
package scheven;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubjectUtils;
import utils.Database;

/**
 * @author baumann
 *
 */
public class FreqGeoDDC {

	/**
	 *
	 */
	private static final String IN_FILE_SWW_GEO = "D:/Normdaten/DNBGND_g.dat.gz";

	private static final String OUT_FILE_DDC = "D:/Analysen/scheven/Statistik_Geo_DDC.txt";

	private static final String OUT_FILE_SWW = "D:/Analysen/scheven/Statistik_Geo_SWw.txt";

	private static Frequency<String> table2Freq = new Frequency<>();

	private static Frequency<String> geoSWwFre = new Frequency<>();

	private static Map<String, Pair<String, List<Pair<String, String>>>> idn2geo = new HashMap<>();

	private static Database database;

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void main(final String[] args)
			throws IOException, SQLException {
		final PrintStream outputStreamDDC = new PrintStream(OUT_FILE_DDC);
		final PrintStream outputStreamSWw = new PrintStream(OUT_FILE_SWW);

		System.err.println("geos lesen");
		final RecordReader readerGeo = RecordReader
				.getMatchingReader(IN_FILE_SWW_GEO);
		readerGeo.forEach(rec ->
		{
			String name = GNDUtils.getNameOfRecord(rec);
			name = StringUtils.unicodeDecomposition(name);
			final List<Pair<String, String>> ddcs = GNDUtils
					.getAllDDCNumbersAndDet(rec);
			final String idn = rec.getId();
			final Pair<String, List<Pair<String, String>>> myrec = new Pair<>(
					name, ddcs);

			idn2geo.put(idn, myrec);
		});

		System.err.println("titel lesen");
		final RecordReader readerTitle = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		//@formatter:off
		final Predicate<String> streamFilter =
				new  ContainsTag("5400",BibTagDB.getDB())
					.or(new ContainsTag("5100", BibTagDB.getDB()));
		//@formatter:on
		readerTitle.setStreamFilter(streamFilter);

		readerTitle.forEach(title ->
		{

			final List<String> table2List = SubjectUtils
					.getTable2Notations(title);
			// ???
			final Set<String> table2set = table2List.stream()
					.collect(Collectors.toSet());
			table2set.forEach(ddc ->
			{
				// Inkonsistenz: Titeldaten mit -T2--, Normdaten mit T2--
				table2Freq.add("T2--" + ddc);
			});

			final Collection<String> idns = SubjectUtils.getRSWKidsSet(title);
			idns.forEach(idn ->
			{
				if (idn2geo.containsKey(idn))
					geoSWwFre.add(idn);
			});
		});

		database = new Database();
		geoSWwFre.forEach(idn ->
		{

			final long freq = geoSWwFre.get(idn);
			final Pair<String, List<Pair<String, String>>> rec = idn2geo
					.get(idn);
			final String name = rec.first;
			final List<Pair<String, String>> ddcs = rec.second;
			outputStreamSWw.println(
					StringUtils.concatenate("\t", idn, name, ddcs, freq));
		});

		table2Freq.forEach(ddc ->
		{
			final long count = table2Freq.get(ddc);
			final Collection<Triplett<String, String, String>> crisscross = database
					.getCrissCrossSWW(ddc);
			outputStreamDDC.println(
					StringUtils.concatenate("\t", ddc, count, crisscross));
		});

		MyFileUtils.safeClose(outputStreamSWw);
		MyFileUtils.safeClose(outputStreamDDC);

	}

}
