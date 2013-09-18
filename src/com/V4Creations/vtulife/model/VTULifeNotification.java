package com.V4Creations.vtulife.model;

import java.util.Calendar;

import android.content.Context;
import android.database.SQLException;

import com.V4Creations.vtulife.db.VTULifeDataBase;

public class VTULifeNotification {
	public static final int TYPE_SOFTWARE_UPDATE = 1,
			TYPE_NORMAL_NOTIFICATION = 0;
	public static final String TAG_TYPE = "type", TAG_TITLE = "title",
			TAG_MESSAGE = "message";
	private int mType;
	private boolean isNotificationSaw;
	private long mId, mTime;
	private String mTitleString, mMessageString;

	public VTULifeNotification(long id, int type, boolean messageSawStatus,
			String titleString, String messageString, long time) {
		mId = id;
		mType = type;
		isNotificationSaw = messageSawStatus;
		mTitleString = titleString;
		mMessageString = messageString;
		mTime = time;
	}

	public VTULifeNotification(String titleString, String messageString, int type,
			Context context) throws SQLException {
		mTime = Calendar.getInstance().getTimeInMillis();
		isNotificationSaw = false;
		mType = type;
		mTitleString = titleString;
		mMessageString = messageString;
		saveToDb(context);
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

	public void toggelNotificationSaw(Context context) {
		isNotificationSaw = !isNotificationSaw;
		VTULifeDataBase.getInstance(context).updateNotificationSawState(this);
	}

	private void saveToDb(Context context) throws SQLException {
		mId = VTULifeDataBase.getInstance(context).insertNotification(this);
	}

	public int getType() {
		return mType;
	}
}
