package com.V4Creations.vtulife.view.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.ResultAdapter;
import com.V4Creations.vtulife.controller.adapters.UsnHistoryArrayAdapter;
import com.V4Creations.vtulife.controller.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.controller.db.VTULifeDataBase;
import com.V4Creations.vtulife.controller.server.ResultLoaderManager;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.model.ResultItem;
import com.V4Creations.vtulife.model.interfaces.RefreshListener;
import com.V4Creations.vtulife.model.interfaces.ResultLoadedInterface;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;
import com.google.analytics.tracking.android.Tracker;

import de.keyboardsurfer.android.widget.crouton.Style;

public class FastResultListFragment extends ListFragment implements
		ResultLoadedInterface, FragmentInfo, RefreshListener, OnClickListener,
		OnEditorActionListener {
	String TAG = "FastResultListFragment";

	private CheckBox revalCheckBox;
	private AutoCompleteTextView usnAutoCompleteTextView;
	private ImageButton submitImageButton;
	private UsnHistoryArrayAdapter mUsnHistoryAdapter;
	private VTULifeMainActivity activity;
	private ResultAdapter mAdapter;
	private ActionBarStatus mActionBarStatus;
	private Tracker mTracker;
	private ResultLoaderManager mResultLoaderManager;

	private final String usnRegx = "[1-4][a-zA-Z][a-zA-Z][0-9][0-9][a-zA-Z][a-zA-Z][a-zA-Z0-9][0-9][0-9]";

	public FastResultListFragment() {
		mActionBarStatus = new ActionBarStatus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.fragemnt_fast_result, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTracker = GoogleAnalyticsManager.getGoogleAnalyticsTracker(activity);
		initView();
	}

	private void initView() {
		initListAdapter();
		submitImageButton = (ImageButton) getView().findViewById(
				R.id.submitImageButton);
		initAutoCompleteTextView();
		initActionBarCustomView();
		initListener();
	}

	private void initActionBarCustomView() {
		mActionBarStatus.customView = LayoutInflater.from(activity).inflate(
				R.layout.check_box_layout, null);
		revalCheckBox = (CheckBox) mActionBarStatus.customView
				.findViewById(R.id.revalCheckBox);
		mActionBarStatus.isCustomViewOnActionBarEnabled = true;
		callActionBarReflect();
	}

	private void initAutoCompleteTextView() {
		usnAutoCompleteTextView = (AutoCompleteTextView) getView()
				.findViewById(R.id.usnAutoCompleteTextView);
		mUsnHistoryAdapter = new UsnHistoryArrayAdapter(activity);
		usnAutoCompleteTextView.setAdapter(mUsnHistoryAdapter);
		mUsnHistoryAdapter.reloadHistory(false);

		usnAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().length() == 10
						&& !s.toString().matches(usnRegx))
					showInvalodUsn();
				else
					usnAutoCompleteTextView.setError(null);
			}
		});
	}

	private void initListAdapter() {
		mAdapter = new ResultAdapter(activity);
		setListAdapter(mAdapter);
	}

	private void initListener() {
		submitImageButton.setOnClickListener(this);
		usnAutoCompleteTextView.setOnEditorActionListener(this);
	}

	protected void manageResultFetch() {
		String usn = usnAutoCompleteTextView.getText().toString();
		if (usn.matches(usnRegx))
			if (mResultLoaderManager != null
					&& mResultLoaderManager.isLoading())
				cancel();
			else
				getResult(usn);
		else
			showInvalodUsn();
	}

	private void showInvalodUsn() {
		usnAutoCompleteTextView.setError(getString(R.string.invalid_usn));
	}

	private void cancel() {
		mResultLoaderManager.cancel();
		stopLoading();
		activity.showCrouton(R.string.cancelled, Style.INFO, false);
	}

	protected void getResult(String usn) {
		mResultLoaderManager = new ResultLoaderManager(activity, this, usn,
				revalCheckBox.isChecked(), ResultLoaderManager.MULTY_SEM);
	}

	private void stopLoading() {
		mResultLoaderManager = null;
		submitImageButton.setImageResource(R.drawable.ic_action_send_now);
		usnAutoCompleteTextView.setEnabled(true);
		mActionBarStatus.subTitle = null;
		mActionBarStatus.isInterminatePorogressBarVisible = false;
		revalCheckBox.setEnabled(true);
		callActionBarReflect();
	}

	protected void saveAndRefreshUsnHistory(String usn) {
		GoogleAnalyticsManager.infomGoogleAnalytics(mTracker,
				GoogleAnalyticsManager.CATEGORY_RESULT,
				GoogleAnalyticsManager.ACTION_FAST_RESULT, usn, 0L);
		VTULifeDataBase.setUSNHistory(activity, usn);
		mUsnHistoryAdapter.reloadHistory(false);
	}

	@Override
	public String getTitle() {
		return FastResultListFragment.getFeatureName(activity);
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}

	@Override
	public void refresh() {
		if (mUsnHistoryAdapter != null)
			mUsnHistoryAdapter.reloadHistory(false);
	}

	public static String getFeatureName(Context context) {
		return context.getString(R.string.result);
	}

	@Override
	public void onStartLoading() {
		mAdapter.clear();
		submitImageButton.setImageResource(R.drawable.ic_action_cancel);
		usnAutoCompleteTextView.setEnabled(false);
		mActionBarStatus.subTitle = getString(R.string.loading);
		mActionBarStatus.isInterminatePorogressBarVisible = true;
		revalCheckBox.setEnabled(false);
		callActionBarReflect();
	}

	@Override
	public void onLoadingSuccess(ArrayList<ResultItem> resultItems, String usn) {
		stopLoading();
		mAdapter.supportAddAll(resultItems);
		saveAndRefreshUsnHistory(usn);
	}

	@Override
	public void onLoadingFailure(String message, String trackMessage,
			int statusCode, String usn) {
		stopLoading();
		activity.showCrouton(message, Style.ALERT, false);
		if (!trackMessage.equals(""))
			GoogleAnalyticsManager.infomGoogleAnalytics(mTracker,
					GoogleAnalyticsManager.CATEGORY_RESULT,
					GoogleAnalyticsManager.ACTION_NETWORK_ERROR, trackMessage
							+ "-" + statusCode, 0L);
	}

	@Override
	public void onClick(View v) {
		manageResultFetch();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
				|| (actionId == EditorInfo.IME_ACTION_DONE))
			manageResultFetch();
		return false;
	}

	private void callActionBarReflect() {
		activity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_FAST_RESULT_FRAGMENT, true);
	}
}