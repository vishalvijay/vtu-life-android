package com.V4Creations.vtulife.model;

import java.util.ArrayList;

public class ResourceStackItem {
	public ArrayList<ResourceItem> mResourceItems;
	public String mUrl;
	public String mDirName;

	public ResourceStackItem(ArrayList<ResourceItem> resourceItem,
			String dirName, String url) {
		mResourceItems = resourceItem;
		mUrl = url;
		mDirName = dirName;
	}
}