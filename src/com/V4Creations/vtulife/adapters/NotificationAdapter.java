package com.V4Creations.vtulife.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.Notification;

public class NotificationAdapter extends ArrayAdapter<Notification> {

	public NotificationAdapter(Context context, int resource,
			List<Notification> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		if (view.getTag() == null) {
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mTypeImageView = (ImageView) view
					.findViewById(R.id.notificationTypeImageView);
			viewHolder.mTitleTextView = (TextView) view
					.findViewById(R.id.titleTextView);
			viewHolder.mMessageTextView = (TextView) view
					.findViewById(R.id.messageTextView);
			viewHolder.mTimeTextView = (TextView) view
					.findViewById(R.id.timeTextView);
			viewHolder.mDownloadButton = (Button) view
					.findViewById(R.id.downloadButton);
			view.setTag(viewHolder);
		}
		if (position % 2 == 0)
			view.setBackgroundResource(R.drawable.vtu_life_list_item_selector_even);
		else
			view.setBackgroundResource(R.drawable.vtu_life_list_item_selector_odd);
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		Notification notification = getItem(position);
		viewHolder.mTitleTextView.setText(notification.getTitleString());
		viewHolder.mMessageTextView.setText(notification.getMessageString());
		viewHolder.mTimeTextView.setText(getFormatedTimeStamb(notification
				.getTime()));
		int imageId;
		if (notification.isNormalNotification()) {
			imageId = notification.isNotificationSaw() ? R.drawable.notification_normal_disabled
					: R.drawable.notification_normal;
			viewHolder.mDownloadButton.setVisibility(View.GONE);
		} else {
			imageId = notification.isNotificationSaw() ? R.drawable.notification_download_disabled
					: R.drawable.notification_downlaod;
			viewHolder.mDownloadButton.setVisibility(View.VISIBLE);
		}
		viewHolder.mTypeImageView.setImageResource(imageId);
		return view;
	}

	private CharSequence getFormatedTimeStamb(long time) {
		return null;
	}

	private static class ViewHolder {
		public ImageView mTypeImageView;
		public TextView mTitleTextView, mMessageTextView, mTimeTextView;
		public Button mDownloadButton;
	}
}
