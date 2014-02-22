package com.V4Creations.vtulife.util;

import android.app.Activity;

import com.bugsense.trace.BugSenseHandler;

public class BugSenseManager {
	public static void initBugSense(Activity activity) {
		if (VTULifeUtils.isProduction)
			BugSenseHandler.initAndStartSession(activity,
					VTULifeConstance.BUG_SENSE_KEY);
	}
}
