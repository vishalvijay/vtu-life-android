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
	private Context context;

	public NavigationDrawerArrayAdapter(Context context) {
		super(context);
		this.context = context;
		initMenu();
	}

	private void initMenu() {
		add(new NavigationDrawerMenu(WebFragment.getFeatureName(context),
				R.drawable.home));
		add(new NavigationDrawerMenu(
				DirectoryListingFragment.getFeatureName(context),
				R.drawable.notes));
		add(new NavigationDrawerMenu(
				FastResultListFragment.getFeatureName(context),
				R.drawable.fast_result));
		add(new NavigationDrawerMenu(
				ClassResultListFragment.getFeatureName(context),
				R.drawable.class_result));
		add(new NavigationDrawerMenu(
				UploadFileFragment.getFeatureName(context),
				R.drawable.share_notes));
		add(new NavigationDrawerMenu(ShareAPicFragment.getFeatureName(context),
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
