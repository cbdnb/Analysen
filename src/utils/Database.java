package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.DbProperties;
import de.dnb.basics.applicationComponents.DbUtils;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.TreeMultimap;
import de.dnb.basics.filtering.StringContains;
import de.dnb.basics.marc.DDCMarcUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class Database {

	public Database() throws SQLException {

		final DbProperties properties = new DbProperties(
				"./dbconfig/db.properties");
		final String url = properties.getUrl()
				+ ";ifexists=false;create=false;readonly=true";
		connection = DriverManager.getConnection(url, properties.getUserName(),
				properties.getPassword());

		statementSearchID = connection.prepareStatement(
				"SELECT type, level, name, id FROM sw WHERE id = ?",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	private static final String FILENAME_DDC_XML = "Z:/cbs_sync/ddc/ddc.xml";

	private final Connection connection;

	private final PreparedStatement statementSearchID;

	// ------------

	/**
	 * ddc-Nummer -> (id, name).
	 */
	private ListMultimap<String, Pair<String, String>> ddc2Det34 = null;

	private static final String FILENAME_DDC_2_DET3AND4_OUT = "D:/Normdaten/ddc2Det3and4.out";

	/**
	 *
	 * ddc-Nummer -> (id, name).
	 *
	 * @throws IOException
	 *             wenn Normdatenabzug nicht geöffnet werden kann
	 */
	public final void createDDC2Det3And4() throws IOException {

		ddc2Det34 = new ListMultimap<>();
		final DownloadWorker ddcWorker = new DownloadWorker() {

			final List<String> dets = Arrays.asList("3", "4");

			@Override
			protected void processRecord(final Record record) {
				final Collection<Line> ddcLines = GNDUtils
						.getValidDDCLines(record);
				for (final Line line : ddcLines) {
					final String det = SubfieldUtils
							.getContentOfFirstSubfield(line, 'd');
					if (det == null || !dets.contains(det))
						continue;

					final String number = SubfieldUtils
							.getContentOfFirstSubfield(line, 'c');
					final String id = record.getId();
					final String name = GNDUtils.getNameOfRecord(record);
					final Pair<String, String> pair = new Pair<String, String>(
							id, name);
					ddc2Det34.add(number, pair);
				}
			}
		};

		// 083 in Pica+
		final Predicate<String> gndFilter = new StringContains(
				Constants.RS + "037G " + Constants.US);
		ddcWorker.setStreamFilter(gndFilter);
		ddcWorker.gzipSettings();

		ddcWorker.processFile("D:/Normdaten/DNBGND_s.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_g.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_u.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_p.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_b.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_f.dat.gz");

		// System.out.println(ddc2Det34);

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(FILENAME_DDC_2_DET3AND4_OUT));
		out.writeObject(ddc2Det34);
		FileUtils.safeClose(out);
	}

	/**
	 * ddc-Nummer -> (id, name).
	 *
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void loadDDC2Det34() throws ClassNotFoundException, IOException {
		if (ddc2Det34 != null)
			return;
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(
					new FileInputStream(FILENAME_DDC_2_DET3AND4_OUT));
		} catch (final FileNotFoundException e) {
			createDDC2Det3And4();
		}
		ddc2Det34 = (ListMultimap<String, Pair<String, String>>) objectInputStream
				.readObject();
		FileUtils.safeClose(objectInputStream);
	}

	/**
	 *
	 * @param ddc
	 *            nicht null
	 * @return SW-ID, SW-Name auch null, wenn nichts gefunden
	 */
	public Collection<Pair<String, String>> getCrissCrossHighDet(
			final String ddc) {
		try {
			loadDDC2Det34();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return ddc2Det34.get(ddc);
	}

	// ------------------------------

	public static final String FILENAME_DDC_2_SWW = "D:/Normdaten/ddc2allSWW.out";

	/**
	 * ddc-Nummer -> (id, name, determiniertheit).
	 */
	private ListMultimap<String, Triplett<String, String, String>> ddc2Sww = null;

	public void createDDC2SWW() throws IOException {

		ddc2Sww = new ListMultimap<>();

		final DownloadWorker ddcWorker = new DownloadWorker() {
			@Override
			protected void processRecord(final Record record) {
				try {
					final Collection<Line> ddcLines = GNDUtils
							.getValidDDCLines(record);
					for (final Line line : ddcLines) {
						String det = SubfieldUtils
								.getContentOfFirstSubfield(line, 'd');
						if (StringUtils.isNullOrEmpty(det))
							det = "0";
						final String number = SubfieldUtils
								.getContentOfFirstSubfield(line, 'c');
						final String id = record.getId();
						final String name = GNDUtils.getNameOfRecord(record);
						final Triplett<String, String, String> triplett = new Triplett<>(
								id, name, det);
						ddc2Sww.add(number, triplett);
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		};

		// 083 in Pica+
		final Predicate<String> gndFilter = new StringContains(
				Constants.RS + "037G " + Constants.US);
		ddcWorker.setStreamFilter(gndFilter);
		ddcWorker.gzipSettings();

		ddcWorker.processFile("D:/Normdaten/DNBGND_s.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_g.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_u.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_p.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_b.dat.gz");
		ddcWorker.processFile("D:/Normdaten/DNBGND_f.dat.gz");

		System.out.println(ddc2Sww);

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(FILENAME_DDC_2_SWW));
		out.writeObject(ddc2Sww);
		FileUtils.safeClose(out);

	}

	@SuppressWarnings("unchecked")
	private void loadDDC2SWW() throws ClassNotFoundException, IOException {
		if (ddc2Sww != null)
			return;
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(
					new FileInputStream(FILENAME_DDC_2_SWW));
		} catch (final FileNotFoundException e) {
			createDDC2SWW();
		}
		ddc2Sww = (ListMultimap<String, Triplett<String, String, String>>) objectInputStream
				.readObject();
		FileUtils.safeClose(objectInputStream);
	}

	/**
	 *
	 * @param ddc
	 *            DDC-Nummer
	 * @return SW-ID, SW-Name, SW-Determiniertheit; null, wenn nichts gefunden
	 */
	public Collection<Triplett<String, String, String>> getCrissCrossSWW(
			final String ddc) {
		try {
			loadDDC2SWW();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return ddc2Sww.get(ddc);
	}

	// ------------------------------------------

	private static final String FILENAME_DDC_2_CAPTION = "D:/Normdaten/ddc2Caption.out";

	/**
	 * ddc -> Name.
	 */
	private TreeMap<String, String> ddc2Caption = null;

	public void createDDC2Caption() throws IOException, ClassNotFoundException {

		ddc2Caption = new TreeMap<>();
		final InputStream input = new FileInputStream(FILENAME_DDC_XML);
		final MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			final org.marc4j.marc.Record record = marcReader.next();
			// if (!DDCMarcUtils.isUsedInWinIBW(record))
			// continue;

			final String number = DDCMarcUtils
					.getFullClassificationNumber(record);
			final String caption = DDCMarcUtils.getCaption(record);
			if (caption == null)
				continue;
			ddc2Caption.put(number, caption);
		}

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(FILENAME_DDC_2_CAPTION));
		out.writeObject(ddc2Caption);
		FileUtils.safeClose(out);
		// System.err.println("Saved");
		final Set<Entry<String, String>> entrySet = ddc2Caption.entrySet();
	}

	private void loadDDC2Caption() throws ClassNotFoundException, IOException {

		if (ddc2Caption != null)
			return;
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(
					new FileInputStream(FILENAME_DDC_2_CAPTION));
		} catch (final FileNotFoundException e) {
			createDDC2Caption();
		}
		objectInputStream = new ObjectInputStream(
				new FileInputStream(FILENAME_DDC_2_CAPTION));
		ddc2Caption = (TreeMap<String, String>) objectInputStream.readObject();
		FileUtils.safeClose(objectInputStream);
	}

	/**
	 * Gibt alle Klassenbenennungen, auch wenn synthetisiert.
	 *
	 * @param ddc
	 * @return auch null, wenn nicht gefunden
	 */
	public String getCaption(final String ddc) {

		try {
			loadDDC2Caption();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return ddc2Caption.get(ddc);
	}

	// -----------------------------------------------

	private static final String FILENAME_DDC_2_REGISTER = "D:/Normdaten/ddc2Registers.out";

	/**
	 * ddc -> Registereinträge
	 */
	private Multimap<String, String> ddc2Register = null;

	public void createDDC2Register()
			throws IOException, ClassNotFoundException {

		ddc2Register = new ListMultimap<>();
		final InputStream input = new FileInputStream(FILENAME_DDC_XML);
		final MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			final org.marc4j.marc.Record record = marcReader.next();
			if (!DDCMarcUtils.isUsedInWinIBW(record))
				continue;
			final String number = DDCMarcUtils
					.getFullClassificationNumber(record);
			final List<String> indexTerms = DDCMarcUtils
					.getFullIndexTerms(record);
			for (final String indexTerm : indexTerms) {
				ddc2Register.add(number, indexTerm);
			}
		}

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(FILENAME_DDC_2_REGISTER));
		out.writeObject(ddc2Register);
		FileUtils.safeClose(out);
		// System.err.println("Saved");
	}

	@SuppressWarnings("unchecked")
	private void loadDDC2Register() throws ClassNotFoundException, IOException {
		if (ddc2Register != null)
			return;
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(
					new FileInputStream(FILENAME_DDC_2_REGISTER));
		} catch (final FileNotFoundException e) {
			createDDC2Register();
		}
		ddc2Register = (Multimap<String, String>) objectInputStream
				.readObject();
		FileUtils.safeClose(objectInputStream);
	}

	/**
	 *
	 * @param ddc
	 * @return auch null, wenn nicht gefunden
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Collection<String> getRegisters(final String ddc) {
		try {
			loadDDC2Register();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return ddc2Register.get(ddc);
	}

	// -------------------------------------------
	private static final String FILENAME_DDC_2_TITLES = "D:/Normdaten/ddc2Titles.out";

	private static final String FILENAME_TITEL_GESAMT = "D:/Normdaten/DNBtitelgesamt.dat.gz";

	private TreeMultimap<String, String> ddc2Titles = null;

	public void createDDC2Titles() throws IOException {

		ddc2Titles = new TreeMultimap<>();

		final DownloadWorker worker = new DownloadWorker() {

			@Override
			protected void processRecord(final Record record) {
				final Set<String> ddcs = SubjectUtils
						.getAllDDCNotations(record);
				for (final String ddc : ddcs) {
					String newDDC = ddc.trim();
					// Titeldaten enthalten führende "-":
					newDDC = newDDC.replaceAll("^-", "");
					ddc2Titles.add(newDDC, record.getId());
					// System.err.println(newDDC);
				}
			}
		};

		// vorab nach DDC filtern (045F):
		final Predicate<String> titleFilter = new StringContains(
				Constants.RS + "045F " + Constants.US);

		worker.setStreamFilter(titleFilter);
		System.err.println("Titeldaten flöhen:");
		try {
			worker.processGZipFile(FILENAME_TITEL_GESAMT);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(FILENAME_DDC_2_TITLES));
		out.writeObject(ddc2Titles);
		FileUtils.safeClose(out);
		System.out.println(ddc2Titles);

		for (final String string : ddc2Titles) {
			System.out.println(ddc2Titles.get(string));
		}
	}

	@SuppressWarnings("unchecked")
	private void loadDDC2Titles() throws ClassNotFoundException, IOException {
		if (ddc2Titles != null)
			return;
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(
					new FileInputStream(FILENAME_DDC_2_TITLES));
		} catch (final FileNotFoundException e) {
			createDDC2Titles();
		}
		ddc2Titles = (TreeMultimap<String, String>) objectInputStream
				.readObject();
		FileUtils.safeClose(objectInputStream);
	}

	/**
	 * Gibt eine Liste von Ids (keine Duplikate!), deren Titel die
	 * DDC-Grundnotation tragen.
	 *
	 * @param ddc
	 *            nicht null
	 * @return Liste, auch leer, wenn Nichts gefunden; null, wenn Daten nicht
	 *         geladen werden konnten
	 */
	public final Collection<String> getTitleIDsForDDC(final String ddc) {
		Objects.requireNonNull(ddc);
		try {
			loadDDC2Titles();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return ddc2Titles.getNullSafe(ddc);
	}

	// -------------------------------------

	/**
	 * Holt zur idn den Namen aus der Tablle sw.
	 *
	 * @param idn
	 *            nicht null
	 * @return Name oder null
	 */
	public synchronized String getNameForIDN(final String idn) {
		ResultSet resultSetSearchID = null;
		try {
			statementSearchID.setString(1, idn);
			resultSetSearchID = statementSearchID.executeQuery();
			resultSetSearchID.next();
			final String name = resultSetSearchID.getString("name");
			DbUtils.safeCloseResultSet(resultSetSearchID);
			return name;
		} catch (final SQLException e) {
			DbUtils.safeCloseResultSet(resultSetSearchID);
			return null;
		}
	}

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException, SQLException {
		final Database database = new Database();
		// database.loadDDC2Titles();
		// TreeMultimap<String, String> ddc2titles = database.ddc2Titles;
		// for (String ddc : ddc2titles) {
		// System.out.println(ddc + "\t" + ddc2titles.get(ddc).size());
		// }
		// System.out.println(database.ddc2Titles);
		System.out.println(database.getCrissCrossSWW("T2--43"));
		// System.out.println(database.getNameForIDN("994794525"));
		// System.out.println(database.getCaption("001.01"));
		// System.out.println(database.getRegisters("001"));
		// System.out.println(database.getCrissCrossSWW("001.01"));
		// System.out.println(database.getCrissCrossHighDet("001"));
	}

}
