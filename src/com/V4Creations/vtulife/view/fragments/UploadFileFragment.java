package com.V4Creations.vtulife.view.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.afilechooser.utils.FileUtils;
import com.V4Creations.vtulife.controller.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;

import de.keyboardsurfer.android.widget.crouton.Style;

public class UploadFileFragment extends Fragment implements TextWatcher,
		FragmentInfo {
	String TAG = "UploadFileFragment";

	private VTULifeMainActivity activity;
	private EditText descriptionEditText, departmentEditText, subjectEditText;
	private TextView fileTextView;
	private Button browseFileButton;
	private Button sendButton;
	private String filePathString;
	private final int BROUSER_FILE = 1, SEND_EMAIL = 2;
	private final String[] allowedFileTypeStrings = { "pdf", "doc", "docx",
			"ppt", "pptx", "zip", "rar", "txt" };
	private ActionBarStatus mActionBarStatus;

	public UploadFileFragment() {
		mActionBarStatus = new ActionBarStatus();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.fragemnt_upload_file, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		filePathString = "";
	}

	private void initView() {
		browseFileButton = (Button) getView().findViewById(
				R.id.browseFileButton);
		sendButton = (Button) getView().findViewById(R.id.sendButton);
		fileTextView = (TextView) getView().findViewById(R.id.fileTextView);
		subjectEditText = (EditText) getView().findViewById(
				R.id.subjectEditText);
		departmentEditText = (EditText) getView().findViewById(
				R.id.departmentEditText);
		descriptionEditText = (EditText) getView().findViewById(
				R.id.descriptionEditText);
		subjectEditText.addTextChangedListener(this);
		departmentEditText.addTextChangedListener(this);
		descriptionEditText.addTextChangedListener(this);
		browseFileButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getFile();
			}
		});
		sendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				send();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
			switch (requestCode) {
			case BROUSER_FILE:
				filePathString = data.getData().getPath();
				fileTextView.setText(filePathString);
				break;
			}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.clear_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_clear:
			clearAllData();
			return true;
		}
		return false;
	}

	private void clearAllData() {
		filePathString = "";
		fileTextView.setText(R.string.select_a_file);
		subjectEditText.setText("");
		departmentEditText.setText("");
		descriptionEditText.setText("");
	}

	public boolean isEditTextEmpty(EditText editText) {
		if (editText.getText().toString().trim().equals(""))
			return true;
		return false;
	}

	public void sendMail() {
		Uri uri = Uri.parse("file://" + filePathString);
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "someone@vtulife.com" });
		i.putExtra(Intent.EXTRA_SUBJECT,
				getString(R.string.share_notes_mail_subject));
		String subjectString = subjectEditText.getText().toString();
		String departmentString = departmentEditText.getText().toString();
		String descriptionString = descriptionEditText.getText().toString();
		String message = String.format(
				getString(R.string.share_notes_mail_body), subjectString,
				departmentString, descriptionString);
		i.putExtra(Intent.EXTRA_TEXT, message);
		i.putExtra(Intent.EXTRA_STREAM, uri);
		try {
			startActivityForResult(
					Intent.createChooser(i, getString(R.string.send_email)),
					SEND_EMAIL);
		} catch (android.content.ActivityNotFoundException ex) {
			activity.showCrouton(R.string.email_client_missing, Style.INFO,
					true);
		}
	}

	public void getFile() {
		Intent target = FileUtils.createGetContentIntent();
		target.setPackage("com.V4Creations.vtulife");
		List<String> extent = new ArrayList<String>();
		Collections.addAll(extent, allowedFileTypeStrings);
		target.putStringArrayListExtra("ext", (ArrayList<String>) extent);
		Intent intent = Intent.createChooser(target,
				getString(R.string.select_a_file));
		try {
			startActivityForResult(intent, BROUSER_FILE);
		} catch (ActivityNotFoundException e) {
			activity.showCrouton(R.string.file_browser_not_found, Style.INFO,
					true);
		}
	}

	public void send() {
		if (!filePathString.equals("") && !isEditTextEmpty(departmentEditText)
				&& !isEditTextEmpty(descriptionEditText)
				&& !isEditTextEmpty(subjectEditText))
			sendMail();
		else {
			activity.showCrouton(R.string.provide_all_details, Style.CONFIRM,
					true);
			setErrorOnEditText(true);
		}
	}

	private void setErrorOnEditText(boolean isErrorShow) {
		if (isErrorShow) {
			if (isEditTextEmpty(departmentEditText))
				departmentEditText
						.setError(getString(R.string.department_required));
			if (isEditTextEmpty(descriptionEditText))
				descriptionEditText
						.setError(getString(R.string.description_required));
			if (isEditTextEmpty(subjectEditText))
				subjectEditText.setError(getString(R.string.subject_required));
		} else {
			departmentEditText.setError(null);
			descriptionEditText.setError(null);
			subjectEditText.setError(null);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		setErrorOnEditText(false);
	}

	@Override
	public String getTitle(Context context) {
		return UploadFileFragment.getFeatureName(context);
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}

	public static String getFeatureName(Context context) {
		return context.getString(R.string.share_notes);
	}
}
