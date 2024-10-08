package scheven.koerperschaften;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunPicaRS {
	protected String exe = "D:/pica-0.22.0-x86_64-pc-windows-msvc/pica.exe";
	protected String source = "D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz";
	protected String target = "-o D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz";
	protected boolean useTarget = false;
	protected Commands command = Commands.PRINT;
	protected List<String> options = Arrays.asList("-l4");

	public void setExe(final String exe) {
		this.exe = exe;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	public void setTarget(final String target) {
		this.target = target;
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
		final List<String> argList = new ArrayList<String>();
		argList.add(exe);
		argList.add(command.toString());
		argList.addAll(options);
		argList.add(source);
		if (useTarget)
			argList.add(target);

		final Runtime rt = Runtime.getRuntime();
		final Process process = rt.exec(argList.toArray(new String[] {}));
		final InputStream inputStream = process.getInputStream();
		final BufferedReader in = new BufferedReader(
				new InputStreamReader(inputStream));
		String line;
		while ((line = in.readLine()) != null)
			System.out.println(line);
		in.close();
	}

	public static void main(final String[] args) throws IOException {
		final RunPicaRS batch = new RunPicaRS();
		batch.exec();
	}

}
