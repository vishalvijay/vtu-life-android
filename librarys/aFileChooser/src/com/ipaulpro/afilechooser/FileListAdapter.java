/* 
 * Copyright (C) 2012 Paul Burke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package com.ipaulpro.afilechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * List adapter for Files.
 * 
 * @version 2013-06-25
 * 
 * @author paulburke (ipaulpro)
 * 
 */
public class FileListAdapter extends BaseAdapter {

	private final static int ICON_FOLDER = R.drawable.folder;

	private List<File> mFiles = new ArrayList<File>();
	private final LayoutInflater mInflater;

	public FileListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	public ArrayList<File> getListItems() {
		return (ArrayList<File>) mFiles;
	}

	public void setListItems(List<File> files) {
		this.mFiles = files;
		validateFileWRTExtention();
		notifyDataSetChanged();
	}

	private void validateFileWRTExtention() {
		ArrayList<String> extentionsArrayList = FileChooserActivity.EXTENSIONS;
		if (extentionsArrayList != null) {
			for (int i = 0; i < mFiles.size(); i++) {
				boolean isExtentionExist = false;
				for (int j = 0; j < extentionsArrayList.size(); j++)
					if (getExtention(mFiles.get(i).getAbsolutePath()).matches(
							"(?i)" + extentionsArrayList.get(j))) {
						isExtentionExist = true;
						break;
					}
				if (!isExtentionExist && !mFiles.get(i).isDirectory())
					mFiles.remove(i--);
			}
		}
	}

	@Override
	public int getCount() {
		return mFiles.size();
	}

	public void add(File file) {
		mFiles.add(file);
		notifyDataSetChanged();
	}

	public void clear() {
		mFiles.clear();
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return mFiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			row = mInflater.inflate(R.layout.file, parent, false);
			holder = new ViewHolder(row);
			row.setTag(holder);
		} else {
			// Reduce, reuse, recycle!
			holder = (ViewHolder) row.getTag();
		}

		// Get the file at the current position
		final File file = (File) getItem(position);

		// Set the TextView as the file name
		holder.nameView.setText(file.getName());
		int fileIcon = selectImage(getExtention(file.getAbsolutePath()));
		// If the item is not a directory, use the file icon
		holder.iconView.setImageResource(file.isDirectory() ? ICON_FOLDER
				: fileIcon);
		return row;
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

	static class ViewHolder {
		TextView nameView;
		ImageView iconView;

		ViewHolder(View row) {
			nameView = (TextView) row.findViewById(R.id.file_name);
			iconView = (ImageView) row.findViewById(R.id.file_icon);
		}
	}
}
