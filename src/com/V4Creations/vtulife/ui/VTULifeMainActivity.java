package com.V4Creations.vtulife.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.adapters.VTULifeFragmentAdapter;
import com.V4Creations.vtulife.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.db.VTULifeDataBase;
import com.V4Creations.vtulife.fragments.ClassResultListFragment;
import com.V4Creations.vtulife.fragments.DirectoryListingFragment;
import com.V4Creations.vtulife.fragments.FastResultListFragment;
import com.V4Creations.vtulife.fragments.MenuFragment;
import com.V4Creations.vtulife.fragments.ShareAPicFragment;
import com.V4Creations.vtulife.fragments.UploadFileFragment;
import com.V4Creations.vtulife.fragments.VTULifeWebFragment;
import com.V4Creations.vtulife.interfaces.RefreshListener;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.server.GCMRegisterAsyncTask;
import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.V4Creations.vtulife.util.BaseActivity;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.util.Settings;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class VTULifeMainActivity extends BaseActivity {
	private final static int INTERNET_CHECK_TIME_DELAY_HIGH = 10000;
	private final static int INTERNET_CHECK_TIME_DELAY_LOW = 2000;
	private final static int PREFERENCE_REQUEST_CODE = 1000,
			NOTIFICATION_REQUEST_CODE = 1001;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String TAG = "VTULifeMainActivity";

	public static final int NUM_OF_FRAGMENTS = 6;

	public static final int ID_VTU_LIFE_WEB_FRAGMENT = 0;
	public static final int ID_DIRECTORY_LISTING_FRAGMENT = 1;
	public static final int ID_FAST_RESULT_FRAGMENT = 2;
	public static final int ID_CLASS_RESULT_FRAGMENT = 3;
	public static final int ID_UPLOAD_FILE_FRAGEMENT = 4;
	public static final int ID_SHARE_A_PIC_FRAGMENT = 5;

	private ViewPager mViewPager;
	private PagerSlidingTabStrip mTabs;
	private VTULifeFragmentAdapter mVtuLifeFragmentAdapter;
	private ArrayList<Fragment> vtuLifeFragments;
	private boolean isInternetCheckRunning, isHighDelay;
	private Handler handler;
	private Crouton infiniteCrouton;
	private static final Configuration CONFIGURATION_INFINITE = new Configuration.Builder()
			.setDuration(Configuration.DURATION_INFINITE).build();
	private Tracker tracker;
	private boolean mExitFlag = false;
	private MenuFragment mMenuFragment;
	private Dialog mHelpDialog;
	public VTULifeMainActivity() {
		super(R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vtulife_main_activity_layout);
		init();
		gcmCheck();
		setBehindContentView(R.layout.menu_frame);
		mMenuFragment = new MenuFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_fram, mMenuFragment).commit();
		checkFirstTimeUse();
	}

	private void checkFirstTimeUse() {
		if (Settings.isFirtsTime(getApplicationContext())) {
			Settings.setFirstTime(getApplicationContext(), false);
			showCrouton("Welcome to VTU Life", Style.INFO, true);
		}
	}

	private void gcmCheck() {
		if (checkPlayServices()
				&& !Settings.isGCMRegistered(getApplicationContext()))
			registerGCMToServer();
	}

	private void registerGCMToServer() {
		new GCMRegisterAsyncTask(this).execute();
	}

	private void init() {
		handler = new Handler();
		initFragments();
		initTabs();
	}

	private void initTabs() {
		tracker = GoogleAnalyticsManager
				.getGoogleAnalyticsTracker(getApplicationContext());
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
				FragmentInfo fragmentInfo = (FragmentInfo) mVtuLifeFragmentAdapter
						.getItem(position);
				reflectActionBarChange(fragmentInfo.getActionBarStatus(),
						position);
				GoogleAnalyticsManager.infomGoogleAnalytics(tracker,
						GoogleAnalyticsManager.CATEGORY_FRAGMENT,
						GoogleAnalyticsManager.ACTION_FRAGMENT_SELECTED,
						fragmentInfo.getTitle(), 0L);
			}
		});
		mViewPager.setCurrentItem(Settings
				.getFavoritePage(getApplicationContext()));
	}

	private void initFragments() {
		vtuLifeFragments = new ArrayList<Fragment>();
		vtuLifeFragments.add(new VTULifeWebFragment());
		vtuLifeFragments.add(new DirectoryListingFragment());
		vtuLifeFragments.add(new FastResultListFragment());
		vtuLifeFragments.add(new ClassResultListFragment());
		vtuLifeFragments.add(new UploadFileFragment());
		vtuLifeFragments.add(new ShareAPicFragment());
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
		checkPlayServices();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else
				finish();
			return false;
		}
		return true;
	}

	public void changeCurrentFragemnt(int id) {
		mViewPager.setCurrentItem(id);
		if (getSlidingMenu().isMenuShowing())
			toggle();
	}

	@Override
	public void onBackPressed() {
		if (mExitFlag)
			finish();
		else {
			Toast.makeText(getApplicationContext(), "Press back again to exit",
					Toast.LENGTH_SHORT).show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					mExitFlag = true;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
					mExitFlag = false;
				}
			}).start();
		}
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

	public void clearPendingCrouton() {
		if (infiniteCrouton == null)
			Crouton.clearCroutonsForActivity(this);
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
		GoogleAnalyticsManager.startGoogleAnalyticsForActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalyticsManager.stopGoogleAnalyticsForActivity(this);
	}

	@Override
	protected void onDestroy() {
		VTULifeDataBase.closeDb();
		super.onDestroy();
	}

	public void showPreferences() {
		Intent intent = new Intent(getApplicationContext(),
				VTULifePreferencesSherlockActivity.class);
		startActivityForResult(intent, PREFERENCE_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (PREFERENCE_REQUEST_CODE == requestCode)
			notifyUSNCleaner();
		else if (NOTIFICATION_REQUEST_CODE == requestCode)
			notifyNotificationRefresh();
	}

	private void notifyNotificationRefresh() {
		((RefreshListener) mMenuFragment).refresh();
	}

	private void notifyUSNCleaner() {
		((RefreshListener) mVtuLifeFragmentAdapter
				.getItem(ID_CLASS_RESULT_FRAGMENT)).refresh();
		((RefreshListener) mVtuLifeFragmentAdapter
				.getItem(ID_FAST_RESULT_FRAGMENT)).refresh();
	}

	public void showAbout() {
		Intent intent = new Intent(getApplicationContext(),
				VTULifeAboutShelockActivity.class);
		startActivity(intent);
	}

	public void showHelp() {

		mHelpDialog = new Dialog(this);
		mHelpDialog.setContentView(R.layout.help_layout);

		TextView facebookTextView = (TextView) mHelpDialog
				.findViewById(R.id.facebookTextView);
		TextView mailTextView = (TextView) mHelpDialog
				.findViewById(R.id.emailTextView);
		TextView downloadTextView = (TextView) mHelpDialog
				.findViewById(R.id.downloadTextView);
		View.OnClickListener onClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.facebookTextView:
					likeUsOnFacebook();
					break;
				case R.id.emailTextView:
					sendNormalMail();
					break;
				case R.id.downloadTextView:
					downloadHelpManual();
					break;
				}
			}
		};
		facebookTextView.setOnClickListener(onClickListener);
		mailTextView.setOnClickListener(onClickListener);
		downloadTextView.setOnClickListener(onClickListener);
		mHelpDialog.setTitle("Help");
		mHelpDialog.show();
	}

	protected void downloadHelpManual() {
		if (isManualIsAlradyDownloaded()) {
			openManual();
		} else {
			if(mHelpDialog!=null)
				mHelpDialog.dismiss();
			SystemFeatureChecker.downloadFile(VTULifeMainActivity.this,
					Settings.WEB_URL + Settings.ANDROID_USER_MANUAL, false);
		}
	}

	private void openManual() {
		Uri path = Uri.fromFile(new File(getManualFileUrl()));
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(path, "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this,
					"No Application Available to View PDF",
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isManualIsAlradyDownloaded() {
		String fileName=getManualFileUrl();
		return new File(fileName).exists();
	}

	private String getManualFileUrl() {
		//TODO take it to a commen class
		return Settings.getDefaultRootFolder()+Settings.ANDROID_USER_MANUAL;
	}

	protected void sendNormalMail() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL,
				new String[] { Settings.VTU_LIFE_EMAILS[1] });
		try {
			startActivity(Intent.createChooser(i, "Send email for help"));
		} catch (android.content.ActivityNotFoundException ex) {
			throw ex;
		}
	}

	public void likeUsOnFacebook() {
		SystemFeatureChecker.openUrlInBrowser(this, Settings.FACEBOOK_PAGE_URL);
	}

	public void showNotification() {
		Intent intent = new Intent(getApplicationContext(),
				VTULifeNotificationSherlockListActivity.class);
		startActivityForResult(intent, NOTIFICATION_REQUEST_CODE);
	}

	public void showRateApp() {
		try {
			SystemFeatureChecker.rateAppOnPlayStore(this);
		} catch (ActivityNotFoundException e) {
			showCrouton("Google play app not installed.", Style.ALERT, true);
		}
	}

	public void showFeedback() {
		try {
			SystemFeatureChecker.sendFeedback(this);
		} catch (ActivityNotFoundException e) {
			showCrouton("There are no email clients installed.", Style.ALERT,
					true);
		}
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			savedInstanceState = new Bundle();
			savedInstanceState.putBoolean("SlidingActivityHelper.open", true);
		}
		super.onPostCreate(savedInstanceState);
	}
}
