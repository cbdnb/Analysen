package scheven;

import java.util.Collection;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;

public class IDN2NID {

    public static void main(String[] args) {
        Collection<String> idns = StringUtils.readLinesFromClip();
        for (String idn : idns) {
            Record record = RecordUtils.readFromPortal(idn);
            if (record == null) {
                idn = "0" + idn;
                record = RecordUtils.readFromPortal(idn);
                if (record == null) {
                    idn = "0" + idn;
                    record = RecordUtils.readFromPortal(idn);
                }
            }
            if (record == null)
                System.out.println("nicht vorhanden");
            else {
                String name = GNDUtils.getNameOfRecord(record);
//                System.out.println(StringUtils.concatenate("\t",
//                        record.getId(), GNDUtils.getNID(record), name));
                System.out.println(GNDUtils.getNID(record));
            }
        }

    }

}
