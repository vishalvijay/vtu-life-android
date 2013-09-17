package com.V4Creations.vtulife.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.db.VTULifeDataBase;
import com.V4Creations.vtulife.util.Settings;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class VTULifePreferences extends SherlockActivity {
	private CompoundButton mFullResultDetailsCompoundButton,
			mSortedResultCompoundButton,
			mStopResultFetchingAfter10ErrorsCompoundButton;
	private Spinner mFavoritePageSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vtu_life_preference_activity_layout);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initViews();
	}

	private void initViews() {
		mFavoritePageSpinner = (Spinner) findViewById(R.id.favoritePageSpinner);
		mFullResultDetailsCompoundButton = (CompoundButton) findViewById(
				R.id.compoundFrameLayout1).findViewById(R.id.enabled);
		mSortedResultCompoundButton = (CompoundButton) findViewById(
				R.id.compoundFrameLayout2).findViewById(R.id.enabled);
		mStopResultFetchingAfter10ErrorsCompoundButton = (CompoundButton) findViewById(
				R.id.compoundFrameLayout3).findViewById(R.id.enabled);
		mFavoritePageSpinner.setSelection(Settings
				.getFavoritePage(getApplicationContext()));
		mFullResultDetailsCompoundButton.setChecked(Settings
				.isFullSemResult(getApplicationContext()));
		mSortedResultCompoundButton.setChecked(Settings
				.isSortedResult(getApplicationContext()));
		mStopResultFetchingAfter10ErrorsCompoundButton.setChecked(Settings
				.isDeepSearch(getApplicationContext()));
		mFullResultDetailsCompoundButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Settings.setFullSemResultStatus(
								getApplicationContext(), isChecked);
					}
				});
		mSortedResultCompoundButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Settings.setSortedResultStatus(getApplicationContext(),
								isChecked);
					}
				});
		mStopResultFetchingAfter10ErrorsCompoundButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Settings.setDeepSearch(
								getApplicationContext(), isChecked);
					}
				});
		mFavoritePageSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						((TextView) parent.getChildAt(0))
								.setTextColor(getResources().getColor(
										R.color.gray));
						Settings.setFavoritePage(getApplicationContext(),
								position);
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
					text + " history cleared",
					Style.INFO).show();
		} else
			Toast.makeText(getApplicationContext(),
					"No search history available", Toast.LENGTH_SHORT).show();
	}
}
