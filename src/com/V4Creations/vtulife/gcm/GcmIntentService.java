package com.V4Creations.vtulife.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.VTULifeNotification;
import com.V4Creations.vtulife.ui.VTULifeNotificationSherlockListActivity;
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
						titleString, messageString, type,
						getApplicationContext());
				sendNotification(notification.getId(),
						notification.getTitleString(),
						notification.getMessageString());
			} catch (SQLException e) {
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(long id, String titleString,
			String messageString) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(titleString)
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(messageString))
				.setContentText(messageString);
		Intent intent = new Intent(this,
				VTULifeNotificationSherlockListActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder
				.create(getApplicationContext());
		stackBuilder.addParentStack(VTULifeNotificationSherlockListActivity.class);
		stackBuilder.addNextIntent(intent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setSound(RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify((int) id, notification);
	}
}