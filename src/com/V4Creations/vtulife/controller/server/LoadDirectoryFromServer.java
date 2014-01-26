package com.V4Creations.vtulife.controller.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.V4Creations.vtulife.model.DirectoryListItem;
import com.V4Creations.vtulife.model.interfaces.DirectoryLoadedInterface;
import com.V4Creations.vtulife.util.JSONParser;
import com.V4Creations.vtulife.util.VTULifeConstance;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;

public class LoadDirectoryFromServer extends AsyncTask<String, String, String> {
	String TAG = "LoadDataFromServer";
	public static final String TAG_ITEMS = "items";
	public static final String TAG_HREF = "href";
	public static final String TAG_NAME = "name";
	public static final String TAG_EXT = "ext";
	public static final String TAG_EXT_IMAGE = "ext_image";
	public static final String TAG_SIZE = "size";
	public static final String TAG_DATE = "date";
	private ArrayList<DirectoryListItem> itemList;
	private boolean isConnectionOk;
	private String errorMessage;
	private VTULifeMainActivity vtuLifeMainActivity;
	private String url;
	private JSONObject json;
	private DirectoryLoadedInterface directoryLoadedInterface;

	public LoadDirectoryFromServer(VTULifeMainActivity vtuLifeMainActivity,
			Fragment fragment, String url) {
		isConnectionOk = true;
		errorMessage = "";
		this.vtuLifeMainActivity = vtuLifeMainActivity;
		this.url = url;
		itemList = new ArrayList<DirectoryListItem>();
		directoryLoadedInterface = (DirectoryLoadedInterface) fragment;
	}

	protected String doInBackground(String... args) {
		if (SystemFeatureChecker.isInternetConnection(vtuLifeMainActivity)) {
			JSONParser jParser = new JSONParser();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				json = jParser.makeHttpRequest(VTULifeConstance.WEB_URL + url, "GET",
						params);
			} catch (Exception e1) {
				isConnectionOk = false;
				errorMessage = e1.getMessage();
				return null;
			}

			try {
				JSONArray items = json.getJSONArray(TAG_ITEMS);
				if (items.length() == 0) {
					isConnectionOk = false;
					errorMessage = "Empty folder";
					return null;
				}
				for (int i = 0; i < items.length(); i++) {
					JSONObject item = items.getJSONObject(i);
					DirectoryListItem directoryListItem = new DirectoryListItem();
					directoryListItem.href = item.getString(TAG_HREF);
					directoryListItem.name = item.getString(TAG_NAME);
					directoryListItem.size = item.getString(TAG_SIZE);
					directoryListItem.date = item.getString(TAG_DATE);
					directoryListItem.ext = item.getString(TAG_EXT);
					directoryListItem.color = i;
					itemList.add(directoryListItem);
				}
			} catch (JSONException e) {
				isConnectionOk = false;
				errorMessage = e.getMessage();
			}
		} else {
			isConnectionOk = false;
			errorMessage = "Sorry, internet connection is not available.";
		}
		return null;
	}

	protected void onPostExecute(String file_url) {
		directoryLoadedInterface.notifyDirectoryLoaded(itemList,
				isConnectionOk, errorMessage, json);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		directoryLoadedInterface.notifyDirectoryLoaded(itemList, false,
				"Canceled", json);
	}
}