package com.V4Creations.vtulife.interfaces;

import java.util.ArrayList;

import org.json.JSONObject;

import com.V4Creations.vtulife.util.DirectoryListItem;

public interface DirectoryLoadedInterface {
	void notifyDirectoryLoaded(ArrayList<DirectoryListItem> itemList,
			boolean isConnectionOk, String errorMessage,JSONObject json);
}
