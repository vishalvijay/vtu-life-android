package com.V4Creations.vtulife.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.ResultItem;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;

public class ResultAdapter extends BaseAdapter {
	private ArrayList<ResultItem> itemList;
	private LayoutInflater mInflater;

	public static final int TYPE_STUDENT_DETAILS = 0;
	public static final int TYPE_RESULT = 1;
	public static final int TYPE_SEMESTER = 2;
	public static final int TYPE_SUBJECT_RESULT = 3;
	public static final int TYPE_SUBJECT_REVAL_RESULT = 4;
	public static final int TYPE_LOADING = 5;
	public static final int TYPE_RESULT_SINGLE = 6;
	private static final int TYPE_MAX_COUNT = 7;

	public ResultAdapter(VTULifeMainActivity vtuLifeMainActivity,
			ArrayList<ResultItem> itemList) {
		this.itemList = itemList;
		mInflater = (LayoutInflater) vtuLifeMainActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getItemViewType(int position) {
		return itemList.get(position).tag;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public ResultItem getItem(int position) {
		return (ResultItem) itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResultItem tempResultItem = getItem(position);
		int type = getItemViewType(position);
		ViewHolder viewHolder;
		switch (type) {
		case TYPE_STUDENT_DETAILS:
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.student_details_list_item, null);
				ViewHolder holder = new ViewHolder();
				holder.nameTextView = (TextView) convertView
						.findViewById(R.id.nameTextView);
				holder.usnTextView = (TextView) convertView
						.findViewById(R.id.usnTextView);
				convertView.setTag(holder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.nameTextView.setText(tempResultItem.name);
			viewHolder.usnTextView.setText(tempResultItem.usn);
			break;
		case TYPE_RESULT:
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.result_list_item, null);
				ViewHolder holder = new ViewHolder();
				holder.resultTextView = (TextView) convertView
						.findViewById(R.id.resultTextView);
				holder.percentageTextView = (TextView) convertView
						.findViewById(R.id.percentageTextView);
				holder.totalTextView = (TextView) convertView
						.findViewById(R.id.totalTextView);
				convertView.setTag(holder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			if (tempResultItem.result.equals("Result : FAIL")) {
				viewHolder.resultTextView.setTextColor(Color
						.parseColor("#ff0000"));
				viewHolder.percentageTextView.setTextColor(Color
						.parseColor("#ff0000"));
				viewHolder.totalTextView.setTextColor(Color
						.parseColor("#ff0000"));
			} else {
				viewHolder.resultTextView.setTextColor(Color
						.parseColor("#178304"));
				viewHolder.percentageTextView.setTextColor(Color
						.parseColor("#178304"));
				viewHolder.totalTextView.setTextColor(Color
						.parseColor("#178304"));
			}
			viewHolder.resultTextView.setText(tempResultItem.result);
			viewHolder.percentageTextView.setText(tempResultItem.percentage);
			viewHolder.totalTextView.setText(tempResultItem.total);
			break;
		case TYPE_SEMESTER:
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.semester_list_item,
						null);
				ViewHolder holder = new ViewHolder();
				holder.semesterTextView = (TextView) convertView
						.findViewById(R.id.semesterNumber);
				convertView.setTag(holder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.semesterTextView.setText(tempResultItem.semester);
			break;
		case TYPE_SUBJECT_RESULT:
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.subject_result_list_item, null);
				ViewHolder holder = new ViewHolder();
				holder.subjectNameTextView = (TextView) convertView
						.findViewById(R.id.subjectTextView);
				holder.externalTextView = (TextView) convertView
						.findViewById(R.id.externalTextView);
				holder.internalTextView = (TextView) convertView
						.findViewById(R.id.internalTextView);
				holder.subjectTotalTextView = (TextView) convertView
						.findViewById(R.id.subjectTotalTextView);
				holder.subjectResultTextView = (TextView) convertView
						.findViewById(R.id.subjectResultTextView);
				holder.subjectMarkRelativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.subjectResultRelativeLayout);
				convertView.setTag(holder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			if (position % 2 == 0)
				viewHolder.subjectMarkRelativeLayout.setBackgroundColor(Color
						.parseColor("#0d4b6e"));
			else
				viewHolder.subjectMarkRelativeLayout.setBackgroundColor(Color
						.parseColor("#0a3b57"));
			if (!tempResultItem.subjectResult.equals("P")) {
				viewHolder.subjectResultTextView.setTextColor(Color
						.parseColor("#ff0000"));
				viewHolder.subjectTotalTextView.setTextColor(Color
						.parseColor("#ff0000"));
			} else {
				viewHolder.subjectResultTextView.setTextColor(Color
						.parseColor("#04b304"));
				viewHolder.subjectTotalTextView.setTextColor(Color
						.parseColor("#04b304"));
			}
			viewHolder.subjectNameTextView.setText(tempResultItem.subjectName);
			viewHolder.externalTextView.setText(tempResultItem.external);
			viewHolder.internalTextView.setText(tempResultItem.internal);
			viewHolder.subjectTotalTextView
					.setText(tempResultItem.subjectTotal);
			viewHolder.subjectResultTextView
					.setText(tempResultItem.subjectResult);
			break;
		case TYPE_SUBJECT_REVAL_RESULT:
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.subject_reval_result_list_item, null);
				ViewHolder holder = new ViewHolder();
				holder.subjectNameRevalTextView = (TextView) convertView
						.findViewById(R.id.subjectRevalTextView);
				holder.previousExternalRevalTextView = (TextView) convertView
						.findViewById(R.id.previousExternalTextView);
				holder.finalExternalRevalTextView = (TextView) convertView
						.findViewById(R.id.finalExternalTextView);
				holder.internalRevalTextView = (TextView) convertView
						.findViewById(R.id.internalRevalTextView);
				holder.subjectTotalRevalTextView = (TextView) convertView
						.findViewById(R.id.subjectTotalRevalTextView);
				holder.subjectResultRevalTextView = (TextView) convertView
						.findViewById(R.id.subjectResultRevalTextView);
				holder.subjectMarkRevalRelativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.subjectRevalResultRelativeLayout);
				convertView.setTag(holder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			if (position % 2 == 0)
				viewHolder.subjectMarkRevalRelativeLayout
						.setBackgroundColor(Color.parseColor("#0d4b6e"));
			else
				viewHolder.subjectMarkRevalRelativeLayout
						.setBackgroundColor(Color.parseColor("#0a3b57"));
			if (!tempResultItem.subjectResult.equals("P")) {
				viewHolder.subjectResultRevalTextView.setTextColor(Color
						.parseColor("#ff0000"));
				viewHolder.subjectTotalRevalTextView.setTextColor(Color
						.parseColor("#ff0000"));
			} else {
				viewHolder.subjectResultRevalTextView.setTextColor(Color
						.parseColor("#04b304"));
				viewHolder.subjectTotalRevalTextView.setTextColor(Color
						.parseColor("#04b304"));
			}
			viewHolder.subjectNameRevalTextView
					.setText(tempResultItem.subjectName);
			viewHolder.previousExternalRevalTextView
					.setText(tempResultItem.external);
			viewHolder.finalExternalRevalTextView
					.setText(tempResultItem.finalMark);
			viewHolder.internalRevalTextView.setText(tempResultItem.internal);
			viewHolder.subjectTotalRevalTextView
					.setText(tempResultItem.subjectTotal);
			viewHolder.subjectResultRevalTextView
					.setText(tempResultItem.subjectResult);
			break;
		case TYPE_LOADING:
			if (convertView == null)
				convertView = mInflater.inflate(R.layout.loading_list_item,
						null);
			break;
		case TYPE_RESULT_SINGLE:
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.result_single_list_item, null);
				ViewHolder holder = new ViewHolder();
				holder.resultSingleTextView = (TextView) convertView
						.findViewById(R.id.resultSingleTextView);
				convertView.setTag(holder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			if (tempResultItem.result.equals("Result : FAIL"))
				viewHolder.resultSingleTextView.setTextColor(Color
						.parseColor("#ff0000"));
			else
				viewHolder.resultSingleTextView.setTextColor(Color
						.parseColor("#178304"));
			viewHolder.resultSingleTextView.setText(tempResultItem.result);
			break;
		}
		return convertView;
	}

	private static class ViewHolder {
		TextView nameTextView;
		TextView usnTextView;
		TextView resultTextView;
		TextView percentageTextView;
		TextView totalTextView;
		TextView semesterTextView;
		TextView subjectNameTextView;
		TextView externalTextView;
		TextView internalTextView;
		TextView subjectTotalTextView;
		TextView subjectResultTextView;
		RelativeLayout subjectMarkRelativeLayout;
		TextView subjectNameRevalTextView;
		TextView previousExternalRevalTextView;
		TextView finalExternalRevalTextView;
		TextView internalRevalTextView;
		TextView subjectTotalRevalTextView;
		TextView subjectResultRevalTextView;
		RelativeLayout subjectMarkRevalRelativeLayout;
		TextView resultSingleTextView;
	}
}
