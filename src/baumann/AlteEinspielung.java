package baumann;

import java.io.IOException;

import de.dnb.gnd.exceptions.IgnoringHandler;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;

public class AlteEinspielung extends DownloadWorker {

    @Override
    protected void processRecord(final Record record) {
        // System.out.println(record.getId());
        System.out.println(GNDUtils.getNID(record));

    }

    public static void main(final String[] args) throws IOException {
        final AlteEinspielung einspielung = new AlteEinspielung();
        einspielung.setRecordDelimiter("005 ");
        einspielung.setHandler(new IgnoringHandler());
        einspielung
                .processFile("D:/Normdaten/archiv/wim/dma-wim-einspielung.txt");

    }

}
