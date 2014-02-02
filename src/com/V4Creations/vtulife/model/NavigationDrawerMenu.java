package com.V4Creations.vtulife.model;

public class NavigationDrawerMenu {
	public String menuName;
	public int imageResourceId;
	public boolean isSelected = false;

	public NavigationDrawerMenu(String menuName, int imageResourceId) {
		this.menuName = menuName;
		this.imageResourceId = imageResourceId;
	}
}
