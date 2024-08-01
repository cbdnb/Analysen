/**
 * 
 */
package scheven.verbuende;

import java.util.Collection;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;

/**
 * @author baumann
 *
 */
public class Statistik1Dim {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Collection<String> strings = StringUtils.readLinesFromClip();
		Frequency<String> frequency = new Frequency<>();
		strings.forEach(frequency::add);
		System.out.println(frequency);

	}

}
