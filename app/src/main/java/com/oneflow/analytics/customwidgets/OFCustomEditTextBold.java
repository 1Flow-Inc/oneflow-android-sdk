/*
 *  Copyright 2021 1Flow, Inc.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.oneflow.analytics.customwidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.AppCompatEditText;

import com.oneflow.analytics.OneFlow;
import com.oneflow.analytics.R;
import com.oneflow.analytics.utils.OFHelper;

public class OFCustomEditTextBold extends AppCompatEditText {
    public OFCustomEditTextBold(Context context) {
       super(context, null, R.style.EditTextCustomStyle);
        init();
    }

    public OFCustomEditTextBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.EditTextStyle);
        values.recycle();
        init();

    }

    public OFCustomEditTextBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        init();

    }

    private void init() {

        OFHelper.v(this.getClass().getName(),"OneFlow font Name["+OneFlow.fontNameStr+"]");
        if(OFHelper.validateString(OneFlow.fontNameStr).equalsIgnoreCase("NA")){
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Regular.ttf");
            setTypeface(typeface);
        }else{
            try {
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), OneFlow.fontNameStr);
                setTypeface(typeface);
            }catch (Exception e){
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Regular.ttf");
                setTypeface(typeface);
            }
        }

        getSelectionStart();

        this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                // mode
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode,
                                               MenuItem item) {
                return false;
            }
        });


    }
}
