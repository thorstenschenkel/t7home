package de.t7soft.android.t7home.smarthome.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class InputStream2String {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private InputStream2String() {
		// utility class
	}

	public static String copyFromInputStream(InputStream in, String encoding)
			throws IOException {
		StringWriter writer = new StringWriter();
		copy(in, writer, encoding);
		String outString = writer.toString();
		return outString;
	}

	private static void copy(InputStream input, Writer output, String encoding)
			throws IOException {
		if (encoding == null) {
			InputStreamReader in = new InputStreamReader(input);
			copy(in, output);
		} else {
			InputStreamReader in = new InputStreamReader(input, encoding);
			copy(in, output);
		}
	}

	private static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	private static long copyLarge(Reader input, Writer output)
			throws IOException {
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

}
