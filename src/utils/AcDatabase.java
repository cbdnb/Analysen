package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;

public class AcDatabase {

	private static HashMap<String, Pair<String, TIEFE>> MAP_AC_LEVEL = null;
	private static HashMap<String, String> MAP_AC_TITLE = null;
	private static HashMap<String, String> MAP_AC_PUB = null;

	/**
	 *
	 * @param idn
	 *            beliebig
	 * @return (DDC, Level)
	 */
	public static Pair<String, TIEFE> getStatus(final String idn) {
		if (MAP_AC_LEVEL == null)
			try {
				loadAc2Level();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return MAP_AC_LEVEL.get(idn);
	}

	/**
	 *
	 * @param record
	 *            nicht null
	 * @return Die DDC am Datensatz oder, wenn nicht vorhanden, versuchsweise
	 *         die des übergeordneten Datensatzes. Sonst null.
	 */
	public static DDC_SG getSG(final Record record) {
		DDC_SG dhs = SGUtils.getDDCDHS(record);
		if (dhs == null) {
			final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
			final Pair<String, TIEFE> pair = AcDatabase.getStatus(idnBroader);
			if (pair != null) {
				final String ddcStr = pair.first;
				dhs = SGUtils.getSG(ddcStr);
			}
		}
		return dhs;
	}

	/**
	 *
	 * @param idn
	 *            beleibig
	 * @return null oder Verlage durch ' / ' getrennt, Orte nach Pica3 durch ' :
	 *         '
	 */
	public static String getVerlageUndOrte(final String idn) {
		if (MAP_AC_PUB == null)
			try {
				loadAc2Verlag();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return MAP_AC_PUB.get(idn);
	}

	public static String getTitel(final String idn) {
		if (MAP_AC_TITLE == null)
			try {
				loadAc2Titel();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return MAP_AC_TITLE.get(idn);
	}

	/**
	 * Sucht den Datensatz ab. Wenn erfolglos, dann den übergeordneten
	 * Datensatz.
	 *
	 * @param record
	 *            nicht null
	 * @return Titel aus 4000 + Titel des Teils aus 4004, durch '. ' getrennt
	 *         oder null. Unicode-Composition.
	 */
	public static String getVollstaendigenTitel(final Record record) {
		final String vollst = BibRecUtils.getVollstaendigenTitel(record);
		if (vollst != null)
			return vollst;
		// dann ist wohl der Haupttitel nicht gefunden worden:
		final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
		final String haupt = getTitel(idnBroader);
		if (haupt == null)
			return null;
		final String neben = BibRecUtils.getTitelDesTeils(record);
		if (neben == null)
			return haupt;
		else
			return haupt + ". " + neben;
	}

	/**
	 * Sucht den Datensatz ab. Wenn erfolglos, dann den übergeordneten
	 * Datensatz.
	 *
	 * @param record
	 *            nicht null
	 * @return Verlage durch ' / ' getrennt, Orte nach Pica3 durch ' : ' oder
	 *         null
	 */
	public static String getVerlageUndOrte(final Record record) {
		final String verlageOrte = BibRecUtils.getVerlageUndOrte(record);
		if (!StringUtils.isNullOrEmpty(verlageOrte))
			return verlageOrte;
		final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
		return getVerlageUndOrte(idnBroader);
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static void loadAc2Titel()
			throws IOException, FileNotFoundException, ClassNotFoundException {
		ObjectInputStream objectInputStream;
		objectInputStream = new ObjectInputStream(
				new FileInputStream(MakeAcDatabase.FILE_AC_TO_TITLE));
		MAP_AC_TITLE = (HashMap<String, String>) objectInputStream.readObject();
		MyFileUtils.safeClose(objectInputStream);
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static void loadAc2Verlag()
			throws IOException, FileNotFoundException, ClassNotFoundException {
		ObjectInputStream objectInputStream;
		objectInputStream = new ObjectInputStream(
				new FileInputStream(MakeAcDatabase.FILE_AC_TO_PUB));
		MAP_AC_PUB = (HashMap<String, String>) objectInputStream.readObject();
		MyFileUtils.safeClose(objectInputStream);
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static void loadAc2Level()
			throws IOException, FileNotFoundException, ClassNotFoundException {
		final ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(MakeAcDatabase.FILE_AC_TO_LEVEL));
		MAP_AC_LEVEL = (HashMap<String, Pair<String, TIEFE>>) objectInputStream
				.readObject();
		MyFileUtils.safeClose(objectInputStream);
	}

	public static void main(final String[] args) throws IOException {

		final String idn = StringUtils.readClipboard();
		System.out.println(getStatus(idn));
		System.out.println(getTitel(idn));
		System.out.println(getVerlageUndOrte(idn));

	}

}
