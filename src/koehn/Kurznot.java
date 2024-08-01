package koehn;

import java.sql.SQLException;
import java.util.Collection;

import utils.Database;

import de.dnb.basics.applicationComponents.strings.StringUtils;

public class Kurznot {

	public static void main(final String[] args) throws SQLException {
		final Database database = new Database();
		final Collection<String> kurzDDCs = StringUtils.readLinesFromClip();
		for (final String ddc : kurzDDCs) {
			final String out = StringUtils.concatenate("\t", ddc,
					database.getCaption(ddc),
					database.getCrissCrossHighDet(ddc));
			System.out.println(out);
		}

	}

}
