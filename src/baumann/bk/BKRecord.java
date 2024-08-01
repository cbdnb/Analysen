/**
 *
 */
package baumann.bk;

import java.util.ArrayList;
import java.util.List;

/**
 * @author baumann
 *
 */
public class BKRecord {

	@Override
	public String toString() {
		//@formatter:off
		return "BKRecord "
				+ "\n\tNummer=" + nummer +
				"\n\tName(n)=" + namen +
				"\n\tSyn. (freie Umschreibungen)=" + syn +
				"\n\tRegistereinträge=" + register +
				"\n\tErl.=" + erl +
				"\n\tHier=" + hier
				+ "\n\tVerw.=" + verw;
	}

	/**
	 * Nummer:
	 * <br><br>
	 * 153 $a
	 */
	String nummer;

	/**
	 * Name:
	 * <br><br>
	 * K10plus, Druckversion: Die Einträge durch ", " getrennt.
	 * <br><br>
	 * DNB: 153 $j (nicht wiederholbar), die Einträge werden durch " ; " getrennt.
	 * Disambiguierende Angaben kommen in <>.
	 * <br><br>
	 * K10plus, Datenbank: 153 $j..$X...
	 * <br>$j, $X wiederholbar. $X == Disambiguierende Angaben für die Anzeige.
	 */
	List<String> namen= new ArrayList<>();

	/**
	 * Register:
	 * <br><br>
	 * K10plus, Druckversion: Im Register (Anhang) enthalten:<br>
	 * Struktur: [Begriff] : [Nummer]
	 *  <br><br>
	 * DNB: 453 $Sa $a..., 100-mal vorhanden. Winkelklammern 2-mal vorhanden.
	 * Wurden in Unterfeld $v umgesetzt, ein Fehler?
	 * <br><br>
	 * K10plus, Datenbank: 453 $a...
	 *
	 */
	List<String> register = new ArrayList<>();


	/**
	 * "Syn.:"
	 *  <br><br>
	 * DNB: 453 $Sb $a&lt;Synonym>, 255-mal
	 * ("Freie Umschreibung"). Allerdings gibt es eigentlich keine
	 * freien Umschreibungen. Alle Synonyme sind von Registereinträgen
	 * abgeleitet.
	 * Einzige Ausnahme ist 21.50 (Bildhauerei: Allgemeines). Hier ist "Plastik"
	 * freie Umschreibung, der Registereintrag lautet "Plastik &lt;Bildhauerei>".
	 * <br><br>
	 * Winkelklammern 2-mal vorhanden (81.45 Schulverwaltung, 38.17 Geochronologie).
	 * Wurden in Unterfeld $v umgesetzt, ein Fehler?
	 * <br><br>
	 * K10plus, Druckversion: Listenelemente durch "; " getrennt (13 mal).
	 * <br>
	 * K10plus, Datenbank: 453 $a...$4nfrv
	 *
	 */
	List<String> syn = new ArrayList<>();

	/**
	 * "Erl.:"
	 *  <br><br>
	 * DNB: 900 $a...
	 * <br>
	 * Hier aber auch Anmerkungen von Mitarbeitern der DNB (Traiser, Löschel?)
	 * <br><br>
	 * K10plus, Datenbank: 900 $a..., aber 900 == 047A/00
	 *
	 */
	String erl;

	/**
	 * "Hier:"
	 * <br><br>
	 * Untergeordnete, die auch noch dazugehören.
	 * <br><br>
	 * DNB: 553 $a ... $4 nsav, Winkelklammer in runde umgewandelt.
	 * <br><br>
	 * K10plus, Druckversion: In einer Zeile. Werden durch "; " getrennt
	 * <br><br>
	 * K10plus, Datenbank: 550 $a ..., Winkelklammer 70-mal in runde umgewandelt, 165-mal nicht.
	 *
	 */
	List<String> hier = new ArrayList<>();

	/**
	 * "Verw.:"
	 * <br><br>
	 * VB, Verweis auf andere Notation.
	 * <br><br>
	 * K10plus, Druckversion:
	 * <br>
	 * Auf mehreren Zeilen am Ende des Datensatzes. Werden durch \n getrennt.
	 * <br>Struktur: <br>
	 * [Verweis auf Notation] -> \d\d\.\d\d(-\d\d\.\d\d)? ([Name der Notation(en)]).
	 * Mehrere Notationen zusammenfassend (s. 43.50)
	 * <br>oder strukturlos:<br>
	 * "Atlanten oder Karten mit Bezug auf ein bestimmtes Fach siehe unter dem betreffenden Fach"
	 * <br><br>
	 * DNB: 553 $a $4 nsiv, <br>
	 * " -> " in " siehe: " umgewwandelt. ([Name der Notation(en)]) wurde weggelassen.
	 *<br><br>
	 * K10plus, Datenbank: 553 $a $4 nsiv, <br>
	 * " -> " in " siehe: " umgewwandelt. [Name der Notation(en)]  in Unterfeld $v.
	 * Mehrere Notationen aufgezählt (s. 43.50)

	 */
	List<String> verw = new ArrayList<>();

}
