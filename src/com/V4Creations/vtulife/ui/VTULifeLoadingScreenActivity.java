package com.V4Creations.vtulife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.actionbarsherlock.app.SherlockActivity;

public class VTULifeLoadingScreenActivity extends SherlockActivity {
	String TAG = "VTULifeLoadingScreenActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vtu_life_loading_layout);

		TextView vtuLifeTextView = (TextView) findViewById(R.id.vtuLifeTextView);
		AlphaAnimation blinkanimation = new AlphaAnimation(1, 0);
		blinkanimation.setDuration(1500);
		blinkanimation.setInterpolator(new LinearInterpolator());
		blinkanimation.setRepeatCount(1);
		blinkanimation.setRepeatMode(Animation.REVERSE);
		vtuLifeTextView.startAnimation(blinkanimation);

		blinkanimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				openMianActivity();
			}
		});
	}

	public void openMianActivity() {
		Intent intent = new Intent(VTULifeLoadingScreenActivity.this,
				VTULifeMainActivity.class);
		startActivity(intent);
		finish();
	}

	public int getPosition() {
		return SystemFeatureChecker.getDisplayHeight(this) / 2;
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalyticsManager.startGoogleAnalyticsForActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalyticsManager.stopGoogleAnalyticsForActivity(this);
	}
}
