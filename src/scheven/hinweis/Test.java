/**
 *
 */
package scheven.hinweis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.OperationNotSupportedException;

import de.dnb.gnd.exceptions.IllFormattedLineException;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws IllFormattedLineException
	 * @throws OperationNotSupportedException
	 */
	public static void main(final String[] args)
			throws OperationNotSupportedException, IllFormattedLineException {
		final String lc = "XA-IT-32-44";
		final Pattern p = Pattern.compile("..-..");
		final Matcher m = p.matcher(lc);
		if (m.find())
			System.out.println(m.group());
		if (m.find())
			System.out.println(m.group());
		else
			System.out.println("nix");

	}

}
