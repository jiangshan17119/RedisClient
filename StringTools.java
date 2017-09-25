package com;

import org.apache.commons.lang.StringUtils;

public class StringTools {
	public String getJsonValue(String message, String key) {
		int num = message.indexOf(key);
		if (num != -1) {
			int begin = num + key.length() + 3;
			String half = message.substring(begin);
			return StringUtils.substringBefore(half, "\"");
		} else {
			return null;
		}
	}

	public String setJsonValue(String message, String key, String value) {
		int num = message.indexOf(key);
		int begin = num + key.length() + 5;
		String half = message.substring(begin);
		String valueold = StringUtils.substringBefore(half, "\\\"");
		int end = begin + valueold.length();
		return message.substring(0, begin) + value + message.substring(end);
	}

	public String setXmlValue(String message, String key, String value) {
		int num = message.indexOf(key);
		int begin = num + key.length() + 1;
		String half = message.substring(begin);
		String valueold = StringUtils.substringBefore(half, "<");
		int end = begin + valueold.length();
		return message.substring(0, begin) + value + message.substring(end);
	}

	public String getXmlValue(String message, String key) {
		int num = message.indexOf(key);
		if (num != -1) {
			int begin = num + key.length() + 1;
			String half = message.substring(begin);
			return StringUtils.substringBefore(half, "<");
		} else {
			return null;
		}
	}
}
