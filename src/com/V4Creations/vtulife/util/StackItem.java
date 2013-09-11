package com.V4Creations.vtulife.util;

import org.json.JSONObject;

public class StackItem {
	public JSONObject mJson;
	public String mUrl;
	public String mDirName;

	public StackItem(JSONObject json, String url, String dirName) {
		mJson = json;
		mUrl = url;
		mDirName = dirName;
	}
}