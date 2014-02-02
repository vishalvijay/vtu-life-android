package com.V4Creations.vtulife.view.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.V4Creations.vtulife.R;
import com.V4Creations.vtulife.util.VTULifeConstance;

public class CustomeTextView extends TextView {

	public CustomeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public CustomeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public void init(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.CustomViewDefault);
		String font = VTULifeConstance.DEFAULT_FONT;
		if (a.getString(R.styleable.CustomViewDefault_isItalic) != null)
			font = VTULifeConstance.DEFAULT_FONT_ITALIC;
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), font);
		setTypeface(tf);
		a.recycle();
	}
}