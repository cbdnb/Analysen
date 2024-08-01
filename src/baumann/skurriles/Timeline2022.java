/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;

import baumann.skurriles.filter.Covid;
import baumann.skurriles.filter.FIFA_WM_Katar;
import baumann.skurriles.filter.Katar;
import baumann.skurriles.filter.Klima;
import baumann.skurriles.filter.Ukraine;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Timeline2022 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final List<Triplett<Predicate<Record>, Frequency<String>, String>> tripletts = new ArrayList<>();

		final Predicate<Record> isCovid = new Covid();
		final Frequency<String> covidFreq = new Frequency<>();
		tripletts.add(new Triplett<>(isCovid, covidFreq, "covid"));

		final Predicate<Record> isFifaKatar = new FIFA_WM_Katar();
		final Frequency<String> fifaKatarFrequency = new Frequency<>();
		tripletts.add(
				new Triplett<>(isFifaKatar, fifaKatarFrequency, "fifa+Katar"));

		final Predicate<Record> isKatar = new Katar();
		final Frequency<String> katarFrequency = new Frequency<>();
		tripletts.add(new Triplett<>(isKatar, katarFrequency, "katar"));

		final Predicate<Record> isFIFA = isFifaKatar.and(isKatar.negate());
		final Frequency<String> fifaFrequency = new Frequency<>();
		tripletts.add(new Triplett<>(isFIFA, fifaFrequency, "fifa"));

		final Predicate<Record> isKlima = new Klima();
		final Frequency<String> klimaFrequency = new Frequency<>();
		tripletts.add(new Triplett<>(isKlima, klimaFrequency, "klima"));

		final Predicate<Record> isUkraine = new Ukraine();
		final Frequency<String> ukraineFrequency = new Frequency<>();
		tripletts.add(new Triplett<>(isUkraine, ukraineFrequency, "ukraine"));

		final Frequency<String> gesamtFrequency = new Frequency<>();

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);

		final TreeSet<String> quartale = new TreeSet<>();

		reader.forEach(record ->
		{
			final Date date = RecordUtils.getDateEntered(record);
			if (date == null)
				return;
			final Calendar zugCal = TimeUtils.getCalendar(date);
			final int jahr = zugCal.get(Calendar.YEAR);
			final int quartal = TimeUtils.getQuartal(zugCal);
			final String jq = jahr + ", " + quartal + ". Quartal ";
			// System.err.println(jq);
			quartale.add(jq);
			gesamtFrequency.add(jq);
			tripletts.forEach(triplett ->
			{
				if (triplett.first.test(record))
					triplett.second.add(jq);
			});
		});

		final PrintWriter pw = FileUtils.oeffneAusgabeDatei(
				SkurConstants.FOLDER + "timeline2022.txt", false);
		pw.print("Quartal");
		tripletts.forEach(triplett -> pw.print("\t" + triplett.third));
		pw.println();
		quartale.forEach(qq ->
		{
			pw.print(qq);
			final double gesamt = gesamtFrequency.get(qq);
			tripletts.forEach(triplett ->
			{
				final double zahl = triplett.second.get(qq);
				pw.print("\t" + zahl / gesamt);
			});
			pw.println();
		});

	}

}
