package com.V4Creations.vtulife.view.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.NavigationDrawerArrayAdapter;

public class NavigationMenu implements OnClickListener, OnItemClickListener {
	private View mNavigationDrawerView;
	private ImageButton mHelpImageButton, mRateAppImageButton,
			mLikeOnFbImageButton, mFeedbackImageButton, mAboutImageButton;
	private ListView mMenuList;
	private NavigationDrawerArrayAdapter mMenuAdapter;
	private VTULifeMainActivity vtuLifeMainActivity;

	public NavigationMenu(VTULifeMainActivity vtuLifeMainActivity,
			View navigationDrawerView) {
		this.vtuLifeMainActivity = vtuLifeMainActivity;
		mNavigationDrawerView = navigationDrawerView;
		initView();
	}

	private void initView() {
		initMenuList();
		initFooterMenu();
	}

	private void initMenuList() {
		mMenuList = (ListView) mNavigationDrawerView
				.findViewById(R.id.menuListView);
		mMenuAdapter = new NavigationDrawerArrayAdapter(vtuLifeMainActivity);
		mMenuList.setAdapter(mMenuAdapter);
		mMenuList.setOnItemClickListener(this);
	}

	private void initFooterMenu() {
		mRateAppImageButton = (ImageButton) mNavigationDrawerView
				.findViewById(R.id.rateAppImageButton);
		mLikeOnFbImageButton = (ImageButton) mNavigationDrawerView
				.findViewById(R.id.likeOnFbImageButton);
		mFeedbackImageButton = (ImageButton) mNavigationDrawerView
				.findViewById(R.id.feedbackImageButton);
		mHelpImageButton = (ImageButton) mNavigationDrawerView
				.findViewById(R.id.helpImageButton);
		mAboutImageButton = (ImageButton) mNavigationDrawerView
				.findViewById(R.id.aboutImageButton);

		mRateAppImageButton.setOnClickListener(this);
		mLikeOnFbImageButton.setOnClickListener(this);
		mFeedbackImageButton.setOnClickListener(this);
		mHelpImageButton.setOnClickListener(this);
		mAboutImageButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rateAppImageButton:
			vtuLifeMainActivity.showRateApp();
			break;
		case R.id.likeOnFbImageButton:
			vtuLifeMainActivity.likeUsOnFacebook();
			break;
		case R.id.feedbackImageButton:
			vtuLifeMainActivity.showFeedback();
			break;
		case R.id.helpImageButton:
			vtuLifeMainActivity.showHelp();
			break;
		case R.id.aboutImageButton:
			vtuLifeMainActivity.showAbout();
			break;
		default:
			vtuLifeMainActivity.changeCurrentFragemnt(Integer
					.parseInt((String) v.getTag()));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		vtuLifeMainActivity.changeCurrentFragemnt(position);
		changeSelected(position);
	}

	public void changeSelected(int position) {
		mMenuAdapter.changeSelected(position);
	}
}
