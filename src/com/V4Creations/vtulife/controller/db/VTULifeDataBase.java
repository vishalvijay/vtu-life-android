package com.V4Creations.vtulife.controller.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.V4Creations.vtulife.model.VTULifeNotification;
import com.V4Creations.vtulife.model.interfaces.NotificationFromDBListener;

public class VTULifeDataBase {

	String TAG = "VTULifeDataBase";
	private static VTULifeDataBase singltonObject;
	private static int TYPE_USN = 0;
	private static int TYPE_CLASS_USN = 1;
	private final Context mContext;
	private DatabaseHelper mDatabaseHelper;
	public static final String DATABASE_NAME = "vtu_life.db";
	public static final int DATABASE_VERSION = 1;
	private static final String COL_COUNT = "count(*)";
	private static final String TABLE_RESULT_USN_HISTORY = "result_usn_history";
	private static final String COL_USN = "usn";
	private static final String COL_USN_TYPE = "usn_type";
	public static final String CREATE_TABLE_RESULT_USN_HISTORY = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_RESULT_USN_HISTORY
			+ "("
			+ COL_USN
			+ " VARCHAR(15),"
			+ COL_USN_TYPE
			+ " INTEGER, "
			+ "PRIMARY KEY ("
			+ COL_USN
			+ ","
			+ COL_USN_TYPE + "));";

	public static final String DROP_TABLE_RESULT_USN_HISTORY = "DROP TABLE IF EXISTS "
			+ TABLE_RESULT_USN_HISTORY + ";";

	private static final String TABLE_NOTIFICATIONS = "notifications";
	private static final String COL_ID = "_id";
	private static final String COL_TYPE = "notification_type";
	private static final String COL_TITLE = "title";
	private static final String COL_MESSAGE = "message";
	private static final String COL_IS_SAW_MESSAGE = "is_saw_message";
	private static final String COL_TIME_OF_NOTIFICATION = "time_of_notification";

	public static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NOTIFICATIONS
			+ "("
			+ COL_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_TYPE
			+ " INTEGER NOT NULL, "
			+ COL_IS_SAW_MESSAGE
			+ " BOOLEAN, "
			+ COL_TITLE
			+ " VARCHAR(128) NOT NULL, "
			+ COL_MESSAGE
			+ " VARCHAR(512) NOT NULL, "
			+ COL_TIME_OF_NOTIFICATION
			+ " INTEGER NOT NULL " + ");";
	public static final String DROP_TABLE_NOTIFICATIONS = "DROP TABLE IF EXISTS "
			+ TABLE_NOTIFICATIONS + ";";

	private VTULifeDataBase(Context context) {
		mContext = context;
		mDatabaseHelper = new DatabaseHelper(mContext);
	}

	public static VTULifeDataBase getInstance(Context context) {
		if (singltonObject == null)
			singltonObject = new VTULifeDataBase(context);
		return singltonObject;
	}

	synchronized public static void closeDb() {
		if (singltonObject != null) {
			singltonObject.mDatabaseHelper.close();
			singltonObject = null;
		}
	}

