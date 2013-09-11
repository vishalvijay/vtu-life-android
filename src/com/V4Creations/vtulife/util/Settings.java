package com.V4Creations.vtulife.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
	public static final String WEB_URL = "http://www.vtulife.com";
	public static final String RESULT_FROM_VTU = "/result/result_json.php";
	private static final String PREFS_USN = "usn";
	private static final String PRIFS_USN_HISTORY_SIZE = "usnSize";
	private static final String PREFS_CLASS_USN = "classUsn";
	private static final String PRIFS_CLASS_USN_HISTORY_SIZE = "classUsnSize";

	public static void setUsnHistory(Context context, String usnHistory) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		int size = prefs.getInt(PRIFS_USN_HISTORY_SIZE, 0);
		edit.putString(PREFS_USN + Integer.toString(size), usnHistory);
		edit.putInt(PRIFS_USN_HISTORY_SIZE, size + 1);
		edit.commit();
	}

	public static String[] getUsnHistory(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int size = prefs.getInt(PRIFS_USN_HISTORY_SIZE, 0);
		String[] tempUsnStrings = new String[size];

		for (int i = 0; i < size; i++)
			tempUsnStrings[i] = prefs.getString(
					PREFS_USN + Integer.toString(i), null);
		return tempUsnStrings;
	}

	public static void setClassUsnHistory(Context context, String usnHistory) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		int size = prefs.getInt(PRIFS_CLASS_USN_HISTORY_SIZE, 0);
		edit.putString(PREFS_CLASS_USN + Integer.toString(size), usnHistory);
		edit.putInt(PRIFS_CLASS_USN_HISTORY_SIZE, size + 1);
		edit.commit();
	}

	public static String[] getClassUsnHistory(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int size = prefs.getInt(PRIFS_CLASS_USN_HISTORY_SIZE, 0);
		String[] tempUsnStrings = new String[size];

		for (int i = 0; i < size; i++)
			tempUsnStrings[i] = prefs.getString(
					PREFS_CLASS_USN + Integer.toString(i), null);
		return tempUsnStrings;
	}

	public static void deleteAllUsnHistory(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		int size = prefs.getInt(PRIFS_USN_HISTORY_SIZE, 0);
		for (int i = 0; i < size; i++)
			edit.remove(PREFS_USN + Integer.toString(i));
		edit.remove(PRIFS_USN_HISTORY_SIZE);

		size = prefs.getInt(PRIFS_CLASS_USN_HISTORY_SIZE, 0);
		for (int i = 0; i < size; i++)
			edit.remove(PREFS_CLASS_USN + Integer.toString(i));
		edit.remove(PRIFS_CLASS_USN_HISTORY_SIZE);
		edit.commit();
	}

}
