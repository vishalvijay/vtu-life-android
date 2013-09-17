package com.V4Creations.vtulife.model;

import android.view.View;

public class ActionBarStatus {
	public String title;
	public String subTitle;
	public boolean isInterminatePorogressBarVisible;
	public boolean isCustomViewOnActionBarEnabled;
	public View customView;

	public ActionBarStatus() {
		title = null;
		subTitle = null;
		customView = null;
		isInterminatePorogressBarVisible = false;
		isCustomViewOnActionBarEnabled = false;
	}
}
