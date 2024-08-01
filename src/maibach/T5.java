package maibach;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * Sucht in einem ersten Durchgang nach T5-Notationen, die an mehr als 5 Titeln
 * hängen.
 *
 * Diese werden in ein Multimap frequentT5 eingetragen.
 *
 * Dann werden die Sach-SW durchforstet, ob sie eine T5-Notation tragen, die in
 * frequentT5 vorkommt. Ist das der Fall, wird das Sach-SW unter der T5-Notation
 * eingetragen.
 *
 * @author baumann
 *
 */
public class T5 {

	/**
	 * Findet alle T5-Notationen am Titel und erstellt ihre Statistik in der
	 * Variablen {@link #frequencyT5}.
	 *
	 * @author baumann
	 *
	 */
	static class WorkerTitle extends DownloadWorker {

		final String t5Prefix = "T5--";

		Frequency<String> frequencyT5 = new Frequency<String>();

		int i = 0;

		@Override
		protected void processRecord(final Record record) {

			// i++;
			// if (i > 10000)
			// throw new IllegalArgumentException();
			// System.err.println(i);

			final List<String> t5List = SubjectUtils.getTable5Notations(record);
			if (t5List.isEmpty())
				return;

			// System.err.println(list);
			// Set, um doppelte auszuschließen
			final Set<String> table5 = new HashSet<String>(t5List);
			for (final String notation : table5) {
				frequencyT5.add(t5Prefix + notation);
			}
		}
	};

	/**
	 *
	 * @author baumann
	 *
	 */
	static class WorkerGND extends DownloadWorker {

		private WorkerGND(final Multimap<String, Pair<String, String>> multimap) {
			t5Multimap = multimap;
		}

		final Multimap<String, Pair<String, String>> t5Multimap;

		@Override
		protected void processRecord(final Record record) {

			if (GNDUtils.isUseCombination(record))
				return;

			final List<String> ddcs = GNDUtils.getValidDDCNumbers(record);

			if (ddcs.isEmpty())
				return;
			// doppelte vermeiden:
			final TreeSet<String> ddcSet = new TreeSet<>(ddcs);
			// System.err.println(ddcs);
			for (final String ddc : ddcSet) {
				if (t5Multimap.containsKey(ddc)) {
					final String name = GNDUtils.getNameOfRecord(record);
					final String idn = record.getId();
					final Pair<String, String> idnNamePair =
							new Pair<String, String>(idn, name);
					t5Multimap.add(ddc, idnNamePair);
				}
			}

		}
	};

	public static void main(final String[] args) throws IOException {

		// vorab nach DDC filtern (045F):
		final Predicate<String> titleFilter =
				new StringContains(Constants.RS + "045F " + Constants.US);
		final WorkerTitle workerTitle = new WorkerTitle();
		workerTitle.setStreamFilter(titleFilter);
		System.err.println("Titeldaten flöhen:");
		try {
			workerTitle.processGZipFile("D:/Normdaten/DNBtitelgesamt.dat.gz");
		} catch (final Exception e) {
		}

		/*
		 * Enthält eine Zuornung t5 -> (idn, Schlagwortname). Es sind aber nur
		 * die t5-Terme enthalten, die mehr als 5-mal an einem Titel vorkommen.
		 */
		final Multimap<String, Pair<String, String>> t5Map = new ListMultimap<>();

		final Frequency<String> frequencyT5 = workerTitle.frequencyT5;
		System.out.println("sehe nach > 5");
		// Häufigkeitsverteilung filtern und in t5Map übertragen:
		for (final String t5Notation : frequencyT5) {
			final long count = frequencyT5.get(t5Notation);
			if (count >= 5) {
				t5Map.add(t5Notation);
			}
		}

		System.err.println(t5Map);
		System.err.println("vgl. GND");

		final WorkerGND workerGND = new WorkerGND(t5Map);
		// 083 in Pica+
		final Predicate<String> gndFilter =
				new StringContains(Constants.RS + "037G " + Constants.US);
		workerGND.setStreamFilter(gndFilter);

		try {
			workerGND.processGZipFile("D:/Normdaten/DNBGND_s.dat.gz");
			workerGND.processGZipFile("D:/Normdaten/DNBGND_g.dat.gz");
		} catch (final Exception e) {
		}

		for (final String t5Notation : t5Map) {
			String s = "\"" + t5Notation + "\"\t";
			final Collection<Pair<String, String>> gndPairs = t5Map.get(t5Notation);
			s += StringUtils.makeExcelCellFromCollection(gndPairs);
			System.out.println(s);
		}

		System.out.println();
		System.out.println("-----------");
		System.out.println(t5Map);

	}

}
