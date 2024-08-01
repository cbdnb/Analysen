/**
 *
 */
package jahns;

import java.util.Collection;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class FalscheIDNs {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Collection<String> idns = StringUtils.readLinesFromClip();
		idns.forEach(idn ->
		{
			final int ii = IDNUtils.ppn2int(idn);
			System.out.println(IDNUtils.int2PPN(ii));
		});

	}

}
