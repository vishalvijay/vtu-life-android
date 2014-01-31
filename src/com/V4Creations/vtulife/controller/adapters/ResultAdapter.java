package com.V4Creations.vtulife.controller.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.model.ResultItem;

public class ResultAdapter extends ArrayAdapter<ResultItem> {
	private LayoutInflater mInflater;

	public ResultAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getType();
	}

	@Override
	public int getViewTypeCount() {
		return ResultItem.TYPE_MAX_COUNT;
	}

	@SuppressLint("NewApi")
	@Override
	public void addAll(ResultItem... items) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.addAll(items);
		} else {
			for (ResultItem element : items) {
				super.add(element);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		switch (getItemViewType(position)) {
		case ResultItem.TYPE_STUDENT_DETAILS:
			convertView = getStudentDetails(convertView, position);
			break;
		case ResultItem.TYPE_RESULT:
			convertView = getResult(convertView, position);
			break;
		case ResultItem.TYPE_SEMESTER:
			convertView = getSemester(convertView, position);
			break;
		case ResultItem.TYPE_SUBJECT_RESULT:
			convertView = getSubjectResult(convertView, position);
			break;
		case ResultItem.TYPE_SUBJECT_REVAL_RESULT:
			convertView = getSubjectRevalResult(convertView, position);
			break;
		case ResultItem.TYPE_RESULT_SINGLE:
			convertView = getSingleResult(convertView, position);
			break;
		}
		return convertView;
	}

	private View getSingleResult(View convertView, int position) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.result_single_list_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.resultSingleTextView = (TextView) convertView
					.findViewById(R.id.resultSingleTextView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		ResultItem resultItem = getItem(position);
		int color = getColor(R.color.pass_color);
		int bgColor = R.color.bg_result_pass;
		if (resultItem.getResult().equals(ResultItem.RESULT_FAIL)) {
			color = getColor(R.color.fail_color);
			bgColor = R.color.bg_result_fail;
		}
		viewHolder.resultSingleTextView.setTextColor(color);
		viewHolder.resultSingleTextView.setText(resultItem.getResult());
		convertView.setBackgroundResource(bgColor);
		return convertView;
	}

	private View getSubjectRevalResult(View convertView, int position) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.subject_reval_result_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.subjectNameRevalTextView = (TextView) convertView
					.findViewById(R.id.subjectRevalTextView);
			viewHolder.previousExternalRevalTextView = (TextView) convertView
					.findViewById(R.id.previousExternalTextView);
			viewHolder.finalExternalRevalTextView = (TextView) convertView
					.findViewById(R.id.finalExternalTextView);
			viewHolder.internalRevalTextView = (TextView) convertView
					.findViewById(R.id.internalRevalTextView);
			viewHolder.subjectTotalRevalTextView = (TextView) convertView
					.findViewById(R.id.subjectTotalRevalTextView);
			viewHolder.subjectResultRevalTextView = (TextView) convertView
					.findViewById(R.id.subjectResultRevalTextView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		ResultItem resultItem = getItem(position);
		int color = getColor(R.color.pass_color);
		if (!resultItem.getSubjectResult().equals("P"))
			color = getColor(R.color.fail_color);
		viewHolder.subjectResultRevalTextView.setTextColor(color);
		viewHolder.subjectTotalRevalTextView.setTextColor(color);
		viewHolder.subjectNameRevalTextView
				.setText(resultItem.getSubjectName());
		viewHolder.previousExternalRevalTextView.setText(resultItem
				.getExternal());
		viewHolder.finalExternalRevalTextView
				.setText(resultItem.getFinalMark());
		viewHolder.internalRevalTextView.setText(resultItem.getInternal());
		viewHolder.subjectTotalRevalTextView.setText(resultItem
				.getSubjectTotal());
		viewHolder.subjectResultRevalTextView.setText(resultItem
				.getSubjectResult());
		int bgColor = R.color.odd_color;
		if (position % 2 == 0)
			bgColor = R.color.even_color;
		convertView.setBackgroundResource(bgColor);
		return convertView;
	}

	private View getSubjectResult(View convertView, int position) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.subject_result_list_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.subjectNameTextView = (TextView) convertView
					.findViewById(R.id.subjectTextView);
			viewHolder.externalTextView = (TextView) convertView
					.findViewById(R.id.externalTextView);
			viewHolder.internalTextView = (TextView) convertView
					.findViewById(R.id.internalTextView);
			viewHolder.subjectTotalTextView = (TextView) convertView
					.findViewById(R.id.subjectTotalTextView);
			viewHolder.subjectResultTextView = (TextView) convertView
					.findViewById(R.id.subjectResultTextView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		ResultItem resultItem = getItem(position);
		int color = getColor(R.color.pass_color);
		if (!resultItem.getSubjectResult().equals("P"))
			color = getColor(R.color.fail_color);
		viewHolder.subjectResultTextView.setTextColor(color);
		viewHolder.subjectTotalTextView.setTextColor(color);
		viewHolder.subjectNameTextView.setText(resultItem.getSubjectName());
		viewHolder.externalTextView.setText(resultItem.getExternal());
		viewHolder.internalTextView.setText(resultItem.getInternal());
		viewHolder.subjectTotalTextView.setText(resultItem.getSubjectTotal());
		viewHolder.subjectResultTextView.setText(resultItem.getSubjectResult());
		int bgColor = R.color.odd_color;
		if (position % 2 == 0)
			bgColor = R.color.even_color;
		convertView.setBackgroundResource(bgColor);
		return convertView;
	}

	private View getSemester(View convertView, int position) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.semester_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.semesterTextView = (TextView) convertView
					.findViewById(R.id.semesterNumber);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.semesterTextView.setText(getItem(position).getSemester());
		return convertView;
	}

	private View getResult(View convertView, int position) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.result_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.resultTextView = (TextView) convertView
					.findViewById(R.id.resultTextView);
			viewHolder.percentageTextView = (TextView) convertView
					.findViewById(R.id.percentageTextView);
			viewHolder.totalTextView = (TextView) convertView
					.findViewById(R.id.totalTextView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		ResultItem resultItem = getItem(position);
		int color = getColor(R.color.pass_color);
		int bgColor = R.color.bg_result_pass;
		if (resultItem.getResult().equals(ResultItem.RESULT_FAIL)) {
			color = getColor(R.color.fail_color);
			bgColor = R.color.bg_result_fail;
		}
		viewHolder.resultTextView.setTextColor(color);
		viewHolder.percentageTextView.setTextColor(color);
		viewHolder.totalTextView.setTextColor(color);
		viewHolder.resultTextView.setText(resultItem.getResult());
		viewHolder.percentageTextView.setText(resultItem.getPercentage());
		viewHolder.totalTextView.setText(resultItem.getTotal());
		convertView.setBackgroundResource(bgColor);
		return convertView;
	}

	private int getColor(int resColor) {
		return getContext().getResources().getColor(resColor);
	}

	private View getStudentDetails(View convertView, int position) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.student_details_list_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.nameTextView);
			viewHolder.usnTextView = (TextView) convertView
					.findViewById(R.id.usnTextView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		ResultItem resultItem = getItem(position);
		viewHolder.nameTextView.setText(resultItem.getName());
		viewHolder.usnTextView.setText(resultItem.getUsn());
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
		TextView subjectNameRevalTextView;
		TextView previousExternalRevalTextView;
		TextView finalExternalRevalTextView;
		TextView internalRevalTextView;
		TextView subjectTotalRevalTextView;
		TextView subjectResultRevalTextView;
		TextView resultSingleTextView;
	}
}
