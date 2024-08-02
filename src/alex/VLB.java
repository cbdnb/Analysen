/**
 *
 */
package alex;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.applicationComponents.tuples.Quadruplett;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import utils.DB.GND_DB_UTIL;

//@formatter:off

/**
 *
 * Zur Verbesserung der abgelieferten Sachgruppen, die über eine Konkordanz zu
 * den VLB-Warengruppen erzeugt werden, hätten wir Referatsleiter gerne eine
 * statistische Auswertung. Und zwar geht es darum, welches die am häufigsten
 * intellektuell vergebenen Sachgruppen für Titel mit den jeweiligen
 * Warengruppen sind. Eine Abfrage in der WinIBW ist nicht nur sehr aufwändig,
 * sondern ganz gezielt auch gar nicht möglich, da nicht sichergestellt werden
 * kann, dass die gesuchte Warengruppe (vor dieser steht immer noch die
 * Materialart, also 1 oder 2) überhaupt zu [VLB-WN] gehört. So wird z.B. mit
 * der Abfrage (und eine bessere fällt mir nicht ein)
 *
 * „f dsf (1935 or 2935) and dsq vlbwn (and dhs i943)“
 *
 * auch idn 1217014993 gefunden, weil es die beiden folgenden 5560 gibt: 5560
 * [noScheme]1935 5560 [VLB-WN]1361: Hardcover, Softcover /
 * Reisen/Reiseberichte, Reiseerzählungen/Deutschland
 *
 * Wäre es möglich, dass Sie eine entsprechende statistische Auswertung
 * vornehmen? Genaueres müssten wir ggf. besprechen, z.B.
 * - für welchen Zeitraum (die Warengruppen gelten ab 2007)
 * - nur für einen Teil der Warengruppen oder für alle
 * - für die häufigsten z.B. 3 oder 5 intellektuell vergebenen SG
 * - nur die SG mit $Ei oder auch die ohne Kennzeichnung
 *
 *
 * @author baumann
 *
 */

//@formatter:on
public class VLB {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		final PrintWriter out = FileUtils
				.outputFile("D:/Analysen/alex/vlb-wn.txt", false);
		final Map<Integer, CrossProductFrequency> wn2sgg = new TreeMap<>();
		final RecordReader reader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz");
		reader.setStreamFilter(
				new ContainsTag("5560", 'b', "VLB-WN", BibTagDB.getDB()));
		final Map<Integer, Quadruplett<DDC_SG, DDC_SG, TIEFE, String>> id2Broader = GND_DB_UTIL
				.getIdn2Status();

		long i = 0;
		for (final de.dnb.gnd.parser.Record record : reader) {

			final Integer jahr = BibRecUtils.getYearOfPublication(record);

			if (jahr == null || jahr < 2010) {
				continue;
			}

			final Quadruplett<DDC_SG, DDC_SG, TIEFE, String> status = SubjectUtils
					.getErschliessungsStatus(record, id2Broader);
			if (status.first == null)
				continue;
			final String dollarE = status.forth;
			if (!Arrays.asList("i", null).contains(dollarE))
				continue;
			final List<Line> lines = RecordUtils.getLinesWithSubfield(record,
					"5560", 'b', "VLB-WN");

			i++;
			if (i % 5000 == 0)
				System.err.println(i + ": " + record.getId());

			lines.forEach(line ->
			{
				final String dollarA = SubfieldUtils
						.getContentOfFirstSubfield(line, 'a');
				final Pair<Integer, Integer> zerlegt = VLB_DB
						.zerlegeWN(dollarA);
				if (!VLB_DB.isValid(zerlegt))
					return;

				final Integer warengruppe = zerlegt.second;
				final DDC_SG dhs = status.first;
				final DDC_SG dns = status.second;
				CrossProductFrequency freq = wn2sgg.get(warengruppe);
				if (freq == null) {
					freq = new CrossProductFrequency();
					wn2sgg.put(warengruppe, freq);
				}

				freq.addValues(dhs, dns);
			});
		}
		System.err.println("Ausgabe");
		System.err.println(wn2sgg.size());

		final DecimalFormat df = new DecimalFormat("#.0");
		final int MAX = 7;

		wn2sgg.forEach((wn, sg2count) ->
		{
			out.print(wn + "\t" + VLB_DB.getWarengruppeDescription(wn));
			final long sum = sg2count.getSum();
			final Collection<Pair<Collection<? extends Object>, Long>> ordered = sg2count
					.getOrderedDistribution();
			int j = 0;
			for (final Pair<Collection<? extends Object>, Long> p : ordered) {
				j++;
				final List<DDC_SG> sgg = (List<DDC_SG>) p.first;
				final String dhs = sgg.get(0).getDDCString();
				final DDC_SG dns = sgg.get(1);
				String sgOut = dhs;
				if (dns != null)
					sgOut += ";" + dns.getDDCString();
				final long count = p.second;
				final double prozent = 100. * count / sum;

				out.print("\t" + StringUtils.concatenateTab(sgOut, count,
						df.format(prozent)));
				if (j == MAX)
					break;
			}

			out.println();
		});

		FileUtils.safeClose(out);

	}

}
