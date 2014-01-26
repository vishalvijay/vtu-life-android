package com.V4Creations.vtulife.view.customviews;

import com.V4Creations.vtulife.util.VTULifeConstance;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomeButton extends Button {

	public CustomeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomeButton(Context context) {
		super(context);
		init();
	}

	public void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				VTULifeConstance.DEFAULT_FONT);
		setTypeface(tf);
	}
}