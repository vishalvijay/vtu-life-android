package com.V4Creations.vtulife.fragments;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.adapters.ResultAdapter;
import com.V4Creations.vtulife.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.db.VTULifeDataBase;
import com.V4Creations.vtulife.interfaces.RefreshListener;
import com.V4Creations.vtulife.interfaces.ResultLoadedInterface;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.model.ResultItem;
import com.V4Creations.vtulife.server.LoadResultFromServer;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.util.Settings;
import com.actionbarsherlock.app.SherlockListFragment;
import com.google.analytics.tracking.android.Tracker;

import de.keyboardsurfer.android.widget.crouton.Style;

public class ClassResultListFragment extends SherlockListFragment implements
		ResultLoadedInterface, FragmentInfo, RefreshListener {
	String TAG = "ClassResultListFragment";

	private CheckBox revalCheckBox;
	private AutoCompleteTextView classUsnAutoCompleteTextView;
	private ImageButton classSubmitImageButton;
	private ArrayAdapter<String> mClassUsnHistoryAdapter;
	private ArrayList<ResultItem> itemList;
	private VTULifeMainActivity vtuLifeMainActivity;
	private ResultAdapter resultAdapter;
	private ActionBarStatus mActionBarStatus;
	private final String classUsnRegx = "[1-4][a-zA-Z][a-zA-Z][0-9][0-9][a-zA-Z][a-zA-Z]";
	private int currentUsn;
	private int continuesFailCount;
	private int lastLoadingAdded;
	private boolean isCanceled;
	private String classUsn;
	private Tracker tracker;
	private int mSuccessResultCounter, mResultThreadCounter;

	public ClassResultListFragment() {
		itemList = new ArrayList<ResultItem>();
		mActionBarStatus = new ActionBarStatus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.list_class_result, null, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mResultThreadCounter = 0;
		tracker = GoogleAnalyticsManager
				.getGoogleAnalyticsTracker(vtuLifeMainActivity);
		initListAdapter();
		initViews();
		initActionBarCustomView();
		initEventListeners();
	}

	private void initEventListeners() {
		classSubmitImageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				manageResultFetch();
			}
		});
		classUsnAutoCompleteTextView
				.setOnEditorActionListener(new OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
								|| (actionId == EditorInfo.IME_ACTION_DONE)) {
							manageResultFetch();
						}
						return false;
					}
				});
	}

	private void initActionBarCustomView() {
		mActionBarStatus.customView = LayoutInflater.from(vtuLifeMainActivity)
				.inflate(R.layout.check_box_layout, null);
		revalCheckBox = (CheckBox) mActionBarStatus.customView
				.findViewById(R.id.revalCheckBox);
		mActionBarStatus.isCustomViewOnActionBarEnabled = true;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_CLASS_RESULT_FRAGMENT);
	}

	protected void manageResultFetch() {
		if (mResultThreadCounter == 0) {
			String usn = classUsnAutoCompleteTextView.getText().toString();
			if (usn.matches(classUsnRegx)) {
				mSuccessResultCounter = 0;
				classUsn = usn;
				getResult();
			} else
				classUsnAutoCompleteTextView.setError("Invalid Class USN");
		} else {
			cancelIt();
		}
	}

	private void initViews() {
		classSubmitImageButton = (ImageButton) getView().findViewById(
				R.id.classSubmitImageButton);
		initAutoCompleteTextView();
	}

	private void initAutoCompleteTextView() {
		classUsnAutoCompleteTextView = (AutoCompleteTextView) getView()
				.findViewById(R.id.classUsnAutoCompleteTextView);
		ArrayList<String> classUsnHistory = VTULifeDataBase
				.getClassUSNHistory(vtuLifeMainActivity);
		mClassUsnHistoryAdapter = new ArrayAdapter<String>(vtuLifeMainActivity,
				android.R.layout.simple_dropdown_item_1line, classUsnHistory);
		classUsnAutoCompleteTextView.setAdapter(mClassUsnHistoryAdapter);
		classUsnAutoCompleteTextView.addTextChangedListener(filterTextWatcher);
	}

	private void initListAdapter() {
		resultAdapter = new ResultAdapter(vtuLifeMainActivity, itemList);
		setListAdapter(resultAdapter);
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.toString().length() == 7
					&& !s.toString().matches(classUsnRegx)) {
				classUsnAutoCompleteTextView.setError("Invalid Class USN");
			} else {
				classUsnAutoCompleteTextView.setError(null);
			}
		}

	};

	private void cancelIt() {
		isCanceled = true;
		classSubmitImageButton.setEnabled(false);
		mActionBarStatus.subTitle = "Canceling...";
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_CLASS_RESULT_FRAGMENT);
	}

	protected void getResult() {
		classUsn = classUsn.toUpperCase(Locale.ENGLISH);
		classSubmitImageButton.setImageResource(R.drawable.ic_action_cancel);
		classUsnAutoCompleteTextView.setEnabled(false);
		mActionBarStatus.subTitle = "Loading...";
		removeListItems();
		revalCheckBox.setEnabled(false);
		mActionBarStatus.isInterminatePorogressBarVisible = true;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_CLASS_RESULT_FRAGMENT);
		continuesFailCount = 0;
		currentUsn = 0;
		lastLoadingAdded = -1;
		isCanceled = false;
		saveAndRefreshUsnHistory(classUsn);
		askResultOfUsn(5);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void askResultOfUsn(int numOfTimes) {
		mResultThreadCounter += numOfTimes;
		for (int i = 0; i < numOfTimes; i++) {
			String subUsn;
			currentUsn++;
			if (currentUsn < 10)
				subUsn = "00" + Integer.toString(currentUsn);
			else if (currentUsn < 100)
				subUsn = "0" + Integer.toString(currentUsn);
			else
				subUsn = Integer.toString(currentUsn);
			int whichTypeResult = Settings.isFullSemResult(vtuLifeMainActivity) ? LoadResultFromServer.MULTY_SEM
					: LoadResultFromServer.SINGLE_SEM;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
					&& !Settings.isSortedResult(vtuLifeMainActivity))
				new LoadResultFromServer(vtuLifeMainActivity, this,
						Settings.RESULT_FROM_VTU, whichTypeResult, classUsn
								+ subUsn, revalCheckBox.isChecked())
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new LoadResultFromServer(vtuLifeMainActivity, this,
						Settings.RESULT_FROM_VTU, whichTypeResult, classUsn
								+ subUsn, revalCheckBox.isChecked()).execute();
		}
	}

	@Override
	synchronized public void notifyResultLoaded(ArrayList<ResultItem> itemList,
			boolean isConnectionOk, String errorMessage, String usn) {
		mResultThreadCounter--;
		if (lastLoadingAdded != -1) {
			this.itemList.remove(lastLoadingAdded);
			resultAdapter.notifyDataSetChanged();
			lastLoadingAdded = -1;
		}
		if (isCanceled) {
			if (mResultThreadCounter == 0) {
				vtuLifeMainActivity.showCrouton("Canceled", Style.INFO, false);
				stopLoading();
			}
			return;
		}
		if (isConnectionOk) {
			for (int i = 0; i < itemList.size(); i++)
				this.itemList.add(itemList.get(i));
			lastLoadingAdded = this.itemList.size();
			this.itemList.add(new ResultItem());
			this.itemList.get(lastLoadingAdded).tag = ResultAdapter.TYPE_LOADING;
			resultAdapter.notifyDataSetChanged();
			continuesFailCount = 0;
			mSuccessResultCounter++;
		} else {
			if (!"Result not available.".equals(errorMessage) && !isCanceled)
				vtuLifeMainActivity.showCrouton(
						errorMessage + " (" + usn + ")", Style.ALERT, false);
			if (!Settings.isDeepSearch(vtuLifeMainActivity))
				continuesFailCount++;
		}
		if ((continuesFailCount > 10 && currentUsn < 179) || currentUsn == 179) {
			currentUsn = 399;
			continuesFailCount = 0;
			int year = Integer.parseInt(usn.substring(3, 5));
			year++;
			String yearString;
			if (year < 10)
				yearString = "0" + Integer.toString(year);
			else
				yearString = Integer.toString(year);
			classUsn = usn.substring(0, 3) + yearString + usn.substring(5, 7);
			askResultOfUsn(1);
		} else if ((continuesFailCount > 10) || currentUsn == 420)
			stopLoading();
		else
			askResultOfUsn(1);
	}

	private void stopLoading() {
		if (mResultThreadCounter == 0) {
			classSubmitImageButton.setEnabled(true);
			mActionBarStatus.subTitle = null;

			classSubmitImageButton
					.setImageResource(R.drawable.ic_send_holo_dark);
			classUsnAutoCompleteTextView.setEnabled(true);
			mActionBarStatus.isInterminatePorogressBarVisible = false;
			vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
					VTULifeMainActivity.ID_CLASS_RESULT_FRAGMENT);
			revalCheckBox.setEnabled(true);
			vtuLifeMainActivity.showCrouton(mSuccessResultCounter
					+ " results fetched", Style.INFO, true);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isCanceled = true;
	}

	public void removeListItems() {
		itemList.clear();
		resultAdapter.notifyDataSetChanged();
	}

	protected void saveAndRefreshUsnHistory(String usn) {
		GoogleAnalyticsManager.infomGoogleAnalytics(tracker,
				GoogleAnalyticsManager.CATEGORY_RESULT,
				GoogleAnalyticsManager.ACTION_CLASS_RESULT, usn, 0L);
		if (VTULifeDataBase.setClassUSNHistory(vtuLifeMainActivity, usn))
			mClassUsnHistoryAdapter.add(usn);
	}

	@Override
	public String getTitle() {
		return "Class Results";
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}

	@Override
	public void refresh() {
		mClassUsnHistoryAdapter.clear();
		ArrayList<String> classUsnHistory = VTULifeDataBase
				.getClassUSNHistory(vtuLifeMainActivity);
		for (int i = 0; i < classUsnHistory.size(); i++)
			mClassUsnHistoryAdapter.add(classUsnHistory.get(i));
	}
}
