package com.V4Creations.vtulife.controller.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.NavigationDrawerMenu;
import com.V4Creations.vtulife.view.fragments.ClassResultListFragment;
import com.V4Creations.vtulife.view.fragments.DirectoryListingFragment;
import com.V4Creations.vtulife.view.fragments.FastResultListFragment;
import com.V4Creations.vtulife.view.fragments.ShareAPicFragment;
import com.V4Creations.vtulife.view.fragments.UploadFileFragment;
import com.V4Creations.vtulife.view.fragments.WebFragment;

public class NavigationDrawerArrayAdapter extends
		SupportArrayAdapter<NavigationDrawerMenu> {
	private int mSelectedPosion = -1;

	public NavigationDrawerArrayAdapter(Context context) {
		super(context);
		initMenu();
	}

	private void initMenu() {
		add(new NavigationDrawerMenu(WebFragment.getFeatureName(),
				R.drawable.home));
		add(new NavigationDrawerMenu(DirectoryListingFragment.getFeatureName(),
				R.drawable.notes));
		add(new NavigationDrawerMenu(FastResultListFragment.getFeatureName(),
				R.drawable.fast_result));
		add(new NavigationDrawerMenu(ClassResultListFragment.getFeatureName(),
				R.drawable.class_result));
		add(new NavigationDrawerMenu(UploadFileFragment.getFeatureName(),
				R.drawable.share_notes));
		add(new NavigationDrawerMenu(ShareAPicFragment.getFeatureName(),
				R.drawable.share_a_pic));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView menuNameTextView;
		if (convertView == null) {
			convertView = getLayoutInflater().inflate(
					R.layout.navigation_menu_list_item, null);
			menuNameTextView = (TextView) convertView
					.findViewById(R.id.menuNameTextView);
			convertView.setTag(menuNameTextView);
		} else
			menuNameTextView = (TextView) convertView.getTag();
		NavigationDrawerMenu menu = getItem(position);
		menuNameTextView.setText(" " + menu.menuName);
		menuNameTextView.setCompoundDrawablesWithIntrinsicBounds(
				menu.imageResourceId, 0, 0, 0);
		convertView
				.setBackgroundResource(menu.isSelected ? R.color.menu_color_selected
						: android.R.color.transparent);
		return convertView;
	}

	public void changeSelected(int position) {
		if (position != mSelectedPosion) {
			if (mSelectedPosion != -1)
				getItem(mSelectedPosion).isSelected = false;
			getItem(position).isSelected = true;
			mSelectedPosion = position;
			notifyDataSetChanged();
		}
	}
}
