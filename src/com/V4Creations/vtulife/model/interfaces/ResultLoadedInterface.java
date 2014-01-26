package com.V4Creations.vtulife.model.interfaces;

import java.util.ArrayList;

import com.V4Creations.vtulife.model.ResultItem;

public interface ResultLoadedInterface {
	void notifyResultLoaded(ArrayList<ResultItem> itemList,
			boolean isConnectionOk, String errorMessage,String usn);
}
