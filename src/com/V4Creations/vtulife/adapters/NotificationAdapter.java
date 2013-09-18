package com.V4Creations.vtulife.adapters;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.VTULifeNotification;

public class NotificationAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<VTULifeNotification> mNotifications;
	private LayoutInflater mInflater;

	public NotificationAdapter(Context context,
			ArrayList<VTULifeNotification> notifications) {
		mContext = context;
		mNotifications = notifications;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mNotifications.size();
	}

	@Override
	public VTULifeNotification getItem(int position) {
		return mNotifications.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.notification_list_item,
					null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mTypeImageView = (ImageView) convertView
					.findViewById(R.id.notificationTypeImageView);
			viewHolder.mTitleTextView = (TextView) convertView
					.findViewById(R.id.titleTextView);
			viewHolder.mMessageTextView = (TextView) convertView
					.findViewById(R.id.messageTextView);
			viewHolder.mTimeTextView = (TextView) convertView
					.findViewById(R.id.timeTextView);
			viewHolder.mDownloadButton = (Button) convertView
					.findViewById(R.id.downloadButton);
			convertView.setTag(viewHolder);
		}
		if (position % 2 == 0)
			convertView
					.setBackgroundResource(R.drawable.vtu_life_list_item_selector_even);
		else
			convertView
					.setBackgroundResource(R.drawable.vtu_life_list_item_selector_odd);
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		VTULifeNotification notification = getItem(position);
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
		if (notification.isNotificationSaw()) {
			viewHolder.mTimeTextView.setTextColor(mContext.getResources()
					.getColor(R.color.gray));
			viewHolder.mTitleTextView.setTextColor(mContext.getResources()
					.getColor(R.color.gray));
			viewHolder.mMessageTextView.setTextColor(mContext.getResources()
					.getColor(R.color.gray));
		} else {
			viewHolder.mTimeTextView.setTextColor(mContext.getResources()
					.getColor(android.R.color.white));
			viewHolder.mTitleTextView.setTextColor(mContext.getResources()
					.getColor(android.R.color.white));
			viewHolder.mMessageTextView.setTextColor(mContext.getResources()
					.getColor(android.R.color.white));
		}
		viewHolder.mTypeImageView.setImageResource(imageId);
		return convertView;
	}

	private CharSequence getFormatedTimeStamb(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return DateFormat.getDateFormat(mContext).format(calendar.getTime());
	}

	private static class ViewHolder {
		public ImageView mTypeImageView;
		public TextView mTitleTextView, mMessageTextView, mTimeTextView;
		public Button mDownloadButton;
	}
}
