package com.V4Creations.vtulife.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;

public class Settings {
	public static final String FACEBOOK_PAGE_URL = "https://www.facebook.com/thevtulife";
	public static final String WEB_URL = "http://www.vtulife.com";
	public static final String RESULT_FROM_VTU = "/result/result_json.php";
	public static final String GCM_REGISTER = "/nm/register.php";
	public static final String ANDROID_USER_MANUAL = "/vtuLifeAndroidAppDoc.pdf";
	public static final String[] VTU_LIFE_EMAILS = new String[] {
			"v4appfarm@gmail.com", "someone@vtulife.com" };
	public static final String GCM_SENDER_ID = "812211262410";
	public static final String DEFAULT_FOLDER = "vtulife";
	public static final CharSequence PACKAGE = "com.V4Creations.vtulife";

	private static String PREFS_IS_FULL_SEM_RESULT = "isFullSemResult";
	private static String PREFS_IS_SORTED_RESULT = "isSortedResult";
	private static String PREFS_FAVORITE_PAGE = "isFavoritePage";
	private static String PREFS_IS_DEEP_SEARCH = "isDeepSearch";
	private static String PREFS_GCM_REGISTER_ID = "gcm_register_id";
	private static String PREFS_APP_VERSION_CODE = "app_version_code";
	private static String PREFS_IS_FIRST_TIME = "isFirstTime";

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
		return prefs.getInt(PREFS_FAVORITE_PAGE,
				VTULifeMainActivity.ID_VTU_LIFE_WEB_FRAGMENT);
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

	public static boolean isFirtsTime(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_FIRST_TIME, true);
	}

	public static void setFirstTime(Context context, boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(PREFS_IS_FIRST_TIME, status);
		edit.commit();
	}

}
