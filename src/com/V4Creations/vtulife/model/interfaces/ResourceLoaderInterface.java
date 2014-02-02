package com.V4Creations.vtulife.model.interfaces;

import com.V4Creations.vtulife.model.ResourceStackItem;

public interface ResourceLoaderInterface {
	public void onStartLoading();

	public void onLoadingSuccess(ResourceStackItem resourceStackItem);

	public void onLoadingFailure(String message, String trackMessage);
}
