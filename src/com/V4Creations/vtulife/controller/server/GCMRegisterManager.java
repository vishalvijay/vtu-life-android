package com.V4Creations.vtulife.controller.server;

import java.io.IOException;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.V4Creations.vtulife.util.Settings;
import com.V4Creations.vtulife.util.VTULifeConstance;
import com.V4Creations.vtulife.util.VTULifeRestClient;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;

import de.keyboardsurfer.android.widget.crouton.Style;

public class GCMRegisterManager extends AsyncTask<Void, Void, String> {
	private Context mContext;
	public static final String GCM_REGISTER = "/nm/register.php";
	public static final String PARAM_REGISTER_ID = "gcm_regid",
			PARAM_DEVICE_ID = "android_id";

	public GCMRegisterManager(Context context) {
		mContext = context;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			return GoogleCloudMessaging.getInstance(mContext).register(
					VTULifeConstance.GCM_SENDER_ID);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(final String gcmRegisterIdString) {
		super.onPostExecute(gcmRegisterIdString);
		if (gcmRegisterIdString != null) {
			VTULifeRestClient.registerGcm(gcmRegisterIdString,
					SystemFeatureChecker.getDeviceUuid(mContext),
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode,
								JSONObject response) {
							super.onSuccess(statusCode, response);
							Settings.storeRegistrationIdWithAppVersion(
									mContext, gcmRegisterIdString);
							((VTULifeMainActivity) mContext).showCrouton(
									"Registered for notification", Style.INFO,
									true);
						}
					});
		}

	}
}