package com.V4Creations.vtulife.controller.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

public class SupportArrayAdapter<T> extends ArrayAdapter<T> {
	private LayoutInflater mInflater;

	public SupportArrayAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("NewApi")
	public void supportAddAll(ArrayList<T> result) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.addAll(result);
		} else {
			setNotifyOnChange(false);
			for (T element : result) {
				super.add(element);
			}
			notifyDataSetChanged();
		}
	}

	public LayoutInflater getLayoutInflater() {
		return mInflater;
	}

}
