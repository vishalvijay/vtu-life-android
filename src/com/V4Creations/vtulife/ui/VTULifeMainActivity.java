package com.V4Creations.vtulife.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.adapters.VTULifeFragmentAdapter;
import com.V4Creations.vtulife.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.fragments.ClassResultListFragment;
import com.V4Creations.vtulife.fragments.DirectoryListingFragment;
import com.V4Creations.vtulife.fragments.FastResultListFragment;
import com.V4Creations.vtulife.fragments.MenuFragment;
import com.V4Creations.vtulife.fragments.PostAPicFragment;
import com.V4Creations.vtulife.fragments.UploadFileFragment;
import com.V4Creations.vtulife.fragments.VTULifeWebFragment;
import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.V4Creations.vtulife.util.ActionBarStatus;
import com.V4Creations.vtulife.util.BaseActivity;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.google.analytics.tracking.android.EasyTracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class VTULifeMainActivity extends BaseActivity {

	private final int DIALOG_ABOUT = 201;
	private final int INTERNET_CHECK_TIME_DELAY_HIGH = 10000;
	private final int INTERNET_CHECK_TIME_DELAY_LOW = 2000;
	String TAG = "VTULifeMainActivity";

	public static final int NUM_OF_FRAGMENTS = 6;

	public static final int ID_VTU_LIFE_WEB_FRAGMENT = 0;
	public static final int ID_DIRECTORY_LISTING_FRAGMENT = 1;
	public static final int ID_FAST_RESULT_FRAGMENT = 2;
	public static final int ID_CLASS_RESULT_FRAGMENT = 3;
	public static final int ID_UPLOAD_FILE_FRAGEMENT = 4;
	public static final int ID_POST_A_PIC_FRAGMENT = 5;

	private ViewPager mViewPager;
	private PagerSlidingTabStrip mTabs;
	private VTULifeFragmentAdapter mVtuLifeFragmentAdapter;
	private ArrayList<Fragment> vtuLifeFragments;
	private boolean isInternetCheckRunning, isHighDelay;
	private Handler handler;
	private Crouton infiniteCrouton;
	private static final Configuration CONFIGURATION_INFINITE = new Configuration.Builder()
			.setDuration(Configuration.DURATION_INFINITE).build();

	public VTULifeMainActivity() {
		super(R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vtulife_main_activity_layout);
		init();
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();
	}

	private void init() {
		handler = new Handler();
		initFragments();
		initTabs();
	}

	private void initTabs() {
		mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setBackgroundResource(R.drawable.bg_noise_grey);
		mViewPager.setOffscreenPageLimit(NUM_OF_FRAGMENTS);
		mVtuLifeFragmentAdapter = new VTULifeFragmentAdapter(
				getSupportFragmentManager(), vtuLifeFragments);
		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		mViewPager.setPageMargin(pageMargin);
		mViewPager.setAdapter(mVtuLifeFragmentAdapter);
		mTabs.setViewPager(mViewPager);
		mTabs.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				if (position == 0)
					getSlidingMenu().setTouchModeAbove(
							SlidingMenu.TOUCHMODE_FULLSCREEN);
				else
					getSlidingMenu().setTouchModeAbove(
							SlidingMenu.TOUCHMODE_MARGIN);
				reflectActionBarChange(((FragmentInfo) mVtuLifeFragmentAdapter
						.getItem(position)).getActionBarStatus(), position);
			}
		});
	}

	private void initFragments() {
		vtuLifeFragments = new ArrayList<Fragment>();
		vtuLifeFragments.add(new VTULifeWebFragment());
		vtuLifeFragments.add(new DirectoryListingFragment());
		vtuLifeFragments.add(new FastResultListFragment());
		vtuLifeFragments.add(new ClassResultListFragment());
		vtuLifeFragments.add(new UploadFileFragment());
		vtuLifeFragments.add(new PostAPicFragment());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopInternetCheck();
	}

	private void stopInternetCheck() {
		isInternetCheckRunning = false;
		if (infiniteCrouton != null) {
			Crouton.hide(infiniteCrouton);
			infiniteCrouton = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateInternetConnection();
	}

	public void switchContent(int id) {
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.content_frame, mContent).commit();
		// getSlidingMenu().showContent();
	}

	public void rateAppOnPlayStore() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(myAppLinkToMarket);
		} catch (ActivityNotFoundException e) {
			showCrouton("Unable to find google play app.", Style.INFO, true);
		}
	}

	private void reportBug() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "v4appfarm@gmail.com" });
		i.putExtra(Intent.EXTRA_SUBJECT, "Bug of "
				+ getString(R.string.app_name) + " android app.");
		String bugReportBody = "Phone model : "
				+ SystemFeatureChecker.getDeviceName()
				+ "\n"
				+ "Application name : "
				+ getString(R.string.app_name)
				+ "\n"
				+ "Application version name: "
				+ SystemFeatureChecker
						.getAppVersionName(getApplicationContext())
				+ "\n"
				+ "Application version code : "
				+ SystemFeatureChecker
						.getAppVersionCode(getApplicationContext()) + "\n"
				+ "Phone android version : "
				+ SystemFeatureChecker.getAndroidVersion() + "\n"
				+ "-----------------------\n"
				+ "Please provide more details below :\n";
		i.putExtra(Intent.EXTRA_TEXT, bugReportBody);
		try {
			startActivity(Intent.createChooser(i, "Report bug"));
		} catch (android.content.ActivityNotFoundException ex) {
			showCrouton("There are no email clients installed.", Style.INFO,
					true);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);

		switch (id) {
		case DIALOG_ABOUT:
			final View aboutView = factory.inflate(R.layout.about, null);
			TextView versionLabel = (TextView) aboutView
					.findViewById(R.id.version_label);
			String versionName = SystemFeatureChecker
					.getAppVersionName(getApplicationContext());
			versionLabel.setText(getString(R.string.version, versionName));
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.app_name).setView(aboutView)
					.setPositiveButton("OK", null).create();
		}
		return null;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				VTULifeMainActivity.this);
		builder.setMessage("Are you sure ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.setTitle("Exit");
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.show();

	}

	public void reflectActionBarChange(ActionBarStatus actionBarStatus,
			int fragmentId) {
		if (mViewPager.getCurrentItem() == fragmentId) {
			getSupportActionBar().setTitle("VTU Life");
			getSupportActionBar().setSubtitle(actionBarStatus.subTitle);
			setSupportProgressBarIndeterminateVisibility(actionBarStatus.isInterminatePorogressBarVisible);
			if (actionBarStatus.isCustomViewOnActionBarEnabled)
				getSupportActionBar().setCustomView(actionBarStatus.customView);
			getSupportActionBar().setDisplayShowCustomEnabled(
					actionBarStatus.isCustomViewOnActionBarEnabled);
		}
		supportInvalidateOptionsMenu();
	}

	public void showCrouton(String message, Style style, boolean isHighPriority) {
		if (infiniteCrouton == null || isHighPriority)
			Crouton.makeText(this, message, style).show();
	}

	public void showCrouton(int messageFromResource, Style style,
			boolean isHighPriority) {
		if (infiniteCrouton == null || isHighPriority)
			Crouton.makeText(this, messageFromResource, style).show();
	}

	private void updateInternetConnection() {
		isInternetCheckRunning = true;
		isHighDelay = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isInternetCheckRunning) {
					try {
						handler.post(new Runnable() {

							@Override
							public void run() {
								if (SystemFeatureChecker
										.isInternetConnection(getApplicationContext())) {
									if (infiniteCrouton != null) {
										Crouton.hide(infiniteCrouton);
										infiniteCrouton = null;
										showCrouton(
												"Internet connection established.",
												Style.INFO, true);
										isHighDelay = true;
									}
								} else if (infiniteCrouton == null) {
									Crouton.clearCroutonsForActivity(VTULifeMainActivity.this);
									infiniteCrouton = Crouton.makeText(
											VTULifeMainActivity.this,
											"Internet connection lost.",
											Style.ALERT).setConfiguration(
											CONFIGURATION_INFINITE);
									infiniteCrouton.show();
									isHighDelay = false;
								}
							}

						});
						Thread.sleep(isHighDelay ? INTERNET_CHECK_TIME_DELAY_HIGH
								: INTERNET_CHECK_TIME_DELAY_LOW);
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	public void test(View v) {

	}
}
