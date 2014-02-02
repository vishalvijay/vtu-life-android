package com.V4Creations.vtulife.util;

import java.util.ArrayList;

import android.view.Menu;
import android.view.MenuItem;

import com.V4Creations.vtulife.R;

public class NavigationMenuManager {
	private static final int[] MENU_RESOURCE_IDS = { R.id.menu_back,
			R.id.menu_clear, R.id.menu_forward, R.id.menu_refresh };
	private static final int[] NAV_MENU_RESOURCE_IDS = { R.id.menu_settings,R.id.menu_notifications };
	private ArrayList<Integer> mBackup;
	private NavigationMenuManagerListener navigationMenuManagerListener;

	public NavigationMenuManager(
			NavigationMenuManagerListener navigationMenuManagerListener) {
		this.navigationMenuManagerListener = navigationMenuManagerListener;
		mBackup = new ArrayList<Integer>();
	}

	public void toggleNavMenu(Menu menu, boolean isShowing) {
		if (menu == null)
			return;
		if (isShowing) {
			for (int menuId : MENU_RESOURCE_IDS) {
				MenuItem menuitem = menu.findItem(menuId);
				if (menuitem != null) {
					menuitem.setVisible(false);
					mBackup.add(menuId);
				}
			}
		} else {
			for (int menuId : mBackup) {
				MenuItem menuitem = menu.findItem(menuId);
				if (menuitem != null)
					menuitem.setVisible(true);
			}
			mBackup.clear();
		}
		for (int menuId : NAV_MENU_RESOURCE_IDS) {
			MenuItem menuitem = menu.findItem(menuId);
			if (menuitem != null)
				menuitem.setVisible(isShowing);
		}
		navigationMenuManagerListener.reflectNavChange(isShowing);
	}

	public static interface NavigationMenuManagerListener {
		public void reflectNavChange(boolean isShowing);
	}
}
