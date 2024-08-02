/**
 *
 */
package langer.instrumentDesJahres;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dnb.basics.collections.BiMapVisitor;
import de.dnb.basics.collections.BiMapVisitor.Direction;
import de.dnb.basics.collections.BiMapVisitor.Order;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.RankingQueue;
import de.dnb.gnd.utils.IDNUtils;
import utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 */
public class Utils {

	static final String UND_UNTERBEGRIFFE = " und Unterbegriffe: ";

	private Utils() {
		super();
	}

	// Immer wieder anzupassen:-------------------------

	// Im Plural:
	static final List<String> BERUFE = Arrays.asList("Mandolinenspielerinnen",
			"Mandolinenspieler");
	// Zugehörige IDNs:
	static final List<String> BERUFE_IDNS = Arrays.asList("982373503",
			"982373511");

	static final List<String> INSTRUMENTE_NAMEN = Arrays.asList("Mandoline",
			"Zupforchester");
	/** Zu den Instrumenten gehörige IDNs: */
	static final List<String> INSTRUMENTE_IDNS = Arrays.asList("042329396",
			"04323707X");

	/** etwa: Schlagzeugmusik */
	static final List<String> GATTUNGS_NAMEN = Arrays.asList("Mandolinenmusik");
	/** Zu den Gattungen gehörige IDNs: */
	static final List<String> GATTUNGS_IDNS = Arrays.asList("944303757");

	static final int RAENGE = 10;

	static final String OUT_FILE = "D:/Analysen/langer/Mandoline.txt";

	// -----------------------------------------

	private static BiMultimap<Integer, Integer> pers2beruf = null;
	private static BiMultimap<Integer, Integer> pers2Instrument = null;

	private static BiMultimap<Integer, Integer> werk2Instrument = null;
	private static BiMultimap<Integer, Integer> werk2komponist = null;

	private static BiMultimap<Integer, Integer> titel2musiker = null;
	private static BiMultimap<Integer, Integer> titel2werke = null;
	private static BiMultimap<Integer, Integer> ub2ob = null;

	/**
	 * Findet für alle Werte diejenigen mit den meisten Schlüsseln.
	 *
	 * @param map
	 * @param values
	 * @return
	 */
	static RankingQueue<Integer> machRangfolge(
			final BiMultimap<Integer, Integer> map, final Set<Integer> values) {
		final RankingQueue<Integer> rangfolge = RankingQueue
				.createHighestRanksQueue(RAENGE);
		values.forEach(value ->
		{
			final Set<Integer> keys = map.searchKeys(value);
			final int size = keys.size();
			rangfolge.add(value, size);
		});
		return rangfolge;
	}

	/**
	 * @return the pers2Instrument
	 */
	public static BiMultimap<Integer, Integer> getPers2Instrument() {
		if (pers2Instrument == null) {
			try {
				pers2Instrument = GND_DB_UTIL.getPersInstr();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return pers2Instrument;
	}

	/**
	 * @return the pers2beruf
	 */
	public static BiMultimap<Integer, Integer> getPers2beruf() {
		if (pers2beruf == null) {
			try {
				pers2beruf = GND_DB_UTIL.getPersBerufe();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return pers2beruf;
	}

	/**
	 * @return the werk2Instrument
	 */
	public static BiMultimap<Integer, Integer> getWerk2Instrument() {
		if (werk2Instrument == null) {
			try {
				werk2Instrument = GND_DB_UTIL.getWerkInstr();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return werk2Instrument;
	}

	/**
	 * @return the titel2musiker
	 */
	public static BiMultimap<Integer, Integer> getTitel2musiker() {
		if (titel2musiker == null)
			try {
				titel2musiker = GND_DB_UTIL.getTitelPersonen();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		return titel2musiker;
	}

	/**
	 * @return the titel2werke
	 */
	public static BiMultimap<Integer, Integer> getTitel2werke() {
		if (titel2werke == null)
			try {
				titel2werke = GND_DB_UTIL.getTitelWerke();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		return titel2werke;
	}

	/**
	 * @return the werk2komponist
	 */
	public static BiMultimap<Integer, Integer> getWerk2komponist() {
		if (werk2komponist == null)
			try {
				werk2komponist = GND_DB_UTIL.getWerkeAutor();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		return werk2komponist;
	}

	public static BiMultimap<Integer, Integer> getub2ob() {
		if (ub2ob == null)
			try {
				ub2ob = GND_DB_UTIL.getUBOB();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		return ub2ob;
	}

	/**
	 * @param ub2ob
	 * @param ausgangsmenge
	 * @return
	 *
	 */
	static Set<Integer> findeAlleUnterbegriffeZuInstrumenten(
			final List<String> ausgangsmenge) {
		final Set<Integer> instrumenteUndRel = new HashSet<>();
		final BiMapVisitor<Integer> visitor = new BiMapVisitor<>(getub2ob(),
				instrumenteUndRel::add);
		visitor.visitNodesAndChildren(Order.PREORDER, Direction.VALUE2KEY,
				IDNUtils.idns2ints(ausgangsmenge));
		return instrumenteUndRel;
	}

}
