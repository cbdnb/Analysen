package baumann.musik;

import java.io.IOException;
import java.util.Collection;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.utils.IDNUtils;
import utils.DB.GND_DB_UTIL;

public class AH02 {

	public final static int NAME = 2;
	public final static int NID = NAME + 1;

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final BiMap<Integer, String> ppn2nid = GND_DB_UTIL.getppn2nid();
		final BiMultimap<Integer, String> ppn2name = GND_DB_UTIL
				.getppn2RDAName();
		final String[][] table = StringUtils.readTableFromClip();

		System.out.println(StringUtils.concatenate(" / ", "Zeile Nr",
				"Angeblicher Name", "richtiger Name", "NID"));

		for (int i = 0; i < table.length; i++) {

			final String nid = StringUtils.getCellAt(table, i, NID);
			if (nid == null)
				continue;
			if (!IDNUtils.isKorrekteIDN(nid))
				continue;
			final Integer idn = ppn2nid.getKey(nid);
			if (idn == null) {
				System.out
						.println(i + ": NID ohne zugehörige IDN in DB: " + nid);
				continue;
			}

			final Collection<String> names = ppn2name.get(idn);
			if (names == null || names.isEmpty()) {
				System.out.println(i + ": IDN ohne zugehörigen Namen in DB: "
						+ idn + ", nid = " + nid);
				continue;
			}
			final String nameOfNID = ListUtils.getFirst(names);
			final String name = StringUtils.getCellAt(table, i, NAME);

			if (!StringUtils.equals(nameOfNID, name))
				System.out.println(StringUtils.concatenate(" / ", i + 1, name,
						nameOfNID, nid));

		}

	}

}
