package bee;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class AlleTheoGND {

    /**
     * idn -> (Name, Level, (DDC, Det)* )
     */
    static TreeMap<String, Triplett<String, Integer, List<Pair<String, String>>>> map =
            new TreeMap<>();

    static Frequency<String> theoFrequency = new Frequency<>();

    static class FilterTheo extends DownloadWorker {
        @Override
        protected void processRecord(final Record record) {
            if (GNDUtils.isUseCombination(record))
                return;
            // Theologie?
            if (!GNDUtils.containsGNDClassificationsTrunk(record, "3."))
                return;
            final String idn = record.getId();
            final String name = GNDUtils.getNameOfRecord(record);
            final int level = GNDUtils.getLevel(record);
            final List<Pair<String, String>> ddcs =
                    GNDUtils.getAllDDCNumbersAndDet(record);
            final Triplett<String, Integer, List<Pair<String, String>>> triplett =
                    new Triplett<String, Integer, List<Pair<String, String>>>(
                            name, level, ddcs);
            map.put(idn, triplett);
        }
    }

    static class ZahlTitel extends DownloadWorker {
        @Override
        protected void processRecord(final Record record) {
            final Collection<String> rswkIds = SubjectUtils.getRSWKidsSet(record);
            for (final String id : rswkIds) {
                if (map.containsKey(id))
                    theoFrequency.add(id);
            }

        }

    }

    public static void main(final String[] args) throws IOException {
        final FilterTheo filterTheo = new FilterTheo();
        System.err.println("filter");
        filterTheo.processGZipFile("D:/Normdaten/DNBGND_s.dat.gz");
        final ZahlTitel zahlTitel = new ZahlTitel();
		final Predicate<String> streamFilter =
                new StringContains(Constants.RS + "041A " + Constants.US);
        zahlTitel.setStreamFilter(streamFilter);
        System.err.println("titel");
        zahlTitel.processGZipFile("D:/Normdaten/DNBtitelgesamt.dat.gz");

        final Set<String> set = map.keySet();
        for (final String idn : set) {
            final Triplett<String, Integer, List<Pair<String, String>>> tripel =
                    map.get(idn);
            final long titlecount = theoFrequency.get(idn);
            final String s =
                    StringUtils.concatenate("\t", idn, tripel.first,
                            tripel.second, tripel.third, tripel.third.size(),
                            titlecount);
            System.out.println(s);
        }

    }

}
