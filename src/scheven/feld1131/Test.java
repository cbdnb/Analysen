/**
 *
 */
package scheven.feld1131;

import java.io.IOException;

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
			throws IOException, ClassNotFoundException {
		final String geo = "LaPlata";
		RateNamen.initialisiere();
		System.out.println(RateNamen.getRealName(geo));
	}

}
