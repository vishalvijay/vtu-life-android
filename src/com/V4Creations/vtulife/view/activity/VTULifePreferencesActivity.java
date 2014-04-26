package com.V4Creations.vtulife.view.activity;

import org.jraf.android.backport.switchwidget.Switch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.db.VTULifeDataBase;
import com.V4Creations.vtulife.util.BugSenseManager;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.util.Settings;
import com.google.analytics.tracking.android.EasyTracker;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class VTULifePreferencesActivity extends ActionBarActivity {
	private Switch mFullResultDetailsSwitch, mSortedResultSwitch,
			mDeepSearchResultSwitch;
	private Spinner mFavoritePageSpinner;
	private EasyTracker mEasyTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseManager.initBugSense(this);
		setContentView(R.layout.activity_preference);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mEasyTracker = GoogleAnalyticsManager
				.getGoogleAnalyticsTracker(getApplicationContext());
		initViews();
	}

	private void initViews() {
		mFavoritePageSpinner = (Spinner) findViewById(R.id.favoritePageSpinner);
		mFullResultDetailsSwitch = (Switch) findViewById(R.id.fullResultViewCompoundButton);
		mSortedResultSwitch = (Switch) findViewById(R.id.sortedResultCompoundButton);
		mDeepSearchResultSwitch = (Switch) findViewById(R.id.deepSearchCompoundButton);
		mFavoritePageSpinner.setSelection(Settings
				.getFavoritePage(getApplicationContext()));
		mFullResultDetailsSwitch.setChecked(Settings
				.isFullSemResult(getApplicationContext()));
		mSortedResultSwitch.setChecked(Settings
				.isSortedResult(getApplicationContext()));
		mDeepSearchResultSwitch.setChecked(Settings
				.isDeepSearch(getApplicationContext()));
		mFullResultDetailsSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Settings.setFullSemResultStatus(
								getApplicationContext(), isChecked);
						GoogleAnalyticsManager.infomGoogleAnalytics(
								mEasyTracker,
								GoogleAnalyticsManager.CATEGORY_PREFERENCES,
								GoogleAnalyticsManager.ACTION_FULL_RESULT_VIEW,
								isChecked ? "true" : "false", 0L);
					}
				});
		mSortedResultSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Settings.setSortedResultStatus(getApplicationContext(),
								isChecked);
						GoogleAnalyticsManager.infomGoogleAnalytics(
								mEasyTracker,
								GoogleAnalyticsManager.CATEGORY_PREFERENCES,
								GoogleAnalyticsManager.ACTION_SORTED_RESULT,
								isChecked ? "true" : "false", 0L);
					}
				});
		mDeepSearchResultSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Settings.setDeepSearch(getApplicationContext(),
								isChecked);
						GoogleAnalyticsManager
								.infomGoogleAnalytics(
										mEasyTracker,
										GoogleAnalyticsManager.CATEGORY_PREFERENCES,
										GoogleAnalyticsManager.ACTION_DEEP_RESULT_SEARCH,
										isChecked ? "true" : "false", 0L);
					}
				});
		mFavoritePageSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						// ((TextView) parent.getChildAt(0))
						// .setTextColor(getResources().getColor(
						// R.color.gray));
						Settings.setFavoritePage(getApplicationContext(),
								position);
						GoogleAnalyticsManager.infomGoogleAnalytics(
								mEasyTracker,
								GoogleAnalyticsManager.CATEGORY_PREFERENCES,
								GoogleAnalyticsManager.ACTION_FAVORITE_PAGE,
								getResources().getStringArray(
										R.array.fragmentNames)[position], 0L);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void clearButtonClick(View v) {
		boolean result;
		if (((String) v.getTag()).equals("0")) {
			result = VTULifeDataBase.clearUSNHistory(getApplicationContext());
		} else {
			result = VTULifeDataBase
					.clearClassUSNHistory(getApplicationContext());
		}
		if (result) {
			String text = ((Button) v).getText().toString();
			Crouton.makeText(this,
					getString(R.string.usn_history_cleared, text), Style.INFO)
					.show();
		} else
			Toast.makeText(getApplicationContext(),
					R.string.no_search_history_available, Toast.LENGTH_SHORT)
					.show();
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
