package com.V4Creations.vtulife.util;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;

public class GoogleAnalyticsManager {
	public static String CATEGORY_RESULT = "result";
	public static String CATEGORY_NOTES = "notes";
	public static String ACTION_FAST_RESULT = "fast_result";
	public static String ACTION_CLASS_RESULT = "class_result";
	public static String ACTION_FOLDER = "folder";

	public static void infomGoogleAnalytics(Tracker tracker, String category,
			String action, String label, long value) {
		tracker.sendEvent(category, action, label, value);
	}

	public static Tracker getGoogleAnalyticsTracker(Context context) {
		EasyTracker.getInstance().setContext(context);
		return EasyTracker.getTracker();
	}
}
