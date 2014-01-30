package com.V4Creations.vtulife.view.fragments;

import java.util.ArrayList;

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
import com.V4Creations.vtulife.controller.server.ClassResultLoaderManager;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.model.ResultItem;
import com.V4Creations.vtulife.model.interfaces.ClassResultLoaderInterface;
import com.V4Creations.vtulife.model.interfaces.RefreshListener;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;
import com.google.analytics.tracking.android.Tracker;

import de.keyboardsurfer.android.widget.crouton.Style;

public class ClassResultListFragment extends ListFragment implements
		ClassResultLoaderInterface, FragmentInfo, RefreshListener,
		OnClickListener, OnEditorActionListener {
	String TAG = "ClassResultListFragment";

	private CheckBox revalCheckBox;
	private AutoCompleteTextView classUsnAutoCompleteTextView;
	private ImageButton classSubmitImageButton;
	private UsnHistoryArrayAdapter mClassUsnHistoryAdapter;
	private VTULifeMainActivity vtuLifeMainActivity;
	private ResultAdapter mAdapter;
	private ActionBarStatus mActionBarStatus;
	private Tracker mTracker;
	private ClassResultLoaderManager mClassResultLoaderManager;
	private boolean isClassUsnSaved = false;

	private final String classUsnRegx = "[1-4][a-zA-Z][a-zA-Z][0-9][0-9][a-zA-Z][a-zA-Z][a-zA-Z]{0,1}";

	public ClassResultListFragment() {
		mActionBarStatus = new ActionBarStatus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.fragemnt_class_result, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTracker = GoogleAnalyticsManager
				.getGoogleAnalyticsTracker(vtuLifeMainActivity);
		initView();
	}

	private void initView() {
		initListAdapter();
		classSubmitImageButton = (ImageButton) getView().findViewById(
				R.id.classSubmitImageButton);
		initAutoCompleteTextView();
		initActionBarCustomView();
		initListener();
	}

	private void initActionBarCustomView() {
		mActionBarStatus.customView = LayoutInflater.from(vtuLifeMainActivity)
				.inflate(R.layout.check_box_layout, null);
		revalCheckBox = (CheckBox) mActionBarStatus.customView
				.findViewById(R.id.revalCheckBox);
		mActionBarStatus.isCustomViewOnActionBarEnabled = true;
		callActionBarReflect();
	}

	private void initAutoCompleteTextView() {
		classUsnAutoCompleteTextView = (AutoCompleteTextView) getView()
				.findViewById(R.id.classUsnAutoCompleteTextView);
		mClassUsnHistoryAdapter = new UsnHistoryArrayAdapter(
				vtuLifeMainActivity);
		classUsnAutoCompleteTextView.setAdapter(mClassUsnHistoryAdapter);
		mClassUsnHistoryAdapter.reloadHistory(true);

		classUsnAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().length() >= 7
						&& !s.toString().matches(classUsnRegx))
					showInvalodUsn();
				else
					classUsnAutoCompleteTextView.setError(null);
			}
		});
	}

	private void initListAdapter() {
		mAdapter = new ResultAdapter(vtuLifeMainActivity);
		setListAdapter(mAdapter);
	}

	private void initListener() {
		classSubmitImageButton.setOnClickListener(this);
		classUsnAutoCompleteTextView.setOnEditorActionListener(this);
	}

	protected void manageResultFetch() {
		String classUsn = classUsnAutoCompleteTextView.getText().toString();
		if (classUsn.matches(classUsnRegx))
			if (mClassResultLoaderManager != null
					&& mClassResultLoaderManager.isLoading())
				cancel();
			else
				getResult(classUsn);
		else
			showInvalodUsn();
	}

	private void showInvalodUsn() {
		classUsnAutoCompleteTextView.setError("Invalid class USN.");
	}

	private void cancel() {
		mClassResultLoaderManager.cancel();
	}

	protected void getResult(String classUsn) {
		mClassResultLoaderManager = new ClassResultLoaderManager(
				vtuLifeMainActivity, this, classUsn, revalCheckBox.isChecked());
		isClassUsnSaved = false;
	}

	private void stopLoading() {
		mClassResultLoaderManager = null;
		classSubmitImageButton.setImageResource(R.drawable.ic_action_send_now);
		classUsnAutoCompleteTextView.setEnabled(true);
		mActionBarStatus.subTitle = null;
		mActionBarStatus.isInterminatePorogressBarVisible = false;
		revalCheckBox.setEnabled(true);
		callActionBarReflect();
	}

	protected void saveAndRefreshUsnHistory() {
		GoogleAnalyticsManager.infomGoogleAnalytics(mTracker,
				GoogleAnalyticsManager.CATEGORY_RESULT,
				GoogleAnalyticsManager.ACTION_FAST_RESULT,
				mClassResultLoaderManager.getClassUsn(), 0L);
		VTULifeDataBase.setClassUSNHistory(vtuLifeMainActivity,
				mClassResultLoaderManager.getClassUsn());
		mClassUsnHistoryAdapter.reloadHistory(true);
	}

	@Override
	public String getTitle() {
		return ClassResultListFragment.getFeatureName();
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}

	public void refresh() {
		mClassUsnHistoryAdapter.clear();
		ArrayList<String> usnHistory = VTULifeDataBase
				.getClassUSNHistory(vtuLifeMainActivity);
		for (int i = 0; i < usnHistory.size(); i++)
			mClassUsnHistoryAdapter.add(usnHistory.get(i));
	}

	public static String getFeatureName() {
		return "Class Result";
	}

	@Override
	public void onStartLoading() {
		mAdapter.clear();
		classSubmitImageButton.setImageResource(R.drawable.ic_action_cancel);
		classUsnAutoCompleteTextView.setEnabled(false);
		mActionBarStatus.subTitle = "Loading...";
		mActionBarStatus.isInterminatePorogressBarVisible = true;
		revalCheckBox.setEnabled(false);
		callActionBarReflect();
	}

	@Override
	public void onLoadingSuccess(ArrayList<ResultItem> resultItems, String usn) {
		mAdapter.addAll(resultItems);
		if (!isClassUsnSaved) {
			saveAndRefreshUsnHistory();
			isClassUsnSaved = true;
		}
	}

	@Override
	public void onLoadingFailure(String message, String trackMessage,
			int statusCode, String usn) {
		vtuLifeMainActivity.showCrouton(message + "(" + usn + ")", Style.ALERT,
				false);
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
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_CLASS_RESULT_FRAGMENT, true);
	}

	@Override
	public void onFinishLoading() {
		String message = "Finished";
		if (mClassResultLoaderManager.isCancelled())
			message = "Cancelled";
		vtuLifeMainActivity.showCrouton(message + " ("
				+ mClassResultLoaderManager.getTotatlSuccessResult()
				+ " results).", Style.INFO, false);
		stopLoading();
	}
}