package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.marc.DDCMarcUtils;

/**
 * Erzeugt eine Abbildung ddc -> Registereinträge als MultiMap und speichert in
 * 
 * D:/Normdaten/ddcRegister.out .
 * 
 * @author baumann
 * 
 */
public class DDCRegister {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        /*
         * ddc -> Registereinträge
         */
        Multimap<String, String> ddc2Register = new ListMultimap<>();

        InputStream input = new FileInputStream("Z:/cbs_sync/ddc/ddc.xml");
        MarcReader marcReader = new MarcXmlReader(input);

        while (marcReader.hasNext()) {
            Record record = marcReader.next();
            if (!DDCMarcUtils.isDDCRecord(record))
                continue;
            if (DDCMarcUtils.isOverview(record))
                continue;
            if (DDCMarcUtils.isSpan(record))
                continue;
            if (DDCMarcUtils.isSynthesizedNumber(record))
                continue; // ?
            if (DDCMarcUtils.isAddInstructionOld(record))
                continue;
            String number = DDCMarcUtils.getFullClassificationNumber(record);
            List<String> indexTerms = DDCMarcUtils.getFullIndexTerms(record);
            for (String indexTerm : indexTerms) {
            	ddc2Register.add(number, indexTerm);
			}
        }

        ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream("D:/Normdaten/ddcRegister.out"));
        out.writeObject(ddc2Register);
        MyFileUtils.safeClose(out);
        System.err.println("Saved");
    }
}
