package com.V4Creations.vtulife.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.V4Creations.vtulife.util.DirectoryListItem;

public class DirectoryAdapter extends BaseAdapter {
	private ArrayList<DirectoryListItem> itemList;
	private LayoutInflater mInflater;

	public DirectoryAdapter(VTULifeMainActivity vtuLifeMainActivity,
			ArrayList<DirectoryListItem> itemList) {
		this.itemList = itemList;
		mInflater = (LayoutInflater) vtuLifeMainActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public DirectoryListItem getItem(int position) {
		return (DirectoryListItem) itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DirectoryListItem tempDirectoryListItem = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_directory_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.hrefTextView = (TextView) convertView
					.findViewById(R.id.hrefTextView);
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.nameTextView);
			viewHolder.sizeTextView = (TextView) convertView
					.findViewById(R.id.sizeTextView);
			viewHolder.dateTextView = (TextView) convertView
					.findViewById(R.id.dateTextView);
			viewHolder.extTextView = (TextView) convertView
					.findViewById(R.id.extTextView);
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.iconImageView);
			viewHolder.directoryRelativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.directoryListingRelativeLayout);
			convertView.setTag(viewHolder);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.hrefTextView.setText(tempDirectoryListItem.href);
		viewHolder.nameTextView.setText(tempDirectoryListItem.name);
		viewHolder.sizeTextView.setText(tempDirectoryListItem.size);
		viewHolder.dateTextView.setText(tempDirectoryListItem.date);
		viewHolder.extTextView.setText(tempDirectoryListItem.ext);
		viewHolder.iconImageView
				.setBackgroundResource(selectImage(tempDirectoryListItem.ext));
		if (tempDirectoryListItem.color % 2 != 0)
			viewHolder.directoryRelativeLayout
					.setBackgroundResource(R.drawable.directory_list_item_selector_odd);
		else
			viewHolder.directoryRelativeLayout
					.setBackgroundResource(R.drawable.directory_list_item_selector_even);
		return convertView;
	}

	private int selectImage(String ext) {
		if (ext.matches("(?i)dir"))
			return R.drawable.folder;
		else if (ext.matches("(?i)png") || ext.matches("(?i)jpeg")
				|| ext.matches("(?i)jpg") || ext.matches("(?i)bmp"))
			return R.drawable.jpeg;
		else if (ext.matches("(?i)gif"))
			return R.drawable.gif;
		else if (ext.matches("(?i)zip") || ext.matches("(?i)rar")
				|| ext.matches("(?i)tar") || ext.matches("(?i)7zip")
				|| ext.matches("(?i)gz"))
			return R.drawable.archive;
		else if (ext.matches("(?i)exe"))
			return R.drawable.exe;
		else if (ext.matches("(?i)txt"))
			return R.drawable.txt;
		else if (ext.matches("(?i)htm") || ext.matches("(?i)html")
				|| ext.matches("(?i)xml") || ext.matches("(?i)php")
				|| ext.matches("(?i)pl"))
			return R.drawable.html;
		else if (ext.matches("(?i)flv"))
			return R.drawable.flash;
		else if (ext.matches("(?i)swf"))
			return R.drawable.swf;
		else if (ext.matches("(?i)xls"))
			return R.drawable.xsl;
		else if (ext.matches("(?i)doc") || ext.matches("(?i)docx"))
			return R.drawable.doc;
		else if (ext.matches("(?i)pdf"))
			return R.drawable.pdf;
		else if (ext.matches("(?i)psd"))
			return R.drawable.psd;
		else if (ext.matches("(?i)rm"))
			return R.drawable.real;
		else if (ext.matches("(?i)mpeg") || ext.matches("(?i)mpg")
				|| ext.matches("(?i)mov") || ext.matches("(?i)avi")
				|| ext.matches("(?i)mp4") || ext.matches("(?i)3gp")
				|| ext.matches("(?i)vob"))
			return R.drawable.video;
		else
			return R.drawable.unknown;
	}

	private static class ViewHolder {
		TextView hrefTextView;
		TextView nameTextView;
		TextView sizeTextView;
		TextView dateTextView;
		TextView extTextView;
		ImageView iconImageView;
		RelativeLayout directoryRelativeLayout;
	}
}
