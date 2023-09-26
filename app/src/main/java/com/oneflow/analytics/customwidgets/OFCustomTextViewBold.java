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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.oneflow.analytics.OneFlow;
import com.oneflow.analytics.utils.OFHelper;


/**
 * Created by Amit on 06-10-2022.
 */

public class OFCustomTextViewBold extends AppCompatTextView {
    public OFCustomTextViewBold(Context context) {
        super(context);
        init();
    }

    public OFCustomTextViewBold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OFCustomTextViewBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OFCustomTextViewBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context,attrs,defStyleAttr);
        init();
    }

    private void init()
    {
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
    }
}
