package scheven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.utils.HTMLEntities;

/**
 * Wandelt die CONVERSION REPORTs des CBS in ein lesbares Format um.
 * Insebesondere werden Sonderzeichen, die als HTML ausgegeben werden, in
 * Unicode-Zeichen umgewandelt.
 *
 * @author baumann
 *
 */
public class IBWOutPretty {

    private static final int POS_NEXT_REC = 49;
    private static final boolean doOutput = true;

    public static void main(final String[] args) throws IOException {
        final JFileChooser fileChooser = new JFileChooser();
        final int retval = fileChooser.showOpenDialog(null);
        if (retval == JFileChooser.CANCEL_OPTION)
            return;
        final File file = fileChooser.getSelectedFile();
        final String filePath = file.getAbsolutePath();
        System.out.println(filePath);

        final String ausgabe = filePath + " - Kopie (Unicode).txt";

        PrintWriter pw = null;
        if (doOutput)
            pw = new PrintWriter(ausgabe);
        final BufferedReader in = new BufferedReader(new FileReader(filePath));

        final ArrayList<String> splits = new ArrayList<>();
        String line = null;
        while ((line = in.readLine()) != null) {
            splits.clear();

            if (StringUtils.charAt(line, POS_NEXT_REC) == '|') {
                splits.add(line.substring(0, POS_NEXT_REC - 1));
                splits.add(line.substring(POS_NEXT_REC + 1));
            } else {
                splits.add(line);
            }

            for (int i = 0; i < splits.size(); i++) {
                String frac = splits.get(i);
                frac = frac.replace("\\", "");
                frac = HTMLEntities.unhtmlentities(frac);
                // Das nur, weil das bescheuerte WordPad kein Unicode kann!
                // frac =
                // new String(frac.getBytes(),
                // Charset.forName("ISO-8859-1"));
                frac = StringUtils.rightPadding(frac, 51, ' ');
                // System.err.println(frac.length());
                splits.set(i, frac);

            }
            final String newline = StringUtils.concatenate("|", splits);
            if (doOutput)
                pw.println(newline);
            System.out.println(newline);

        }

        MyFileUtils.safeClose(in);
        MyFileUtils.safeClose(pw);

    }

}
