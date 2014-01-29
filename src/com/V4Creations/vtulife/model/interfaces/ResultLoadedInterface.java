package com.V4Creations.vtulife.model.interfaces;

import java.util.ArrayList;

import com.V4Creations.vtulife.model.ResultItem;

public interface ResultLoadedInterface {
	public void onStartLoading();

	public void onLoadingSuccess(ArrayList<ResultItem> resultItems, String usn);

	public void onLoadingFailure(String message, String trackMessage,
			int statusCode, String usn);
}
