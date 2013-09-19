package com.V4Creations.vtulife.model;

public class ReferredLibrary {
	private String mName;
	private String mUrl;
	private boolean isUrlVisible;

	public ReferredLibrary(String name, String url) {
		mName = name;
		mUrl = url;
		setUrlVisible(false);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public boolean isUrlVisible() {
		return isUrlVisible;
	}

	public void setUrlVisible(boolean isUrlVisible) {
		this.isUrlVisible = isUrlVisible;
	}
}
