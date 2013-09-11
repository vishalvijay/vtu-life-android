package com.V4Creations.vtulife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;

public class VTULifeLoadingScreenActivity extends SherlockActivity {
	String TAG = "VTULifeLoadingScreenActivity";
	ImageView loadImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.vtu_life_loading_layout);
		loadImageView = (ImageView) findViewById(R.id.centerIconImageView);
		ObjectAnimator mover = ObjectAnimator.ofFloat(loadImageView,
				"translationY", -1 * getPosition(), 0f);
		mover.setDuration(2000);
		mover.start();
		mover.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				openMianActivity();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
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
}
