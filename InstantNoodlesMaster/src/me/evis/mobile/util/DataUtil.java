package me.evis.mobile.util;

import java.util.UUID;

public class DataUtil {

	private DataUtil() {
		// No instance.
	}

	public static String generateUuid() {
		return UUID.randomUUID().toString();
	}
}
