package com.V4Creations.vtulife.view.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.V4Creations.vtulife.util.VTULifeConstance;

public class CustomeEditText extends EditText {

	public CustomeEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomeEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomeEditText(Context context) {
		super(context);
		init();
	}

	public void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				VTULifeConstance.DEFAULT_FONT);
		setTypeface(tf);
	}
}