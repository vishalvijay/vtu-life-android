package com.V4Creations.vtulife.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.V4Creations.vtulife.R;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class PreferencesActivity extends SherlockPreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences_xml);
	}

/*	@Override
	@Deprecated
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference.toString().equals(
				getString(R.string.click_to_delete_all_data)))
			showClearAllDialog();
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	protected void showClearAllDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.do_you_want_to_clear_all_data)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								CampFoodManagerDataBase db = new CampFoodManagerDataBase(
										getApplicationContext());
								db.clearAllData();
								db.close();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.clear_all_date);
		alert.setIcon(R.drawable.ic_action_delete_light);
		alert.show();
	}*/
}
