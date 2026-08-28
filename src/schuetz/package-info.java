/**
 * Wir würden gerne eine Vorstellung bekommen, wie viele Titel in der GND davon
 * betroffen sind. Könnte man die Menge der Datensätze ermitteln, bei denen die
 * folgenden Bedingungen zutrifft:
 *
 * a) Wie viele Datensätze für Körperschaften in der GND haben eine Vorgänger- /
 * Nachfolger-Beziehung (510 $4nach, 510 $4vorg)?
 *
 * b) Teilmenge von a): Wie viele der ermittelten Datensätze haben eine
 * Verknüpfung mit einem *bvz oder *dvz-Datensatz? Dabei geht es nur um die
 * Felder 3100 und 3110.
 *
 * Es wäre also interessant, wie viele Datensatze in der ZDB mit einem
 * GND-Körperschaftsdatensatz verknüpft sind, der eine Vorgänger- /
 * Nachfolger-Beziehung hat.
 *
 * Da im Gesamtdatenabzug der DNB nicht alle Datensätze der ZDB enthalten sind,
 * muss man den Auszug der ZDB verwenden. Der liegt im selben Ordner wie die
 * anderen Datenabzüge.
 *
 * Filtern mit Pica-rs:
 *
 * .\pica filter -s "002@.0 =~ '.[bd]vz' && (029A? || "029F?")"
 * D:\Normdaten\ZDBGesamtbestand.dat_alt.gz -o .\temp.dat.gz
 *
 *
 */
package schuetz;
