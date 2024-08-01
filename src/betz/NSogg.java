/**
 *
 */
package betz;

import de.dnb.basics.applicationComponents.strings.StringUtils;

/**
 * Die Datei enthält entweder ppn, nid oder mid (+lcsh-Daten)
 *
 * @author baumann
 *
 */
public class NSogg {

	public static final String FOLDER = "D:/Analysen/betz/";

	public static final String TC = FOLDER + "Macs.gzip";

	public String quelle; // aus Datei
	public String nSoggUri; // aus Datei
	public String nSoggPrefLabel; // aus Datei
	public String gndBevBez; // aus Datenabzug via nid/ppn, aus Tc ($8)
	public String gndUri; // aus Datei, aus nid
	public String nid; // aus Datei, aus Tc-ppn mit Abzug, aus PPN mit Abzug
	public String ppn; // aus Datei, aus Datenabzug via nid, aus Tc via MACS-ID
	public String mid; // aus Datei, aus Tc (via nid, via ppn)
	public String lcshLabel; // dito
	public String lcshIdentifier; // dito

	static final String TOP_500_NID = FOLDER + "top500nid.out";

	/**
	 *
	 */
	static final String TOP_500_PPN = FOLDER + "top500ppn.out";

	static final String MID2PPN = FOLDER + "mid2ppn.out";

	static final String MID2LCSH_P = FOLDER + "mid2lcshP.out";

	static final String LCSH2NAME = FOLDER + "lcsh2name.out";

	static final String MID2LCSH = FOLDER + "mid2lcsh.out";

	static final String MID2TC_ID = FOLDER + "mid2TcId.out";

	static final String PPN2NAME = FOLDER + "ppn2name.out";

	static final String PPN2NID = FOLDER + "ppn2nid.out";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return StringUtils.concatenateTab(quelle, nSoggUri, nSoggPrefLabel,
				gndBevBez, gndUri, nid, ppn, "MACS" + mid, lcshLabel,
				lcshIdentifier.replace(" ", ""));
	}

}
