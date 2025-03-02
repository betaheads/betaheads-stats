package net.betaheads.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class Utils {
	public static void download(Logger log, URL url, File file) throws IOException {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdir();
		if (file.exists())
			file.delete();
		file.createNewFile();
		final int size = url.openConnection().getContentLength();
		log.info("Downloading " + file.getName() + " (" + size / 1024 + "kb) ...");
		final InputStream in = url.openStream();
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		final byte[] buffer = new byte[1024];
		int len, downloaded = 0, msgs = 0;
		final long start = System.currentTimeMillis();
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
			downloaded += len;
			if ((int) ((System.currentTimeMillis() - start) / 500) > msgs) {
				log.info((int) (downloaded / (double) size * 100d) + "%");
				msgs++;
			}
		}
		in.close();
		out.close();
		log.info("Download finished");
	}

	public static String readURL(URL url) throws IOException {
		final StringBuilder content = new StringBuilder();
		final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			content.append(inputLine);
		in.close();
		return content.toString();
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (final NumberFormatException ex) {
		}
		return false;
	}

	public static String listing(String[] entries, String delimiter, String finalDelimiter) {
		final int len = entries.length;
		if (len == 0)
			return "";
		if (len == 1)
			return entries[0];
		final StringBuilder builder = new StringBuilder(entries[0]);
		for (int i = 1; i < len - 1; i++)
			builder.append(delimiter + entries[i]);
		builder.append(finalDelimiter + entries[len - 1]);
		return builder.toString();
	}

	public static int parseTimeSpec(String[] spec) {
		if (spec == null || spec.length < 1 || spec.length > 2)
			return -1;
		if (spec.length == 1 && isInt(spec[0]))
			return Integer.valueOf(spec[0]);
		if (!spec[0].contains(":") && !spec[0].contains("."))
			if (spec.length == 2) {
				if (!isInt(spec[0]))
					return -1;
				int min = Integer.parseInt(spec[0]);
				if (spec[1].startsWith("h"))
					min *= 60;
				else if (spec[1].startsWith("d"))
					min *= 1440;
				return min;
			} else if (spec.length == 1) {
				int days = 0, hours = 0, minutes = 0;
				int lastIndex = 0, currIndex = 1;
				while (currIndex <= spec[0].length()) {
					while (currIndex <= spec[0].length() && isInt(spec[0].substring(lastIndex, currIndex)))
						currIndex++;
					if (currIndex - 1 != lastIndex) {
						final String param = spec[0].substring(currIndex - 1, currIndex).toLowerCase();
						if (param.equals("d"))
							days = Integer.parseInt(spec[0].substring(lastIndex, currIndex - 1));
						else if (param.equals("h"))
							hours = Integer.parseInt(spec[0].substring(lastIndex, currIndex - 1));
						else if (param.equals("m"))
							minutes = Integer.parseInt(spec[0].substring(lastIndex, currIndex - 1));
					}
					lastIndex = currIndex;
					currIndex++;
				}
				if (days == 0 && hours == 0 && minutes == 0)
					return -1;
				return minutes + hours * 60 + days * 1440;
			} else
				return -1;
		final String timestamp;
		if (spec.length == 1) {
			if (spec[0].contains(":"))
				timestamp = new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()) + " " + spec[0];
			else
				timestamp = spec[0] + " 00:00:00";
		} else
			timestamp = spec[0] + " " + spec[1];
		try {
			return (int) ((System.currentTimeMillis()
					- new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(timestamp).getTime()) / 60000);
		} catch (final ParseException ex) {
			return -1;
		}
	}

	public static String spaces(int count) {
		final StringBuilder filled = new StringBuilder(count);
		for (int i = 0; i < count; i++)
			filled.append(' ');
		return filled.toString();
	}

	// System.out.println(formatMillis(3784000)); // Output: "1 h, 3 min, 4 sec"
	public static String formatMillis(long millis) {
		long totalSeconds = millis / 1000;
		long seconds = totalSeconds % 60;
		long totalMinutes = totalSeconds / 60;
		long minutes = totalMinutes % 60;
		long hours = totalMinutes / 60;

		StringBuilder result = new StringBuilder();
		if (hours > 0) {
			result.append(hours).append(" h");
		}

		if (minutes > 0) {
			if (result.length() > 0) {
				result.append(", ");
			}

			result.append(minutes).append(" min");
		}

		if (seconds > 0 || result.length() == 0) {
			if (result.length() > 0) {
				result.append(", ");
			}

			result.append(seconds).append(" sec");
		}

		return result.toString();
	}

	public static String toReadableName(String str) {
		String name = str;
		name = name.replace("_", " ");

		name = capitalizeWords(name);

		return name;
	}

	public static String capitalizeWords(String input) {
		StringBuilder result = new StringBuilder();
		String[] words = input.split(" ");

		for (String word : words) {
			if (word.length() > 0) {
				result.append(Character.toUpperCase(word.charAt(0)))
						.append(word.substring(1).toLowerCase())
						.append(" ");
			}
		}

		return result.toString().trim();
	}
}
