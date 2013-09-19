package com.V4Creations.vtulife.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.adapters.ReferredLibraryAdapter;
import com.V4Creations.vtulife.model.ReferredLibrary;
import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

public class VTULifeAboutShelockActivity extends SherlockListActivity {
	private ReferredLibraryAdapter mAdapter;
	ArrayList<ReferredLibrary> mReferredLibrarys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vtulife_about_activity_layout);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mReferredLibrarys = new ArrayList<ReferredLibrary>();
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
		String[] libraryNames = getResources().getStringArray(
				R.array.library_names);
		String[] libraryUrls = getResources().getStringArray(
				R.array.library_urls);
		for (int i = 0; i < libraryNames.length; i++)
			mReferredLibrarys.add(new ReferredLibrary(libraryNames[i],
					libraryUrls[i]));
		mAdapter = new ReferredLibraryAdapter(this, mReferredLibrarys);
		setListAdapter(mAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ReferredLibrary referredLibraryModel = mReferredLibrarys.get(position);
		referredLibraryModel
				.setUrlVisible(!referredLibraryModel.isUrlVisible());
		mAdapter.notifyDataSetChanged();
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
}