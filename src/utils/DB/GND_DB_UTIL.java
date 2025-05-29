/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Quadruplett;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;

/**
 * @author baumann
 *
 */
public class GND_DB_UTIL {

	/**
	 *
	 * @return Tabelle mit int-ppns UBB:OBB
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, Integer> getUBOB()
			throws ClassNotFoundException, IOException {
		System.err.println("lade UBB:OBB");
		final BiMultimap<Integer, Integer> table = TableGenerator.UB_OB_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int-ppns Werke:Autor
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, Integer> getWerkeAutor()
			throws ClassNotFoundException, IOException {
		System.err.println("lade Werke:Autor");
		final BiMultimap<Integer, Integer> table = TableGenerator.WORK_AUTOR_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;

	}

	/**
	 *
	 * @return Tabelle mit int-ppns Titel:Werke(Tu) aus 3210 und 3211
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, Integer> getTitelWerke()
			throws ClassNotFoundException, IOException {
		System.err.println("lade Titel:Werke");
		final BiMultimap<Integer, Integer> table = TableGenerator.TITLE_WORK_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int-ppns Titel:Übergeordnetes Werk aus den 4000,
	 *         4140, 4160, 4180, 4181, 4182
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, Integer> getTitelBT()
			throws ClassNotFoundException, IOException {
		System.err.println("lade Titel:Broader_Term (dauert knapp 3 min)");
		final BiMultimap<Integer, Integer> table = TableGenerator.TITLE_BT_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int-ppns Titel:Personen aus dem Personensegment
	 *         (3000, 3010,3019)
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, Integer> getTitelPersonen()
			throws ClassNotFoundException, IOException {
		System.err.println("lade Titel:Personen (dauert etwa 1 min)");
		final BiMultimap<Integer, Integer> table = TableGenerator.TITLE_PERSON_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int ppns:Namen (mit $ in den Unterfeldern)
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static HashMap<Integer, String> getppn2name()
			throws ClassNotFoundException, IOException {
		System.err.println("lade ppns:Namen");
		final HashMap<Integer, String> table = TableGenerator.NAMES_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int ppns:Namen (mit $ in den Unterfeldern)
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static HashMap<Integer, Integer> getTb2Ort()
			throws ClassNotFoundException, IOException {
		System.err.println("lade Tb:Ort");
		final HashMap<Integer, Integer> table = TableGenerator.TB_ORTA_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int ppns:DHS als Quadrupel (DHS, DNS - wenn B,K,S,
	 *         Tiefe, $E). Diese können weiterverarbeitet werden.
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static Map<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> getIdn2Status()
			throws ClassNotFoundException, IOException {
		System.err.println("lade ppns:DHS " + TimeUtils.getActualTimehhMM());
		final HashMap<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> table = TableGenerator.DHS_GENERATOR
				.getTable();
		System.err.println("fertig " + TimeUtils.getActualTimehhMM());
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int ppns:Bio-Namen (ohne $ in den Unterfeldern, nach
	 *         RDA, aber klein und in Unicode-Composition (damit besser gesucht
	 *         werden kann) Siehe
	 *         {@link StringUtils#unicodeComposition(CharSequence)})
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, String> getppn2bioName()
			throws ClassNotFoundException, IOException {
		System.err.println("lade ppns:Bio-Namen");
		final BiMultimap<Integer, String> table = TableGenerator.BIO_NAMES_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int ppns:RDA-Namen (ohne $ in den Unterfeldern, nach
	 *         RDA in Unicode-Composition (damit besser gesucht werden kann)
	 *         Siehe {@link StringUtils#unicodeComposition(CharSequence)}). Das
	 *         Ganze ist eine Multimap, da theoretisch auch zu einem Namen
	 *         mehrere PPNs gehören könnten (umgekehrt natürlich nicht!).
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, String> getppn2RDAName()
			throws ClassNotFoundException, IOException {
		System.err.println("lade ppns:RDA-Namen");
		final BiMultimap<Integer, String> table = TableGenerator.RDA_NAMES_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int ppns:RDA-Namen (ohne $ in den Unterfeldern, nach
	 *         RDA in Unicode-Composition (damit besser gesucht werden kann)
	 *         Siehe {@link StringUtils#unicodeComposition(CharSequence)})
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMap<Integer, String> getppn2nid()
			throws ClassNotFoundException, IOException {
		System.err.println("lade ppns:nid");
		final BiMap<Integer, String> table = TableGenerator.IDN_NID_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int ppns:Bio-Verweisungen (ohne $ in den
	 *         Unterfeldern, nach RDA, aber klein und in Unicode-Composition
	 *         (damit besser gesucht werden kann) Siehe
	 *         {@link StringUtils#unicodeComposition(CharSequence)})
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, String> getppn2bioVerweisungen()
			throws ClassNotFoundException, IOException {
		System.err.println("lade ppns:Bio-Verweisungen");
		final BiMultimap<Integer, String> table = TableGenerator.BIO_VERWEISUNG_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int ppns:Bio-Verweisungen (ohne $ in den
	 *         Unterfeldern, nach RDA, aber klein und in Unicode-Composition
	 *         (damit besser gesucht werden kann) Siehe
	 *         {@link StringUtils#unicodeComposition(CharSequence)})
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, String> getppn2RDAVerweisungen()
			throws ClassNotFoundException, IOException {
		System.err.println("lade ppns:RDA-Verweisungen");
		final BiMultimap<Integer, String> table = TableGenerator.RDA_VERWEISUNG_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 * Die Instrumente aus 550 $4 istr.
	 *
	 * @return Tabelle mit int-ppns Personen:Instrumente
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, Integer> getPersInstr()
			throws ClassNotFoundException, IOException {
		System.err.println("lade Person:Instrument");
		final BiMultimap<Integer, Integer> table = TableGenerator.P_INSTR_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int-ppns Personen:Berufe
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, Integer> getPersBerufe()
			throws ClassNotFoundException, IOException {
		System.err.println("lade Person:Beruf (kann mehrere Minuten dauern)");
		final BiMultimap<Integer, Integer> table = TableGenerator.P_PROF_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 *
	 * @return Tabelle mit int-ppns Werk:Instrument
	 * @throws ClassNotFoundException
	 *             wenns schiefgeht
	 * @throws IOException
	 *             wenns noch nicht erzeugt ist
	 */
	public static BiMultimap<Integer, Integer> getWerkInstr()
			throws ClassNotFoundException, IOException {
		System.err.println("lade Werk:Instrument");
		final BiMultimap<Integer, Integer> table = TableGenerator.WORK_INSTR_GENERATOR
				.getTable();
		System.err.println("fertig");
		return table;
	}

