package com.V4Creations.vtulife.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	String TAG = "DatabaseHelper";

	public DatabaseHelper(Context context) {
		super(context, VTULifeDataBase.DATABASE_NAME, null,
				VTULifeDataBase.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(VTULifeDataBase.CREATE_TABLE_RESULT_USN_HISTORY);
		db.execSQL(VTULifeDataBase.CREATE_TABLE_NOTIFICATIONS);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(VTULifeDataBase.DROP_TABLE_RESULT_USN_HISTORY);
		db.execSQL(VTULifeDataBase.DROP_TABLE_NOTIFICATIONS);
		onCreate(db);
	}
}
