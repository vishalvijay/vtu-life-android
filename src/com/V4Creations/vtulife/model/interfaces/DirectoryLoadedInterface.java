package com.V4Creations.vtulife.model.interfaces;

import java.util.ArrayList;

import org.json.JSONObject;

import com.V4Creations.vtulife.model.DirectoryItem;

public interface DirectoryLoadedInterface {
	void notifyDirectoryLoaded(ArrayList<DirectoryItem> itemList,
			boolean isConnectionOk, String errorMessage,JSONObject json);
}
