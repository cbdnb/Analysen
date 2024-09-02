package baumann;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * Speicher und lädt eine Zuordnung ddc-in-4200 -> Zahl-der-Titel.
 *
 * @author baumann
 *
 */
public class Statistik5400 {

	private static final String FILENAME_5400_2_Titles = "D:/Normdaten/5400toTitles.out";

	public static void main1(final String... args) throws IOException {
		safe();
	}

	public static void main(final String... args)
			throws IOException, ClassNotFoundException {
		final Frequency<String> f = getDDC2Titles();
		System.out.println(f.get("001"));
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void safe() throws IOException, FileNotFoundException {
		final Frequency<String> ddc2Titles = new Frequency<>();

		final RecordReader recordReader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);

		final Predicate<String> titleFilter = new ContainsTag("5400",
				BibTagDB.getDB());
		recordReader.setStreamFilter(titleFilter);
		recordReader.forEach(record ->
		{
			final String ddc = SubjectUtils.getMainDDCNotation(record);
			if (ddc == null) {
				System.err.println(record.getId() + " - null");
			} else {
				final String trim = ddc.trim();
				if (!trim.equals(ddc))
					System.err.println(record.getId() + " - trim");
				ddc2Titles.add(trim);
			}
		});

		MyFileUtils.safeClose(recordReader);
		ddc2Titles.safe(FILENAME_5400_2_Titles);
		System.out.println(ddc2Titles);
	}

	public static Frequency<String> getDDC2Titles()
			throws IOException, ClassNotFoundException {
		return new Frequency<>(FILENAME_5400_2_Titles);
	}

}
