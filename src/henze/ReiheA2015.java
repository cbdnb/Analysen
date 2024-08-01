package henze;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;

import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import de.dnb.ie.utils.AcDatabase;
import de.dnb.gnd.utils.WV;

public class ReiheA2015 extends DownloadWorker {

    Frequency<TIEFE> frequency = new Frequency<>();

    @Override
    protected void processRecord(final Record record) {

        // richtiges Jahr?
		final Collection<WV> jahrUndReihe = BibRecUtils.getWVs(record);
        if (jahrUndReihe.isEmpty())
            return;
        String jahr = null;
        String reihe = null;

		for (final WV pair : jahrUndReihe) {
			if (pair.getYear() == 2015 && pair.getSeries() == 'A') {
				jahr = "" + pair.getYear();
                reihe = "A";
            }
        }
        if (jahr == null || reihe == null)
            return;

        TIEFE status = SubjectUtils.getErschliessungsTiefe(record);
        if (status == null) { // übergeordneten versuchen
            final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
            final Pair<String, TIEFE> pair = AcDatabase.getStatus(idnBroader);
            // System.err.println(pair);
            if (pair != null) {
                status = pair.second;
            }
        }
        // if (status == null)
        // return;

        frequency.add(status);

    }

    public static void main(final String[] args) throws IOException {

        final ReiheA2015 ra2015 = new ReiheA2015();

		final Predicate<String> titleFilter =
                new ContainsTag("2105", '0', "15", BibTagDB.getDB());

        ra2015.setStreamFilter(titleFilter);
        ra2015.gzipSettings();

        System.err.println("Titeldaten flöhen:");

        try {
            // ra2015.processFile("D:/Normdaten/DNBtitel_Stichprobe.dat.gz");
            ra2015.processFile("Z:/cbs/zen/vollabzug/aktuell/Pica+/DNBtitelgesamt.dat.gz");
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.println(ra2015.frequency);

    }

}
