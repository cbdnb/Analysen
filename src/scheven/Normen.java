/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.collections.ListUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.SGUtils;

/**
 * @author baumann
 *
 */
public class Normen {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final CrossProductFrequency frequency = new CrossProductFrequency();

		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Analysen/baumann/Normen.txt");

		reader.forEach(record ->
		{

			final String shortTitle = BibRecUtils.createShortTitle(record);
			final String idn = record.getId();
			final String jahr = BibRecUtils.getYearOfPublicationString(record);
			final Line dhsline = SGUtils.getDHSLine(record);
			final String dhs = SGUtils.getDhsStringPair(dhsline).getFirst();
			final List<String> dnsList = SGUtils.getDNSstrings(dhsline);
			final List<String> dnsNeu = ListUtils.newSubList(dnsList, 0, 4);
			final String dnsStr = dnsNeu.stream()
					.map(dns -> dns != null ? dns : "")
					.collect(Collectors.joining("\t"));

			final String out = StringUtils.concatenate("\t", idn, jahr,
					shortTitle, dhs, dnsStr);
			// System.out.println(out);
			dnsList.add(0, dhs);
			frequency.add(dnsList);

		});

		System.out.println("----");
		frequency.getEntries().forEach(entry ->
		{
			final Collection<? extends Object> key = entry.getKey();
			final Object dhs = ListUtils.get(key, 0).get();
			final Object dns1 = ListUtils.get(key, 1).orElse(null);
			final Object dns2 = ListUtils.get(key, 2).orElse(null);
			System.out.println(StringUtils.concatenate("\t", dhs, dns1, dns2,
					entry.getValue()));

		});
		System.err.println(frequency);

	}

}
