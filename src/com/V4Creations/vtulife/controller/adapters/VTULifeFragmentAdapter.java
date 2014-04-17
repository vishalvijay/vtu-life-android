package com.V4Creations.vtulife.controller.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.V4Creations.vtulife.model.ActionBarStatus;

public class VTULifeFragmentAdapter extends FragmentPagerAdapter {
	ArrayList<Fragment> mVtuLifeFragments;
	Context context;

	public VTULifeFragmentAdapter(FragmentManager fm,
			ArrayList<Fragment> vtuLifeFragments, Context context) {
		super(fm);
		mVtuLifeFragments = vtuLifeFragments;
		this.context = context;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ((FragmentInfo) mVtuLifeFragments.get(position))
				.getTitle(context);
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
		public String getTitle(Context context);

		public ActionBarStatus getActionBarStatus();
	}
}