	synchronized public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	synchronized private ArrayList<String> getUSNHistoryByType(int type) {
		ArrayList<String> usnArrayList = new ArrayList<String>();
		try {
			SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_RESULT_USN_HISTORY,
					new String[] { COL_USN }, COL_USN_TYPE + "=? ",
					new String[] { type + "" }, null, null, null);
			while (cursor.moveToNext())
				usnArrayList.add(cursor.getString(cursor
						.getColumnIndex(COL_USN)));
			cursor.close();
			db.close();
		} catch (IllegalStateException ex) {
		}
		return usnArrayList;
	}

	synchronized private boolean setUSNHistoryByType(String usn, int type) {
		long result;
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(COL_USN, usn);
			contentValues.put(COL_USN_TYPE, type);
			SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
			result = db.insertOrThrow(TABLE_RESULT_USN_HISTORY, COL_USN,
					contentValues);
			db.close();
		} catch (SQLException e) {
			result = -1;
		}
		return result != -1;
	}

	synchronized private boolean clearUSNHistoryByType(int type) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int result = db.delete(TABLE_RESULT_USN_HISTORY, COL_USN_TYPE + "=?",
				new String[] { type + "" });
		db.close();
		return result != 0;
	}

	public static ArrayList<String> getUSNHistory(Context context) {
		return getInstance(context).getUSNHistoryByType(TYPE_USN);
	}

	public static ArrayList<String> getClassUSNHistory(Context context) {
		return getInstance(context).getUSNHistoryByType(TYPE_CLASS_USN);
	}

	public static boolean setUSNHistory(Context context, String usn) {
		return getInstance(context).setUSNHistoryByType(usn, TYPE_USN);
	}

	public static boolean setClassUSNHistory(Context context, String classUsn) {
		return getInstance(context).setUSNHistoryByType(classUsn,
				TYPE_CLASS_USN);
	}

	public static boolean clearUSNHistory(Context context) {
		return getInstance(context).clearUSNHistoryByType(TYPE_USN);
	}

	public static boolean clearClassUSNHistory(Context context) {
		return getInstance(context).clearUSNHistoryByType(TYPE_CLASS_USN);
	}

	synchronized public void getNotifications(
			final NotificationFromDBListener notificationFromDBListener) {
		new AsyncTask<String, String, ArrayList<VTULifeNotification>>() {

			@Override
			protected ArrayList<VTULifeNotification> doInBackground(
					String... params) {
				ArrayList<VTULifeNotification> notifications = new ArrayList<VTULifeNotification>();
				try {
					SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
					Cursor cursor = db.query(TABLE_NOTIFICATIONS, new String[] {
							COL_ID, COL_TYPE, COL_IS_SAW_MESSAGE, COL_TITLE,
							COL_MESSAGE, COL_TIME_OF_NOTIFICATION }, null,
							null, null, null, COL_TIME_OF_NOTIFICATION
									+ " DESC", "50");
					while (cursor.moveToNext()) {
						long id = cursor.getLong(cursor.getColumnIndex(COL_ID));
						int type = cursor.getInt(cursor
								.getColumnIndex(COL_TYPE));
						boolean isMessageSaw = cursor.getString(
								cursor.getColumnIndex(COL_IS_SAW_MESSAGE))
								.equals("1") ? true : false;
						String titleString = cursor.getString(cursor
								.getColumnIndex(COL_TITLE));
						String messageString = cursor.getString(cursor
								.getColumnIndex(COL_MESSAGE));
						long time = cursor.getLong(cursor
								.getColumnIndex(COL_TIME_OF_NOTIFICATION));
						notifications.add(new VTULifeNotification(mContext, id,
								type, isMessageSaw, titleString, messageString,
								time));
					}
					cursor.close();
					db.close();
				} catch (IllegalStateException ex) {
				}
				return notifications;
			}

			@Override
			protected void onPostExecute(
					ArrayList<VTULifeNotification> notifications) {
				notificationFromDBListener
						.notificationListCreated(notifications);
			}
		}.execute();
	}

	synchronized public long insertNotification(VTULifeNotification notification)
			throws SQLException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_TYPE, notification.getType());
		contentValues.put(COL_IS_SAW_MESSAGE, notification.isNotificationSaw());
		contentValues.put(COL_TITLE, notification.getTitleString());
		contentValues.put(COL_MESSAGE, notification.getMessageString());
		contentValues.put(COL_TIME_OF_NOTIFICATION, notification.getTime());
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		long result = db.insertOrThrow(TABLE_NOTIFICATIONS, COL_ID,
				contentValues);
		db.close();
		return result;
	}

	synchronized public int getUnreadedNotificationCount() {
		String result = "0";
		try {
			SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NOTIFICATIONS,
					new String[] { COL_COUNT }, COL_IS_SAW_MESSAGE + "=?",
					new String[] { "0" }, null, null, null);
			if (cursor.moveToNext()) {
				result = cursor.getString(cursor.getColumnIndex(COL_COUNT));
			}
			cursor.close();
			db.close();
		} catch (IllegalStateException ex) {
		}
		return Integer.parseInt(result);
	}

	synchronized public boolean updateNotificationSawState(
			VTULifeNotification notification) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_IS_SAW_MESSAGE, notification.isNotificationSaw());
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		long result = db.update(TABLE_NOTIFICATIONS, contentValues, COL_ID
				+ "=?", new String[] { notification.getId() + "" });
		db.close();
		return result != 0;
	}

	synchronized public boolean clearAllNotifications() {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int result = db.delete(TABLE_NOTIFICATIONS, "1", null);
		db.close();
		return result != 0;
	}

	synchronized public boolean deleteNotification(long id) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int result = db.delete(TABLE_NOTIFICATIONS, COL_ID + "=?",
				new String[] { id + "" });
		db.close();
		return result != 0;
	}
}
