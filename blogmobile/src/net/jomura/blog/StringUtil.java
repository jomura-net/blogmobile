package net.jomura.blog;

import java.io.UnsupportedEncodingException;

/**
 * @author Jomora
 */
public final class StringUtil {

	public static String escapeHtml(Object str) {
		return String.valueOf(str).replaceAll("<[^>]+>", "");
	}

	public static String unescapeSql(Object str) {
		return String.valueOf(str)
				.replaceAll("\\\\\"", "\"")
				.replaceAll("\\\\'", "'")
				.replaceAll("\n", "<br />\n");
	}

	public static boolean isNullOrEmpty(Object obj) {
		return obj == null || String.valueOf(obj).length() == 0;
	}

	public static String convertUTF8(String str) {
		if (isNullOrEmpty(str)) return str;
		try {
			return new String(str.getBytes("ISO8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
