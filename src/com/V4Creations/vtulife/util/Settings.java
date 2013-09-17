package com.V4Creations.vtulife.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
	public static final String WEB_URL = "http://www.vtulife.com";
	public static final String RESULT_FROM_VTU = "/result/result_json.php";
	public static final String[] VTU_LIFE_EMAILS = new String[] {
			"v4appfarm@gmail.com", "someone@gmail.com" };

	private static String PREFS_IS_FULL_SEM_RESULT = "isFullSemResult";
	private static String PREFS_IS_SORTED_RESULT = "isSortedResult";
	private static String PREFS_FAVORITE_PAGE = "isFavoritePage";
	private static String PREFS_IS_DEEP_SEARCH = "isDeepSearch";

	public static void setFullSemResultStatus(Context context, boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(PREFS_IS_FULL_SEM_RESULT, status);
		edit.commit();
	}

	public static boolean isFullSemResult(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_FULL_SEM_RESULT, false);
	}

	public static void setSortedResultStatus(Context context, boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(PREFS_IS_SORTED_RESULT, status);
		edit.commit();
	}

	public static boolean isSortedResult(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_SORTED_RESULT, false);
	}

	public static void setFavoritePage(Context context, int fragmentIndex) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(PREFS_FAVORITE_PAGE, fragmentIndex);
		edit.commit();
	}

	public static int getFavoritePage(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getInt(PREFS_FAVORITE_PAGE, 0);
	}

	public static void setDeepSearch(Context context,
			boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(PREFS_IS_DEEP_SEARCH, status);
		edit.commit();
	}

	public static boolean isDeepSearch(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_DEEP_SEARCH,
				false);
	}
}
