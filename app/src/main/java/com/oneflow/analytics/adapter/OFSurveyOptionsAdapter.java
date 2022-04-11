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

package com.oneflow.analytics.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.core.graphics.ColorUtils;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.oneflow.analytics.OneFlow;
import com.oneflow.analytics.R;
import com.oneflow.analytics.customwidgets.OFCustomEditText;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.customwidgets.OFCustomTextViewBold;
import com.oneflow.analytics.customwidgets.OFDynamicSquareLayout;
import com.oneflow.analytics.model.survey.OFSurveyInputs;
import com.oneflow.analytics.utils.OFGenericClickHandler;
import com.oneflow.analytics.utils.OFHelper;

public class OFSurveyOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    OFGenericClickHandler gch;
    private int listSize = 0;
    private int strokeWidth = 4;
    private int viewType = -1;
    private OFSurveyInputs surveyInputs;
    String tag = this.getClass().getName();
    private String themeColor;
    private LayoutInflater mInflater;
    // private Integer[] emojis = new Integer[]{R.drawable.smiley1, R.drawable.smiley2, R.drawable.smiley3, R.drawable.smiley4, R.drawable.smiley5};
    private Integer[] emojis = new Integer[]{R.string.smileyHtml1, R.string.smileyHtml2, R.string.smileyHtml3, R.string.smileyHtml4, R.string.smileyHtml5};
    // private Integer[] emojis = new Integer[]{0X12639,0x1F601,0x1F610,0x1F601,0x1F60A};
    //String[] emojiStr = new String[]{"\u00F0\u009F\u0098\u008A","","","",""};

    public class RatingsViewHolder extends RecyclerView.ViewHolder {
        public OFCustomTextView title;


        public RatingsViewHolder(View view) {
            super(view);
            title = (OFCustomTextView) view.findViewById(R.id.ratings_list_child_tv);
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public OFCustomTextView title;
        RelativeLayout innerView;
        View viewLocal;
        OFCustomTextView txtFestivalName;
        RelativeLayout rlDate;


        public TextViewHolder(View view) {
            super(view);

            title = (OFCustomTextView) view.findViewById(R.id.child_title);

        }
    }

    public class MCQRadioViewHolder extends RecyclerView.ViewHolder {
        public RadioButton title;
        public OFCustomEditText othersEditText;
        public LinearLayout otherLayout;
        public OFCustomTextViewBold otherSubmit;

        public MCQRadioViewHolder(View view) {
            super(view);

            title = (RadioButton) view.findViewById(R.id.child_title);
            otherLayout = (LinearLayout) view.findViewById(R.id.other_child_layout);
            otherSubmit = (OFCustomTextViewBold) view.findViewById(R.id.other_submit_btn);
            othersEditText = (OFCustomEditText) view.findViewById(R.id.child_text_others);

        }
    }

    public class MCQCheckBoxViewHolder extends RecyclerView.ViewHolder {
        public CheckBox title;
        public OFCustomEditText othersEditText;
        public LinearLayout otherLayout;
        public OFCustomTextViewBold otherSubmit;

        public MCQCheckBoxViewHolder(View view) {
            super(view);
            title = (CheckBox) view.findViewById(R.id.child_title);
            otherLayout = (LinearLayout) view.findViewById(R.id.other_child_layout);
            otherSubmit = (OFCustomTextViewBold) view.findViewById(R.id.other_submit_btn);
            othersEditText = (OFCustomEditText) view.findViewById(R.id.child_text_others);
        }
    }

    public class RatingsStar extends RecyclerView.ViewHolder {
        public ImageView stars;

        public RatingsStar(View view) {
            super(view);
            stars = (ImageView) view.findViewById(R.id.ratings_list_child_image_view);
        }
    }

    public class RatingsEmojis extends RecyclerView.ViewHolder {
        //public EmojiAppCompatTextView emojis;
        public OFCustomTextView emojis;

        public RatingsEmojis(View view) {
            super(view);
            emojis = (OFCustomTextView) view.findViewById(R.id.ratings_list_child_tv);

        }
    }

    public OFSurveyOptionsAdapter(Context mContext, OFSurveyInputs surveyInputs, OFGenericClickHandler onClickListener, String themeColor) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.gch = onClickListener;
        this.surveyInputs = surveyInputs;
        this.themeColor = themeColor;
        OFHelper.v(tag, "OneFlow theme color [" + themeColor + "]input type [" + surveyInputs.getInput_type() + "]childSize[" + new Gson().toJson(surveyInputs.getRatingsList()) + "]");
        if (surveyInputs.getInput_type().equalsIgnoreCase("rating-emojis")) {
            viewType = 4;
            listSize = surveyInputs.getRatingsList().size();
        } else if (surveyInputs.getInput_type().equalsIgnoreCase("rating") || surveyInputs.getInput_type().equalsIgnoreCase("rating-5-star")) {
            viewType = 5;
            listSize = surveyInputs.getRatingsList().size();
        } else if (surveyInputs.getInput_type().equalsIgnoreCase("nps") || surveyInputs.getInput_type().equalsIgnoreCase("rating-numerical")) {
            viewType = 0;
            listSize = surveyInputs.getRatingsList().size();
        } else if (surveyInputs.getInput_type().equalsIgnoreCase("mcq")) {
            listSize = surveyInputs.getChoices().size();
            viewType = 1;
        } else if (surveyInputs.getInput_type().equalsIgnoreCase("checkbox")) {
            listSize = surveyInputs.getChoices().size();
            viewType = 3;
        } else if (surveyInputs.getInput_type().equalsIgnoreCase("text")) {
            listSize = surveyInputs.getChoices().size();
            viewType = 2;
        }
       /* if (surveyInputs.getInput_type().contains("rating")) {
            if (surveyInputs.getEmoji()) {
                viewType = 4;
            } else if (surveyInputs.getStars()) {
                viewType = 5;
            } else {
                viewType = 0;
            }
            listSize = surveyInputs.getRatingsList().size();
        } else if (surveyInputs.getInput_type().contains("nps")) {
            viewType = 0;
            listSize = surveyInputs.getRatingsList().size();
        } else if (surveyInputs.getInput_type().equalsIgnoreCase("mcq")) {
            listSize = surveyInputs.getChoices().size();
            viewType = 1;
        } else if (surveyInputs.getInput_type().equalsIgnoreCase("checkbox")) {
            listSize = surveyInputs.getChoices().size();
            viewType = 3;
        } else if (surveyInputs.getInput_type().equalsIgnoreCase("text")) {
            listSize = surveyInputs.getChoices().size();
            viewType = 2;
        }*/
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        OFHelper.v(tag, "OneFlow createViewHolder called viewtype [" + viewType + "]");
        switch (viewType) {
            case 0:
                //numericals
                view = mInflater.inflate(R.layout.ratings_list_child, parent, false);
                return new RatingsViewHolder(view);
            case 1:
                //Radio
                view = mInflater.inflate(R.layout.mcq_radio_list_child, parent, false);
                return new MCQRadioViewHolder(view);
            case 2:
                //Text
                view = mInflater.inflate(R.layout.text_list_child, parent, false);
                return new TextViewHolder(view);

            case 3:
                //Checkboxs
                view = mInflater.inflate(R.layout.mcq_checkbox_list_child, parent, false);
                return new MCQCheckBoxViewHolder(view);
            case 4:
                //Emojis
                view = mInflater.inflate(R.layout.ratings_emoji_child, parent, false);
                return new RatingsEmojis(view);
            case 5:
                //Stars
                view = mInflater.inflate(R.layout.ratings_star_list_child, parent, false);
                return new RatingsStar(view);

        }
        return null;
    }

    RadioButton lastChecked = null; // to maintain radio behaviour
    LinearLayout otherLayoutGlobal = null; // as last view will not be accessable
    CheckBox cbGlobal;

    public String handleCheckboxFromOutside() {
        OFHelper.v(tag, "OneFlow handleCheckboxFromOutside [" + otherLayoutGlobal + "]");
        String retString = "";
        if (otherLayoutGlobal != null) {
            OFHelper.v(tag, "OneFlow handleCheckboxFromOutside [" + otherLayoutGlobal.getVisibility() + "]");
            if (otherLayoutGlobal.getVisibility() == View.VISIBLE) {
                otherLayoutGlobal.setVisibility(View.GONE);
                cbGlobal.setText(otherLayoutGlobal.getTag().toString());
                cbGlobal.setChecked(false);
                GradientDrawable gdCheckbox = (GradientDrawable) ((RelativeLayout) (cbGlobal).getParent()).getBackground();
                gdCheckbox.setStroke(strokeWidth, mContext.getResources().getColor(R.color.ratings_focused));
            } else {
                if (cbGlobal.isChecked()) {
                    retString = cbGlobal.getText().toString();
                }

            }
        }
        return retString;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        OFHelper.v(tag, "OneFlow viewtype [" + viewType + "]");
        int colorAlpha = ColorUtils.setAlphaComponent(Color.parseColor(themeColor), 125);
        int statesRadio[][] = {{android.R.attr.state_checked}, {}};
        int colorsRadio[] = {Color.parseColor(themeColor), mContext.getResources().getColor(R.color.ratings_focused)};
        try {
            switch (viewType) {
                case 0:
                    //OFHelper.v(tag, "OneFlow position[" + position + "]value [" + surveyInputs.getRatingsList().get(position).getId() + "]isSelected[" + surveyInputs.getRatingsList().get(position).getSelected() + "]");
                    ((RatingsViewHolder) holder).title.setText(surveyInputs.getRatingsList().get(position).getId() + "");
                    ((RatingsViewHolder) holder).title.setTag(surveyInputs.getRatingsList().get(position).getId());

               /* if (position == 0) {

                    ((RelativeLayout) (((RatingsViewHolder) holder).title).getParent()).setBackground(mContext.getResources().getDrawable(R.drawable.left_gray_rectangle));
                    ((RatingsViewHolder) holder).title.setTextColor(mContext.getResources().getColor(R.color.txtblack));

                } else if (position == surveyInputs.getRatingsList().size() - 1) {

                    ((RelativeLayout) (((RatingsViewHolder) holder).title).getParent()).setBackground(mContext.getResources().getDrawable(R.drawable.right_gray_rectangle));
                    ((RatingsViewHolder) holder).title.setTextColor(mContext.getResources().getColor(R.color.txtblack));

                } else {*/

                    ((RelativeLayout) (((RatingsViewHolder) holder).title).getParent()).setBackground(mContext.getResources().getDrawable(R.drawable.gray_rectangle_new_theme));
                    ((RatingsViewHolder) holder).title.setTextColor(mContext.getResources().getColor(R.color.txtblack));

                    //}
                    GradientDrawable gd = (GradientDrawable) ((RelativeLayout) (((RatingsViewHolder) holder).title).getParent()).getBackground();

                    ((RatingsViewHolder) holder).title.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            switch (event.getAction()) {

                                case MotionEvent.ACTION_DOWN:
                                    gd.setColor(colorAlpha);
                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    // touch move code
                                    //Helper.makeText(mContext,"Moved",1);
                                    break;

                                case MotionEvent.ACTION_UP:

                                    gd.setColor(Color.parseColor(themeColor));
                                    ((RatingsViewHolder) holder).title.setTextColor(mContext.getResources().getColor(R.color.txtwhite));

                                    break;
                            }
                            return false;
                        }
                    });
                    if (surveyInputs.getRatingsList().get(position).getSelected()) {
                        gd.setColor(Color.parseColor(themeColor));
                        ((RatingsViewHolder) holder).title.setTextColor(mContext.getResources().getColor(R.color.txtwhite));
                    } else {
                        gd.setColor(mContext.getResources().getColor(R.color.new_theme_gray));
                        ((RatingsViewHolder) holder).title.setTextColor(mContext.getResources().getColor(R.color.txtblack));
                    }

                    ((RatingsViewHolder) holder).title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gch.itemClicked(v, null, "");
                        }
                    });
                    break;
                case 1:
                    OFHelper.v(tag, "OneFlow title [" + surveyInputs.getChoices().get(position).getTitle() + "]tag[" + surveyInputs.getChoices().get(position).getId() + "]");

                    if (OneFlow.optionsFace != null) {
                        if (OneFlow.optionsFace.getTypeface() != null) {
                            ((MCQRadioViewHolder) holder).title.setTypeface(OneFlow.optionsFace.getTypeface());
                        }
                        if (OneFlow.optionsFace.getFontSize() != null) {
                            OFHelper.v(tag, "OneFlow changing font size");
                            ((MCQRadioViewHolder) holder).title.setTextSize(OneFlow.optionsFace.getFontSize());
                        }
                    }
                    ((MCQRadioViewHolder) holder).title.setText(surveyInputs.getChoices().get(position).getTitle());
                    ((MCQRadioViewHolder) holder).title.setTag(surveyInputs.getChoices().get(position).getId() == null ? String.valueOf(position) : surveyInputs.getChoices().get(position).getId());
                    ((MCQRadioViewHolder) holder).title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                           /* OFHelper.v(tag,"OneFlow radio visibility["+((MCQRadioViewHolder) holder).otherLayout.getVisibility()+"]");

                            if(otherLayoutGlobal!=null){
                                if(otherLayoutGlobal.getVisibility() == View.VISIBLE) {
                                    ((MCQRadioViewHolder) holder).otherLayout.setVisibility(View.GONE);
                                }
                            }
                        }*/

                            if (!surveyInputs.getOtherOption().equalsIgnoreCase(surveyInputs.getChoices().get(position).getId())) {

                                gch.itemClicked(v, null, "");
                            }
                        }
                    });

                    GradientDrawable gdRadio = (GradientDrawable) ((RelativeLayout) (((MCQRadioViewHolder) holder).title).getParent()).getBackground();
                    GradientDrawable gdRadioSubmit = (GradientDrawable) ((MCQRadioViewHolder) holder).otherSubmit.getBackground();
                    gdRadioSubmit.setColor(Color.parseColor(themeColor));
                    gdRadio.setStroke(strokeWidth, mContext.getResources().getColor(R.color.new_theme_gray));
                    gdRadio.setColor(mContext.getResources().getColor(R.color.new_theme_gray));

                    ((MCQRadioViewHolder) holder).otherSubmit.setTag(surveyInputs.getChoices().get(position).getId() == null ? String.valueOf(position) : surveyInputs.getChoices().get(position).getId());
                    ((MCQRadioViewHolder) holder).otherSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OFHelper.hideKeyboard((Activity) mContext, ((MCQRadioViewHolder) holder).othersEditText);
                            if (((MCQRadioViewHolder) holder).othersEditText.getText().toString().isEmpty()) {
                                gdRadio.setStroke(strokeWidth, mContext.getResources().getColor(R.color.new_theme_gray));
                                gdRadio.setColor(mContext.getResources().getColor(R.color.new_theme_gray));
                                ((MCQRadioViewHolder) holder).otherLayout.setVisibility(View.GONE);
                                ((MCQRadioViewHolder) holder).title.setText(surveyInputs.getChoices().get(position).getTitle());
                                ((MCQRadioViewHolder) holder).title.setTextColor(mContext.getResources().getColor(R.color.txtblack));
                                ((MCQRadioViewHolder) holder).title.setChecked(false);
                            } else {

                                gdRadio.setStroke(strokeWidth, Color.parseColor(themeColor));
                                gdRadio.setColor(Color.parseColor(themeColor));
                                ((MCQRadioViewHolder) holder).title.setTextColor(mContext.getResources().getColor(R.color.txtwhite));
                                ((MCQRadioViewHolder) holder).otherLayout.setVisibility(View.GONE);
                                ((MCQRadioViewHolder) holder).title.setText(((MCQRadioViewHolder) holder).othersEditText.getText());
                                gch.itemClicked(v, ((MCQRadioViewHolder) holder).title.getText().toString(), "");
                            }
                            //onClickListener.onClick(v); // handling final submit for radio

                        }
                    });


                    ((MCQRadioViewHolder) holder).title.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            RadioButton rb = (RadioButton) v;
                            switch (event.getAction()) {

                                case MotionEvent.ACTION_DOWN:

                                    int colorsRadioAlpha[] = {Color.parseColor(themeColor), colorAlpha};
                                    CompoundButtonCompat.setButtonTintList(((MCQRadioViewHolder) holder).title, new ColorStateList(statesRadio, colorsRadioAlpha));
                                    gdRadio.setStroke(strokeWidth, colorAlpha);
                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    // touch move code
                                    //Helper.makeText(mContext,"Moved",1);
                                    break;

                                case MotionEvent.ACTION_UP:

                                    if (lastChecked == null) {
                                        lastChecked = rb;
                                    } else {

                                        OFHelper.v(tag, "OneFlow radio visibility[" + otherLayoutGlobal + "]");
                                        if (otherLayoutGlobal != null) {
                                            OFHelper.v(tag, "OneFlow radio visibility[" + otherLayoutGlobal.getVisibility() + "]");
                                            if (otherLayoutGlobal.getVisibility() == View.VISIBLE) {
                                                otherLayoutGlobal.setVisibility(View.GONE);
                                                lastChecked.setText(otherLayoutGlobal.getTag().toString());
                                                gdRadio.setStroke(strokeWidth, mContext.getResources().getColor(R.color.ratings_focused));

                                            }
                                        }
                                        lastChecked.setChecked(false);
                                        lastChecked = rb;

                                    }

                                    // touch up code
                                    CompoundButtonCompat.setButtonTintList(((MCQRadioViewHolder) holder).title, new ColorStateList(statesRadio, colorsRadio));//ColorStateList.valueOf(Color.parseColor(themeColor)));
                                    if (!rb.isChecked()) {

                                        gdRadio.setStroke(strokeWidth, Color.parseColor(themeColor));

                                        rb.setTextColor(mContext.getResources().getColor(R.color.txtwhite));
                                        if (surveyInputs.getOtherOption().equalsIgnoreCase(surveyInputs.getChoices().get(position).getId())) {
                                            //  OFHelper.makeText(mContext, "Id and other same", 1);
                                            rb.setText("");
                                            otherLayoutGlobal = ((MCQRadioViewHolder) holder).otherLayout;
                                            otherLayoutGlobal.setTag(surveyInputs.getChoices().get(position).getTitle());
                                            ((MCQRadioViewHolder) holder).otherLayout.setVisibility(View.VISIBLE);
                                            ((MCQRadioViewHolder) holder).othersEditText.requestFocus();
                                            OFHelper.showKeyboard((Activity) mContext, ((MCQRadioViewHolder) holder).othersEditText);
                                            gdRadio.setColor(mContext.getResources().getColor(R.color.plainwhite));
                                        } else {
                                            gdRadio.setColor(Color.parseColor(themeColor));
                                        }

                                    } else {

                                        gdRadio.setStroke(strokeWidth, mContext.getResources().getColor(R.color.ratings_focused));
                                        gdRadio.setColor(mContext.getResources().getColor(R.color.new_theme_gray));
                                        if (((MCQRadioViewHolder) holder).otherLayout.getVisibility() == View.VISIBLE) {
                                            otherLayoutGlobal = null;
                                            rb.setText(surveyInputs.getChoices().get(position).getTitle());
                                            ((MCQRadioViewHolder) holder).otherLayout.setVisibility(View.GONE);
                                        }
                                    }
                                    break;
                            }
                            return false;
                        }
                    });
                    break;
                case 2:
                    ((TextViewHolder) holder).title.setText(String.valueOf(position));
                    break;
                case 3:
                    OFHelper.v(tag, "OneFlow title [" + surveyInputs.getChoices().get(position).getTitle() + "]tag[" + surveyInputs.getChoices().get(position).getId() + "]");
                    if (OneFlow.optionsFace != null) {
                        if (OneFlow.optionsFace.getTypeface() != null) {
                            ((MCQCheckBoxViewHolder) holder).title.setTypeface(OneFlow.optionsFace.getTypeface());
                        }
                        if (OneFlow.optionsFace.getFontSize() != null) {
                            ((MCQCheckBoxViewHolder) holder).title.setTextSize(OneFlow.optionsFace.getFontSize());
                        }
                    }
                    ((MCQCheckBoxViewHolder) holder).title.setText(surveyInputs.getChoices().get(position).getTitle());
                    ((MCQCheckBoxViewHolder) holder).title.setTag(surveyInputs.getChoices().get(position).getId() == null ? String.valueOf(position) : surveyInputs.getChoices().get(position).getId());
                    ((MCQCheckBoxViewHolder) holder).title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!surveyInputs.getOtherOption().equalsIgnoreCase(surveyInputs.getChoices().get(position).getId())) {
                                gch.itemClicked(v, null, "");
                            } else {
                                gch.itemClicked(v, null, "");
                            }
                        }
                    });
                    GradientDrawable gdCheckbox = (GradientDrawable) ((RelativeLayout) (((MCQCheckBoxViewHolder) holder).title).getParent()).getBackground();

                    GradientDrawable gdCheckSubmit = (GradientDrawable) ((MCQCheckBoxViewHolder) holder).otherSubmit.getBackground();
                    gdCheckSubmit.setColor(Color.parseColor(themeColor));

                    gdCheckbox.setStroke(strokeWidth, mContext.getResources().getColor(R.color.new_theme_gray));
                    gdCheckbox.setColor(mContext.getResources().getColor(R.color.new_theme_gray));
                    GradientDrawable submitGradient = (GradientDrawable) ((MCQCheckBoxViewHolder) holder).otherSubmit.getBackground();
                    submitGradient.setColor(Color.parseColor(themeColor));
                    ((MCQCheckBoxViewHolder) holder).otherSubmit.setTag(surveyInputs.getChoices().get(position).getId() == null ? String.valueOf(position) : surveyInputs.getChoices().get(position).getId());
                    ((MCQCheckBoxViewHolder) holder).otherSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OFHelper.hideKeyboard((Activity) mContext, ((MCQCheckBoxViewHolder) holder).othersEditText);
                            if (((MCQCheckBoxViewHolder) holder).othersEditText.getText().toString().isEmpty()) {
                                ((MCQCheckBoxViewHolder) holder).otherLayout.setVisibility(View.GONE);
                                ((MCQCheckBoxViewHolder) holder).title.setText(surveyInputs.getChoices().get(position).getTitle());
                                ((MCQCheckBoxViewHolder) holder).title.setChecked(false);
                                gch.itemClicked(v, false, ((MCQCheckBoxViewHolder) holder).othersEditText.getText().toString());

                                gdCheckbox.setStroke(strokeWidth, mContext.getResources().getColor(R.color.new_theme_gray));
                                gdCheckbox.setColor(mContext.getResources().getColor(R.color.new_theme_gray));
                            } else {
                                gdCheckbox.setStroke(strokeWidth, Color.parseColor(themeColor));
                                gdCheckbox.setColor(mContext.getResources().getColor(R.color.plainwhite));
                                ((MCQCheckBoxViewHolder) holder).otherLayout.setVisibility(View.GONE);
                                ((MCQCheckBoxViewHolder) holder).title.setText(((MCQCheckBoxViewHolder) holder).othersEditText.getText());
                                gch.itemClicked(v, true, ((MCQCheckBoxViewHolder) holder).othersEditText.getText().toString());

                            }

                        }
                    });
                    ((MCQCheckBoxViewHolder) holder).title.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            CheckBox cb = (CheckBox) v;
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    // touch down code

                                    int colorsRadioAlpha[] = {Color.parseColor(themeColor), colorAlpha};
                                    CompoundButtonCompat.setButtonTintList(((MCQCheckBoxViewHolder) holder).title, new ColorStateList(statesRadio, colorsRadioAlpha));
                                    gdCheckbox.setStroke(strokeWidth, colorAlpha);
                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    // touch move code
                                    //Helper.makeText(mContext,"Moved",1);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    // touch up code
                                    //Helper.makeText(mContext, "Released", 1);
                                    CompoundButtonCompat.setButtonTintList(((MCQCheckBoxViewHolder) holder).title, new ColorStateList(statesRadio, colorsRadio));//ColorStateList.valueOf(Color.parseColor(themeColor)));
                                    if (!cb.isChecked()) {

                                        gdCheckbox.setStroke(strokeWidth, Color.parseColor(themeColor));
                                        gdCheckbox.setColor(mContext.getResources().getColor(R.color.plainwhite));
                                        // others logic
                                        if (surveyInputs.getOtherOption().equalsIgnoreCase(surveyInputs.getChoices().get(position).getId())) {
                                            // OFHelper.makeText(mContext, "Id and other same", 1);
                                            cbGlobal = cb;
                                            cb.setText("");
                                            ((MCQCheckBoxViewHolder) holder).otherLayout.setVisibility(View.VISIBLE);
                                            ((MCQCheckBoxViewHolder) holder).othersEditText.requestFocus();
                                        /*InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.showSoftInput(((MCQCheckBoxViewHolder) holder).othersEditText, InputMethodManager.SHOW_IMPLICIT);*/
                                            OFHelper.showKeyboard((Activity) mContext, ((MCQCheckBoxViewHolder) holder).othersEditText);
                                            otherLayoutGlobal = ((MCQCheckBoxViewHolder) holder).otherLayout;
                                            otherLayoutGlobal.setTag(surveyInputs.getChoices().get(position).getTitle());
                                        }

                                    } else {

                                        gdCheckbox.setStroke(strokeWidth, mContext.getResources().getColor(R.color.new_theme_gray));
                                        gdCheckbox.setColor(mContext.getResources().getColor(R.color.new_theme_gray));

                                        if (((MCQCheckBoxViewHolder) holder).otherLayout.getVisibility() == View.VISIBLE) {
                                            cb.setText(surveyInputs.getChoices().get(position).getTitle());
                                            ((MCQCheckBoxViewHolder) holder).otherLayout.setVisibility(View.GONE);
                                            otherLayoutGlobal = null;
                                        }

                                    }
                                    break;
                            }
                            return false;
                        }
                    });
                    break;
                case 4:
               /* OFHelper.v(tag,"OneFlow emojis["+emojis[position]+
                        "]["+StringEscapeUtils.unescapeJava(mContext.getResources().getString(emojis[position]))+
                          "]["+ mContext.getResources().getString(emojis[position]).replace("\\u","0x")+
                        "]0["+new String(mContext.getResources().getString(emojis[position]).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)+
                        "]");*/
                    ((RatingsEmojis) holder).emojis.setText(mContext.getResources().getString((emojis[position])));//new String(mContext.getResources().getString(emojis[position]).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
               /* if (position == 0) {

                    ((OFDynamicSquareLayout) ((((RatingsEmojis) holder).emojis).getParent()).getParent()).setBackground(mContext.getResources().getDrawable(R.drawable.left_gray_rectangle));

                } else if (position == surveyInputs.getRatingsList().size() - 1) {

                    ((OFDynamicSquareLayout) ((((RatingsEmojis) holder).emojis).getParent()).getParent()).setBackground(mContext.getResources().getDrawable(R.drawable.right_gray_rectangle));

                } else {*/

                    ((OFDynamicSquareLayout) ((((RatingsEmojis) holder).emojis).getParent()).getParent()).setBackgroundResource(R.drawable.main_rounded_rectangle_new_theme_circular);

                    //}
                    GradientDrawable gdEmojis = (GradientDrawable) ((OFDynamicSquareLayout) ((((RatingsEmojis) holder).emojis).getParent()).getParent()).getBackground();


                    ((RatingsEmojis) holder).emojis.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    //Helper.makeText(mContext,"Down called",0);
                                    gdEmojis.setColor(colorAlpha);
                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    // touch move code
                                    //Helper.makeText(mContext,"Moved",1);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    gdEmojis.setColor(Color.parseColor(themeColor));
                                    break;
                            }
                            return false;
                        }
                    });

                    if (surveyInputs.getRatingsList().get(position).getSelected()) {
                        gdEmojis.setColor(Color.parseColor(themeColor));
                    } else {
                        gdEmojis.setColor(mContext.getResources().getColor(R.color.white));
                    }
                    ((RatingsEmojis) holder).emojis.setTag(position);
                    ((RatingsEmojis) holder).emojis.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gch.itemClicked(v, null, "");
                        }
                    });
                    break;
                case 5:
                    OFHelper.v(tag, "OneFlow binding views");

                    if (surveyInputs.getRatingsList().get(position).getSelected()) {
                        ((RatingsStar) holder).stars.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selected_star));//mContext.getResources().getDrawable(R.drawable.ic_baseline_star_92));
                        //DrawableCompat.setTint(((RatingsStar) holder).stars.getDrawable(), Color.parseColor(themeColor));
                    } else {
                        ((RatingsStar) holder).stars.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unselected_star));//ic_baseline_star_outline_92));
                        // DrawableCompat.setTint(((RatingsStar) holder).stars.getDrawable(), Color.parseColor(themeColor));
                    }

                    ((RatingsStar) holder).stars.setTag(position);
                    ((RatingsStar) holder).stars.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            gch.itemClicked(v, null, "");
                        }
                    });
                    break;

            }

        } catch (Exception ex) {
            OFHelper.e(tag, "OnBind" + ex.getMessage());
        }
    }



    @Override
    public int getItemCount() {
//        Helper.v(tag, "OneFlow attendance Calender adapter getItemCount() :::: " + String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + startPosition));
        return listSize;
    }

    public void notifyMyList(OFSurveyInputs surveyInputs) {
        this.surveyInputs = null;
        this.surveyInputs = surveyInputs;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        return viewType;
    }
}
