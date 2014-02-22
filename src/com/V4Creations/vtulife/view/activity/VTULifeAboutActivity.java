package com.V4Creations.vtulife.view.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.ReferredLibraryAdapter;
import com.V4Creations.vtulife.model.ReferredLibrary;
import com.V4Creations.vtulife.util.BugSenseManager;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;

public class VTULifeAboutActivity extends ActionBarActivity {
	private ReferredLibraryAdapter mAdapter;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseManager.initBugSense(this);
		setContentView(R.layout.activity_about);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initListView();
		TextView marqueeTextView = (TextView) this
				.findViewById(R.id.marqueueTextView);
		marqueeTextView.setSelected(true); // required for marquee effect
		TextView versionTextView = (TextView) findViewById(R.id.versionTextView);
		String versionString = getString(R.string.version,
				SystemFeatureChecker.getAppVersionName(getApplicationContext()));
		versionTextView.setText(versionString);
	}

	private void initListView() {
		mListView = (ListView) findViewById(R.id.listView);
		mAdapter = new ReferredLibraryAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ReferredLibrary referredLibraryModel = mAdapter.getItem(position);
				referredLibraryModel.setUrlVisible(!referredLibraryModel
						.isUrlVisible());
				mAdapter.notifyDataSetChanged();
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