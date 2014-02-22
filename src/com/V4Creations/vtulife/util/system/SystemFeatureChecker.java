package com.V4Creations.vtulife.util.system;

import java.io.File;
import java.util.Calendar;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.util.StringOperator;
import com.V4Creations.vtulife.util.VTULifeConstance;
import com.V4Creations.vtulife.util.VTULifeUtils;

public class SystemFeatureChecker {

	public static boolean isInternetConnection(Context context) {
		ConnectivityManager cn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nf = cn.getActiveNetworkInfo();
		if (nf != null && nf.isConnected() == true)
			return true;
		else
			return false;
	}

	public static String getImei(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		return telephonyManager.getDeviceId();
	}

	public static int getDisplayWidth(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}

	public static int getDisplayHeight(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

	public static int getAppVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getAppVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return StringOperator.toFullNameFormate(model);
		} else {
			return StringOperator.toFullNameFormate(manufacturer) + " " + model;
		}
	}

	public static String getAndroidVersion() {
		return Build.VERSION.RELEASE;
	}

	public static void rateAppOnPlayStore(Activity activity) {
		Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			activity.startActivity(myAppLinkToMarket);
		} catch (ActivityNotFoundException e) {
			throw e;
		}
	}

	public static void sendFeedback(Activity activity) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, VTULifeConstance.VTU_LIFE_EMAILS);
		i.putExtra(Intent.EXTRA_SUBJECT,
				"Feedback of " + activity.getString(R.string.app_name)
						+ " android app.");
		String bugReportBody = "Phone model : "
				+ SystemFeatureChecker.getDeviceName() + "\n"
				+ "Application name : " + activity.getString(R.string.app_name)
				+ "\n" + "Application version name: "
				+ SystemFeatureChecker.getAppVersionName(activity) + "\n"
				+ "Application version code : "
				+ SystemFeatureChecker.getAppVersionCode(activity) + "\n"
				+ "Phone android version : "
				+ SystemFeatureChecker.getAndroidVersion() + "\n"
				+ "-----------------------\n"
				+ "Please provide more details below :\n";
		i.putExtra(Intent.EXTRA_TEXT, bugReportBody);
		try {
			activity.startActivity(Intent.createChooser(i, "Send feedback"));
		} catch (android.content.ActivityNotFoundException ex) {
			throw ex;
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void downloadFile(Activity activity, String urlString,
			boolean isBrowserDownload) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& !isBrowserDownload) {
			DownloadManager dm = SystemFeatureChecker
					.getDownloadManager(activity);
			try {
				String fileName = getFileNameFromGetURL(urlString);
				Request request = new Request(Uri.parse(urlString))
						.setDestinationInExternalPublicDir(
								VTULifeUtils.getRootFolder(),
								VTULifeUtils
										.getOnlyFolderWithFileName(fileName))
						.setTitle(fileName)
						.setDescription("Downloading from VTU Life ...");
				manageApiIssues(request);
				dm.enqueue(request);
			} catch (IllegalArgumentException e) {
				try {
					Request request = new Request(Uri.parse(urlString))
							.setDestinationInExternalPublicDir(
									VTULifeUtils.getRootFolder(),
									VTULifeUtils
											.getOnlyFolderWithFileName(getFileNameFromURL(urlString)))
							.setDescription("Downloading from VTU Life ...");
					manageApiIssues(request);
					dm.enqueue(request);
				} catch (Exception ex) {
					openUrlInBrowser(activity, urlString);
				}
			}
		} else {
			openUrlInBrowser(activity, urlString);
		}
	}

	private static String getFileNameFromURL(String urlString) {
		try {
			return new File(urlString).getName();
		} catch (Exception ex) {

		}
		return Calendar.getInstance().getTimeInMillis() + "";
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static DownloadManager getDownloadManager(Context context) {
		return (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static void manageApiIssues(Request request) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
				request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			} else {
				request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
			}
		} else
			request.setShowRunningNotification(true);
	}

	public static void openUrlInBrowser(Activity activity, String urlString) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(urlString));
		activity.startActivity(i);
	}

	private static String getFileNameFromGetURL(String urlString) {
		String[] fileNameStrings = urlString.split("download=");
		if (fileNameStrings.length < 2)
			throw new IllegalArgumentException("Invalid URL");
		return fileNameStrings[1].replace("+", "_");
	}

	public static String getDeviceUuid(Context context) {
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
	}
}
