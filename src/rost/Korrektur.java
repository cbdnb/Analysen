package rost;

import java.util.HashMap;
import java.util.LinkedHashMap;

import de.dnb.basics.applicationComponents.strings.StringUtils;

public class Korrektur {

    public static void main(String[] args) {

        System.out.println(" alte Liste und ENTER ");
        StringUtils.readConsole();
        String[][] alteListe =
                StringUtils.makeTable(StringUtils.readClipboard());
        LinkedHashMap<String, String> nummer2Kommentar = new LinkedHashMap<>();
        for (int i = 1; i <= alteListe.length; i++) {
            nummer2Kommentar.put(StringUtils.getExcelCellAt(alteListe, 'A', i),
                    StringUtils.getExcelCellAt(alteListe, 'B', i));
        }

        System.out.println(" neue Liste und ENTER ");
        StringUtils.readConsole();
        String[][] neueListe =
                StringUtils.makeTable(StringUtils.readClipboard());
        for (int i = 1; i <= neueListe.length; i++) {
            String nummerNeu = StringUtils.getExcelCellAt(neueListe, 'A', i);
            String zeileNeu = nummerNeu + "\t";
            String kommNeu = StringUtils.getExcelCellAt(neueListe, 'B', i);
            String kommAlt = nummer2Kommentar.get(nummerNeu);
            if (kommAlt != null) {
                if (kommAlt.length() == 0)
                    zeileNeu += kommNeu;
                else
                    zeileNeu += kommAlt;
            }
            System.out.println(zeileNeu);
        }

    }
}
