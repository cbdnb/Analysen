package baumann;

import java.io.IOException;
import java.util.Collection;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.TreeMultimap;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

public class DoppelteTu extends DownloadWorker {

    int i = 0;

    Multimap<String, String> titlemap = new TreeMultimap<>();

    @Override
    protected void processRecord(final Record record) {

        if (RecordUtils.isBibliographic(record))
            return;

        String ansetzung = null;

        try {
            ansetzung = GNDUtils.getNameOfRecord(record);
        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }

        titlemap.add(ansetzung, record.getId());

        i++;

        // System.err.println(i + " / " + ansetzung);

    }

    public static void main(final String[] args) throws IOException {

        final DoppelteTu doppelte = new DoppelteTu();

        try {
            doppelte.processGZipFile("D:/Normdaten/DNBGND_u.dat.gz");
        } catch (final Exception e) {

        }

        final Multimap<String, String> multimap = doppelte.titlemap;

        for (final String title : multimap) {
            final Collection<String> ids = multimap.getNullSafe(title);
            if (ids.size() > 1)
                System.out.println(StringUtils.concatenate("\t", title, ids));
        }

    }
}
