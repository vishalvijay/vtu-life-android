package com.V4Creations.vtulife.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;

public class VTULifeLoadingScreenActivity extends Activity {
	String TAG = "VTULifeLoadingScreenActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		AlphaAnimation blinkanimation = new AlphaAnimation(1, 0);
		blinkanimation.setDuration(1500);
		blinkanimation.setInterpolator(new LinearInterpolator());
		blinkanimation.setRepeatCount(1);
		blinkanimation.setRepeatMode(Animation.REVERSE);
		findViewById(R.id.vtuLifeTextView).startAnimation(blinkanimation);

		blinkanimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				openMainActivity();
			}
		});
	}

	public void openMainActivity() {
		Intent intent = new Intent(this, VTULifeMainActivity.class);
		startActivity(intent);
		finish();
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
