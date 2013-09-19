package com.V4Creations.vtulife.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.db.VTULifeDataBase;
import com.V4Creations.vtulife.interfaces.RefreshListener;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class MenuFragment extends SherlockFragment implements
		View.OnTouchListener, View.OnClickListener, RefreshListener {
	String TAG = "MenuFragment";
	private VTULifeMainActivity vtuLifeMainActivity;
	private ImageButton mHelpImageButton, mRateAppImageButton,
			mLikeOnFbImageButton, mPreferencesImageButton,
			mFeedbackImageButton, mAboutImageButton, mWebsiteImageButton,
			mNotesImageButton, mFastResultImageButton, mClassResultImageButton,
			mShareNotesImageButton, mShareAPicImageButton;
	private TextView subMenuNameTextView;
	private Button mNotificationButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.menu_content, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		subMenuNameTextView = (TextView) getView().findViewById(
				R.id.subMenuNameTextView);
		initNotificationButton();
		initMainMenu();
		initSubMenu();
	}

	private void initNotificationButton() {
		mNotificationButton = (Button) getView().findViewById(
				R.id.notificationButton);
		mNotificationButton.setOnClickListener(this);
		mNotificationButton.setOnTouchListener(this);
		refreshUnReadedNotificationCount();
	}

	private void initSubMenu() {
		mRateAppImageButton = (ImageButton) getView().findViewById(
				R.id.rateAppImageButton);
		mLikeOnFbImageButton = (ImageButton) getView().findViewById(
				R.id.likeOnFbImageButton);
		mFeedbackImageButton = (ImageButton) getView().findViewById(
				R.id.feedbackImageButton);
		mPreferencesImageButton = (ImageButton) getView().findViewById(
				R.id.preferencesImageButton);
		mHelpImageButton = (ImageButton) getView().findViewById(
				R.id.helpImageButton);
		mAboutImageButton = (ImageButton) getView().findViewById(
				R.id.aboutImageButton);
		mRateAppImageButton.setOnTouchListener(this);
		mLikeOnFbImageButton.setOnTouchListener(this);
		mFeedbackImageButton.setOnTouchListener(this);
		mPreferencesImageButton.setOnTouchListener(this);
		mHelpImageButton.setOnTouchListener(this);
		mAboutImageButton.setOnTouchListener(this);

		mRateAppImageButton.setOnClickListener(this);
		mLikeOnFbImageButton.setOnClickListener(this);
		mFeedbackImageButton.setOnClickListener(this);
		mPreferencesImageButton.setOnClickListener(this);
		mHelpImageButton.setOnClickListener(this);
		mAboutImageButton.setOnClickListener(this);
	}

	private void initMainMenu() {
		mWebsiteImageButton = (ImageButton) getView().findViewById(
				R.id.websiteImageButton);
		mNotesImageButton = (ImageButton) getView().findViewById(
				R.id.notesImageButton);
		mFastResultImageButton = (ImageButton) getView().findViewById(
				R.id.fastResultImageButton);
		mClassResultImageButton = (ImageButton) getView().findViewById(
				R.id.classResultImageButton);
		mShareNotesImageButton = (ImageButton) getView().findViewById(
				R.id.shareNotesImageButton);
		mShareAPicImageButton = (ImageButton) getView().findViewById(
				R.id.shareAPicImageButton);
		mWebsiteImageButton.setOnClickListener(this);
		mNotesImageButton.setOnClickListener(this);
		mFastResultImageButton.setOnClickListener(this);
		mClassResultImageButton.setOnClickListener(this);
		mShareNotesImageButton.setOnClickListener(this);
		mShareAPicImageButton.setOnClickListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v.getTag() == null)
				return false;
			String tagString = (String) v.getTag();
			subMenuNameTextView.setText(tagString);
			subMenuNameTextView.setVisibility(View.VISIBLE);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			subMenuNameTextView.setText("");
			subMenuNameTextView.setVisibility(View.GONE);
		}
		return false;
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
		case R.id.preferencesImageButton:
			vtuLifeMainActivity.showPreferences();
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
		case R.id.notificationButton:
			vtuLifeMainActivity.showNotification();
			break;
		default:
			vtuLifeMainActivity.changeCurrentFragemnt(Integer
					.parseInt((String) v.getTag()));
		}
	}

	@Override
	public void refresh() {
		refreshUnReadedNotificationCount();
	}

	private void refreshUnReadedNotificationCount() {
		int count = VTULifeDataBase.getInstance(vtuLifeMainActivity)
				.getUnreadedNotificationCount();
		mNotificationButton.setText(count + "");
	}
}
