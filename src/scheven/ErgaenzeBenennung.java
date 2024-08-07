package scheven;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.utils.IDNUtils;
import utils.DB.GND_DB_UTIL;

/**
 * Liest eine idn-Menge vom Clip und durchsucht die GND nach der korrekten
 * Benennung. Die Datenbank in {@link GND_DB_UTIL} muss zuvor aktualisiert
 * werden.
 */
public class ErgaenzeBenennung {

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final List<String> idns = StringUtils.readLinesFromClip();
		final HashMap<Integer, String> idn2name = GND_DB_UTIL.getppn2name();

		idns.forEach(
				idn -> System.out.println(idn2name.get(IDNUtils.ppn2int(idn))));
	}

}
