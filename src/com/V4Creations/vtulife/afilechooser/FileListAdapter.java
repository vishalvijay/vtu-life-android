package com.V4Creations.vtulife.afilechooser;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.controller.adapters.SupportArrayAdapter;

public class FileListAdapter extends SupportArrayAdapter<File> {

	private final static int ICON_FOLDER = R.drawable.folder;

	public FileListAdapter(Context context) {
		super(context);
	}

	public void setListItems(ArrayList<File> files) {
		validateFileWRTExtention(files);
		clear();
		supportAddAll(files);
	}

	private void validateFileWRTExtention(ArrayList<File> files) {
		ArrayList<String> extentionsArrayList = FileChooserActivity.EXTENSIONS;
		if (extentionsArrayList != null) {
			for (int i = 0; i < files.size(); i++) {
				boolean isExtentionExist = false;
				for (String ext : extentionsArrayList)
					if (getExtention(files.get(i).getAbsolutePath()).matches(
							"(?i)" + ext)) {
						isExtentionExist = true;
						break;
					}
				if (!isExtentionExist && !files.get(i).isDirectory())
					files.remove(i--);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		File file = getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = getLayoutInflater().inflate(
					R.layout.list_directory_item, null);
			viewHolder = new ViewHolder();
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.nameTextView);
			viewHolder.sizeTextView = (TextView) convertView
					.findViewById(R.id.sizeTextView);
			viewHolder.dateTextView = (TextView) convertView
					.findViewById(R.id.dateTextView);
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.iconImageView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		int fileIcon = selectImage(getExtention(file.getAbsolutePath()));
		viewHolder.iconImageView
				.setImageResource(file.isDirectory() ? ICON_FOLDER : fileIcon);
		viewHolder.nameTextView.setText(file.getName());
		viewHolder.sizeTextView.setText(readableFileSize(file.length()));
		viewHolder.sizeTextView.setTag(position);
		if (file.isDirectory())
			setFolderSize(viewHolder.sizeTextView, file, position);
		viewHolder.dateTextView
				.setText(getLastModifiedDate(file.lastModified()));
		int color = R.color.odd_color;
		if (position % 2 == 0)
			color = R.color.even_color;
		convertView.setBackgroundResource(color);
		return convertView;
	}

	private CharSequence getLastModifiedDate(long lastModified) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
				Locale.getDefault());
		return sdf.format(lastModified);
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

	private int selectImage(String ext) {
		if (ext.matches("(?i)png") || ext.matches("(?i)jpeg")
				|| ext.matches("(?i)jpg") || ext.matches("(?i)bmp"))
			return R.drawable.jpeg;
		else if (ext.matches("(?i)gif"))
			return R.drawable.gif;
		else if (ext.matches("(?i)zip") || ext.matches("(?i)rar")
				|| ext.matches("(?i)tar") || ext.matches("(?i)7zip")
				|| ext.matches("(?i)gz"))
			return R.drawable.archive;
		else if (ext.matches("(?i)exe"))
			return R.drawable.exe;
		else if (ext.matches("(?i)txt"))
			return R.drawable.txt;
		else if (ext.matches("(?i)htm") || ext.matches("(?i)html")
				|| ext.matches("(?i)xml") || ext.matches("(?i)php")
				|| ext.matches("(?i)pl"))
			return R.drawable.html;
		else if (ext.matches("(?i)flv"))
			return R.drawable.flash;
		else if (ext.matches("(?i)swf"))
			return R.drawable.swf;
		else if (ext.matches("(?i)xls"))
			return R.drawable.xsl;
		else if (ext.matches("(?i)doc") || ext.matches("(?i)docx"))
			return R.drawable.doc;
		else if (ext.matches("(?i)pdf"))
			return R.drawable.pdf;
		else if (ext.matches("(?i)psd"))
			return R.drawable.psd;
		else if (ext.matches("(?i)rm"))
			return R.drawable.real;
		else if (ext.matches("(?i)mpeg") || ext.matches("(?i)mpg")
				|| ext.matches("(?i)mov") || ext.matches("(?i)avi")
				|| ext.matches("(?i)mp4") || ext.matches("(?i)3gp")
				|| ext.matches("(?i)vob"))
			return R.drawable.video;
		else
			return R.drawable.unknown;
	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}

	private void setFolderSize(final TextView sizeTextView, final File file,
			final int position) {
		new AsyncTask<Void, Void, Long>() {

			private long folderSize(File directory) {
				long length = 0;
				for (File file : directory.listFiles()) {
					if (file.isFile())
						length += file.length();
					else
						length += folderSize(file);
				}
				return length;
			}

			@Override
			protected Long doInBackground(Void... params) {
				return folderSize(file);
			}

			@Override
			protected void onPostExecute(Long result) {
				super.onPostExecute(result);
				int pos = (Integer) sizeTextView.getTag();
				if (pos == position)
					sizeTextView.setText(readableFileSize(result));
			}
		}.execute();
	}

	private static class ViewHolder {
		TextView nameTextView;
		TextView sizeTextView;
		TextView dateTextView;
		ImageView iconImageView;
	}
}
