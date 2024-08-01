/**
 *
 */
package baumann.skurriles;

import de.dnb.basics.applicationComponents.strings.StringUtils;

/**
 * @author baumann
 *
 */
public class StringVerbesserer {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		String s = StringUtils.readClipboard();
		s = StringUtils.unicodeComposition(s);
		System.out.println(s);

	}

}
