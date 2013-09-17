package com.V4Creations.vtulife.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.V4Creations.vtulife.system.SystemFeatureChecker;

public class Settings {
	public static final String WEB_URL = "http://www.vtulife.com";
	public static final String RESULT_FROM_VTU = "/result/result_json.php";
	public static final String GCM_REGISTER = "/nm/register.php";
	public static final String[] VTU_LIFE_EMAILS = new String[] {
			"v4appfarm@gmail.com", "someone@gmail.com" };
	public static final String SENDER_ID="615350300672";
	

	private static String PREFS_IS_FULL_SEM_RESULT = "isFullSemResult";
	private static String PREFS_IS_SORTED_RESULT = "isSortedResult";
	private static String PREFS_FAVORITE_PAGE = "isFavoritePage";
	private static String PREFS_IS_DEEP_SEARCH = "isDeepSearch";
	private static String PREFS_GCM_REGISTER_ID = "gcm_register_id";
	private static String PREFS_APP_VERSION_CODE = "app_version_code";

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

	public static void setDeepSearch(Context context, boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(PREFS_IS_DEEP_SEARCH, status);
		edit.commit();
	}

	public static boolean isDeepSearch(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_DEEP_SEARCH, false);
	}

	public static void storeRegistrationIdWithAppVersion(Context context,
			String regId) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int appVersion = SystemFeatureChecker.getAppVersionCode(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PREFS_GCM_REGISTER_ID, regId);
		editor.putInt(PREFS_APP_VERSION_CODE, appVersion);
		editor.commit();
	}

	public static String getRegistrationId(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String registrationId = prefs.getString(PREFS_GCM_REGISTER_ID, "");
		if (registrationId.isEmpty())
			return "";
		int registeredVersion = prefs.getInt(PREFS_APP_VERSION_CODE,
				Integer.MIN_VALUE);
		int currentVersion = SystemFeatureChecker.getAppVersionCode(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}
}
