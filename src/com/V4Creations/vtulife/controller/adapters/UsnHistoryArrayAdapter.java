package com.V4Creations.vtulife.controller.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ArrayAdapter;

import com.V4Creations.vtulife.controller.db.VTULifeDataBase;

public class UsnHistoryArrayAdapter extends ArrayAdapter<String> {

	public UsnHistoryArrayAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}

	@SuppressLint("NewApi")
	@Override
	public void addAll(String... items) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.addAll(items);
		} else {
			for (String element : items) {
				super.add(element);
			}
		}
	}

	public void reloadHistory(final boolean isClassUsn) {
		new AsyncTask<Void, Void, ArrayList<String>>() {
			@Override
			protected ArrayList<String> doInBackground(Void... params) {
				return isClassUsn ? VTULifeDataBase
						.getClassUSNHistory(getContext()) : VTULifeDataBase
						.getUSNHistory(getContext());
			}

			protected void onPostExecute(java.util.ArrayList<String> result) {
				clear();
				addAll(result);
			};
		}.execute();
	}
}
