package com.V4Creations.vtulife.controller.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.ReferredLibrary;

public class ReferredLibraryAdapter extends
		SupportArrayAdapter<ReferredLibrary> {

	public ReferredLibraryAdapter(Context context) {
		super(context);
		String[] libraryNames = getContext().getResources().getStringArray(
				R.array.library_names);
		String[] libraryUrls = getContext().getResources().getStringArray(
				R.array.library_urls);
		ArrayList<ReferredLibrary> referredLibrarys = new ArrayList<ReferredLibrary>();
		for (int i = 0; i < libraryNames.length; i++)
			referredLibrarys.add(new ReferredLibrary(libraryNames[i],
					libraryUrls[i]));
		supportAddAll(referredLibrarys);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getLayoutInflater().inflate(
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