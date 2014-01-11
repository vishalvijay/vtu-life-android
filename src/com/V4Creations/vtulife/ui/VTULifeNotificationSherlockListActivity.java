package com.V4Creations.vtulife.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.adapters.NotificationAdapter;
import com.V4Creations.vtulife.db.VTULifeDataBase;
import com.V4Creations.vtulife.interfaces.NotificationFromDBListener;
import com.V4Creations.vtulife.model.VTULifeNotification;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.timroes.swipetodismiss.SwipeDismissList;
import de.timroes.swipetodismiss.SwipeDismissList.SwipeDirection;

public class VTULifeNotificationSherlockListActivity extends
		SherlockListActivity {
	private NotificationAdapter mNotificationAdapter;
	private ArrayList<VTULifeNotification> mNotifications;
	private SwipeDismissList mSwipeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.vtu_life_notification_activity_layout);
		setSupportProgressBarIndeterminateVisibility(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TextView noteTextView = (TextView) findViewById(R.id.noteTextView);
		noteTextView.setSelected(true);
		VTULifeDataBase.getInstance(getApplicationContext()).getNotifications(
				new NotificationFromDBListener() {

					@Override
					public void notificationCreated(
							ArrayList<VTULifeNotification> notifications) {
						setSupportProgressBarIndeterminateVisibility(false);
						mNotifications = notifications;
						mNotificationAdapter = new NotificationAdapter(
								VTULifeNotificationSherlockListActivity.this,
								mNotifications);
						setListAdapter(mNotificationAdapter);
						initSwipeToDelete();
					}
				});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mNotificationAdapter.getItem(position).toggelNotificationSaw();
		mNotificationAdapter.notifyDataSetChanged();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mNotificationAdapter.notifyDataSetChanged();
			}
		}, 500);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_clear:
			clearNotifications();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void clearNotifications() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure ?")
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								processClearNotification();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.setTitle("Clear all notifications");
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.show();
	}

	protected void processClearNotification() {
		if (VTULifeDataBase.getInstance(getApplicationContext())
				.clearAllNotifications()) {
			Crouton.makeText(this, "Notification cleared", Style.INFO).show();
			mNotifications.clear();
			mNotificationAdapter.notifyDataSetChanged();
		} else
			Toast.makeText(getApplicationContext(),
					"There is no notification to clear", Toast.LENGTH_SHORT)
					.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.clear_menu, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalyticsManager.startGoogleAnalyticsForActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalyticsManager.stopGoogleAnalyticsForActivity(this);
		mSwipeList.discardUndo();
	}

	private void initSwipeToDelete() {
		mNotificationAdapter = (NotificationAdapter) getListAdapter();
		SwipeDismissList.OnDismissCallback callback = new SwipeDismissList.OnDismissCallback() {
			public SwipeDismissList.Undoable onDismiss(AbsListView listView,
					final int position) {
				final VTULifeNotification deletedItem = mNotificationAdapter
						.getItem(position);
				mNotificationAdapter.remove(position);
				return new SwipeDismissList.Undoable() {
					public void undo() {
						mNotificationAdapter.insert(position, deletedItem);
					}

					public String getTitle() {
						return deletedItem.toString() + " deleted";
					}

					public void discard() {
						deletedItem.delete();
					}
				};
			}
		};
		mSwipeList = new SwipeDismissList(getListView(), callback);
		mSwipeList.setSwipeDirection(SwipeDirection.START);
	}
}
