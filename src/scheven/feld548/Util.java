/**
 *
 */
package scheven.feld548;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Util {

	public static String macheAusgabeZeile(final Record record,
			final char subVon548) {
		final String idn = record.getId();

		final String nid = GNDUtils.getNID(record);
		final String kat005 = RecordUtils.getDatatype(record);
		final String verbund = GNDUtils.getIsilVerbund(record);
		final String name = GNDUtils.getNameOfRecord(record);

		final List<String> unterfelder548 = RecordUtils
				.getContentsOfAllSubfields(record, "548", subVon548);
		final String spalten548 = StringUtils.concatenate("\t", unterfelder548);

		return StringUtils.concatenateTab(idn, nid, kat005, verbund, name,
				spalten548);
	}

	public static String macheAusgabeZeile(final Record record) {
		final String idn = record.getId();

		final String nid = GNDUtils.getNID(record);
		final String kat005 = RecordUtils.getDatatype(record);
		final String verbund = GNDUtils.getIsilVerbund(record);
		final String name = GNDUtils.getNameOfRecord(record);

		final ArrayList<String> felder548 = FilterUtils.mapNullFiltered(
				RecordUtils.getLines(record, "548"),
				RecordUtils::toPicaWithoutTag);

		final String spalten548 = StringUtils.concatenate("\t", felder548);

		return StringUtils.concatenateTab(idn, nid, kat005, verbund, name,
				spalten548);
	}

	static Pattern x4Pattern = Pattern.compile("v?[xX\\d]{1,4}");
	static Pattern x8Pattern = Pattern
			.compile("v?[xX\\d]{2}\\.[xX\\d]{2}\\.[xX\\d]{1,4}");

	public static final Predicate<String> istZulaessig = datum ->
	{
		final Matcher x4Matcher = x4Pattern.matcher(datum);
		final Matcher x8Matcher = x8Pattern.matcher(datum);
		return x4Matcher.matches() || x8Matcher.matches();
	};

	public static void main(final String[] args) {
		final String s = "1230";
		System.out.println(istZulaessig.test(s));
	}

}
