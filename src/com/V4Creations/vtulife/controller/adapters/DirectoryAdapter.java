package com.V4Creations.vtulife.controller.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.DirectoryItem;

public class DirectoryAdapter extends ArrayAdapter<DirectoryItem> {
	private LayoutInflater mInflater;

	public DirectoryAdapter(Context context, ArrayList<DirectoryItem> itemList) {
		super(context, android.R.layout.simple_list_item_1, itemList);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DirectoryItem tempDirectoryItem = getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_directory_item, null);
			viewHolder = new ViewHolder();
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.nameTextView);
			viewHolder.sizeTextView = (TextView) convertView
					.findViewById(R.id.sizeTextView);
			viewHolder.dateTextView = (TextView) convertView
					.findViewById(R.id.dateTextView);
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.iconImageView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.nameTextView.setText(tempDirectoryItem.name);
		viewHolder.sizeTextView.setText(tempDirectoryItem.size);
		viewHolder.dateTextView.setText(tempDirectoryItem.date);
		viewHolder.iconImageView.setBackgroundResource(tempDirectoryItem
				.getIcon());
		return convertView;
	}

	private static class ViewHolder {
		TextView nameTextView;
		TextView sizeTextView;
		TextView dateTextView;
		ImageView iconImageView;
	}
}
