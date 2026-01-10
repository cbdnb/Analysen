package baumann.musik.orgel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.marc.MarcUtils;
import de.dnb.basics.utils.PortalUtils;

/**
 * Erzeugt eine verkürzte Version der DDC im MARC-XML und im MARC-Format. Diese
 * Version kann weitergegeben werden, ohne die Urheberrechte von OCLC zu
 * verletzen.
 *
 * @author baumann
 *
 */
public class MarcAuslieferung {

	private static final String OUTFILE = "Orgelwerke";

	private static final String IN_FILE = ErzeugeOrgelListe.IDN_FILE_PATH;

	private static final String EXTENSION_MRC = ".mrc.gz";
	private static final String EXTENSION_XML = ".xml.gz";

	private static final String OUT_FILE_MARC = ErzeugeOrgelListe.FOLDER
			+ OUTFILE + EXTENSION_MRC;
	private static final String OUT_FILE_XML = ErzeugeOrgelListe.FOLDER
			+ OUTFILE + EXTENSION_XML;

	public static void main(final String[] args) throws IOException {

		final PrintStream marcPS = MyFileUtils
				.getGZipPrintStream(OUT_FILE_MARC);
		final PrintStream xmlgzPS = MyFileUtils
				.getGZipPrintStream(OUT_FILE_XML);

		final MarcStreamWriter marcStreamWriter = new MarcStreamWriter(marcPS,
				"UTF-8");
		final MarcXmlWriter xmlWriter = new MarcXmlWriter(xmlgzPS, "UTF-8",
				true);

		final BufferedReader input = new BufferedReader(
				new FileReader(IN_FILE));

		final AtomicInteger i = new AtomicInteger();
		input.lines().forEach(idn ->
		{

			final Record record = PortalUtils.getMarcRecord(idn);

			final Leader leader = record.getLeader();
			final char[] pos17to19 = leader.getImplDefined2();
			pos17to19[0] = 'o';
			leader.setImplDefined2(pos17to19);
			record.setLeader(leader);
			marcStreamWriter.write(record);

			final int ix = i.incrementAndGet();
			if (ix % 10 == 0)
				System.err.println(ix);

			/*
			 * Das Folgende wäre gar nicht nötig, da der Leader der
			 * ursprünglichen Datensätze nicht korrekt ist: Die Länge und
			 * Basis-Adresse sind mit 0 angegeben.
			 */
			final Record normalizedRecord = MarcUtils.normalize(record);
			xmlWriter.write(normalizedRecord);

		});

		MyFileUtils.safeClose(input);
		marcStreamWriter.close();
		xmlWriter.close();

		System.out.println("Anzahl: " + i);
	}
}