	/**
	 * Die implementierten Tabellen der Datenbank.
	 */
	public static final List<TableGenerator> IMPLEMENTED_TABLES = TableGenerator.TABLES;

	/**
	 *
	 * Hilfsfunktion, die auf {@link Constants#SATZ_TYPEN} zugreift.
	 *
	 * @return eine sprechende Darstellung der erhältlichen Datenabzüge, bzw.
	 *         ihrer Pfade. Eine Teilmenge kann dann an
	 *         {@link #createTables(Collection, Collection)} übergeben werden.
	 */
	public static Collection<String> getAvailableSources() {
		return Constants.SATZ_TYPEN.keySet();
	}

	/**
	 * Erzeugt die benötigten Tabellen aus dem Datenabzug.
	 *
	 * @param sources
	 *            Teildatenabzüge, deren Liste man mittels
	 *            {@link #getAvailableSources()} erhalten kann. Wenn "alle" in
	 *            der Liste enthalten ist, dann wird nur der Gesamtabzug der GND
	 *            verarbeitet.
	 * @param tables
	 *            gewünschte Tabellen, deren Liste man mittels
	 *            {@link #IMPLEMENTED_TABLES} erhalten kann.
	 */
	public static void createTables(final Collection<String> sources,
			final Collection<TableGenerator> tables) {
		if (sources.isEmpty() || tables.isEmpty())
			return;

		List<String> sourcePaths = CollectionUtils
				.getValues(Constants.SATZ_TYPEN, sources);
		if (sourcePaths.contains(Constants.GND))
			sourcePaths = Collections.singletonList(Constants.GND);

		System.err.println(sourcePaths);
		System.err.println(tables);
		System.err.println("Anfang: " + TimeUtils.getActualTimehhMM());

		final DownloadWorker myWorker = new DownloadWorker() {
			@Override
			protected void processRecord(final Record record) {
				final String idn = record.getId();
				if (!IDNUtils.isKorrektePPN(idn))
					return;
				// System.err.println(idn);
				tables.forEach(generator -> generator.process(record));
			}
		};

		Predicate<String> streamFilter = null;
		for (final TableGenerator generator : tables) {
			final Predicate<String> predicate = generator.getStreamFilter();
			if (predicate != null) {
				if (streamFilter == null)
					streamFilter = predicate;
				else
					streamFilter = streamFilter.or(predicate);
			}
		}
		if (streamFilter != null)
			myWorker.setStreamFilter(streamFilter);

		try {
			final String[] paths = sourcePaths.toArray(new String[0]);
			myWorker.processGZipFiles(paths);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tables.forEach(generator -> generator.save());

		System.err.println("fertig: " + TimeUtils.getActualTimehhMM());
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {

		final BiMap<Integer, String> ppn2nid = getppn2nid();
		final BiMultimap<Integer, String> ppn2name = getppn2RDAName();

		final Scanner scan = new Scanner(System.in);

		System.out.println("Kopieren Sie eine nid und drücken Sie ENTER");
		while (scan.hasNext()) {

			final String nid = StringUtils.readClipboard();
			System.out.println("nid: " + nid);
			final int idn = ppn2nid.getKey(nid);
			System.out.println("idn: " + idn);
			final Collection<String> name = ppn2name.get(idn);

			System.out.println("Name(n):" + name);
			System.out.println();
			System.out.println("Kopieren Sie eine nid und drücken Sie ENTER");
			scan.nextLine();
		}

	}

}
