/**
 *
 */
package schmidt;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class GeoOhneKoo {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Collection<String> zeilenColl = StringUtils.readLinesFromClip();
		final List<String> zeilen = ListUtils.convertToList(zeilenColl);
		final int size = zeilen.size();
		final Set<String> nidsGegeben = new HashSet<>(zeilen);
		// enthält 0/1 für Koordinaten vorhanden/nicht vorhanden.
		// So kann in Excel schnell gezält werden:
		final List<Integer> enthaeltKoo = new ArrayList<Integer>();
		// mit 0 initialisieren:
		for (int i = 0; i < size; i++) {
			enthaeltKoo.add(0);
		}
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tg);
		reader.forEach(record ->
		{
			final Set<String> normnummernSet = GNDUtils
					.getAlleNormnummer(record);
			normnummernSet.retainAll(nidsGegeben);
			if (normnummernSet.isEmpty())
				return;
			final List<String> normnummern = ListUtils
					.convertToList(normnummernSet);
			final Point2D koo = GNDUtils.getCenterPointCoordinates(record);
			if (koo != null) {
				final int index = zeilen.indexOf(normnummern.get(0));
				enthaeltKoo.set(index, 1);
				System.err.println(record);
			}

		});
		enthaeltKoo.forEach(System.out::println);

	}

}
