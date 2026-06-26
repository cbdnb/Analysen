/**
 * @author baumann
 *
 * @implNote
 *
 *           Der Zweck des Paketes ist die Umwandlung von Hinweis-Datensätzen in
 *           normale Datensätze. Da in der Regel zu einer SW-Kombination
 *           ("Kombi") in 260 mehrere Hinweis-Datensätze existieren und da keine
 *           4XX-Felder verwendet werden, ist die Umwandlung nicht trivial. Die
 *           Umwandlung erfolgt in mehreren Schritten:
 *
 *           Aus den 260-Feldern wird in der Regel das 1XX-Feld des normalen
 *           Datensatzes gebildet
 *           {@link scheven.hinweis.Transformer#make1XX(java.util.Set, de.dnb.gnd.parser.Record)}
 *           Dazu wird zumeist die Expansion verwendet. Da diese auch
 *           Informationen über den Typ des Datensatzes enthält (" Ts1" -> s),
 *           kann das zur Bildung des Namens des neuen Datensatzes herangezogen
 *           werden.
 *
 *           Die anderen Felder des neuen Datensatzes werden aus allen zur Kombi
 *           gehörenden Hinweis-Datensätzen durch Kumulation gebildet.
 *           {@link scheven.hinweis.Transformer#mergeRecords(de.dnb.gnd.parser.Record, java.util.Collection)}
 *           <br>
 *           <br>
 *           Vorgehensweise:
 *           <ol>
 *           <li>Hinweis-Datensätze mit der WinIBW nach
 *           {@link scheven.hinweis.Util#FOLDER} downloaden. Suche etwa:<br>
 *           <code>f ent gib and bbg t##e<code/>
 *           <li>{@link scheven.hinweis.Util#DOWNLOAD_FILE} entsprechend setzen.
 *           <li>Die abstrakte Klasse {@link scheven.hinweis.Transformer}
 *           implementieren, um die entsprecheden neuen Datensätze zu erzeugen.
 *           Muster: {@link scheven.hinweis.Transformer313ab} oder
 *           {@link scheven.hinweis.TransformerSammlung}<br>
 *           Dabei filtern nach der Signatur, der Multimenge der Typen in 260,
 *           z.B.: <br>
 *           *
 *
 *           <pre>
 *           <code>
 *
 *           db.retainIfKombi(kombi ->
 *
 *           {
 *
 *           final Multiset<Character> sigset = new
 *           Multiset<>(Util.getSignature(kombi));
 *
 *           return sigset.equals(ps);
 *
 *           });
 *
 *           <code/>
 *
 *           <pre/>
 *           Damit werden nur die Datensätze verarbeitet, die der Signatur
 *           entsprechen. Es wird im Beispiel (ps) z.B. erreicht, dass nur ein
 *           Personen- und ein Sachschlagwort in der Kombination enthalten sind.
 *
 */
package scheven.hinweis;
