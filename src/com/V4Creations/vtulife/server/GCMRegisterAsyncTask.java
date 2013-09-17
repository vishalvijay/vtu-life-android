package com.V4Creations.vtulife.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.V4Creations.vtulife.util.JSONParser;
import com.V4Creations.vtulife.util.Settings;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMRegisterAsyncTask extends AsyncTask<String, String, Boolean> {
	String TAG = "GCMRegisterAsyncTask";
	private String mGCMRegisterIdString;
	private Context mContext;
	private static final String TAG_RESULT = "result",
			RESULT_SUCCESS_VALUSE = "success",
			POST_PARAM_REGISTER_ID = "gsm_id";

	public GCMRegisterAsyncTask(Context context) {
		mContext = context;
	}

	@Override
	protected Boolean doInBackground(String... asyncParams) {
		boolean result = true;
		if (SystemFeatureChecker.isInternetConnection(mContext)) {
			Log.e(TAG, "Ok1");
			try {
				mGCMRegisterIdString = GoogleCloudMessaging.getInstance(
						mContext).register(Settings.SENDER_ID);
				Log.e(TAG, "Ok4");
				JSONParser jParser = new JSONParser();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				try {
					Log.e(TAG, "Ok5");
					params.add(new BasicNameValuePair(POST_PARAM_REGISTER_ID,
							mGCMRegisterIdString));
					JSONObject jsonObject = jParser.makeHttpRequest(
							Settings.WEB_URL + Settings.GCM_REGISTER, "POST",
							params);
					if (!jsonObject.getString(TAG_RESULT).equals(
							RESULT_SUCCESS_VALUSE))
						result = false;
					Log.e(TAG, "Ok6");
				} catch (Exception e1) {
					result = false;
				}
			} catch (Exception e) {
				result = false;
			}
		} else
			result = false;
		return result;
	}

	protected void onPostExecute(boolean result) {
		Log.e(TAG, "Ok2");
		if (result)
			Settings.storeRegistrationIdWithAppVersion(mContext,
					mGCMRegisterIdString);
		Toast.makeText(mContext, result ? "true" : "false", Toast.LENGTH_LONG)
				.show();
	}
}
