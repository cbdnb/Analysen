/**
 *
 */
package scheven.hinweis.archiv;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class Util {

	/**
	 * @param hinweisLines
	 * @return
	 */
	public static List<String> get260IdnsNames(final List<Line> hinweisLines,
			final HashMap<Integer, String> ppn2name) {
		return hinweisLines.stream().map(line ->
		{
			final String idn260 = line.getIdnRelated();

			String zeitOAe = SubfieldUtils.getContentOfFirstSubfield(line, 'a');
			if (idn260 != null) {
				zeitOAe = ppn2name.get(IDNUtils.idn2int(idn260));
				return idn260 + " (" + zeitOAe + ")";
			} else {
				return zeitOAe;
			}
		}).collect(Collectors.toList());
	}

}
