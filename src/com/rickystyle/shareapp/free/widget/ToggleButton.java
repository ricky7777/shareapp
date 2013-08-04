package com.rickystyle.shareapp.free.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.rickystyle.shareapp.free.R;

/**
 * 自訂ㄧToggleButton,可以自定背景<br/>
 * 參考:http://www.anddev.org/creating_custom_views_-_the_togglebutton-t310.html
 * @author Ricky
 */
public class ToggleButton extends Button {
    protected boolean isChecked = false;
    private static final int[] LAST_STATE_SET = { R.attr.state_last };

    public ToggleButton(Context context) {
	super(context);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    public boolean isChecked() {
	return this.isChecked;
    }

    @Override
    public boolean performClick() {
	this.isChecked = !this.isChecked;
	return super.performClick();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
	int[] states;

	if (this.isChecked()) {
	    // Checked
	    states = Button.PRESSED_SELECTED_STATE_SET;
	} else {
	    // Unchecked
	    if (super.hasFocus()) {
		/*
		 * Unchecked && Focus System highlights the Button
		 */
		states = super.onCreateDrawableState(extraSpace);
	    } else {
		// Unchecked && noFocus
		states = LAST_STATE_SET;
	    }
	}
	return states;
    }

    public void setChecked(boolean checked) {
	this.isChecked = checked;
    }
}
