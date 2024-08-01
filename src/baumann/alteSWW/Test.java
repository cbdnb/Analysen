/**
 *
 */
package baumann.alteSWW;

import java.io.IOException;
import java.text.ParseException;

import de.dnb.basics.applicationComponents.strings.StringUtils;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(final String[] args)
			throws ParseException, IOException {
		String s = StringUtils.readClipboard();
		s = StringUtils.unicodeDecomposition(s);
		StringUtils.writeToClipboard(s);
	}

}
