/**
 *
 */
package scheven;

import java.io.IOException;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		for (final String string : args) {
			System.out.format(string, args);
		}
	}

}
