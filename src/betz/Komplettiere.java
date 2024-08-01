/**
 *
 */
package betz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class Komplettiere {

	private static final char MID = 'A';

	private static final char GND_TOP_500 = 'B';

	private static final char NAME_GND = 'C';

	private static final char URI = 'D';

	private static final char NID = 'E';

	private static final char PPN = 'F';

	private static final char BEMERKUNG = 'G';

	private static final char LCSH_ID = 'H'; // dummy

	private static final char LCSH_LABEL = 'I'; // dummy

	private static final char DUMMY = 'J';

	private static final String FEHLER = NSogg.FOLDER + "errors_embne.txt";
	/**
	 *
	 */
	private static final String AUSGABE_DATEI = NSogg.FOLDER + "embne_V1.txt";
	/**
	 *
	 */
	private static final String EINGABE_DATEI = NSogg.FOLDER + "embne.txt";
	/**
	 *
	 */
	private static final String TEST_DATEI = NSogg.FOLDER + "embne-test.txt";

	private static final String TOP_500_FILE = NSogg.FOLDER + "top500.out";

	private static HashSet<String> top500;

	private static String[][] table;
	private static String mid;
	private static String ppn;
	private static String nid;
	private static BiMultimap<String, Pair<String, String>> mid2lcsh;
	private static BiMultimap<String, String> mid2ppn;
	private static BiMap<String, String> ppn2nid;
	private static BiMultimap<String, String> ppn2name;
	private static Pair<String, String> lcshP;
	private static PrintWriter errors;
	private static PrintWriter neueTabelle;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		System.err.println("lade mid2lcsh");
		mid2lcsh = CollectionUtils.loadBiMultimap(NSogg.MID2LCSH_P);

		System.err.println("lade mid2ppn");
		mid2ppn = CollectionUtils.loadBiMultimap(NSogg.MID2PPN);

		System.err.println("lade ppn2nid");
		ppn2nid = CollectionUtils.loadBimap(NSogg.PPN2NID);

		System.err.println("lade ppn2name");
		ppn2name = CollectionUtils.loadBiMultimap(NSogg.PPN2NAME);

		top500 = CollectionUtils.loadHashSet(TOP_500_FILE);

		table = StringUtils.readTable(EINGABE_DATEI);

		errors = new PrintWriter(FEHLER);
		neueTabelle = new PrintWriter(AUSGABE_DATEI);

		for (int zeile = 2; zeile <= table.length; zeile++) {
			System.err.println("Zeile " + zeile);
			nid = StringUtils.getExcelCellAt(table, NID, zeile);

			if (!StringUtils.isNullOrWhitespace(nid)) {
				System.err.println("nid=" + nid);
				nidBekannt(zeile);
			} else {
				ppn = StringUtils.getExcelCellAt(table, PPN, zeile);
				if (!StringUtils.isNullOrWhitespace(ppn)) {
					System.err.println("ppn=" + ppn);
					ppnBekannt(zeile);
				} else {
					mid = StringUtils.getExcelCellAt(table, MID, zeile);
					System.err.println("mid=" + mid);
					if (!StringUtils.isNullOrWhitespace(mid)) {
						midBekannt(zeile);
					}
				}
			}
			// Name eintragen:
			final Collection<String> names = ppn2name.get(ppn);
			final String name = names.isEmpty() ? null
					: ListUtils.getFirst(names);
			StringUtils.setExcelCellAt(table, name, NAME_GND, zeile);

			// top500 eintragen:
			if (ppn != null)
				if (top500.contains(ppn))
					StringUtils.setExcelCellAt(table, "+", GND_TOP_500, zeile);
				else
					StringUtils.setExcelCellAt(table, "-", GND_TOP_500, zeile);
			// trimmen:
			for (char c = 'A'; c <= 'J'; c++) {
				String cell = StringUtils.getExcelCellAt(table, c, zeile);
				cell = cell == null ? null : cell.trim();
				StringUtils.setExcelCellAt(table, cell, c, zeile);
			}

			neueTabelle.println(StringUtils.concatenateTab(table[zeile - 1]));
		}
		// neueTabelle.println(StringUtils.table2String(table));
		StreamUtils.safeClose(errors);
		StreamUtils.safeClose(neueTabelle);

	}

	private static void nidBekannt(final int zeile) {
		ppn = ppn2nid.getKey(nid.trim());
		StringUtils.setExcelCellAt(table, ppn, PPN, zeile);
		ppn2mid_und_lcsh(zeile);
	}

	private static void ppnBekannt(final int zeile) {
		ppn2nid(zeile);
		ppn2mid_und_lcsh(zeile);
	}

	/**
	 * NID und URI eintragen:
	 *
	 * @param zeile
	 */
	public static void ppn2nid(final int zeile) {
		nid = ppn2nid.get(ppn.trim());
		StringUtils.setExcelCellAt(table, nid, NID, zeile);
		StringUtils.setExcelCellAt(table, GNDUtils.makeURI(nid), URI, zeile);
	}

	/**
	 * PPN eintragen und weiter:
	 *
	 * @param zeile
	 */
	private static void midBekannt(final int zeile) {
		final Set<String> ppns = mid2ppn.getValueSet(mid.trim());
		final int ppnsSize = ppns.size();
		ppn = ppnsSize == 1 ? ListUtils.getFirst(ppns).trim() : null;
		if (ppnsSize == 0)
			errors.println(
					"Zeile " + zeile + ": keine geeignete ppn zu mid " + mid);
		else if (ppnsSize > 1)
			errors.println("Zeile " + zeile + ": mehrere ppns zu mid " + mid
					+ ": " + ppns);
		else {
			StringUtils.setExcelCellAt(table, ppn, PPN, zeile);
			ppn2nid(zeile);
		}

		// LCSH bekannt
	}

	/**
	 * MID und LCSH eintragen:
	 *
	 * @param zeile
	 */
	private static void ppn2mid_und_lcsh(final int zeile) {
		// mid:
		final Set<String> mids = mid2ppn.getKeySet(ppn.trim());
		final int midsSize = mids.size();
		mid = midsSize == 1 ? ListUtils.getFirst(mids) : null;
		if (midsSize == 0)
			errors.println("Zeile " + zeile + ": keine mid zu ppn " + ppn);
		else if (midsSize > 1)
			errors.println("Zeile " + zeile + ": mehrere mids zu ppn " + ppn
					+ ": " + mids);
		else {
			StringUtils.setExcelCellAt(table, mid, MID, zeile);
		}

		// dann braucht man nicht weiterzusuchen:
		if (mid == null)
			return;

		// lcsh
		final Set<Pair<String, String>> lcshs = mid2lcsh.getValueSet(mid);
		final int lcshSize = lcshs.size();
		lcshP = lcshSize == 1 ? ListUtils.getFirst(lcshs) : null;
		if (lcshSize == 0)
			errors.println("Zeile " + zeile + ": keine lcsh zu mid " + mid);
		else if (lcshSize > 1)
			errors.println("Zeile " + zeile + ": mehrere lcsh zu mid " + mid
					+ ": " + lcshs);
		else {
			StringUtils.setExcelCellAt(table, lcshP.second, LCSH_LABEL, zeile);
			StringUtils.setExcelCellAt(table, lcshP.first, LCSH_ID, zeile);
		}
	}

}
