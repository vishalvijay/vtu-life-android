package com.V4Creations.vtulife.model;

import android.content.Context;

import com.V4Creations.vtulife.R;

public class ResultItem {
	public static final int TYPE_STUDENT_DETAILS = 0, TYPE_RESULT = 1,
			TYPE_SEMESTER = 2, TYPE_SUBJECT_RESULT = 3,
			TYPE_SUBJECT_REVAL_RESULT = 4, TYPE_RESULT_SINGLE = 5,
			TYPE_MAX_COUNT = 6;

	private String usn, name, result, subjectName, subjectResult;
	private double percentage;
	private int type, semester, total, finalMark, external, internal,
			subjectTotal;
	private Context context;

	public ResultItem(Context context) {
		this.context = context;
	}

	public String getResult() {
		return context.getString(R.string.li_result, result);
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectResult() {
		return subjectResult;
	}

	public void setSubjectResult(String subjectResult) {
		this.subjectResult = subjectResult;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsn() {
		return usn;
	}

	public void setUsn(String usn) {
		this.usn = usn;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFinalMark() {
		return context.getString(R.string.li_final, finalMark);
	}

	public void setFinalMark(int finalMark) {
		this.finalMark = finalMark;
	}

	public String getExternal() {
		return context.getString(R.string.li_external, external);
	}

	public void setExternal(int external) {
		this.external = external;
	}

	public String getPercentage() {
		return context.getString(R.string.li_percentage, percentage);
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public String getTotal() {
		return context.getString(R.string.li_total, total);
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getSemester() {
		return context.getString(R.string.li_semester, semester);
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public String getSubjectTotal() {
		return context.getString(R.string.li_total, subjectTotal);
	}

	public void setSubjectTotal(int subjectTotal) {
		this.subjectTotal = subjectTotal;
	}

	public String getInternal() {
		return context.getString(R.string.li_internal, internal);
	}

	public void setInternal(int internal) {
		this.internal = internal;
	}

	public String getFailString() {
		return context.getString(R.string.li_result,
				context.getString(R.string.fail));
	}
}
