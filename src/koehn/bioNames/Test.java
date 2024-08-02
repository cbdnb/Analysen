/**
 *
 */
package koehn.bioNames;

import java.io.IOException;
import java.util.Scanner;

import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.gnd.utils.IDNFinderLeven;
import utils.DB.GND_DB_UTIL;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final IDNFinderLeven finder = new IDNFinderLeven(
				GND_DB_UTIL.getppn2bioName(),
				GND_DB_UTIL.getppn2bioVerweisungen());
		final Scanner scanner = new Scanner(System.in);
		boolean quit = false;

		while (!quit) {
			final String next = scanner.nextLine();
			if (next.equals("stop")) {
				quit = true;
			} else {
				System.out.print(finder.find(next));
			}
		}

		StreamUtils.safeClose(scanner);

	}

}
