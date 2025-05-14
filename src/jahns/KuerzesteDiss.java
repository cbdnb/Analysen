package jahns;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.BoundedPriorityQueue;
import de.dnb.basics.collections.ListUtils;
import de.dnb.basics.utils.NumberUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;

public class KuerzesteDiss extends DownloadWorker {

	static final Collection<String> PHRASEN = Arrays.asList("Seite", "Seiten",
			"Spalte", "Spalten", "Blatt", "Blätter", "S.", "Sp.", "Bl.", "p.",
			"page", "pages");

	final Map<DDC_SG, BoundedPriorityQueue<Triplett<String, String, Integer>>> dhs2recorde;

	private Comparator<Triplett<String, String, Integer>> triplettComparator;

	private final PrintWriter out;

	/**
	 * @throws IOException
	 *
	 */
	public KuerzesteDiss() throws IOException {
		triplettComparator = (Comparator.comparingInt(t -> t.third));
		triplettComparator = triplettComparator.reversed();
		dhs2recorde = new HashMap<>();
		DDC_SG.enumSet().forEach(sg -> dhs2recorde.put(sg, makeQueue()));
		final String folder = "D:\\Analysen\\jahns";
		out = MyFileUtils.outputFile(folder + "\\" + "kürzeste_Diss.txt",
				false);
	}

	private BoundedPriorityQueue<Triplett<String, String, Integer>> makeQueue() {
		return new BoundedPriorityQueue<>(10, triplettComparator);
	}

	@Override
	protected void processRecord(final Record record) {

		// Allgemeine Anmerkung enthält oft Hinweis auf Schriftenverzeichnis.
		// Daher aussondern:
		if (RecordUtils.containsField(record, "4201"))
			return;

		// nur Typ Aa und AF
		if (RecordUtils.getDatatypeCharacterAt(record, 0) != 'A')
			return;
		final char pos2 = RecordUtils.getDatatypeCharacterAt(record, 1);
		if (!(pos2 == 'a' || pos2 == 'F'))
			return;
		if (RecordUtils.getDatatypeCharacterAt(record, 2) != 0)
			return;

		// Bibliogafie neben Hochschulschrift ausschließen, um
		// keine Auflistung von Titeln zu bekommen.
		if (RecordUtils.getLines(record, "1131").size() != 1)
			return;
		// dito:
		final String mainTitle = BibRecUtils.getMainTitle(record);
		if (StringUtils.contains(mainTitle, "Schriften", true))
			return;

		if (!BibRecUtils.istHochschulschrift(record, true))
			return;

		final String umfang = BibRecUtils.getSimpleExtent(record);
		if (umfang == null)
			return;

		final DDC_SG hsg = SGUtils.getDDCDHS(record);
		if (hsg == null)
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
		// Wenn mehrere Zahlen, ist die Auswertung nicht sinnvoll:
		if (numbers.size() != 1)
			return;

		final int number = ListUtils.getFirst(ListUtils.convertToList(numbers));

		final Triplett<String, String, Integer> tripel = new Triplett<String, String, Integer>(
				record.getId(), mainTitle, number);
		dhs2recorde.get(hsg).add(tripel);

	}

	private void ausgeben(final DDC_SG dhs,
			final BoundedPriorityQueue<Triplett<String, String, Integer>> rekordeIDTitAnzahl) {
		rekordeIDTitAnzahl.ordered().forEach(t -> out.println(dhs.getDDCString()
				+ "\t" + StringUtils.concatenateTab(t.asList())));
	}

	public void ausgeben() {
		DDC_SG.enumSet().forEach(sg -> ausgeben(sg, dhs2recorde.get(sg)));
		out.println();
	}

	public static void main(final String[] args) throws IOException {

		final KuerzesteDiss kueDiss = new KuerzesteDiss();

		final ContainsTag contains4060 = new ContainsTag("4060",
				BibTagDB.getDB());
		final ContainsTag isA = new ContainsTag("0500", '0', "A",
				BibTagDB.getDB());
		kueDiss.setStreamFilter(contains4060.and(isA));

		try {
			kueDiss.processGZipFile(Constants.TITEL_GESAMT_D);

		} catch (final Exception e) {
			e.printStackTrace();
		}

		kueDiss.ausgeben();

	}

}
