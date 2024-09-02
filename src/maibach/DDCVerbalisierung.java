package maibach;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import scheven.StatistikDtEng;
import utils.Database;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.marc.DDCMarcUtils;

public class DDCVerbalisierung {

    public static void main(String[] args) throws FileNotFoundException,
            IOException, ClassNotFoundException, SQLException {
        Database database = new Database();
        Frequency<String> frequency = new Frequency<>();

        InputStream input = new FileInputStream("Z:/cbs_sync/ddc/ddc_zap.xml");
        MarcReader marcReader = new MarcXmlReader(input);

        while (marcReader.hasNext()) {
            Record record = marcReader.next();
            if (!DDCMarcUtils.isDDCRecord(record))
                continue;
            if (DDCMarcUtils.isOverview(record))
                continue;
            if (DDCMarcUtils.isSpan(record))
                continue;
            // if (DDCMarcUtils.isSynthesizedNumber(record))
            // continue; // ?
            if (!DDCMarcUtils.isDisplayedInStandardSchedulesOrTables(record))
                continue;
            if (DDCMarcUtils.isAddInstructionOld(record))
                continue;
            String name = DDCMarcUtils.getCaption(record);
            if (name == null)
                continue;
            String number = DDCMarcUtils.getFullClassificationNumber(record);

            List<String> indexTerms = DDCMarcUtils.getFullIndexTerms(record);
            Collection<Pair<String, String>> crissCross =
                    database.getCrissCrossHighDet(number);
            frequency.increment(number, indexTerms.size());

            int crissCrossSize;
            if (crissCross == null)
                crissCrossSize = 0;
            else
                crissCrossSize = crissCross.size();

            System.out.println(StringUtils.makeExcelLine(number, name,
                    indexTerms, indexTerms.size(), crissCross, crissCrossSize,
                    database.getTitleIDsForDDC(number).size()));
        }

        System.out.println(frequency.getAverage());

        MyFileUtils.safeClose(input);

    }
}
