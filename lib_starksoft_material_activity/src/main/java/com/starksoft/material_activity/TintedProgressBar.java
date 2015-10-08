package com.starksoft.material_activity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class TintedProgressBar extends ProgressBar
{
	public TintedProgressBar(Context context)
	{
		super(context);
		init();
	}

	public TintedProgressBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public TintedProgressBar(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	public void init()
	{
		Drawable indeterminateDrawable = getIndeterminateDrawable();
		Drawable progressDrawable = getProgressDrawable();

		if (indeterminateDrawable != null)
			indeterminateDrawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

		if (progressDrawable != null)
			progressDrawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

	}
}
