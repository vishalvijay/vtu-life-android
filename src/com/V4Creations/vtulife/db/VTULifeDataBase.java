package com.V4Creations.vtulife.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class VTULifeDataBase {

	String TAG = "VTULifeDataBase";
	private static VTULifeDataBase singltonObject;
	private static int TYPE_USN = 0;
	private static int TYPE_CLASS_USN = 1;
	private final Context mContext;
	private DatabaseHelper mDatabaseHelper;
	public static final String DATABASE_NAME = "vtu_life";
	public static final int DATABASE_VERSION = 1;
	private static final String TABLE_RESULT_USN_HISTORY = "result_usn_history";
	private  static final String COL_USN = "usn";
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

	public static final String DROP_SME_FILE_DETAILS_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_RESULT_USN_HISTORY + ";";

	private VTULifeDataBase(Context context) {
		mContext = context;
		mDatabaseHelper = new DatabaseHelper(mContext);
	}

	synchronized public static VTULifeDataBase getInstance(Context context) {
		if (singltonObject == null)
			singltonObject = new VTULifeDataBase(context);
		return singltonObject;
	}

	synchronized public static void closeDb() {
		if (singltonObject != null)
			singltonObject.mDatabaseHelper.close();
	}

	synchronized public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	synchronized private ArrayList<String> getUSNHistoryByType(int type) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_RESULT_USN_HISTORY,
				new String[] { COL_USN }, COL_USN_TYPE + "=? ",
				new String[] { type + "" }, null, null, null);
		ArrayList<String> usnArrayList = new ArrayList<String>();
		while (cursor.moveToNext())
			usnArrayList.add(cursor.getString(cursor.getColumnIndex(COL_USN)));
		cursor.close();
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
		} catch (SQLException e) {
			result = -1;
		}
		return result != -1;
	}

	synchronized private boolean clearUSNHistoryByType(int type) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int result = db.delete(TABLE_RESULT_USN_HISTORY, COL_USN_TYPE + "=?",
				new String[] { type + "" });
		return result != 0;
	}

	public static ArrayList<String> getUSNHistory(Context context) {
		return getInstance(context).getUSNHistoryByType(TYPE_USN);
	}

	public static ArrayList<String> getClassUSNHistory(
			Context context) {
		return getInstance(context).getUSNHistoryByType(TYPE_CLASS_USN);
	}

	public static boolean setUSNHistory(Context context, String usn) {
		return getInstance(context).setUSNHistoryByType(usn, TYPE_USN);
	}

	public static boolean setClassUSNHistory(Context context,
			String classUsn) {
		return getInstance(context).setUSNHistoryByType(classUsn,
				TYPE_CLASS_USN);
	}

	public static boolean clearUSNHistory(Context context) {
		return getInstance(context).clearUSNHistoryByType(TYPE_USN);
	}

	public static boolean clearClassUSNHistory(Context context) {
		return getInstance(context).clearUSNHistoryByType(TYPE_CLASS_USN);
	}
}
