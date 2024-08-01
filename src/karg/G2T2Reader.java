package karg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.ListMultimap;

/**
 * Ermittelt zu einem Geografikum wichtige Daten:
 * <br>
 * id -> Liste(SW-Name, T2-Nummer, Determiniertheit) 
 * <br>
 * @author baumann
 *
 */
public class G2T2Reader {

    private static final String FILENAME_IDN_TO_DDC = "D:/Normdaten/g2t2.out";
    /**
     * id -> Liste(SW-Name, T2-Nummer, Determiniertheit) 
     */
    private ListMultimap<String, Triplett<String, String, String>> idn2T2;

    @SuppressWarnings("unchecked")
    private void loadGtoT2() throws ClassNotFoundException, IOException {

        if (idn2T2 != null)
            return;

        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream =
                    new ObjectInputStream(new FileInputStream(
                            FILENAME_IDN_TO_DDC));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        idn2T2 =
                (ListMultimap<String, Triplett<String, String, String>>) objectInputStream
                        .readObject();

        FileUtils.safeClose(objectInputStream);

    }

    /**
     * 
     * @param idn
     *            nicht null
     * @return Tg-id -> Liste(Name, T2-Nummer, Determiniertheit) auch null
     */
    public Collection<Triplett<String, String, String>> getTable2ForIDN(
            final String idn) {
        try {
            loadGtoT2();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return idn2T2.get(idn);
    }

    public static void main(String[] args) throws ClassNotFoundException,
            IOException {
        G2T2Reader g2t2 = new G2T2Reader();
        String idn = StringUtils.readClipboard();
        System.out.println(g2t2.getTable2ForIDN(idn));
        

    }

}
