package com.V4Creations.vtulife.controller.server;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.ResultItem;
import com.V4Creations.vtulife.model.interfaces.ResultLoadedInterface;
import com.V4Creations.vtulife.util.VTULifeRestClient;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ResultLoaderManager extends JsonHttpResponseHandler {

	public static final String RESULT_FROM_VTU = "/result/result_json.php";
	public static final String PARAM_USN = "usn",
			PARAM_RESULT_TYPE = "resultType";

	public static final String KEY_NAME = "name", KEY_USN = "usn",
			KEY_SEMESTER = "semesters", KEY_RESULT = "result",
			KEY_PERCENTAGE = "percentage", KEY_TOTAL = "total",
			KEY_MARK = "mark", KEY_MESSAGE = "message";

	public static final int SINGLE_SEM = 0, MULTY_SEM = 1;

	public static final int REVAVL_RESULT = 1;
	public static final int REGULAR_RESULT = 0;

	private Context context;
	private String mUsn;
	private int mResultType = REGULAR_RESULT, mResultMode = SINGLE_SEM;
	private ResultLoadedInterface mResultLoadedInterface;
	private boolean isCancelled = false;
	private boolean isLoading = false;

	public ResultLoaderManager(Context context,
			ResultLoadedInterface resultLoadedInterface) {
		this.context = context;
		mResultLoadedInterface = resultLoadedInterface;
	}

	public void getResult(String usn, boolean resultType, int resultMode) {
		if (isCancelled())
			return;
		isLoading = true;
		mResultType = getResultType(resultType);
		mUsn = usn.toUpperCase(Locale.getDefault());
		mResultMode = resultMode;
		if (SystemFeatureChecker.isInternetConnection(context)) {
			mResultLoadedInterface.onStartLoading();
			VTULifeRestClient.getResult(mUsn, mResultType, this);
		} else
			onFailure(
					405,
					new Throwable(context
							.getString(R.string.internet_not_available)),
					new JSONObject());
	}

	private int getResultType(boolean resultType) {
		return resultType ? REVAVL_RESULT : REGULAR_RESULT;
	}

	@Override
	public void onSuccess(JSONObject response) {
		if (isCancelled)
			return;
		mResultLoadedInterface.onLoadingSuccess(parseJson(response), mUsn);
		cancel();
	}

	@Override
	public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
		if (isCancelled)
			return;
		String message = context.getString(R.string.default_connection_error);
		try {
			if (statusCode == 405 || statusCode == 401 || statusCode == 403)
				message = errorResponse.getString(VTULifeRestClient.KEY_ERROR);
		} catch (JSONException e1) {
		}
		mResultLoadedInterface.onLoadingFailure(message, e.getMessage() + "");
		cancel();
	}

	private ArrayList<ResultItem> parseJson(JSONObject jsonObject) {
		ArrayList<ResultItem> resultItems = new ArrayList<ResultItem>();
		try {
			resultItems.add(getStudentDetails(jsonObject));
			resultItems.add(getResultDetails(jsonObject));

			JSONArray semesterJsonArray = jsonObject.getJSONArray(KEY_SEMESTER);
			JSONArray markTable = jsonObject.getJSONArray(KEY_MARK);

			for (int i = 0; mResultMode == SINGLE_SEM ? i < 1
					: i < semesterJsonArray.length(); i++) {
				resultItems.add(getSemester(semesterJsonArray, i));
				if (i != 0)
					resultItems.add(getSingleResult(jsonObject, i));
				for (int j = 0; j < markTable.getJSONArray(i).length(); j++)
					resultItems.add(getSubject(markTable.getJSONArray(i)
							.getJSONArray(j)));
			}
		} catch (JSONException e) {
		}
		return resultItems;
	}

	private ResultItem getSubject(JSONArray subjectJsonArray)
			throws JSONException {
		ResultItem resultItem = new ResultItem();
		resultItem.setSubjectName(subjectJsonArray.getString(0));
		if (mResultType == REGULAR_RESULT) {
			resultItem.setType(ResultItem.TYPE_SUBJECT_RESULT);
			resultItem.setExternal(subjectJsonArray.getInt(1));
			resultItem.setInternal(subjectJsonArray.getInt(2));
			resultItem.setSubjectTotal(subjectJsonArray.getInt(3));
			resultItem.setSubjectResult(subjectJsonArray.getString(4));
		} else {
			resultItem.setType(ResultItem.TYPE_SUBJECT_REVAL_RESULT);
			resultItem.setExternal(subjectJsonArray.getInt(1));
			resultItem.setFinalMark(subjectJsonArray.getInt(2));
			resultItem.setInternal(subjectJsonArray.getInt(3));
			resultItem.setSubjectTotal(subjectJsonArray.getInt(4));
			resultItem.setSubjectResult(subjectJsonArray.getString(5));
		}
		return resultItem;
	}

	private ResultItem getSingleResult(JSONObject jsonObject, int index)
			throws JSONException {
		ResultItem resultItem = new ResultItem();
		resultItem.setType(ResultItem.TYPE_RESULT_SINGLE);
		resultItem.setResult(getResult(jsonObject.getJSONArray(KEY_RESULT)
				.getString(index)));
		return resultItem;
	}

	private ResultItem getSemester(JSONArray semesterJsonArray, int index)
			throws JSONException {
		ResultItem resultItem = new ResultItem();
		resultItem.setType(ResultItem.TYPE_SEMESTER);
		resultItem.setSemester(semesterJsonArray.getInt(index));
		return resultItem;
	}

	private ResultItem getResultDetails(JSONObject jsonObject)
			throws JSONException {
		ResultItem resultItem = new ResultItem();
		if (mResultType == REGULAR_RESULT) {
			resultItem.setType(ResultItem.TYPE_RESULT);
			resultItem.setPercentage(jsonObject.getJSONArray(KEY_PERCENTAGE)
					.getDouble(0));
			resultItem.setTotal(jsonObject.getJSONArray(KEY_TOTAL).getInt(0));
		} else {
			resultItem.setType(ResultItem.TYPE_RESULT_SINGLE);
		}
		resultItem.setResult(getResult(jsonObject.getJSONArray(KEY_RESULT)
				.getString(0)));
		return resultItem;
	}

	private ResultItem getStudentDetails(JSONObject jsonObject)
			throws JSONException {
		ResultItem resultItem = new ResultItem();
		resultItem.setType(ResultItem.TYPE_STUDENT_DETAILS);
		resultItem.setName(jsonObject.getString(KEY_NAME));
		resultItem.setUsn(jsonObject.getString(KEY_USN));
		return resultItem;
	}

	private String getResult(String result) throws JSONException {
		if (result.equals("FAIL"))
			return "FAIL";
		else if (result.equals("FIRST CLASS WITH DISTINCTION"))
			return "FCD";
		else if (result.equals("FIRST CLASS"))
			return "FC";
		else if (result.equals("SECOND CLASS"))
			return "SC";
		return "Error";
	}

	public boolean isCancelled() {
		if (isCancelled)
			throw new IllegalStateException(
					"Result loader alrady cancelled. Create new and try");
		return isCancelled;
	}

	public void cancel() {
		isLoading = false;
		isCancelled = true;
	}

	public boolean isLoading() {
		return isLoading;
	}
}
