package com.V4Creations.vtulife.model;

public class ResultItem {
	public static final int TYPE_STUDENT_DETAILS = 0, TYPE_RESULT = 1,
			TYPE_SEMESTER = 2, TYPE_SUBJECT_RESULT = 3,
			TYPE_SUBJECT_REVAL_RESULT = 4, TYPE_RESULT_SINGLE = 5,
			TYPE_MAX_COUNT = 6;
	public static String RESULT_FAIL = "Result : FAIL";

	private String usn, name, result, subjectName, subjectResult;
	private double percentage;
	private int type, semester, total, finalMark, external, internal,
			subjectTotal;

	public String getResult() {
		return "Result : " + result;
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
		return "Final : " + finalMark;
	}

	public void setFinalMark(int finalMark) {
		this.finalMark = finalMark;
	}

	public String getExternal() {
		return "External : " + external;
	}

	public void setExternal(int external) {
		this.external = external;
	}

	public String getPercentage() {
		return "Percentage : " + percentage + "%";
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public String getTotal() {
		return "Total : " + total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getSemester() {
		return "Semester : " + semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public String getSubjectTotal() {
		return "Total : " + subjectTotal;
	}

	public void setSubjectTotal(int subjectTotal) {
		this.subjectTotal = subjectTotal;
	}

	public String getInternal() {
		return "Internal : " + internal;
	}

	public void setInternal(int internal) {
		this.internal = internal;
	}
}
