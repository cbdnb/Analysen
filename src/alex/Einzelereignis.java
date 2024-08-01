/**
 *
 */
package alex;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class Einzelereignis {

	private static Frequency<Record> recFrequency = new Frequency<>();
	private static Map<String, Record> idn2Record = new HashMap<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final RecordReader sachReader = RecordReader
				.getMatchingReader(Constants.Ts);
		// sachReader.setStreamFilter(
		// new ContainsTag("008", 'a', "sih", GNDTagDB.getDB()));
		sachReader.forEach(record ->
		{
			final String datatype = RecordUtils.getDatatype(record);
			if (!"Ts1".equals(datatype))
				return;

			final List<String> ent = GNDUtils.getEntityTypes(record);

			if (ent.contains("sih")) {
				RecordUtils.retainTags(record, "065", "083", "150", "797");
				recFrequency.addKey(record);
				idn2Record.put(record.getId(), record);

			}
		});

		final Set<String> idns = idn2Record.keySet();

		final RecordReader titelReader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		titelReader.setStreamFilter(new ContainsTag("5100", BibTagDB.getDB()));

		titelReader.forEach(record ->
		{
			final char phys = BibRecUtils.getPhysikalischeForm(record);
			if (phys == 'O')
				return;
			final Collection<String> rswkids = SubjectUtils.getRSWKidsSet(record);
			rswkids.retainAll(idns);
			rswkids.forEach(idn ->
			{
				final Record gndRecord = idn2Record.get(idn);
				recFrequency.add(gndRecord);

			});

		});

		recFrequency.forEach(record ->
		{
			final long count = recFrequency.get(record);
			final String name = GNDUtils.getNameOfRecord(record);
			final List<String> systs = GNDUtils.getGNDClassifications(record);
			final String syst = StringUtils.concatenate(";", systs);
			final String ddc = StringUtils.concatenate(" / ",
					GNDUtils.getValidDDCNumbers(record));
			System.out.println(StringUtils.concatenate("\t", record.getId(),
					name, syst, count, ddc));
		});
	}

}
