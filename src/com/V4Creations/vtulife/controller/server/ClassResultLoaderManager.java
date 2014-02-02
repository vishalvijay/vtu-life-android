package com.V4Creations.vtulife.controller.server;

import java.util.ArrayList;

import android.content.Context;

import com.V4Creations.vtulife.model.ResultItem;
import com.V4Creations.vtulife.model.interfaces.ClassResultLoaderInterface;
import com.V4Creations.vtulife.model.interfaces.ResultLoadedInterface;
import com.V4Creations.vtulife.util.Settings;

public class ClassResultLoaderManager implements ResultLoadedInterface {
	String TAG = "ClassResultLoaderManager";
	private static final int MAX_RESULT_THREAD = 4, MAX_ERROR_COUNT = 8,
			MAX_NORMAL_USN = 180, MIN_DIPLOMA_USN = 400, MAX_DIPLOMA_USN = 420;
	private ClassResultLoaderInterface mClassResultLoaderInterface;
	private Context context;
	private String mClassUsn;
	private boolean isRevaluationResult = false, isCancelled = false,
			isLoading = false, isDeepResultSearch;
	private int mResultMode, mThreadCount = 0, mCurrentUsnCount = 0,
			mErrorCount = 0, mTotalSuccess = 0;

	public ClassResultLoaderManager(Context context,
			ClassResultLoaderInterface classResultLoaderInterface,
			String classUsn, boolean resultType) {
		this.context = context;
		mClassResultLoaderInterface = classResultLoaderInterface;
		mResultMode = Settings.isFullSemResult(this.context) ? ResultLoaderManager.MULTY_SEM
				: ResultLoaderManager.SINGLE_SEM;
		isDeepResultSearch = Settings.isDeepSearch(context);
		mClassUsn = classUsn;
		isRevaluationResult = resultType;
		startLoading();
	}

	private void startLoading() {
		isLoading = true;
		mClassResultLoaderInterface.onStartLoading();
		for (int i = 0; i < (Settings.isSortedResult(context) ? 1
				: MAX_RESULT_THREAD); i++)
			getResult();
	}

	private String generateUsn() {
		mCurrentUsnCount++;
		if (mCurrentUsnCount == MAX_NORMAL_USN
				|| (mErrorCount >= MAX_ERROR_COUNT && mCurrentUsnCount < MAX_NORMAL_USN)
				&& isEnggResult()) {
			mErrorCount = 0;
			mCurrentUsnCount = MIN_DIPLOMA_USN;
			int year = Integer.parseInt(mClassUsn.substring(3, 5));
			year++;
			String yearString;
			if (year < 10)
				yearString = "0" + Integer.toString(year);
			else
				yearString = Integer.toString(year);
			mClassUsn = mClassUsn.substring(0, 3) + yearString
					+ mClassUsn.substring(5, mClassUsn.length());
		}

		String usnNumber = mCurrentUsnCount + "";
		if (mCurrentUsnCount < 10)
			if (isEnggResult())
				usnNumber = "00" + mCurrentUsnCount;
			else
				usnNumber = "0" + mCurrentUsnCount;
		else if (mCurrentUsnCount < 100 && isEnggResult())
			usnNumber = "0" + mCurrentUsnCount;
		return mClassUsn + usnNumber;
	}

	private synchronized void getResult() {
		if (isCancelled)
			return;
		if ((mCurrentUsnCount >= MAX_DIPLOMA_USN && isEnggResult())
				|| (mCurrentUsnCount >= MAX_NORMAL_USN && !isEnggResult())) {
			stopLoading();
			return;
		}
		new ResultLoaderManager(context, this, generateUsn(),
				isRevaluationResult, mResultMode);
		mThreadCount++;
	}

	@Override
	public void onStartLoading() {
	}

	@Override
	public void onLoadingSuccess(ArrayList<ResultItem> resultItems, String usn) {
		if (isCancelled)
			return;
		mThreadCount--;
		mTotalSuccess++;
		mErrorCount = 0;
		mClassResultLoaderInterface.onLoadingSuccess(resultItems, usn);
		getResult();
	}

	@Override
	public void onLoadingFailure(String message, String trackMessage,
			int statusCode, String usn) {
		if (isCancelled)
			return;
		if (!message.equals("Result not available."))
			mClassResultLoaderInterface.onLoadingFailure(message, trackMessage,
					statusCode, usn);
		mThreadCount--;
		mErrorCount++;
		if (!isDeepResultSearch && mErrorCount >= MAX_ERROR_COUNT
				&& !(mCurrentUsnCount <= MAX_NORMAL_USN && isEnggResult())) {
			stopLoading();
		} else
			getResult();
	}

	private void stopLoading() {
		if (mThreadCount == 0 || isCancelled) {
			mClassResultLoaderInterface.onFinishLoading();
			isLoading = false;
		}
	}

	private boolean isEnggResult() {
		return mClassUsn.length() == 7;
	}

	public void cancel() {
		isCancelled = true;
		stopLoading();
	}

	public boolean isLoading() {
		return isLoading;
	}

	public String getClassUsn() {
		return mClassUsn;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public int getTotatlSuccessResult() {
		return mTotalSuccess;
	}
}
