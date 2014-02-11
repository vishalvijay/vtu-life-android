package com.V4Creations.vtulife.view.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.VTULifeFragmentAdapter;
import com.V4Creations.vtulife.controller.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.controller.db.VTULifeDataBase;
import com.V4Creations.vtulife.controller.server.GCMRegisterManager;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.model.interfaces.RefreshListener;
import com.V4Creations.vtulife.util.BugSenseManager;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.util.NavigationMenuManager;
import com.V4Creations.vtulife.util.NavigationMenuManager.NavigationMenuManagerListener;
import com.V4Creations.vtulife.util.PagerSlidingTabStrip;
import com.V4Creations.vtulife.util.Settings;
import com.V4Creations.vtulife.util.VTULifeConstance;
import com.V4Creations.vtulife.util.VTULifeUtils;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;
import com.V4Creations.vtulife.view.fragments.ClassResultListFragment;
import com.V4Creations.vtulife.view.fragments.DirectoryListingFragment;
import com.V4Creations.vtulife.view.fragments.FastResultListFragment;
import com.V4Creations.vtulife.view.fragments.ShareAPicFragment;
import com.V4Creations.vtulife.view.fragments.UploadFileFragment;
import com.V4Creations.vtulife.view.fragments.WebFragment;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class VTULifeMainActivity extends ActionBarActivity implements
		NavigationMenuManagerListener {
	String TAG = "VTULifeMainActivity";
	private final static int INTERNET_CHECK_TIME_DELAY_HIGH = 10000;
	private final static int INTERNET_CHECK_TIME_DELAY_LOW = 2000;
	private final static int PREFERENCE_REQUEST_CODE = 1000,
			NOTIFICATION_REQUEST_CODE = 1001;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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
	private Dialog mHelpDialog;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private View mMenuDrawer;
	private NavigationMenuManager mNavigationMenuManager;
	private TextView mNotificationCountTextView;
	private NavigationMenu mNavigationMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseManager.initBugSense(this);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		init();
		gcmCheck();
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
		new GCMRegisterManager(this).execute();
	}

	private void init() {
		handler = new Handler();
		initNavigationDrawer();
		initFragments();
		initTabs();
	}

	private void initNavigationDrawer() {
		mMenuDrawer = findViewById(R.id.left_drawer);
		mNavigationMenu = new NavigationMenu(this, mMenuDrawer);
		mNavigationMenuManager = new NavigationMenuManager(this);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.vtulife_ic_navigation_drawer, R.string.version,
				R.string.external_marks) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(R.string.app_name);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle("Menu");
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerLayout.openDrawer(mMenuDrawer);
	}

	private void initTabs() {
		tracker = GoogleAnalyticsManager
				.getGoogleAnalyticsTracker(getApplicationContext());
		mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(NUM_OF_FRAGMENTS);
		mVtuLifeFragmentAdapter = new VTULifeFragmentAdapter(
				getSupportFragmentManager(), vtuLifeFragments);
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
				FragmentInfo fragmentInfo = (FragmentInfo) mVtuLifeFragmentAdapter
						.getItem(position);
				reflectActionBarChange(fragmentInfo.getActionBarStatus(),
						position, true);
				GoogleAnalyticsManager.infomGoogleAnalytics(tracker,
						GoogleAnalyticsManager.CATEGORY_FRAGMENT,
						GoogleAnalyticsManager.ACTION_FRAGMENT_SELECTED,
						fragmentInfo.getTitle(), 0L);
				mNavigationMenu.changeSelected(position);
			}
		});
		int currentPage = Settings.getFavoritePage(getApplicationContext());
		mViewPager.setCurrentItem(currentPage);
		mNavigationMenu.changeSelected(currentPage);
	}

	private void initFragments() {
		vtuLifeFragments = new ArrayList<Fragment>();
		vtuLifeFragments.add(new WebFragment());
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
		mDrawerLayout.closeDrawer(mMenuDrawer);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mMenuDrawer)) {
			mDrawerLayout.closeDrawer(mMenuDrawer);
			return;
		}
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
			int fragmentId, boolean isInvalidate) {
		if (actionBarStatus != null) {
			if (mViewPager.getCurrentItem() == fragmentId) {
				getSupportActionBar().setTitle("VTU Life");
				getSupportActionBar().setSubtitle(actionBarStatus.subTitle);
				setSupportProgressBarIndeterminateVisibility(actionBarStatus.isInterminatePorogressBarVisible);
				if (actionBarStatus.isCustomViewOnActionBarEnabled)
					getSupportActionBar().setCustomView(
							actionBarStatus.customView);
				getSupportActionBar().setDisplayShowCustomEnabled(
						actionBarStatus.isCustomViewOnActionBarEnabled);
			}
		} else {
			getSupportActionBar().setSubtitle(null);
			setSupportProgressBarIndeterminateVisibility(false);
			getSupportActionBar().setDisplayShowCustomEnabled(false);
		}
		if (isInvalidate)
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
				VTULifePreferencesActivity.class);
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
		int count = VTULifeDataBase.getInstance(this)
				.getUnreadedNotificationCount();
		mNotificationCountTextView.setText(count + "");
	}

	private void notifyUSNCleaner() {
		((RefreshListener) mVtuLifeFragmentAdapter
				.getItem(ID_CLASS_RESULT_FRAGMENT)).refresh();
		((RefreshListener) mVtuLifeFragmentAdapter
				.getItem(ID_FAST_RESULT_FRAGMENT)).refresh();
	}

	public void showAbout() {
		Intent intent = new Intent(getApplicationContext(),
				VTULifeAboutActivity.class);
		startActivity(intent);
	}

	public void showHelp() {

		mHelpDialog = new Dialog(this);
		mHelpDialog.setContentView(R.layout.activity_help);

		Button facebookButton = (Button) mHelpDialog
				.findViewById(R.id.facebookButton);
		Button mailButton = (Button) mHelpDialog.findViewById(R.id.emailButton);
		Button downloadButton = (Button) mHelpDialog
				.findViewById(R.id.downloadButton);
		View.OnClickListener onClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.facebookButton:
					likeUsOnFacebook();
					break;
				case R.id.emailButton:
					sendNormalMail();
					break;
				case R.id.downloadButton:
					downloadHelpManual();
					break;
				}
			}
		};
		facebookButton.setOnClickListener(onClickListener);
		mailButton.setOnClickListener(onClickListener);
		downloadButton.setOnClickListener(onClickListener);
		mHelpDialog.setTitle("Help");
		mHelpDialog.show();
	}

	protected void downloadHelpManual() {
		if (isManualIsAlradyDownloaded()) {
			openManual();
		} else {
			if (mHelpDialog != null)
				mHelpDialog.dismiss();
			SystemFeatureChecker.downloadFile(VTULifeMainActivity.this,
					VTULifeConstance.WEB_URL
							+ VTULifeConstance.ANDROID_USER_MANUAL, false);
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
			Toast.makeText(this, "No Application Available to View PDF",
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isManualIsAlradyDownloaded() {
		String fileName = getManualFileUrl();
		return new File(fileName).exists();
	}

	private String getManualFileUrl() {
		// TODO take it to a commen class
		return VTULifeUtils.getDefaultRootFolder()
				+ VTULifeConstance.ANDROID_USER_MANUAL;
	}

	protected void sendNormalMail() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL,
				new String[] { VTULifeUtils.getVTULifePublicEmailId() });
		try {
			startActivity(Intent.createChooser(i, "Send email for help"));
		} catch (android.content.ActivityNotFoundException ex) {
			throw ex;
		}
	}

	public void likeUsOnFacebook() {
		SystemFeatureChecker.openUrlInBrowser(this,
				VTULifeConstance.FACEBOOK_PAGE_URL);
	}

	public void showNotificationActivity() {
		Intent intent = new Intent(getApplicationContext(),
				VTULifeNotificationActivity.class);
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
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.menu_settings:
			showPreferences();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mNavigationMenuManager.toggleNavMenu(menu,
				mDrawerLayout.isDrawerOpen(mMenuDrawer));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.navigation_drawer_menu, menu);
		MenuItem notificationMenuItem = menu.findItem(R.id.menu_notifications);
		View notificationView = MenuItemCompat
				.getActionView(notificationMenuItem);
		mNotificationCountTextView = (TextView) notificationView
				.findViewById(R.id.notifcationCountTextView);
		notifyNotificationRefresh();
		notificationView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showNotificationActivity();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void reflectNavChange(boolean isShowing) {
		if (!isShowing) {
			FragmentInfo fragmentInfo = (FragmentInfo) mVtuLifeFragmentAdapter
					.getItem(mViewPager.getCurrentItem());
			reflectActionBarChange(fragmentInfo.getActionBarStatus(),
					mViewPager.getCurrentItem(), false);
		} else {
			reflectActionBarChange(null, -1, false);
		}
	}

	public boolean isNavigationDrawerOpen() {
		return mDrawerLayout.isDrawerOpen(mMenuDrawer);
	}

}
