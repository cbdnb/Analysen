package baumann;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class DollarT extends DownloadWorker {

    /**
     * $t:
     * 1055967915 1064045855 1068834064 1069549630 1070659371 1071329596
     * 1074001028
     */

    @Override
    protected void processRecord(final Record record) {

        if (!RecordUtils.isBibliographic(record))
            return;

        final Collection<Line> lines =
                RecordUtils.getLinesBetween(record, "4241", "4245");
        if (lines.isEmpty())
            return;
        // System.err.println(record.getId());
        // System.err.println(lines);
        final List<String> subT =
                SubfieldUtils.getContentsOfFirstSubfields(lines, 'l');
        if (subT.isEmpty())
            return;

        System.out.println(record.getId());

    }

    public static void main(final String[] args) throws IOException {

        final DollarT dollarT = new DollarT();

        try {
            dollarT.processGZipFile("Z:/cbs/zen/vollabzug/aktuell/Pica+/DNBGNDtitel.dat.gz");
        } catch (final Exception e) {

        }

    }
}
