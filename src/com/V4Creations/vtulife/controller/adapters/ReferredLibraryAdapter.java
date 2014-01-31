package com.V4Creations.vtulife.controller.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.ReferredLibrary;

public class ReferredLibraryAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ReferredLibrary> mReferredLibraryArrayList;
	private LayoutInflater mInflater;

	public ReferredLibraryAdapter(Context context,
			ArrayList<ReferredLibrary> referredLibraryArrayList) {
		mContext = context;
		mReferredLibraryArrayList = referredLibraryArrayList;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mReferredLibraryArrayList.size();
	}

	@Override
	public ReferredLibrary getItem(int position) {
		return mReferredLibraryArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.referred_library_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mLibraryName = (TextView) convertView
					.findViewById(R.id.libraryNameTextView);
			viewHolder.mLibraryUrl = (TextView) convertView
					.findViewById(R.id.libraryUrlTextView);
			convertView.setTag(viewHolder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		ReferredLibrary referredLibrary = getItem(position);
		holder.mLibraryName.setText(referredLibrary.getName());
		holder.mLibraryUrl.setText(referredLibrary.getUrl());
		if (referredLibrary.isUrlVisible())
			holder.mLibraryUrl.setVisibility(View.VISIBLE);
		else
			holder.mLibraryUrl.setVisibility(View.GONE);
		int color = R.color.odd_color;
		if (position % 2 == 0)
			color = R.color.even_color;
		convertView.setBackgroundResource(color);
		return convertView;
	}

	private static class ViewHolder {
		public TextView mLibraryName;
		public TextView mLibraryUrl;
	}
}