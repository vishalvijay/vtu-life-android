package com.V4Creations.vtulife.util;

import java.io.File;
import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class VTULifeUtils {
	public static final boolean isProduction = false;

	public static String getRootFolder() {
		return Environment.DIRECTORY_DOWNLOADS;
	}

	public static String getDefaultRootFolder() throws IOException {
		File path = new File(
				Environment.getExternalStoragePublicDirectory(getRootFolder()),
				VTULifeConstance.DEFAULT_FOLDER);
		path.mkdirs();
		return path.getAbsolutePath();
	}

	public static String getOnlyFolderWithFileName(String fileName) {
		return new File(VTULifeConstance.DEFAULT_FOLDER, fileName)
				.getAbsolutePath();
	}

	public static String getVTULifePublicEmailId() {
		return VTULifeConstance.VTU_LIFE_EMAILS[1];
	}

	public static void showNotification(long id, String titleString,
			String messageString, int iconResId, Context context,
			Class<?> className) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(iconResId)
				.setContentTitle(titleString)
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(messageString))
				.setContentText(messageString);
		Intent intent = new Intent(context, className);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(className);
		stackBuilder.addNextIntent(intent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setSound(RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify((int) id, notification);
	}
}
