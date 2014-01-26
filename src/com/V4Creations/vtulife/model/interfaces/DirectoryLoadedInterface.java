package com.V4Creations.vtulife.model.interfaces;

import java.util.ArrayList;

import org.json.JSONObject;

import com.V4Creations.vtulife.model.DirectoryListItem;

public interface DirectoryLoadedInterface {
	void notifyDirectoryLoaded(ArrayList<DirectoryListItem> itemList,
			boolean isConnectionOk, String errorMessage,JSONObject json);
}
