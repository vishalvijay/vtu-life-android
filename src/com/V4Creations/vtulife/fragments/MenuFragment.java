package com.V4Creations.vtulife.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class MenuFragment extends SherlockFragment {

	VTULifeMainActivity vtuLifeMainActivity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
