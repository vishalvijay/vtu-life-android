package com.V4Creations.vtulife.util;

import android.app.Activity;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class GoogleAnalyticsManager {
	public static String CATEGORY_RESULT = "result";
	public static String CATEGORY_NOTES = "notes";
	public static String CATEGORY_FRAGMENT = "fragment";
	public static String CATEGORY_PREFERENCES = "preferences";
	public static String ACTION_FAST_RESULT = "fast_result";
	public static String ACTION_CLASS_RESULT = "class_result";
	public static String ACTION_FOLDER = "folder";
	public static String ACTION_FRAGMENT_SELECTED = "fragment_selected";
	public static String ACTION_FULL_RESULT_VIEW = "full_result_view";
	public static String ACTION_SORTED_RESULT = "sorted_result";
	public static String ACTION_DEEP_RESULT_SEARCH = "deep_result_search";
	public static String ACTION_FAVORITE_PAGE = "favorite_page";
	public static String ACTION_NETWORK_ERROR = "network_error";

	public static void infomGoogleAnalytics(EasyTracker easyTracker,
			String category, String action, String label, long value) {
		if (VTULifeUtils.isProduction)
			easyTracker.send(MapBuilder.createEvent(category, action, label,
					value).build());
	}

	public static EasyTracker getGoogleAnalyticsTracker(Context context) {
		return EasyTracker.getInstance(context);
	}

	public static void startGoogleAnalyticsForActivity(Activity activity) {
		if (VTULifeUtils.isProduction)
			EasyTracker.getInstance(activity).activityStart(activity);
	}

	public static void stopGoogleAnalyticsForActivity(Activity activity) {
		if (VTULifeUtils.isProduction)
			EasyTracker.getInstance(activity).activityStop(activity);
	}
}
