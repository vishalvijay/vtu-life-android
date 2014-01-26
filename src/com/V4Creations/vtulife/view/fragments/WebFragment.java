package com.V4Creations.vtulife.view.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;

import de.keyboardsurfer.android.widget.crouton.Style;

public class WebFragment extends Fragment implements FragmentInfo {

	String TAG = "WebFragment";
	private VTULifeMainActivity vtuLifeMainActivity;
	private WebView mWebView;
	private boolean isForwardEnabled = false, isBackEnabled = true,
			isRefresh = true;
	private ProgressBar loadingProgressBar;
	private TextView progressTextView;
	private String currentUrl = "http://www.vtulife.com";
	private ActionBarStatus mActionBarStatus;

	public WebFragment() {
		mActionBarStatus = new ActionBarStatus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.fragemnt_web, null, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViews();
		mWebView.loadUrl(currentUrl);
	}

	private void initViews() {
		loadingProgressBar = (ProgressBar) getView().findViewById(
				R.id.loadingProgressBar);
		progressTextView = (TextView) getView().findViewById(
				R.id.progressTextView);
		hideLodingProgressBar();
		mWebView = (WebView) getView().findViewById(R.id.vtuLifeWebView);
		initWebView();
	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private void initWebView() {
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new JavaScriptInterface(),
				"HTMLOUT");

		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				loadingProgressBar.setProgress(progress);
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				vtuLifeMainActivity
						.showCrouton(description, Style.ALERT, false);
				showReload();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				isRefresh = false;
				isBackEnabled = mWebView.canGoBack();
				isForwardEnabled = mWebView.canGoForward();
				mActionBarStatus.subTitle = "Loading...";
				vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
						VTULifeMainActivity.ID_VTU_LIFE_WEB_FRAGMENT, true);
				showLoadingProgressBar();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				currentUrl = url;
				mWebView
						.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				isRefresh = true;
				isBackEnabled = mWebView.canGoBack();
				isForwardEnabled = mWebView.canGoForward();
				mActionBarStatus.subTitle = null;
				vtuLifeMainActivity.reflectActionBarChange(mActionBarStatus,
						VTULifeMainActivity.ID_VTU_LIFE_WEB_FRAGMENT, true);
				hideLodingProgressBar();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.equals("https://play.google.com/store/apps/details?id=com.V4Creations.vtulife"))
					vtuLifeMainActivity.showRateApp();
				else if (url.contains("classresults.php"))
					vtuLifeMainActivity
							.changeCurrentFragemnt(VTULifeMainActivity.ID_CLASS_RESULT_FRAGMENT);
				else if (url.contains("fastresults.php"))
					vtuLifeMainActivity
							.changeCurrentFragemnt(VTULifeMainActivity.ID_FAST_RESULT_FRAGMENT);
				else if (url.contains("resource"))
					vtuLifeMainActivity
							.changeCurrentFragemnt(VTULifeMainActivity.ID_DIRECTORY_LISTING_FRAGMENT);
				else if (url.contains("upload.html"))
					vtuLifeMainActivity
							.changeCurrentFragemnt(VTULifeMainActivity.ID_UPLOAD_FILE_FRAGEMENT);
				else
					view.loadUrl(url);
				return true;
			}
		});
		mWebView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				SystemFeatureChecker.downloadFile(vtuLifeMainActivity, url,
						false);
				vtuLifeMainActivity.showCrouton("Downloading started",
						Style.INFO, false);
			}
		});
	}

	class JavaScriptInterface {
		public void showHTML(String html) {
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.vtu_life_web_layout, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (!vtuLifeMainActivity.isNavigationDrawerOpen()) {
			MenuItem forwardMenuItem = menu.findItem(R.id.menu_forward);
			MenuItem backMenuItem = menu.findItem(R.id.menu_back);
			MenuItem refreshMenuItem = menu.findItem(R.id.menu_refresh);
			if (isForwardEnabled) {
				forwardMenuItem.setEnabled(true);
				forwardMenuItem.setIcon(R.drawable.ic_action_next_item);

			} else {
				forwardMenuItem.setEnabled(false);
				forwardMenuItem
						.setIcon(R.drawable.ic_action_next_item_disabled);
			}
			if (isBackEnabled) {
				backMenuItem.setEnabled(true);
				backMenuItem.setIcon(R.drawable.ic_action_previous_item);
			} else {
				backMenuItem.setEnabled(false);
				backMenuItem
						.setIcon(R.drawable.ic_action_previous_item_disabled);
			}
			if (isRefresh)
				refreshMenuItem.setIcon(R.drawable.ic_action_refresh);
			else
				refreshMenuItem.setIcon(R.drawable.ic_action_cancel);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_back:
			mWebView.goBack();
			return true;
		case R.id.menu_forward:
			mWebView.goForward();
			return true;
		case R.id.menu_refresh:
			if (isRefresh)
				mWebView.reload();
			else {
				isRefresh = true;
				vtuLifeMainActivity.supportInvalidateOptionsMenu();
				mWebView.stopLoading();
			}
			return true;
		}
		return false;
	}

	private void hideLodingProgressBar() {
		loadingProgressBar.setVisibility(View.GONE);
	}

	private void showLoadingProgressBar() {
		loadingProgressBar.setProgress(0);
		loadingProgressBar.setVisibility(View.VISIBLE);
		progressTextView.setVisibility(View.GONE);
	}

	private void showReload() {
		hideLodingProgressBar();
		progressTextView.setVisibility(View.VISIBLE);
	}

	@Override
	public String getTitle() {
		return WebFragment.getFeatureName();
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}

	public static String getFeatureName() {
		return "Website";
	}
}
