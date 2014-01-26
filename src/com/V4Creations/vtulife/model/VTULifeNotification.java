package com.V4Creations.vtulife.model;

import java.util.Calendar;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;

import com.V4Creations.vtulife.controller.db.VTULifeDataBase;

public class VTULifeNotification {
	public static final int TYPE_SOFTWARE_UPDATE = 1,
			TYPE_NORMAL_NOTIFICATION = 0;
	public static final String TAG_TYPE = "type", TAG_TITLE = "title",
			TAG_MESSAGE = "message";
	private int mType;
	private boolean isNotificationSaw;
	private long mId, mTime;
	private String mTitleString, mMessageString;
	private Context mContext;

	public VTULifeNotification(Context context, long id, int type,
			boolean messageSawStatus, String titleString, String messageString,
			long time) {
		mContext = context;
		mId = id;
		mType = type;
		isNotificationSaw = messageSawStatus;
		mTitleString = titleString;
		mMessageString = messageString;
		mTime = time;
	}

	public VTULifeNotification(Context context, String titleString,
			String messageString, int type) throws SQLException {
		mContext = context;
		mTime = Calendar.getInstance().getTimeInMillis();
		isNotificationSaw = false;
		mType = type;
		mTitleString = titleString;
		mMessageString = messageString;
		saveToDb();
	}

	public boolean isNormalNotification() {
		return mType == TYPE_NORMAL_NOTIFICATION ? true : false;
	}

	public boolean isNotificationSaw() {
		return isNotificationSaw;
	}

	public String getMessageString() {
		return mMessageString;
	}

	public String getTitleString() {
		return mTitleString;
	}

	public long getId() {
		return mId;
	}

	public long getTime() {
		return mTime;
	}

	public void toggelNotificationSaw() {
		isNotificationSaw = !isNotificationSaw;
		new AsyncTask<String, String, String>() {

			@Override
			protected String doInBackground(String... params) {
				VTULifeDataBase.getInstance(mContext)
						.updateNotificationSawState(VTULifeNotification.this);
				return null;
			}

		}.execute();
	}

	private void saveToDb() throws SQLException {
		mId = VTULifeDataBase.getInstance(mContext).insertNotification(this);
	}

	public int getType() {
		return mType;
	}

	public boolean delete() {
		return VTULifeDataBase.getInstance(mContext).deleteNotification(mId);
	}

	@Override
	public String toString() {
		return mTitleString;
	}
}
