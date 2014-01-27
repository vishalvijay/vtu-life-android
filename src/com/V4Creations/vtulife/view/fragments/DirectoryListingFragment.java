package com.V4Creations.vtulife.view.fragments;

import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.DirectoryAdapter;
import com.V4Creations.vtulife.controller.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.controller.server.LoadDirectoryFromServer;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.model.DirectoryItem;
import com.V4Creations.vtulife.model.StackItem;
import com.V4Creations.vtulife.model.interfaces.DirectoryLoadedInterface;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.util.VTULifeConstance;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;
import com.google.analytics.tracking.android.Tracker;

import de.keyboardsurfer.android.widget.crouton.Style;

public class DirectoryListingFragment extends ListFragment implements
		DirectoryLoadedInterface, FragmentInfo {

	String TAG = "DirectoryListingMainFragment";
	private Stack<StackItem> stack;
	private JSONObject json = null;
	private VTULifeMainActivity vtuLifeMainActivity;
	private ActionBarStatus mActionBarStatus;
	private boolean isFirstTime = true;
	private boolean isBackEnabled = false, isRefresh = true,
			isCloseButtonEnabled = true;
	private String directoryName = null;
	private DirectoryAdapter directoryAdapter;
	private final String PAGE_URL = "/androidDirectoryListing.php";
	private final String SORT_URL = "?sort=date&order=asc";
	private String mUrl = PAGE_URL + SORT_URL;
	private ArrayList<DirectoryItem> mItemList;
	private LoadDirectoryFromServer loadDirectoryFromServer;
	private LinearLayout progressLinearLayout;
	private ProgressBar progressBar;
	private TextView progressTextView;
	private Tracker mTracker;

	public DirectoryListingFragment() {
		mActionBarStatus = new ActionBarStatus();
		mItemList = new ArrayList<DirectoryItem>();
		stack = new Stack<StackItem>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.fragemnt_directory, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTracker = GoogleAnalyticsManager
				.getGoogleAnalyticsTracker(vtuLifeMainActivity);
		initView();
		hideProgressLinearLayout();
		initListAdapter();
		if (isFirstTime || json == null) {
			isFirstTime = false;
			loadDirectory();
		} else
			setCurrentDir();
	}

	private void initListAdapter() {
		directoryAdapter = new DirectoryAdapter(vtuLifeMainActivity,
				this.mItemList);
		setListAdapter(directoryAdapter);
	}

	private void initView() {
		progressLinearLayout = (LinearLayout) getView().findViewById(
				R.id.progressLinearLayout);
		progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
		progressTextView = (TextView) getView().findViewById(
				R.id.progressTextView);
	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		super.onListItemClick(l, view, position, id);
		DirectoryItem directoryListItem = mItemList.get(position);
		String ext = directoryListItem.ext;
		String href = directoryListItem.href;
		String fileOrFolderName = directoryListItem.name;
		GoogleAnalyticsManager.infomGoogleAnalytics(mTracker,
				GoogleAnalyticsManager.CATEGORY_NOTES,
				GoogleAnalyticsManager.ACTION_FOLDER, fileOrFolderName, 0L);
		if (ext.equals("dir")) {
			StackItem stackItem = new StackItem(json, mUrl, directoryName);
			stack.push(stackItem);
			directoryName = ((TextView) view.findViewById(R.id.nameTextView))
					.getText().toString();
			mUrl = href;
			loadDirectory();
		} else {
			SystemFeatureChecker.downloadFile(vtuLifeMainActivity,
					VTULifeConstance.WEB_URL + PAGE_URL + href, false);
			vtuLifeMainActivity.showCrouton("Downloading started", Style.INFO,
					false);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.directory_listing_main_layout, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (!vtuLifeMainActivity.isNavigationDrawerOpen()) {
			MenuItem backMenuItem = menu.findItem(R.id.menu_back);
			MenuItem refreshMenuItem = menu.findItem(R.id.menu_refresh);
			if (!stack.isEmpty() && isBackEnabled) {
				backMenuItem.setEnabled(true);
				backMenuItem.setIcon(R.drawable.ic_action_previous_item);
			} else {
				backMenuItem.setEnabled(false);
				backMenuItem
						.setIcon(R.drawable.ic_action_previous_item_disabled);
			}
			if (isRefresh) {
				refreshMenuItem.setIcon(R.drawable.ic_action_refresh);
				refreshMenuItem.setEnabled(true);
			} else if (isCloseButtonEnabled) {
				refreshMenuItem.setIcon(R.drawable.ic_action_cancel);
				refreshMenuItem.setEnabled(true);
			} else {
				refreshMenuItem.setIcon(R.drawable.ic_action_cancel_disabled);
				refreshMenuItem.setEnabled(false);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_back:
			goBack();
			return true;
		case R.id.menu_refresh:
			if (isRefresh)
				loadDirectory();
			else {
				isCloseButtonEnabled = false;
				loadDirectoryFromServer.cancel(true);
				mActionBarStatus.subTitle = "Canceling...";
				vtuLifeMainActivity
						.reflectActionBarChange(
								mActionBarStatus,
								VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT,
								true);
			}
			return true;
		}
		return false;
	}

	private void removeListItems() {
		mItemList.clear();
		directoryAdapter.notifyDataSetChanged();
	}

	private void setCurrentDir() {
		removeListItems();
		if (json == null) {
			showReload();
			return;
		}
		try {
			JSONArray items = json
					.getJSONArray(LoadDirectoryFromServer.TAG_ITEMS);
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				DirectoryItem directoryListItem = new DirectoryItem();
				directoryListItem.href = item
						.getString(LoadDirectoryFromServer.TAG_HREF);
				directoryListItem.name = item
						.getString(LoadDirectoryFromServer.TAG_NAME);
				directoryListItem.size = item
						.getString(LoadDirectoryFromServer.TAG_SIZE);
				directoryListItem.date = item
						.getString(LoadDirectoryFromServer.TAG_DATE);
				directoryListItem.ext = item
						.getString(LoadDirectoryFromServer.TAG_EXT);
				directoryListItem.color = i;
				mItemList.add(directoryListItem);
			}
			directoryAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			vtuLifeMainActivity.showCrouton(e.getMessage(), Style.ALERT, false);
		}
		mActionBarStatus.subTitle = directoryName;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT, true);
	}

	private void goBack() {
		StackItem stackItem = stack.pop();
		json = stackItem.mJson;
		mUrl = stackItem.mUrl;
		directoryName = stackItem.mDirName;
		setCurrentDir();
		hideProgressLinearLayout();
	}

	@Override
	public void notifyDirectoryLoaded(ArrayList<DirectoryItem> itemList,
			boolean isConnectionOk, String errorMessage, JSONObject json) {
		hideProgressLinearLayout();
		if (isConnectionOk) {
			this.json = json;
			for (int i = 0; i < itemList.size(); i++)
				this.mItemList.add(itemList.get(i));
			directoryAdapter.notifyDataSetChanged();
		} else if (errorMessage.equals("Empty folder")) {
			vtuLifeMainActivity.showCrouton(errorMessage, Style.INFO, false);
			goBack();
		} else if ("Canceled".equals(errorMessage)) {
			setCurrentDir();
			vtuLifeMainActivity.showCrouton(errorMessage, Style.INFO, false);
			isCloseButtonEnabled = true;
		} else {
			vtuLifeMainActivity.showCrouton(errorMessage, Style.ALERT, false);
			showReload();
		}
		isRefresh = true;
		isBackEnabled = true;
		mActionBarStatus.subTitle = directoryName;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT, true);
	}

	private void hideProgressLinearLayout() {
		progressLinearLayout.setVisibility(View.GONE);
	}

	private void showProgressLinearLayout() {
		progressLinearLayout.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		progressTextView.setText("Please wait...");
	}

	private void showReload() {
		progressLinearLayout.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		progressTextView.setText("Please reload");
	}

	private void loadDirectory() {
		removeListItems();
		showProgressLinearLayout();
		isRefresh = false;
		isBackEnabled = false;
		mActionBarStatus.subTitle = "Loading...";
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT, true);
		loadDirectoryFromServer = new LoadDirectoryFromServer(
				vtuLifeMainActivity, this, mUrl);
		loadDirectoryFromServer.execute();
	}

	@Override
	public String getTitle() {
		return DirectoryListingFragment.getFeatureName();
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}

	public static String getFeatureName() {
		return "Notes";
	}
}
