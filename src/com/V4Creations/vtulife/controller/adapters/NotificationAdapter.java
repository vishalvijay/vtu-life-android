package com.V4Creations.vtulife.controller.adapters;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.VTULifeNotification;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;

public class NotificationAdapter extends
		SupportArrayAdapter<VTULifeNotification> {

	public NotificationAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getLayoutInflater().inflate(
					R.layout.notification_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mTypeImageView = (ImageView) convertView
					.findViewById(R.id.notificationTypeImageView);
			viewHolder.mTitleTextView = (TextView) convertView
					.findViewById(R.id.titleTextView);
			viewHolder.mMessageTextView = (TextView) convertView
					.findViewById(R.id.messageTextView);
			viewHolder.mTimeTextView = (TextView) convertView
					.findViewById(R.id.timeTextView);
			viewHolder.mDownloadImageButton = (ImageButton) convertView
					.findViewById(R.id.downloadImageButton);
			convertView.setTag(viewHolder);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		VTULifeNotification notification = getItem(position);
		viewHolder.mTitleTextView.setText(notification.getTitleString());
		viewHolder.mMessageTextView.setText(notification.getMessageString());
		viewHolder.mTimeTextView.setText(getFormatedTimeStamb(notification
				.getTime()));
		viewHolder.mDownloadImageButton
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						SystemFeatureChecker
								.rateAppOnPlayStore((Activity) getContext());
					}
				});
		int imageId;
		if (notification.isNormalNotification()) {
			imageId = notification.isNotificationSaw() ? R.drawable.ic_action_email_disabled
					: R.drawable.ic_action_email;
			viewHolder.mDownloadImageButton.setVisibility(View.GONE);
		} else {
			imageId = notification.isNotificationSaw() ? R.drawable.ic_action_download_disabled
					: R.drawable.ic_action_download;
			viewHolder.mDownloadImageButton.setVisibility(View.VISIBLE);
		}
		if (notification.isNotificationSaw()) {
			viewHolder.mDownloadImageButton.setEnabled(false);
			viewHolder.mTimeTextView
					.setTextColor(getColor(R.color.solid_white));
			viewHolder.mTitleTextView
					.setTextColor(getColor(R.color.solid_white));
			viewHolder.mMessageTextView
					.setTextColor(getColor(R.color.solid_white));
		} else {
			viewHolder.mDownloadImageButton.setEnabled(true);
			viewHolder.mTimeTextView.setTextColor(getColor(R.color.black));
			viewHolder.mTitleTextView
					.setTextColor(getColor(R.color.text_muted));
			viewHolder.mMessageTextView
					.setTextColor(getColor(R.color.text_muted));
		}
		viewHolder.mTypeImageView.setImageResource(imageId);
		int color = R.color.odd_color;
		if (position % 2 == 0)
			color = R.color.even_color;
		convertView.setBackgroundResource(color);
		return convertView;
	}

	private CharSequence getFormatedTimeStamb(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return DateFormat.getDateFormat(getContext())
				.format(calendar.getTime());
	}

	private static class ViewHolder {
		public ImageView mTypeImageView;
		public TextView mTitleTextView, mMessageTextView, mTimeTextView;
		public ImageButton mDownloadImageButton;
	}

	private int getColor(int resColor) {
		return getContext().getResources().getColor(resColor);
	}

}
