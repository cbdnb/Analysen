package junger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.utils.NumberUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;

public class WenigerAlsZehnSeiten extends DownloadWorker {

	static final Frequency<String> DATABASE = new Frequency<>();

	static final Collection<String> JAHRE = Arrays.asList("2013", "2014",
			"2015");

	static final Collection<String> REIHEN = Arrays.asList("ra", "rb");

	static final Collection<String> PHRASEN = Arrays.asList("Seite", "Seiten",
			"Spalte", "Spalten", "Blatt", "Blätter", "S.", "Sp.", "Bl.", "p.",
			"page", "pages");

	int i = 0;

	@Override
	protected void processRecord(final Record record) {
		// nur Typ Aa und AF
		if (RecordUtils.getDatatypeCharacterAt(record, 0) != 'A')
			return;
		final char pos2 = RecordUtils.getDatatypeCharacterAt(record, 1);
		if (pos2 != 'a' && pos2 != 'F')
			return;
		if (RecordUtils.getDatatypeCharacterAt(record, 2) != 0)
			return;

		// richtige Reihen
		final List<String> codes = BibRecUtils.getCodes(record);
		codes.retainAll(REIHEN);
		if (codes.isEmpty())
			return;

		// richtiges Jahr?
		final String jahr = BibRecUtils.getYearOfPublicationString(record);
		if (!JAHRE.contains(jahr))
			return;

		final String umfang = BibRecUtils.getSimpleExtent(record);
		if (umfang == null)
			return;

		// Suche nach S., Seite(n) etc.
		boolean phraseGefunden = false;
		for (final String phrase : PHRASEN) {
			if (StringUtils.containsWord(umfang, phrase, false)) {
				phraseGefunden = true;
				break;
			}
		}
		if (!phraseGefunden)
			return;

		final Collection<Integer> numbers = NumberUtils.getAllInts(umfang);
		if (numbers.size() != 1)
			return;

		final int number = ListUtils.getFirst(ListUtils.convertToList(numbers));
		if (number >= 10)
			return;

		System.out.println(record.getId());
		DATABASE.add(jahr);

	}

	public static void main(final String[] args) throws IOException {

		final WenigerAlsZehnSeiten dlw = new WenigerAlsZehnSeiten();

		dlw.setStreamFilter(
				new ContainsTag("1100", 'a', "20", BibTagDB.getDB()));

		System.err.println("Titeldaten flöhen:");
		try {
			dlw.processGZipFile(
					"Z:/cbs/zen/vollabzug/aktuell/Pica+/DNBtitelgesamt.dat.gz");
			// dlw.processGZipFile("D:/Normdaten/DNBtitel_Stichprobe.dat.gz");
		} catch (final Exception e) {
			e.printStackTrace();
		}

		System.out.println(DATABASE);

	}

}
