package com.V4Creations.vtulife.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class VTULifeRestClient {
	protected String TAG = "VTULifeRestClient";
	public static final String GCM_REGISTER = "/nm/register.php";

	// GCM register
	private static final String TAG_RESULT = "result",
			RESULT_SUCCESS_VALUSE = "success", PARAM_REGISTER_ID = "gcm_regid",
			PARAM_DEVICE_ID = "android_id";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void registerGcm(String gcmRegisterIdString, String deviceId,
			JsonHttpResponseHandler jsonHttpResponseHandler) {
		RequestParams params = new RequestParams();
		params.put(PARAM_REGISTER_ID, gcmRegisterIdString);
		params.put(PARAM_DEVICE_ID, deviceId);
		client.post(getAbsoluteUrl(GCM_REGISTER), params,
				jsonHttpResponseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return VTULifeConstance.WEB_URL + relativeUrl;
	}
}
