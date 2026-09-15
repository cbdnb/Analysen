package baumann.musik;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.IDNUtils;

/**
 * Werktitel, die nur einmalig vergeben werden. Braucht etwa 1 Stunde.
 *
 * @author baumann
 */
public class Eintagsfliegen

{

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {
		final HashSet<Integer> musikIDNs = CollectionUtils
				.loadHashSet(ErzeugeMusikIDs.MUSIK_IDNS_FILE);
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_D);
		Predicate<String> contTag321X = new ContainsTag("3210",
				BibTagDB.getDB());
		contTag321X = contTag321X.or(new ContainsTag("3211", BibTagDB.getDB()));

		reader.setStreamFilter(contTag321X);
		final Frequency<Integer> frequencyEST = new Frequency<>();
		reader.forEach(record ->
		{
			final List<String> werkeS = BibRecUtils.getWorkIds(record);
			final List<Integer> idns = IDNUtils.idns2ints(werkeS);
			idns.forEach(idn ->
			{
				if (musikIDNs.contains(idn)) {
					frequencyEST.add(idn);
				}
			});
		});

		System.out.println(frequencyEST.toString());
		System.out.println();
		System.out.println("Anzahl aller Musikwerktitel: " + musikIDNs.size());
		System.out.println(
				"Anzahl verwendeter Werktitel: " + frequencyEST.size());
		System.out
				.println("Summe aller Verwendungen: " + frequencyEST.getSum());
		System.out.println("Durchschnitt: " + frequencyEST.getAverage());

	}

}
