package com.V4Creations.vtulife.controller.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.V4Creations.vtulife.controller.db.VTULifeDataBase;

public class UsnHistoryArrayAdapter extends SupportArrayAdapter<String> {

	public UsnHistoryArrayAdapter(Context context) {
		super(context);
	}

	public void reloadHistory(final boolean isClassUsn) {
		new AsyncTask<Void, Void, ArrayList<String>>() {
			@Override
			protected ArrayList<String> doInBackground(Void... params) {
				return isClassUsn ? VTULifeDataBase
						.getClassUSNHistory(getContext()) : VTULifeDataBase
						.getUSNHistory(getContext());
			}

			protected void onPostExecute(ArrayList<String> result) {
				clear();
				supportAddAll(result);
			};
		}.execute();
	}
}
