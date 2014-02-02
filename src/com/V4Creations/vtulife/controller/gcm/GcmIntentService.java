package com.V4Creations.vtulife.controller.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.VTULifeNotification;
import com.V4Creations.vtulife.util.VTULifeUtils;
import com.V4Creations.vtulife.view.activity.VTULifeNotificationActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	String TAG = "GcmIntentService";

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()
				&& GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
						.equals(messageType)) {
			String titleString = extras
					.getString(VTULifeNotification.TAG_TITLE);
			String messageString = extras
					.getString(VTULifeNotification.TAG_MESSAGE);
			int type = Integer.parseInt(extras
					.getString(VTULifeNotification.TAG_TYPE));
			try {
				VTULifeNotification notification = new VTULifeNotification(
						getApplicationContext(),titleString, messageString, type);
				VTULifeUtils.showNotification(notification.getId(),
						notification.getTitleString(),
						notification.getMessageString(),
						R.drawable.ic_launcher, getApplicationContext(),
						VTULifeNotificationActivity.class);
			} catch (SQLException e) {
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}