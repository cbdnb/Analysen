/**
 *
 */
package henze;

import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.utils.DDC_SG;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final List<String> lines = StringUtils.readLinesFromClip();
		lines.forEach(line -> System.out.println(DDC_SG.getSG(line)));

	}

}
