package com.V4Creations.vtulife.fragments;

import java.util.ArrayList;
import java.util.Locale;

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
import com.V4Creations.vtulife.interfaces.ResultLoadedInterface;
import com.V4Creations.vtulife.interfaces.RefreshListener;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.model.ResultItem;
import com.V4Creations.vtulife.server.LoadResultFromServer;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.util.Settings;
import com.actionbarsherlock.app.SherlockListFragment;
import com.google.analytics.tracking.android.Tracker;

import de.keyboardsurfer.android.widget.crouton.Style;

public class FastResultListFragment extends SherlockListFragment implements
		ResultLoadedInterface, FragmentInfo, RefreshListener {
	String TAG = "FastResultListFragment";

	private CheckBox revalCheckBox;
	private AutoCompleteTextView usnAutoCompleteTextView;
	private ImageButton submitImageButton;
	private ArrayAdapter<String> mUsnHistoryAdapter;
	private ArrayList<ResultItem> itemList;
	private VTULifeMainActivity vtuLifeMainActivity;
	private ResultAdapter resultAdapter;
	private boolean isLoading = false;
	private LoadResultFromServer loadResultFromServer;
	private ActionBarStatus mActionBarStatus;
	private Tracker mTracker;

	private final String usnRegx = "[1-4][a-zA-Z][a-zA-Z][0-9][0-9][a-zA-Z][a-zA-Z][0-9][0-9][0-9]";

	public FastResultListFragment() {
		itemList = new ArrayList<ResultItem>();
		mActionBarStatus = new ActionBarStatus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.list_fast_result, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTracker = GoogleAnalyticsManager
				.getGoogleAnalyticsTracker(vtuLifeMainActivity);
		initListAdapter();
		initView();
		initActionBarCustomView();
		initEventListener();
	}

	private void initEventListener() {
		submitImageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				manageResultFetch();
			}
		});
		usnAutoCompleteTextView
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

	protected void manageResultFetch() {
		String usn = usnAutoCompleteTextView.getText().toString();
		if (usn.matches(usnRegx)) {
			if (!isLoading)
				getResult(usn);
			else {
				loadResultFromServer.cancel(true);
				submitImageButton.setEnabled(false);
			}
		} else {
			usnAutoCompleteTextView.setError("Invalid USN");
		}
	}

	private void initActionBarCustomView() {
		mActionBarStatus.customView = LayoutInflater.from(vtuLifeMainActivity)
				.inflate(R.layout.check_box_layout, null);
		revalCheckBox = (CheckBox) mActionBarStatus.customView
				.findViewById(R.id.revalCheckBox);
		mActionBarStatus.isCustomViewOnActionBarEnabled = true;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_FAST_RESULT_FRAGMENT);
	}

	private void initView() {
		submitImageButton = (ImageButton) getView().findViewById(
				R.id.submitImageButton);
		initAutoCompleteTextView();
	}

	private void initAutoCompleteTextView() {
		usnAutoCompleteTextView = (AutoCompleteTextView) getView()
				.findViewById(R.id.usnAutoCompleteTextView);
		ArrayList<String> usnHistory = VTULifeDataBase
				.getUSNHistory(vtuLifeMainActivity);
		mUsnHistoryAdapter = new ArrayAdapter<String>(vtuLifeMainActivity,
				android.R.layout.simple_dropdown_item_1line, usnHistory);
		usnAutoCompleteTextView.setAdapter(mUsnHistoryAdapter);

		usnAutoCompleteTextView.addTextChangedListener(filterTextWatcher);
	}

	private void initListAdapter() {
		resultAdapter = new ResultAdapter(vtuLifeMainActivity, itemList);
		setListAdapter(resultAdapter);
	}

	protected void getResult(String usn) {
		usn = usn.toUpperCase(Locale.ENGLISH);
		startLoading();
		removeListItems();
		mActionBarStatus.isInterminatePorogressBarVisible = true;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_FAST_RESULT_FRAGMENT);
		loadResultFromServer = new LoadResultFromServer(vtuLifeMainActivity,
				this, Settings.RESULT_FROM_VTU, LoadResultFromServer.MULTY_SEM,
				usn, revalCheckBox.isChecked());
		loadResultFromServer.execute();
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.toString().length() == 10 && !s.toString().matches(usnRegx)) {
				usnAutoCompleteTextView.setError("Invalid USN");
			} else {
				usnAutoCompleteTextView.setError(null);
			}
		}

	};

	private void startLoading() {
		isLoading = true;
		submitImageButton.setImageResource(R.drawable.ic_action_cancel);
		usnAutoCompleteTextView.setEnabled(false);
		mActionBarStatus.subTitle = "Loading...";
		mActionBarStatus.isInterminatePorogressBarVisible = true;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_FAST_RESULT_FRAGMENT);
		revalCheckBox.setEnabled(false);
	}

	private void stopLoading() {
		isLoading = false;
		submitImageButton.setImageResource(R.drawable.ic_send_holo_dark);
		submitImageButton.setEnabled(true);
		usnAutoCompleteTextView.setEnabled(true);
		mActionBarStatus.subTitle = null;
		mActionBarStatus.isInterminatePorogressBarVisible = false;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_FAST_RESULT_FRAGMENT);
		revalCheckBox.setEnabled(true);
	}

	public void removeListItems() {
		itemList.clear();
		resultAdapter.notifyDataSetChanged();
	}

	protected void saveAndRefreshUsnHistory(String usn) {
		GoogleAnalyticsManager.infomGoogleAnalytics(mTracker,
				GoogleAnalyticsManager.CATEGORY_RESULT,
				GoogleAnalyticsManager.ACTION_FAST_RESULT, usn, 0L);
		if (VTULifeDataBase.setUSNHistory(vtuLifeMainActivity, usn))
			mUsnHistoryAdapter.add(usn);
	}

	@Override
	public void notifyResultLoaded(ArrayList<ResultItem> itemList,
			boolean isConnectionOk, String errorMessage, String usn) {
		stopLoading();
		if (isConnectionOk) {
			for (int i = 0; i < itemList.size(); i++) {
				this.itemList.add(itemList.get(i));
			}
			resultAdapter.notifyDataSetChanged();
			saveAndRefreshUsnHistory(usn);
		} else
			vtuLifeMainActivity.showCrouton(errorMessage, Style.ALERT, false);
	}

	@Override
	public String getTitle() {
		return "Result";
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}

	public void refresh() {
		mUsnHistoryAdapter.clear();
		ArrayList<String> classUsnHistory = VTULifeDataBase
				.getClassUSNHistory(vtuLifeMainActivity);
		mUsnHistoryAdapter.addAll(classUsnHistory);
	}
}
