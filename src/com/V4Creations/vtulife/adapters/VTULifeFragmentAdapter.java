package com.V4Creations.vtulife.adapters;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.V4Creations.vtulife.util.ActionBarStatus;

public class VTULifeFragmentAdapter extends FragmentPagerAdapter {
	ArrayList<Fragment> mVtuLifeFragments;

	public VTULifeFragmentAdapter(FragmentManager fm,
			ArrayList<Fragment> vtuLifeFragments) {
		super(fm);
		mVtuLifeFragments = vtuLifeFragments;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ((FragmentInfo) mVtuLifeFragments.get(position)).getTitle();
	}

	@Override
	public Fragment getItem(int position) {
		return mVtuLifeFragments.get(position);
	}

	@Override
	public int getCount() {
		return mVtuLifeFragments.size();
	}
	

	public interface FragmentInfo {
		public String getTitle();
		public ActionBarStatus getActionBarStatus();
	}
}
