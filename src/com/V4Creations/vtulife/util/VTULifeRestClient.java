package com.V4Creations.vtulife.util;

import com.V4Creations.vtulife.controller.server.GCMRegisterManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class VTULifeRestClient {
	protected String TAG = "VTULifeRestClient";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void registerGcm(String gcmRegisterIdString, String deviceId,
			JsonHttpResponseHandler jsonHttpResponseHandler) {
		RequestParams params = new RequestParams();
		params.put(GCMRegisterManager.PARAM_REGISTER_ID, gcmRegisterIdString);
		params.put(GCMRegisterManager.PARAM_DEVICE_ID, deviceId);
		client.post(getAbsoluteUrl(GCMRegisterManager.GCM_REGISTER), params,
				jsonHttpResponseHandler);
	}

	public static void loadResource(String url,
			JsonHttpResponseHandler jsonHttpResponseHandler) {
		client.get(getAbsoluteUrl(url), new RequestParams(),
				jsonHttpResponseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return VTULifeConstance.WEB_URL + relativeUrl;
	}
}
