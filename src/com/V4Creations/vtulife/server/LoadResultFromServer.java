package com.V4Creations.vtulife.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.V4Creations.vtulife.adapters.ResultAdapter;
import com.V4Creations.vtulife.interfaces.ResultLoadedInterface;
import com.V4Creations.vtulife.system.SystemFeatureChecker;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.V4Creations.vtulife.util.JSONParser;
import com.V4Creations.vtulife.util.ResultItem;
import com.V4Creations.vtulife.util.Settings;

public class LoadResultFromServer extends AsyncTask<String, String, String> {
	String TAG = "LoadResultFromServer";
	public static final String TAG_NAME = "name";
	public static final String TAG_USN = "usn";
	public static final String TAG_SEMESTER = "semesters";
	public static final String TAG_RESULT = "result";
	public static final String TAG_PERCENTAGE = "percentage";
	public static final String TAG_TOTAL = "total";
	public static final String TAG_MARK = "mark";
	public static final String TAG_MESSAGE = "message";
	public static final int SINGLE_SEM = 0;
	public static final int MULTY_SEM = 1;
	public static final int REVAVL_RESULT = 1;
	public static final int REGULAR_RESULT = 0;

	boolean isConnectionOk;
	private String errorMessage;
	private JSONParser jParser = new JSONParser();
	private String usn;
	private int resultType;
	private VTULifeMainActivity vtuLifeMainActivity;
	private ArrayList<ResultItem> itemList;
	private JSONObject currentResultJsonObject;
	private final String RESULT_URL;
	private final int RESULT_REQUEST_MODE;
	private ResultLoadedInterface resultLoadedInterface;

	public LoadResultFromServer(VTULifeMainActivity vtuLifeMainActivity,
			Fragment fragment, String url, int requestMode, String usn,
			Boolean resultType) {
		errorMessage = "";
		isConnectionOk = true;
		this.vtuLifeMainActivity = vtuLifeMainActivity;
		this.usn = usn;
		this.RESULT_URL = url;
		this.RESULT_REQUEST_MODE = requestMode;
		resultLoadedInterface = (ResultLoadedInterface) fragment;
		itemList = new ArrayList<ResultItem>();
		if (resultType)
			this.resultType = REVAVL_RESULT;
		else
			this.resultType = REGULAR_RESULT;
	}

	private String getResult(int index) throws JSONException {
		String result = currentResultJsonObject.getJSONArray(TAG_RESULT)
				.getString(index);
		if (result.equals("FAIL"))
			return "Result : FAIL";
		else if (result.equals("FIRST CLASS WITH DISTINCTION"))
			return "Result : FCD";
		else if (result.equals("FIRST CLASS"))
			return "Result : FC";
		else if (result.equals("SECOND CLASS"))
			return "Result : SC";
		return "Error";
	}

	protected String doInBackground(String... args) {
		if (SystemFeatureChecker.isInternetConnection(vtuLifeMainActivity)) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				params.add(new BasicNameValuePair("usn", usn));
				params.add(new BasicNameValuePair("resultType", Integer
						.toString(resultType)));
				currentResultJsonObject = jParser.makeHttpRequest(
						Settings.WEB_URL + RESULT_URL, "POST", params);
				try {
					if (currentResultJsonObject.getString(TAG_MESSAGE).equals(
							"success")) {
						ResultItem tempResultItem = new ResultItem();
						tempResultItem.tag = ResultAdapter.TYPE_STUDENT_DETAILS;
						tempResultItem.name = currentResultJsonObject
								.getString(TAG_NAME);
						tempResultItem.usn = currentResultJsonObject
								.getString(TAG_USN);
						itemList.add(tempResultItem);
						tempResultItem = new ResultItem();
						if (resultType == REGULAR_RESULT) {
							tempResultItem.tag = ResultAdapter.TYPE_RESULT;
							tempResultItem.percentage = "Percentage : "
									+ currentResultJsonObject.getJSONArray(
											TAG_PERCENTAGE).getString(0) + "%";
							tempResultItem.total = "Total : "
									+ currentResultJsonObject.getJSONArray(
											TAG_TOTAL).getString(0);
						} else {
							tempResultItem.tag = ResultAdapter.TYPE_RESULT_SINGLE;
						}
						tempResultItem.result = getResult(0);
						itemList.add(tempResultItem);

						JSONArray semesterJsonArray = currentResultJsonObject
								.getJSONArray(TAG_SEMESTER);

						JSONArray markTable = currentResultJsonObject
								.getJSONArray(TAG_MARK);

						for (int i = 0; RESULT_REQUEST_MODE == SINGLE_SEM ? i < 1
								: i < semesterJsonArray.length(); i++) {
							tempResultItem = new ResultItem();
							tempResultItem.tag = ResultAdapter.TYPE_SEMESTER;
							tempResultItem.semester = "Semester : "
									+ semesterJsonArray.getString(i);
							itemList.add(tempResultItem);
							if (i != 0) {
								tempResultItem = new ResultItem();
								tempResultItem.tag = ResultAdapter.TYPE_RESULT_SINGLE;
								tempResultItem.result = getResult(i);
								itemList.add(tempResultItem);
							}
							for (int j = 0; j < markTable.getJSONArray(i)
									.length(); j++) {
								JSONArray column = markTable.getJSONArray(i)
										.getJSONArray(j);
								tempResultItem = new ResultItem();
								tempResultItem.subjectName = column
										.getString(0);
								if (resultType == REGULAR_RESULT) {
									tempResultItem.tag = ResultAdapter.TYPE_SUBJECT_RESULT;
									tempResultItem.external = "External : "
											+ column.getString(1);
									tempResultItem.internal = "Internal : "
											+ column.getString(2);
									tempResultItem.subjectTotal = "Total : "
											+ column.getString(3);
									tempResultItem.subjectResult = column
											.getString(4);
								} else {
									tempResultItem.tag = ResultAdapter.TYPE_SUBJECT_REVAL_RESULT;
									tempResultItem.external = "Previous : "
											+ column.getString(1);
									tempResultItem.finalMark = "Final : "
											+ column.getString(2);
									tempResultItem.internal = "Internal   : "
											+ column.getString(3);
									tempResultItem.subjectTotal = "Total : "
											+ column.getString(4);
									tempResultItem.subjectResult = column
											.getString(5);
								}
								itemList.add(tempResultItem);
							}
						}
					} else {
						isConnectionOk = false;
						errorMessage = currentResultJsonObject
								.getString(TAG_MESSAGE);
					}
				} catch (JSONException e) {
					isConnectionOk = false;
					errorMessage = e.getMessage();
				}
			} catch (Exception e1) {
				isConnectionOk = false;
				errorMessage = e1.getMessage();
				return null;
			}

		} else {
			isConnectionOk = false;
			errorMessage = "Sorry, internet connection is not available.";
		}
		return null;
	}

	protected void onPostExecute(String file_url) {
		resultLoadedInterface.notifyResultLoaded(itemList, isConnectionOk,
				errorMessage, usn);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		resultLoadedInterface.notifyResultLoaded(itemList, false, "Canceled",
				usn);
	}
}
