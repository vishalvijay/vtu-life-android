package com.V4Creations.vtulife.view.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.VTULifeFragmentAdapter.FragmentInfo;
import com.V4Creations.vtulife.model.ActionBarStatus;
import com.V4Creations.vtulife.util.VTULifeUtils;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;

import de.keyboardsurfer.android.widget.crouton.Style;

public class ShareAPicFragment extends Fragment implements TextWatcher,
		FragmentInfo {
	String TAG = "PostAPicFragment";

	private VTULifeMainActivity vtuLifeMainActivity;
	private EditText descriptionEditText;
	private ImageView picImageView;
	private Button sendButton;
	private static String shareImageLocation = "";
	private static final int CAMERA_REQUEST = 1888, PICK_FROM_FILE = 1889,
			SEND_EMAIL = 1890;
	private Uri mCapturedImageURI;
	private final String[] allowedFileTypeStrings = { "jpg", "jpeg", "png",
			"bmp", "gif" };
	private ActionBarStatus mActionBarStatus;

	public ShareAPicFragment() {
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
		return inflater.inflate(R.layout.fragemnt_post_a_pic, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setIntialFileAddress();
		initViews();
		initEventListeners();
	}

	private void setIntialFileAddress() {
		shareImageLocation = Environment.getExternalStorageDirectory()
				+ File.separator + "shareVtu.png";
	}

	private void initEventListeners() {
		picImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showImageDialog();
			}
		});
		sendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String description = descriptionEditText.getText().toString();

				if (!description.trim().equals("")
						&& !shareImageLocation.equals("")) {
					sendMail(description);
				} else {
					vtuLifeMainActivity.showCrouton(
							"Please provide all details.", Style.CONFIRM, true);
					descriptionEditText.setError("Enter description");
				}
			}
		});
	}

	private void initViews() {
		descriptionEditText = (EditText) getView().findViewById(
				R.id.descriptionEditText);
		descriptionEditText.addTextChangedListener(this);
		picImageView = (ImageView) getView().findViewById(
				R.id.takeAPhotoImageView);
		sendButton = (Button) getView().findViewById(R.id.sendButton);
	}

	protected void sendMail(String description) {
		if (isFileExist(shareImageLocation)) {
			Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "someone@vtulife.com" });
			i.putExtra(Intent.EXTRA_SUBJECT, "Share photos in VTU Life");
			i.putExtra(Intent.EXTRA_STREAM, shareImageLocation);
			String bugReportBody = description;
			i.putExtra(Intent.EXTRA_TEXT, bugReportBody);
			ArrayList<Uri> uris = new ArrayList<Uri>();
			File fileIn = new File(shareImageLocation);
			uris.add(Uri.fromFile(fileIn));
			i.putExtra(Intent.EXTRA_STREAM, uris);
			try {
				startActivityForResult(
						Intent.createChooser(i, "Complete action using"),
						SEND_EMAIL);
			} catch (android.content.ActivityNotFoundException ex) {
				vtuLifeMainActivity.showCrouton(
						"There are no email clients installed.", Style.INFO,
						true);
			}
		} else
			vtuLifeMainActivity.showCrouton("File doesn't exist", Style.ALERT,
					false);
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
		picImageView.setImageResource(R.drawable.camera);
		descriptionEditText.setText("");
		setIntialFileAddress();
	}

	private void showImageDialog() {
		final String[] items = new String[] { "From Camera", "From SD Card" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				vtuLifeMainActivity, android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				vtuLifeMainActivity);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					String captureAddress = VTULifeUtils.getDefaultRootFolder()
							+ "share_vtu_life_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg";
					Log.e(TAG, captureAddress);
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, captureAddress);
					mCapturedImageURI = vtuLifeMainActivity
							.getContentResolver()
							.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									values);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
					startActivityForResult(intent, CAMERA_REQUEST);
					dialog.dismiss();
				} else {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					List<String> extent = new ArrayList<String>();
					Collections.addAll(extent, allowedFileTypeStrings);
					intent.putStringArrayListExtra("ext",
							(ArrayList<String>) extent);
					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
					dialog.dismiss();
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();
	}

	private String getPath(Uri uri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = vtuLifeMainActivity.getContentResolver().query(uri,
				proj, null, null, null);
		if (cursor.moveToFirst()) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == Activity.RESULT_OK) {
				switch (requestCode) {
				case CAMERA_REQUEST:
					shareImageLocation = getPath(mCapturedImageURI);
					break;
				case PICK_FROM_FILE:
					Uri selectedImageUri = data.getData();
					try {
						shareImageLocation = getPath(selectedImageUri);
					} catch (Exception e) {
						shareImageLocation = data.getData().getPath();
					}
					if (!shareImageLocation.startsWith("/")
							|| !isValideFileWRTExtention(shareImageLocation)) {
						vtuLifeMainActivity.showCrouton("File not supportted",
								Style.ALERT, true);
						setIntialFileAddress();
						return;
					}
					break;
				}
				if (isFileExist(shareImageLocation)) {
					Bitmap myBitmap = BitmapFactory
							.decodeFile(shareImageLocation);
					myBitmap = Bitmap.createScaledBitmap(
							myBitmap,
							getResources().getDimensionPixelOffset(
									R.dimen.post_a_pic_width),
							getResources().getDimensionPixelOffset(
									R.dimen.post_a_pic_heigth), true);
					picImageView.setImageBitmap(myBitmap);
				} else
					vtuLifeMainActivity.showCrouton("File doesn't exist",
							Style.ALERT, true);
			}
		} catch (Exception ex) {
			vtuLifeMainActivity.showCrouton("Error occured please retry",
					Style.ALERT, true);
		}
	}

	private boolean isValideFileWRTExtention(String fileName) {
		String extention = getExtention(fileName);
		for (int i = 0; i < allowedFileTypeStrings.length; i++)
			if (extention.matches("(?i)" + allowedFileTypeStrings[i]))
				return true;
		return false;
	}

	private String getExtention(String fileNameString) {
		String extension = "";
		int i = fileNameString.lastIndexOf('.');
		int p = Math.max(fileNameString.lastIndexOf('/'),
				fileNameString.lastIndexOf('\\'));

		if (i > p) {
			extension = fileNameString.substring(i + 1);
		}
		return extension;
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
		descriptionEditText.setError(null);
	}

	private boolean isFileExist(String fileNameString) {
		File f = new File(fileNameString);
		if (f.exists())
			return true;
		return false;
	}

	@Override
	public String getTitle() {
		return ShareAPicFragment.getFeatureName();
	}

	@Override
	public ActionBarStatus getActionBarStatus() {
		return mActionBarStatus;
	}

	public static String getFeatureName() {
		return "Share A Pic";
	}
}
