package com.oneflow.analytics.customwidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.AppCompatEditText;

import com.oneflow.analytics.R;

public class CustomEditTextBold extends AppCompatEditText {
    public CustomEditTextBold(Context context) {
        //super(context);
       super(context, null, R.style.EditTextCustomStyle);
        init();
    }

    public CustomEditTextBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.EditTextStyle);
        values.recycle();
        init();

    }

    public CustomEditTextBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        init();

    }

    private void init() {

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Bold.ttf");
        setTypeface(typeface);


        getSelectionStart();

        this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                menu.clear();
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub

            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode,
                                               MenuItem item) {
                // TODO Auto-generated method stub
                return false;
            }
        });


    }

    @Override
    public int getSelectionStart() {

        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {


            if (element.getMethodName().equals("canPaste") || element.getMethodName().equals("canCopy") || element.getMethodName().equals("canCut") || element.getMethodName().equalsIgnoreCase("canSelectAll") || element.getMethodName().equalsIgnoreCase("canClipboard")) {
                return -1;
            }
        }

        return super.getSelectionStart();

    }


}