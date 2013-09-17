package com.V4Creations.vtulife.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.ui.VTULifeMainActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ipaulpro.afilechooser.utils.FileUtils;

import de.keyboardsurfer.android.widget.crouton.Style;

public class UploadFileFragment extends SherlockFragment implements
		TextWatcher, FragmentInfo {
	String TAG = "UploadFileFragment";

	private VTULifeMainActivity vtuLifeMainActivity;
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
		vtuLifeMainActivity = (VTULifeMainActivity) getActivity();
		return inflater.inflate(R.layout.upload_file_layout, null);
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
		fileTextView.setText("Select a file");
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
		i.putExtra(Intent.EXTRA_SUBJECT, "Share notes in VTU Life");
		String subjectString = subjectEditText.getText().toString();
		String departmentString = departmentEditText.getText().toString();
		String descriptionString = descriptionEditText.getText().toString();
		String message = "Subject : " + subjectString + "\nDepartment : "
				+ departmentString + "\nDescription : " + descriptionString;
		i.putExtra(Intent.EXTRA_TEXT, message);
		i.putExtra(Intent.EXTRA_STREAM, uri);
		try {
			startActivityForResult(
					Intent.createChooser(i, "Select a email client"),
					SEND_EMAIL);
		} catch (android.content.ActivityNotFoundException ex) {
			vtuLifeMainActivity.showCrouton(
					"There are no email clients installed.", Style.INFO, true);
		}
	}

	public void getFile() {
		Intent target = FileUtils.createGetContentIntent();
		target.setPackage("com.V4Creations.vtulife");
		List<String> extent = new ArrayList<String>();
		Collections.addAll(extent, allowedFileTypeStrings);
		target.putExtra("fullscreen", true);
		target.putStringArrayListExtra("ext", (ArrayList<String>) extent);
		Intent intent = Intent.createChooser(target, "Select a file");
		try {
			startActivityForResult(intent, BROUSER_FILE);
		} catch (ActivityNotFoundException e) {
			vtuLifeMainActivity.showCrouton("VTU Life file browser not found.",
					Style.INFO, true);
		}
	}

	public void send() {
		if (!filePathString.equals("") && !isEditTextEmpty(departmentEditText)
				&& !isEditTextEmpty(descriptionEditText)
				&& !isEditTextEmpty(subjectEditText))
			sendMail();
		else {
			vtuLifeMainActivity.showCrouton("Please provide all details.",
					Style.CONFIRM, true);
			setErrorOnEditText(true);
		}
	}

	private void setErrorOnEditText(boolean isErrorShow) {
		if (isErrorShow) {
			if (isEditTextEmpty(departmentEditText))
				departmentEditText.setError("Enter department");
			if (isEditTextEmpty(descriptionEditText))
				descriptionEditText.setError("Enter description");
			if (isEditTextEmpty(subjectEditText))
				subjectEditText.setError("Enter subject");
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
	public String getTitle() {
		return "Share Notes";
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}
}
