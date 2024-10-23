package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;

/**
 * Test
 */
public class RunPicaRS {
	private static final String OPTION_OUTPUT = "-o ";
	protected String exe = "pica";
	protected String source = "D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz";
	protected String target = OPTION_OUTPUT
			+ "D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz";
	protected boolean useTarget = false;
	protected Commands command = Commands.PRINT;
	protected List<String> options = Arrays.asList("-l4");
	private List<String> argList;

	public void setSource(final String source) {
		this.source = source;
	}

	public void setTarget(final String targetFile) {
		target = OPTION_OUTPUT + targetFile;
	}

	public void setUseTarget(final boolean useTarget) {
		this.useTarget = useTarget;
	}

	public void setCommand(final Commands command) {
		this.command = command;
	}

	public void setOptions(final List<String> options) {
		this.options = options;
	}

	public void setOptions(final String... options) {
		this.options = Arrays.asList(options);
	}

	protected enum Commands {
		//@formatter:off
		CAT("cat"),
		COMPLETIONS("completions"),
		CONVERT("convert"),
		COUNT("count"),
		EXPLODE("explode"),
		FILTER("filter"),
		FREQUENCY("frequency"),
		HASH("hash"),
		INVALID("invalid"),
		PARTITION("partition"),
		PRINT("print"),
		SLICE("slice"),
		SPLIT("split");
		//@formatter:on

		private final String command;

		Commands(final String com) {
			command = com;
		}

		@Override
		public String toString() {
			return command;
		}
	}

	public void exec() throws IOException {
		argList = new ArrayList<>();
		argList.add(exe);
		argList.add(command.toString());
		argList.addAll(options);
		argList.add(source);
		if (useTarget)
			argList.add(target);

		System.out.println(StringUtils.concatenate(" ", argList));

		final Runtime rt = Runtime.getRuntime();
		final Process process = rt.exec(argList.toArray(new String[] {}));
		final InputStream inputStream = process.getInputStream();
		final InputStream errorStream = process.getErrorStream();
		final BufferedReader in = new BufferedReader(
				new InputStreamReader(inputStream));
		final BufferedReader err = new BufferedReader(
				new InputStreamReader(errorStream));
		String lineIn;
		while ((lineIn = in.readLine()) != null)
			System.out.println(lineIn);
		String lineErr;
		while ((lineErr = err.readLine()) != null)
			System.err.println(lineErr);
		MyFileUtils.safeClose(err);
		MyFileUtils.safeClose(in);

	}

	public static void main(final String[] args) throws IOException {
		final RunPicaRS batch = new RunPicaRS();
		batch.setSource(Constants.Ts);
		batch.setOptions("-s", "-l6");
		batch.exec();
	}

}
