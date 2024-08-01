/**
 *
 */
package baumann.musik;

import java.io.IOException;

import de.dnb.gnd.utils.Komprimierer;

/**
 * @author baumann
 *
 */
public class Kompirmiere {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		Komprimierer.komprimiere("D:/Analysen/baumann/Musik", "sab.txt",
				"D:/Analysen/baumann/Musik/sab.gzip");

	}

}
