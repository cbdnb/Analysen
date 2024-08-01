package schmidt;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SWDUtils;

public class SWD_Geo extends DownloadWorker {

    @Override
    protected void processRecord(final Record record) {
        final List<String> types = SWDUtils.getEntityTypes(record);
        if (types.contains("giv") || types.contains("giv"))
            return;

        if (!SWDUtils.containsChronologicalRelations(record))
            return;

        final String ansetzung = SWDUtils.getName(record, true);
        if (ansetzung == null)
            return;
        if (ansetzung.contains("-"))
            return;
        if (ansetzung.contains("|t|"))
            return;

        final List<String> relNames = SWDUtils.getPica3(record, "870", "880");
        for (final String relName : relNames) {
            if (relName.contains("-"))
                return;
        }

        // final String relString = StringUtils.concatenate(relNames);
        final String cell = StringUtils.makeExcelCell(relNames);

        System.out.println(StringUtils.concatenate("\t", record.getId(),
                ansetzung, cell));

    }

    public static void main(final String[] args) throws IOException {

        final SWD_Geo swd = new SWD_Geo();
        swd.processFile("D:/Normdaten/archiv/geo-scheven.dwl");

    }

}
