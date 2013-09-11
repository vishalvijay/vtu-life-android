package com.V4Creations.vtulife.fragments;

import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.adapters.DirectoryAdapter;
import com.V4Creations.vtulife.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.interfaces.DirectoryLoadedInterface;
import com.V4Creations.vtulife.server.LoadDirectoryFromServer;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.V4Creations.vtulife.util.ActionBarStatus;
import com.V4Creations.vtulife.util.DirectoryListItem;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.V4Creations.vtulife.util.Settings;
import com.V4Creations.vtulife.util.StackItem;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.Tracker;

import de.keyboardsurfer.android.widget.crouton.Style;

public class DirectoryListingFragment extends SherlockListFragment implements
		DirectoryLoadedInterface, FragmentInfo {

	String TAG = "DirectoryListingMainFragment";
	private Stack<StackItem> stack;
	private JSONObject json = null;
	private VTULifeMainActivity vtuLifeMainActivity;
	private ActionBarStatus mActionBarStatus;
	private boolean isFirstTime = true;
	private boolean isBackEnabled = false, isRefresh = true,
			isCloseButtonEnabled = true;

	private final int BACK_MENU = 0, REFRESH_MENU = 1;
	private String directoryName = null;
	private DirectoryAdapter directoryAdapter;
	private final String PAGE_URL = "/androidDirectoryListing.php";
	private final String SORT_URL = "?sort=date&order=asc";
	private String url = PAGE_URL + SORT_URL;
	private ArrayList<DirectoryListItem> itemList;
	private LoadDirectoryFromServer loadDirectoryFromServer;
	private LinearLayout progressLinearLayout;
	private ProgressBar progressBar;
	private TextView progressTextView;
	private Tracker tracker;

	public DirectoryListingFragment() {
		mActionBarStatus = new ActionBarStatus();
		itemList = new ArrayList<DirectoryListItem>();
		stack = new Stack<StackItem>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.list_directory, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		tracker = GoogleAnalyticsManager
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
				this.itemList);
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
		String ext = ((TextView) view.findViewById(R.id.extTextView)).getText()
				.toString();
		String href = ((TextView) view.findViewById(R.id.hrefTextView))
				.getText().toString();
		String fileOrFolderName = ((TextView) view
				.findViewById(R.id.nameTextView)).getText().toString();
		GoogleAnalyticsManager.infomGoogleAnalytics(tracker,
				GoogleAnalyticsManager.CATEGORY_NOTES,
				GoogleAnalyticsManager.ACTION_FOLDER, fileOrFolderName, 0L);
		if (ext.equals("dir")) {
			StackItem stackItem = new StackItem(json, url, directoryName);
			stack.push(stackItem);
			directoryName = ((TextView) view.findViewById(R.id.nameTextView))
					.getText().toString();
			url = ((TextView) view.findViewById(R.id.hrefTextView)).getText()
					.toString();
			loadDirectory();
		} else {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(Settings.WEB_URL + PAGE_URL + href));
			startActivity(i);
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
		if (!stack.isEmpty() && isBackEnabled) {
			menu.getItem(BACK_MENU).setEnabled(true);
			menu.getItem(BACK_MENU).setIcon(R.drawable.back);
		} else {
			menu.getItem(BACK_MENU).setEnabled(false);
			menu.getItem(BACK_MENU).setIcon(R.drawable.back_desable);
		}
		if (isRefresh) {
			menu.getItem(REFRESH_MENU).setIcon(R.drawable.ic_refresh);
			menu.getItem(REFRESH_MENU).setEnabled(true);
		} else if (isCloseButtonEnabled) {
			menu.getItem(REFRESH_MENU).setIcon(R.drawable.ic_action_cancel);
			menu.getItem(REFRESH_MENU).setEnabled(true);
		} else {
			menu.getItem(REFRESH_MENU).setIcon(
					R.drawable.ic_action_cancel_desabled);
			menu.getItem(REFRESH_MENU).setEnabled(false);
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
				vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
						VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT);
			}
			return true;
		}
		return false;
	}

	private void removeListItems() {
		itemList.clear();
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
				DirectoryListItem directoryListItem = new DirectoryListItem();
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
				itemList.add(directoryListItem);
			}
			directoryAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			vtuLifeMainActivity.showCrouton(e.getMessage(), Style.ALERT, false);
		}
		mActionBarStatus.subTitle = directoryName;
		vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
				VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT);
	}

	private void goBack() {
		StackItem stackItem = stack.pop();
		json = stackItem.mJson;
		url = stackItem.mUrl;
		directoryName = stackItem.mDirName;
		setCurrentDir();
		hideProgressLinearLayout();
	}

	@Override
	public void notifyDirectoryLoaded(ArrayList<DirectoryListItem> itemList,
			boolean isConnectionOk, String errorMessage, JSONObject json) {
		hideProgressLinearLayout();
		if (isConnectionOk) {
			this.json = json;
			for (int i = 0; i < itemList.size(); i++) {
				this.itemList.add(itemList.get(i));
			}
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
				VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT);
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
				VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT);
		loadDirectoryFromServer = new LoadDirectoryFromServer(
				vtuLifeMainActivity, this, url);
		loadDirectoryFromServer.execute();
	}

	@Override
	public String getTitle() {
		return "Notes";
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}
}
