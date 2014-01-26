package com.V4Creations.vtulife.view.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.NotificationAdapter;
import com.V4Creations.vtulife.controller.db.VTULifeDataBase;
import com.V4Creations.vtulife.model.VTULifeNotification;
import com.V4Creations.vtulife.model.interfaces.NotificationFromDBListener;
import com.V4Creations.vtulife.util.GoogleAnalyticsManager;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.timroes.swipetodismiss.SwipeDismissList;
import de.timroes.swipetodismiss.SwipeDismissList.SwipeDirection;

public class VTULifeNotificationActivity extends ActionBarActivity {
	private NotificationAdapter mNotificationAdapter;
	private ArrayList<VTULifeNotification> mNotifications;
	private SwipeDismissList mSwipeList;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_notification);
		setSupportProgressBarIndeterminateVisibility(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TextView noteTextView = (TextView) findViewById(R.id.noteTextView);
		initViews();
		noteTextView.setSelected(true);
		VTULifeDataBase.getInstance(getApplicationContext()).getNotifications(
				new NotificationFromDBListener() {

					@Override
					public void notificationListCreated(
							ArrayList<VTULifeNotification> notifications) {
						setSupportProgressBarIndeterminateVisibility(false);
						mNotifications = notifications;
						mNotificationAdapter = new NotificationAdapter(
								VTULifeNotificationActivity.this,
								mNotifications);

						mListView.setAdapter(mNotificationAdapter);
						initSwipeToDelete();
					}
				});
	}

	private void initViews() {
		initListView();
	}

	private void initListView() {
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setEmptyView(findViewById(R.id.emptyView));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mNotificationAdapter.getItem(position).toggelNotificationSaw();
				mNotificationAdapter.notifyDataSetChanged();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mNotificationAdapter.notifyDataSetChanged();
					}
				}, 500);
			}
		});
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
		MenuInflater inflater = getMenuInflater();
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
		mSwipeList = new SwipeDismissList(mListView, callback);
		mSwipeList.setSwipeDirection(SwipeDirection.START);
	}
}
