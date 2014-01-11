package com.V4Creations.vtulife.receiver;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.V4Creations.vtulife.util.VTULifeUtils;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)
				&& Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			Bundle extras = intent.getExtras();
			DownloadManager.Query q = new DownloadManager.Query();
			long id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
			q.setFilterById(id);
			Cursor c = SystemFeatureChecker.getDownloadManager(context)
					.query(q);
			if (c.moveToFirst()) {
				int status = c.getInt(c
						.getColumnIndex(DownloadManager.COLUMN_STATUS));
				String title = c.getString(c
						.getColumnIndex(DownloadManager.COLUMN_TITLE));
				String message;
				if (status == DownloadManager.STATUS_SUCCESSFUL)
					message = "Download complete";
				else
					message = "Download unsuccessful";
				VTULifeUtils.showNotification(id, title, message,
						R.drawable.notification_downlaod, context,
						VTULifeMainActivity.class);
			}
		}
	}
}