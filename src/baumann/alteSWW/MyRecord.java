/**
 *
 */
package baumann.alteSWW;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;

/**
 *
 * @author baumann
 *
 *         <br>
 *         <br>
 *         idn <br>
 *         satzart <br>
 *         name <br>
 *         systs
 *
 */
public class MyRecord implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 601201814394086993L;

	@Override
	public String toString() {
		return "MyRecord [idn=" + idn + ", satzart=" + satzart + ", name="
				+ name + ", systs=" + systs + "]";
	}

	public String toStringShort() {
		return idn + ", " + satzart + ", " + name + ", "
				+ StringUtils.concatenate(";", systs);
	}

	public String toCells() {
		return idn + ", " + satzart + ", " + name + "\t"
				+ StringUtils.concatenate(";", systs);
	}

	public String idn;
	public String satzart;
	public String name;
	public List<String> systs = new LinkedList<>();

}