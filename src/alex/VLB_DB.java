/**
 *
 */
package alex;

import java.util.Map;
import java.util.TreeMap;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;

/**
 * @author baumann
 *
 */
public class VLB_DB {

	static Map<Integer, String> warengruppe2description = new TreeMap<>();
	static Map<Integer, String> index2description = new TreeMap<>();

	static {
		warengruppe2description.put(110, "Erzählende Literatur");
		warengruppe2description.put(111, "Hauptwerk vor 1945");
		warengruppe2description.put(112, "Gegenwartsliteratur (ab 1945)");
		warengruppe2description.put(113, "Historische Romane und Erzählungen");
		warengruppe2description.put(114, "Märchen, Sagen, Legenden");
		warengruppe2description.put(115, "Anthologien");
		warengruppe2description.put(116, "Romanhafte Biographien");
		warengruppe2description.put(117, "Briefe, Tagebücher");
		warengruppe2description.put(118,
				"Essays, Feuilleton, Literaturkritik, Interviews");
		warengruppe2description.put(119, "Aphorismen");
		warengruppe2description.put(120, "Spannung");
		warengruppe2description.put(121, "Krimis, Thriller, Spionage");
		warengruppe2description.put(122, "Historische Kriminalromane");
		warengruppe2description.put(123, "Horror");
		warengruppe2description.put(130, "Science Fiction, Fantasy");
		warengruppe2description.put(131, "Science Fiction");
		warengruppe2description.put(132, "Fantasy");
		warengruppe2description.put(133, "Fantastische Literatur");
		warengruppe2description.put(140, "Gemischte Anthologien");
		warengruppe2description.put(150, "Lyrik, Dramatik");
		warengruppe2description.put(151, "Lyrik");
		warengruppe2description.put(152, "Dramatik");
		warengruppe2description.put(160, "Zweisprachige Ausgaben");
		warengruppe2description.put(161, "Deutsch/Englisch");
		warengruppe2description.put(162, "Deutsch/Französisch");
		warengruppe2description.put(163, "Deutsch/Italienisch");
		warengruppe2description.put(164, "Deutsch/Spanisch");
		warengruppe2description.put(165, "Deutsch/weitere Fremdsprache");
		warengruppe2description.put(180, "Comic, Cartoon, Humor, Satire");
		warengruppe2description.put(181, "Comic");
		warengruppe2description.put(182, "Manga, Manhwa");
		warengruppe2description.put(183, "Cartoons");
		warengruppe2description.put(185, "Humor, Satire, Kabarett");
		warengruppe2description.put(190,
				"Geschenkbücher, Alben, Immerwährende Kalender, Postkartenbücher");
		warengruppe2description.put(191, "Geschenkbücher");
		warengruppe2description.put(192, "Alben");
		warengruppe2description.put(193, "Immerwährende Kalender");
		warengruppe2description.put(194, "Postkartenbücher");
		warengruppe2description.put(210, "Bilderbücher");
		warengruppe2description.put(211, "Erzählerische Bilderbücher");
		warengruppe2description.put(212,
				"Pappbilderbücher mit und ohne Zusatzteile, Fühlbilderbücher");
		warengruppe2description.put(213, "Stoff-, Holz- und Badebücher");
		warengruppe2description.put(214, "Religiöse Bilderbücher");
		warengruppe2description.put(230,
				"Vorlesebücher, Märchen, Sagen, Reime, Lieder");
		warengruppe2description.put(231, "Vorlesebücher");
		warengruppe2description.put(232, "Märchen und Sagen");
		warengruppe2description.put(233, "Gedichte und Reime");
		warengruppe2description.put(234, "Lieder und Songs");
		warengruppe2description.put(240, "Erstlesealter, Vorschulalter");
		warengruppe2description.put(250, "Kinderbücher bis 11 Jahre");
		warengruppe2description.put(260, "Jugendbücher ab 12 Jahre");
		warengruppe2description.put(270, "Biographien");
		warengruppe2description.put(280, "Sachbücher / Sachbilderbücher");
		warengruppe2description.put(281, "Allgemeines, Nachschlagewerke");
		warengruppe2description.put(282, "Tiere, Pflanzen, Natur, Umwelt");
		warengruppe2description.put(283, "Naturwissenschaft, Technik");
		warengruppe2description.put(284, "Recht, Wirtschaft");
		warengruppe2description.put(285, "Mensch");
		warengruppe2description.put(286, "Geschichte, Politik");
		warengruppe2description.put(287, "Religion, Philosophie, Psychologie");
		warengruppe2description.put(288, "Kunst, Musik");
		warengruppe2description.put(289, "Sonstiges");
		warengruppe2description.put(290, "Spielen, Lernen");
		warengruppe2description.put(291, "Lernen");
		warengruppe2description.put(292, "Sprachen");
		warengruppe2description.put(293, "Mathematik");
		warengruppe2description.put(295, "Kreativität");
		warengruppe2description.put(296,
				"Abenteuer, Spielgeschichten, Unterhaltung");
		warengruppe2description.put(297, "Quiz, Rätsel");
		warengruppe2description.put(299, "Sonstiges");
		warengruppe2description.put(310, "Reiseführer");
		warengruppe2description.put(311, "Deutschland");
		warengruppe2description.put(312, "Europa");
		warengruppe2description.put(313, "Afrika");
		warengruppe2description.put(314, "Naher Osten");
		warengruppe2description.put(315, "Asien");
		warengruppe2description.put(316, "Nord- und Mittelamerika");
		warengruppe2description.put(317, "Südamerika");
		warengruppe2description.put(318, "Australien, Neuseeland, Ozeanien");
		warengruppe2description.put(319, "Welt, Arktis, Antarktis");
		warengruppe2description.put(320, "Sport- und Aktivreisen");
		warengruppe2description.put(321, "Deutschland");
		warengruppe2description.put(322, "Europa");
		warengruppe2description.put(323, "Afrika");
		warengruppe2description.put(324, "Naher Osten");
		warengruppe2description.put(325, "Asien");
		warengruppe2description.put(326, "Nord- und Mittelamerika");
		warengruppe2description.put(327, "Südamerika");
		warengruppe2description.put(328, "Australien, Neuseeland, Ozeanien");
		warengruppe2description.put(329, "Welt, Arktis, Antarktis");
		warengruppe2description.put(330, "Hotel- und Restaurantführer");
		warengruppe2description.put(331, "Deutschland");
		warengruppe2description.put(332, "Europa");
		warengruppe2description.put(333, "Afrika");
		warengruppe2description.put(334, "Naher Osten");
		warengruppe2description.put(335, "Asien");
		warengruppe2description.put(336, "Nord- und Mittelamerika");
		warengruppe2description.put(337, "Südamerika");
		warengruppe2description.put(338, "Australien, Neuseeland, Ozeanien");
		warengruppe2description.put(339, "Welt, Arktis, Antarktis");
		warengruppe2description.put(340, "Karten, Stadtpläne, Atlanten");
		warengruppe2description.put(341, "Deutschland");
		warengruppe2description.put(342, "Europa");
		warengruppe2description.put(343, "Afrika");
		warengruppe2description.put(344, "Naher Osten");
		warengruppe2description.put(345, "Asien");
		warengruppe2description.put(346, "Nord- und Mittelamerika");
		warengruppe2description.put(347, "Südamerika");
		warengruppe2description.put(348, "Australien, Neuseeland, Ozeanien");
		warengruppe2description.put(349, "Welt, Arktis, Antarktis");
		warengruppe2description.put(350, "Bildbände");
		warengruppe2description.put(351, "Deutschland");
		warengruppe2description.put(352, "Europa");
		warengruppe2description.put(353, "Afrika");
		warengruppe2description.put(354, "Naher Osten");
		warengruppe2description.put(355, "Asien");
		warengruppe2description.put(356, "Nord- und Mittelamerika");
		warengruppe2description.put(357, "Südamerika");
		warengruppe2description.put(358, "Australien, Neuseeland, Ozeanien");
		warengruppe2description.put(359, "Welt, Arktis, Antarktis");
		warengruppe2description.put(360, "Reiseberichte, Reiseerzählungen");
		warengruppe2description.put(361, "Deutschland");
		warengruppe2description.put(362, "Europa");
		warengruppe2description.put(363, "Afrika");
		warengruppe2description.put(364, "Naher Osten");
		warengruppe2description.put(365, "Asien");
		warengruppe2description.put(366, "Nord- und Mittelamerika");
		warengruppe2description.put(367, "Südamerika");
		warengruppe2description.put(368, "Australien, Neuseeland, Ozeanien");
		warengruppe2description.put(369, "Welt, Arktis ,Antarktis");
		warengruppe2description.put(380, "Globen");
		warengruppe2description.put(390, "Kartenzubehör, Sonstiges");
		warengruppe2description.put(410, "Hobby, Haus");
		warengruppe2description.put(411, "Kreatives Gestalten");
		warengruppe2description.put(412, "Handarbeit, Textiles");
		warengruppe2description.put(413, "Malen, Zeichnen, Farbe");
		warengruppe2description.put(414, "Singen, Musizieren");
		warengruppe2description.put(415, "Fotografieren, Filmen, Videofilmen");
		warengruppe2description.put(416, "Spielen, Raten");
		warengruppe2description.put(417, "Sammeln, Sammlerkataloge");
		warengruppe2description.put(418, "Heimwerken, Do it yourself");
		warengruppe2description.put(419,
				"Hausbau, Renovierung, Umbau, Innenausbau");
		warengruppe2description.put(420, "Natur");
		warengruppe2description.put(421, "Garten");
		warengruppe2description.put(422, "Naturführer");
		warengruppe2description.put(423, "Astronomie");
		warengruppe2description.put(424, "Hobbytierhaltung");
		warengruppe2description.put(425, "Pferde, Reiten");
		warengruppe2description.put(426, "Angeln, Jagd");
		warengruppe2description.put(430,
				"Fahrzeuge, Flugzeuge, Schiffe, Raumfahrt");
		warengruppe2description.put(431, "Allgemeines, Lexika, Handbücher");
		warengruppe2description.put(432, "Auto, Motorrad, Moped");
		warengruppe2description.put(433, "Fahrrad");
		warengruppe2description.put(434, "Nutzfahrzeuge");
		warengruppe2description.put(435, "Schienenfahrzeuge");
		warengruppe2description.put(436, "Schiffe");
		warengruppe2description.put(437, "Flugzeuge, Raumfahrt");
		warengruppe2description.put(438,
				"Militärfahrzeuge, -flugzeuge, -schiffe");
		warengruppe2description.put(439, "Modellbau");
		warengruppe2description.put(440, "Sport");
		warengruppe2description.put(441,
				"Allgemeines, Lexika, Handbücher, Jahrbücher, Geschichte");
		warengruppe2description.put(442,
				"Autosport, Motorradsport, Radsport, Flugsport");
		warengruppe2description.put(443, "Wassersport, Segeln");
		warengruppe2description.put(444, "Wintersport");
		warengruppe2description.put(445, "Ballsport");
		warengruppe2description.put(446, "Leichtathletik, Turnen");
		warengruppe2description.put(447, "Kampfsport, Selbstverteidigung");
		warengruppe2description.put(449, "Sonstige Sportarten");
		warengruppe2description.put(450, "Essen & Trinken");
		warengruppe2description.put(451, "Allgemeines, Lexika, Tabellen");
		warengruppe2description.put(453,
				"Allgemeine Kochbücher, Grundkochbücher");
		warengruppe2description.put(454, "Länderküchen");
		warengruppe2description.put(455, "Themenkochbücher");
		warengruppe2description.put(456, "Gesunde Küche, Schlanke Küche");
		warengruppe2description.put(457, "Backen");
		warengruppe2description.put(458, "Getränke");
		warengruppe2description.put(459, "Sonstiges");
		warengruppe2description.put(460, "Gesundheit");
		warengruppe2description.put(461, "Ernährung");
		warengruppe2description.put(462,
				"Entspannung, Yoga, Meditation, Autogenes Training");
		warengruppe2description.put(463, "Schönheit/ Kosmetik");
		warengruppe2description.put(464,
				"Fitness, Aerobic, Bodybuilding, Gymnastik");
		warengruppe2description.put(465, "Erkrankungen, Heilverfahren");
		warengruppe2description.put(466, "Alternative Heilverfahren");
		warengruppe2description.put(467, "Schwangerschaft, Geburt, Säuglinge");
		warengruppe2description.put(469, "Sonstiges");
		warengruppe2description.put(470, "Spiritualität");
		warengruppe2description.put(472, "Esoterik");
		warengruppe2description.put(473, "Astrologie, Kosmos");
		warengruppe2description.put(474, "Lebensdeutung");
		warengruppe2description.put(475, "Altes Wissen, Alte Kulturen");
		warengruppe2description.put(476, "Östliche Weisheit");
		warengruppe2description.put(478, "Anthroposophie");
		warengruppe2description.put(479, "Sonstiges");
		warengruppe2description.put(480, "Lebenshilfe, Alltag");
		warengruppe2description.put(481,
				"Lebensführung, Persönliche Entwicklung");
		warengruppe2description.put(483, "Partnerschaft, Sexualität");
		warengruppe2description.put(484, "Familie");
		warengruppe2description.put(485, "Praktische Anleitungen");
		warengruppe2description.put(486,
				"Adress-, Telefon-, Kursbücher, Einkaufsführer");
		warengruppe2description.put(490, "Recht, Beruf, Finanzen");
		warengruppe2description.put(491, "Familienrecht");
		warengruppe2description.put(492, "Grunderwerb, Immobilien");
		warengruppe2description.put(493, "Erben, Vererben");
		warengruppe2description.put(494, "sonstiges Recht");
		warengruppe2description.put(495, "Steuern");
		warengruppe2description.put(496, "Geld, Bank, Börse");
		warengruppe2description.put(497, "Ausbildung, Beruf, Karriere");
		warengruppe2description.put(498, "Briefe, Rhetorik");
		warengruppe2description.put(499, "Sonstiges");
		warengruppe2description.put(510, "Geisteswissenschaften allgemein");
		warengruppe2description.put(520, "Philosophie");
		warengruppe2description.put(521, "Allgemeines, Lexika");
		warengruppe2description.put(522, "Antike");
		warengruppe2description.put(523, "Mittelalter");
		warengruppe2description.put(524, "Renaissance, Aufklärung");
		warengruppe2description.put(525,
				"Deutscher Idealismus, 19. Jahrhundert");
		warengruppe2description.put(526, "20. und 21. Jahrhundert");
		warengruppe2description.put(527, "Östliche Philosophie");
		warengruppe2description.put(529, "Sonstiges");
		warengruppe2description.put(530, "Psychologie");
		warengruppe2description.put(531, "Allgemeines, Lexika");
		warengruppe2description.put(532, "Grundlagen");
		warengruppe2description.put(533, "Theoretische Psychologie");
		warengruppe2description.put(534, "Angewandte Psychologie");
		warengruppe2description.put(535, "Psychoanalyse");
		warengruppe2description.put(539, "Sonstiges");
		warengruppe2description.put(540, "Religion/Theologie");
		warengruppe2description.put(541, "Allgemeines, Lexika");
		warengruppe2description.put(542, "Christentum");
		warengruppe2description.put(543, "Praktische Theologie");
		warengruppe2description.put(544, "Judentum");
		warengruppe2description.put(545, "Weitere Religionen");
		warengruppe2description.put(546, "Bibelausgaben");
		warengruppe2description.put(547,
				"Religiöse Schriften, Gebete, Gesangbücher, relig. Meditationen");
		warengruppe2description.put(549, "Sonstiges");
		warengruppe2description.put(550, "Geschichte");
		warengruppe2description.put(551, "Allgemeines, Lexika");
		warengruppe2description.put(552, "Vor- und Frühgeschichte");
		warengruppe2description.put(553, "Altertum");
		warengruppe2description.put(554, "Mittelalter");
		warengruppe2description.put(555, "Neuzeit bis 1918");
		warengruppe2description.put(556, "20. Jahrhundert (bis 1945)");
		warengruppe2description.put(557, "Zeitgeschichte (1945 bis 1989)");
		warengruppe2description.put(558, "Regional- und Ländergeschichte");
		warengruppe2description.put(559, "Kulturgeschichte");
		warengruppe2description.put(560, "Sprach- und Literaturwissenschaft");
		warengruppe2description.put(561,
				"Allgemeine und Vergleichende Sprachwissenschaft");
		warengruppe2description.put(562,
				"Allgemeine und Vergleichende Literaturwissenschaft");
		warengruppe2description.put(563,
				"Deutsche Sprachwissenschaft; Deutschsprachige Literaturwissenschaft");
		warengruppe2description.put(564,
				"Englische Sprachwissenschaft / Literaturwissenschaft");
		warengruppe2description.put(565,
				"Übrige Germanische Sprachwissenschaft / Literaturwissenschaft");
		warengruppe2description.put(566,
				"Romanische Sprachwissenschaft / Literaturwissenschaft");
		warengruppe2description.put(567,
				"Klassische Sprachwissenschaft / Literaturwissenschaft");
		warengruppe2description.put(568,
				"Slawische Sprachwissenschaft / Literaturwissenschaft");
		warengruppe2description.put(569,
				"Sonstige Sprachen / Sonstige Literaturen");
		warengruppe2description.put(570, "Pädagogik");
		warengruppe2description.put(571, "Allgemeines, Lexika");
		warengruppe2description.put(572, "Bildungswesen");
		warengruppe2description.put(573, "Kindergarten- und Vorschulpädagogik");
		warengruppe2description.put(574, "Schulpädagogik, Didaktik, Methodik");
		warengruppe2description.put(575, "Grundschule");
		warengruppe2description.put(576, "Sekundarstufe I und II");
		warengruppe2description.put(577, "Erwachsenenbildung");
		warengruppe2description.put(578, "Sozialpädagogik, Soziale Arbeit");
		warengruppe2description.put(579, "Sonderpädagogik");
		warengruppe2description.put(580, "Kunst");
		warengruppe2description.put(581, "Allgemeines, Lexika");
		warengruppe2description.put(582, "Kunstgeschichte");
		warengruppe2description.put(583, "Bildende Kunst");
		warengruppe2description.put(584, "Architektur");
		warengruppe2description.put(585, "Innenarchitektur, Design");
		warengruppe2description.put(586, "Theater, Ballett");
		warengruppe2description.put(587, "Fotografie, Film, Video, TV");
		warengruppe2description.put(588, "Antiquitäten");
		warengruppe2description.put(589, "Sonstiges");
		warengruppe2description.put(590, "Musik");
		warengruppe2description.put(591, "Allgemeines, Lexika");
		warengruppe2description.put(593, "Musikgeschichte");
		warengruppe2description.put(594, "Musiktheorie, Musiklehre");
		warengruppe2description.put(595, "Instrumentenunterricht");
		warengruppe2description.put(596, "Instrumentenkunde");
		warengruppe2description.put(597, "Monografien");
		warengruppe2description.put(598, "Musikalien");
		warengruppe2description.put(599, "Sonstiges");
		warengruppe2description.put(610, "Naturwissenschaften allgemein");
		warengruppe2description.put(620, "Mathematik");
		warengruppe2description.put(621, "Allgemeines, Lexika");
		warengruppe2description.put(622, "Grundlagen");
		warengruppe2description.put(623, "Arithmetik, Algebra");
		warengruppe2description.put(624, "Geometrie");
		warengruppe2description.put(626, "Analysis");
		warengruppe2description.put(627,
				"Wahrscheinlichkeitstheorie, Stochastik, Mathematische Statistik");
		warengruppe2description.put(629, "Sonstiges");
		warengruppe2description.put(630, "Informatik, EDV");
		warengruppe2description.put(631, "Allgemeines, Lexika");
		warengruppe2description.put(632, "Informatik");
		warengruppe2description.put(633, "Programmiersprachen");
		warengruppe2description.put(634,
				"Betriebssysteme, Benutzeroberflächen");
		warengruppe2description.put(635, "Anwendungs-Software");
		warengruppe2description.put(636, "Datenkommunikation, Netzwerke");
		warengruppe2description.put(637, "Internet");
		warengruppe2description.put(638, "Hardware");
		warengruppe2description.put(639, "Sonstiges");
		warengruppe2description.put(640, "Physik, Astronomie");
		warengruppe2description.put(641, "Allgemeines, Lexika");
		warengruppe2description.put(642, "Mechanik, Akustik");
		warengruppe2description.put(643, "Elektrizität, Magnetismus, Optik");
		warengruppe2description.put(644, "Thermodynamik");
		warengruppe2description.put(645, "Atomphysik, Kernphysik");
		warengruppe2description.put(646, "Theoretische Physik");
		warengruppe2description.put(647, "Astronomie");
		warengruppe2description.put(649, "Sonstiges");
		warengruppe2description.put(650, "Chemie");
		warengruppe2description.put(651, "Allgemeines, Lexika");
		warengruppe2description.put(652, "Theoretische Chemie");
		warengruppe2description.put(653, "Anorganische Chemie");
		warengruppe2description.put(654, "Organische Chemie");
		warengruppe2description.put(655, "Physikalische Chemie");
		warengruppe2description.put(659, "Sonstiges");
		warengruppe2description.put(660, "Geowissenschaften");
		warengruppe2description.put(661, "Allgemeines, Lexika");
		warengruppe2description.put(662, "Geografie");
		warengruppe2description.put(663,
				"Stadt-, Raum- und Landschaftsplanung");
		warengruppe2description.put(665, "Geologie");
		warengruppe2description.put(666, "Paläontologie");
		warengruppe2description.put(667, "Mineralogie, Petrografie");
		warengruppe2description.put(669, "Sonstiges");
		warengruppe2description.put(670, "Biologie");
		warengruppe2description.put(671, "Allgemeines, Lexika");
		warengruppe2description.put(672, "Mikrobiologie");
		warengruppe2description.put(673, "Botanik");
		warengruppe2description.put(674, "Zoologie");
		warengruppe2description.put(675, "Biochemie, Biophysik");
		warengruppe2description.put(676, "Ökologie");
		warengruppe2description.put(677, "Genetik, Gentechnik");
		warengruppe2description.put(678,
				"Landwirtschaft, Gartenbau; Forstwirtschaft, Fischerei, Ernährung");
		warengruppe2description.put(679, "Sonstiges");
		warengruppe2description.put(680, "Technik");
		warengruppe2description.put(681, "Allgemeines, Lexika");
		warengruppe2description.put(682, "Maschinenbau, Fertigungstechnik");
		warengruppe2description.put(683,
				"Wärme-, Energie- und Kraftwerktechnik");
		warengruppe2description.put(684,
				"Elektronik, Elektrotechnik, Nachrichtentechnik");
		warengruppe2description.put(685, "Bau- und Umwelttechnik");
		warengruppe2description.put(686, "Luft- und Raumfahrttechnik");
		warengruppe2description.put(687, "Chemische Technik");
		warengruppe2description.put(689, "Sonstiges");
		warengruppe2description.put(690, "Medizin");
		warengruppe2description.put(691, "Allgemeines");
		warengruppe2description.put(692, "Nichtklinische Fächer");
		warengruppe2description.put(693, "Klinische Fächer");
		warengruppe2description.put(694, "Pflege");
		warengruppe2description.put(695, "Medizinische Fachberufe");
		warengruppe2description.put(696, "Ganzheitsmedizin");
		warengruppe2description.put(697, "Zahnheilkunde");
		warengruppe2description.put(698, "Veterinärmedizin");
		warengruppe2description.put(699, "Pharmazie");
		warengruppe2description.put(710, "Sozialwissenschaften allgemein");
		warengruppe2description.put(720, "Soziologie");
		warengruppe2description.put(721, "Allgemeines, Lexika");
		warengruppe2description.put(722, "Soziologische Theorien");
		warengruppe2description.put(723,
				"Methoden der empirischen und qualitativen Sozialforschung");
		warengruppe2description.put(724,
				"Arbeits-, Wirtschafts- und Industriesoziologie");
		warengruppe2description.put(725, "Stadt- und Regionalsoziologie");
		warengruppe2description.put(726, "Frauen- und Geschlechterforschung");
		warengruppe2description.put(727, "Sozialstrukturforschung");
		warengruppe2description.put(728, "Politische Soziologie");
		warengruppe2description.put(729, "Sonstiges");
		warengruppe2description.put(730, "Politikwissenschaft");
		warengruppe2description.put(731, "Allgemeines, Lexika");
		warengruppe2description.put(732,
				"Politische Wissenschaft und Politische Bildung");
		warengruppe2description.put(733,
				"Politische Theorien und Ideengeschichte");
		warengruppe2description.put(734, "Politisches System");
		warengruppe2description.put(735,
				"Staatslehre und politische Verwaltung");
		warengruppe2description.put(736, "Politik und Wirtschaft");
		warengruppe2description.put(737,
				"Vergleichende und internationale Politikwissenschaft");
		warengruppe2description.put(738,
				"Entwicklungstheorie und Entwicklungspolitik");
		warengruppe2description.put(739, "Sonstiges");
		warengruppe2description.put(740, "Medien, Kommunikation");
		warengruppe2description.put(741, "Allgemeines, Lexika");
		warengruppe2description.put(742, "Journalistik");
		warengruppe2description.put(743, "Buchhandel, Bibliothekswesen");
		warengruppe2description.put(744, "Medienwissenschaft");
		warengruppe2description.put(745, "Kommunikationswissenschaft");
		warengruppe2description.put(749, "Sonstiges");
		warengruppe2description.put(750, "Ethnologie");
		warengruppe2description.put(751, "Allgemeines, Lexika");
		warengruppe2description.put(752, "Völkerkunde");
		warengruppe2description.put(753, "Volkskunde");
		warengruppe2description.put(759, "Sonstiges");
		warengruppe2description.put(770, "Recht");
		warengruppe2description.put(771, "Allgemeines, Lexika");
		warengruppe2description.put(772,
				"Bürgerliches Recht, Zivilprozessrecht");
		warengruppe2description.put(773,
				"Öffentliches Recht, Verwaltungs-, Verfassungsprozessrecht");
		warengruppe2description.put(774,
				"Strafrecht, Strafprozessrecht, Kriminologie");
		warengruppe2description.put(775, "Handels-, Wirtschaftsrecht");
		warengruppe2description.put(776, "Steuern");
		warengruppe2description.put(777, "Arbeits-, Sozialrecht");
		warengruppe2description.put(778,
				"Internationales Recht, Ausländisches Recht");
		warengruppe2description.put(779, "Sonstiges");
		warengruppe2description.put(780, "Wirtschaft");
		warengruppe2description.put(781, "Allgemeines, Lexika");
		warengruppe2description.put(782, "Volkswirtschaft");
		warengruppe2description.put(783, "Betriebswirtschaft");
		warengruppe2description.put(784, "Management");
		warengruppe2description.put(785, "Werbung, Marketing");
		warengruppe2description.put(786,
				"Einzelne Wirtschaftszweige, Branchen");
		warengruppe2description.put(787, "Internationale Wirtschaft");
		warengruppe2description.put(789, "Sonstiges");
		warengruppe2description.put(810,
				"Schulbücher Allgemeinbildende Schulen");
		warengruppe2description.put(820, "Unterrichtsvorbereitung");
		warengruppe2description.put(821,
				"Unterrichtsmaterialien, Handreichungen");
		warengruppe2description.put(822, "Kita/Vorschule");
		warengruppe2description.put(823, "Grundschule");
		warengruppe2description.put(824, "Sekundarstufe I");
		warengruppe2description.put(825, "Sekundarstufe II");
		warengruppe2description.put(826, "Förder-/Sonderschule");
		warengruppe2description.put(827, "Berufliche Bildung");
		warengruppe2description.put(829, "Sonstiges");
		warengruppe2description.put(830, "Berufs- & Fachschulbücher");
		warengruppe2description.put(840, "Lernhilfen/Abiturwissen");
		warengruppe2description.put(841, "Kita/Vorschule");
		warengruppe2description.put(842, "Grundschule");
		warengruppe2description.put(843, "Sekundarstufe I");
		warengruppe2description.put(844, "Sekundarstufe II");
		warengruppe2description.put(845, "Nachschlagewerke");
		warengruppe2description.put(849, "Sonstiges");
		warengruppe2description.put(850,
				"Lektüren/Interpretationen/Lektürehilfen");
		warengruppe2description.put(851, "Deutsch");
		warengruppe2description.put(852, "Englisch");
		warengruppe2description.put(853, "Französisch");
		warengruppe2description.put(854, "Spanisch");
		warengruppe2description.put(855, "Latein");
		warengruppe2description.put(859, "Sonstige Sprachen");
		warengruppe2description.put(860, "Erwachsenenbildung/Volkshochschule");
		warengruppe2description.put(861, "VHS-/Kursmaterialien Sprache");
		warengruppe2description.put(862, "Selbstlernmaterialien Sprache");
		warengruppe2description.put(863, "VHS-/Kursmaterialien allgemein");
		warengruppe2description.put(864, "Selbstlernmaterialien allgemein");
		warengruppe2description.put(870, "Deutsch als Zweit-/Fremdsprache");
		warengruppe2description.put(890, "Sonstiges");
		warengruppe2description.put(910, "Lexika, Nachschlagewerke");
		warengruppe2description.put(911, "Lexika, Enzyklopädien");
		warengruppe2description.put(912, "Deutsche Wörterbücher");
		warengruppe2description.put(913, "Fremdsprachige Wörterbücher");
		warengruppe2description.put(914, "Sprachführer");
		warengruppe2description.put(915, "Jahrbücher");
		warengruppe2description.put(916, "Listenbücher");
		warengruppe2description.put(919, "Sonstiges");
		warengruppe2description.put(920, "Philosophie, Religion");
		warengruppe2description.put(921, "Biographien, Autobiographien");
		warengruppe2description.put(922,
				"Philosophie: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(923, "Philosophie: Antike bis Gegenwart");
		warengruppe2description.put(925,
				"Religion: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(926, "Christliche Religionen");
		warengruppe2description.put(927, "Weitere Weltreligionen");
		warengruppe2description.put(929, "Sonstiges");
		warengruppe2description.put(930,
				"Psychologie, Esoterik, Spiritualität, Anthroposophie");
		warengruppe2description.put(931, "Biographien, Autobiographien");
		warengruppe2description.put(932,
				"Psychologie: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(933, "Angewandte Psychologie");
		warengruppe2description.put(934, "Anthroposophie");
		warengruppe2description.put(935,
				"Esoterik: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(936, "Astrologie");
		warengruppe2description.put(937, "Spiritualität");
		warengruppe2description.put(938,
				"Parapsychologie, Grenzwissenschaften");
		warengruppe2description.put(939, "Sonstiges");
		warengruppe2description.put(940, "Geschichte");
		warengruppe2description.put(941, "Biographien, Autobiographien");
		warengruppe2description.put(942, "Allgemeines, Nachschlagewerke");
		warengruppe2description.put(943, "Regional- und Ländergeschichte");
		warengruppe2description.put(944, "Vor- und Frühgeschichte, Antike");
		warengruppe2description.put(945, "Mittelalter");
		warengruppe2description.put(946, "Neuzeit bis 1918");
		warengruppe2description.put(947, "20. Jahrhundert (bis 1945)");
		warengruppe2description.put(948, "Zeitgeschichte (1945 bis 1989)");
		warengruppe2description.put(949, "Sonstiges");
		warengruppe2description.put(950, "Kunst, Literatur");
		warengruppe2description.put(951, "Biographien, Autobiographien");
		warengruppe2description.put(952,
				"Kunst: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(953, "Bildende Kunst");
		warengruppe2description.put(954, "Fotokunst");
		warengruppe2description.put(955, "Architektur");
		warengruppe2description.put(956,
				"Literatur: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(957,
				"Sprache: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(959, "Sonstiges");
		warengruppe2description.put(960, "Musik, Film, Theater");
		warengruppe2description.put(961, "Biographien, Autobiographien");
		warengruppe2description.put(962,
				"Musik: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(963, "Klassik, Oper, Operette, Musical");
		warengruppe2description.put(964, "Jazz, Blues");
		warengruppe2description.put(965, "Pop, Rock");
		warengruppe2description.put(966, "Film: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(967, "TV: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(968,
				"Theater, Ballett: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(969, "Sonstiges");
		warengruppe2description.put(970, "Politik, Gesellschaft, Wirtschaft");
		warengruppe2description.put(971, "Biographien, Autobiographien");
		warengruppe2description.put(972, "Politik");
		warengruppe2description.put(973, "Gesellschaft");
		warengruppe2description.put(974,
				"Wirtschaft: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(975, "Betriebswirtschaft, Unternehmen");
		warengruppe2description.put(976, "Volkswirtschaft");
		warengruppe2description.put(977, "Geld, Bank, Börse");
		warengruppe2description.put(979, "Sonstiges");
		warengruppe2description.put(980, "Natur, Technik");
		warengruppe2description.put(981, "Biographien, Autobiographien");
		warengruppe2description.put(982, "Naturwissenschaft");
		warengruppe2description.put(983,
				"Astronomie: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(984,
				"Natur: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(985,
				"Natur und Gesellschaft: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(986,
				"Technik: Allgemeines, Nachschlagewerke");
		warengruppe2description.put(989, "Sonstiges");
		warengruppe2description.put(990, "FREIBEREICH");

		index2description.put(1, "Hardcover, Softcover");
		index2description.put(2, "Taschenbuch");
		index2description.put(3, "Zeitschrift, Loseblatt-Ausgabe");
		index2description.put(4, "DVD, Video");
		index2description.put(5, "Audio-CD, Kassette");
		index2description.put(6, "CD-ROM, DVD-ROM");
		index2description.put(7, "Kalender");
		index2description.put(8, "Karten, Globen");
		index2description.put(9, "Nonbooks, PBS");

	}

	public static String getWarengruppeDescription(final Integer warengruppe) {
		if (warengruppe == null)
			return null;
		else
			return warengruppe2description.get(warengruppe);
	}

	public static boolean containsWarengruppe(final Integer warengruppe) {
		return getWarengruppeDescription(warengruppe) != null;
	}

	public static String getIndexDescription(final Integer index) {
		if (index == null)
			return null;
		else
			return index2description.get(index);
	}

	public static boolean containsIndex(final Integer index) {
		return getIndexDescription(index) != null;
	}

	public static Pair<Integer, Integer> zerlegeWN(final String wn) {
		if (StringUtils.isNullOrEmpty(wn) || wn.length() < 4)
			return Pair.getNullPair();
		final char first = wn.charAt(0);
		if (!Character.isDigit(first))
			return Pair.getNullPair();
		final int index = first - 48;
		final String hintere3 = wn.substring(1, 4);
		int warengr;
		try {
			warengr = Integer.parseInt(hintere3);
		} catch (final NumberFormatException e) {
			return Pair.getNullPair();
		}
		// Ab hier: alles Zahlen
		if (containsIndex(index) && containsWarengruppe(warengr)) {
			return new Pair<>(index, warengr);
		}
		return Pair.getNullPair();
	}

	public static boolean isValid(final Pair<Integer, Integer> zerlegt) {
		return containsIndex(zerlegt.first)
				&& containsWarengruppe(zerlegt.second);
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final String num = "1935: Hardcover, Softcover / Sachbücher/Esoterik";
		final Pair<Integer, Integer> zerlegt = zerlegeWN(num);
		System.out.println(zerlegt);
		System.out.println(getIndexDescription(zerlegt.first));
		System.out.println(getWarengruppeDescription(zerlegt.second));
		System.out.println(isValid(zerlegt));
	}

}
