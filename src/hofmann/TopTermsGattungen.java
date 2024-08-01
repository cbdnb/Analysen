package hofmann;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class TopTermsGattungen {

    public static void main(final String[] args) throws IOException {

        final HashSet<String> idns = new HashSet<>(2000);

        class AllGenres extends DownloadWorker {
            @Override
            protected void processRecord(final Record record) {
                if (RecordUtils.isBibliographic(record))
                    return;
                final List<String> classifs = GNDUtils.getGNDClassifications(record);

                if (classifs.contains("12.3"))
                    idns.add(record.getId());
            }
        }

        class TopTermsGenres extends DownloadWorker {
            @Override
            protected void processRecord(final Record record) {
                if (RecordUtils.isBibliographic(record))
                    return;

                if (GNDUtils.isUseCombination(record))
                    return;

                final List<String> classifs = GNDUtils.getGNDClassifications(record);

                if (!classifs.contains("12.3"))
                    return;
                final List<Line> obb = GNDUtils.getOBB(record);
                // Sicher Top Term:
                if (obb.isEmpty()) {
                    System.out.println(record.getId() + "\t"
                            + GNDUtils.getNameOfRecord(record));
                    return;
                }

                // Oder die OBB sind keine Gattungen:
                final List<String> obbIDs =
                        SubfieldUtils.getContentsOfFirstSubfields(obb, '9');
                for (final String obID : obbIDs) {
                    if (idns.contains(obID)) // Gattung!
                        return;
                }
                System.out.println(record.getId() + "\t"
                        + GNDUtils.getNameOfRecord(record));
            }
        }

        System.err.println("Vorsortieren:");

        final AllGenres allGenres = new AllGenres();
        allGenres.processGZipFile("D:/Normdaten/DNBGND_s.dat.gz");

        System.err.println("Durchsuchen:");
        final TopTermsGenres topTermsGenres = new TopTermsGenres();
        topTermsGenres.processGZipFile("D:/Normdaten/DNBGND_s.dat.gz");
    }

}
