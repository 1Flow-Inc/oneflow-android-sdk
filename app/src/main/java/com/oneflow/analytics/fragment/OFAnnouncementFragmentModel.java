package com.oneflow.analytics.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oneflow.analytics.R;
import com.oneflow.analytics.adapter.OFAnnouncementListAdapter;
import com.oneflow.analytics.controller.OFEventController;
import com.oneflow.analytics.databinding.FragmentOfannouncementModelBinding;
import com.oneflow.analytics.model.announcement.OFAnnouncementIndex;
import com.oneflow.analytics.model.announcement.OFAnnouncementTheme;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class OFAnnouncementFragmentModel extends Fragment {

    String tag = this.getClass().getName();

    FragmentOfannouncementModelBinding binding;
    View layoutView;
    Context mContext;

    ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses;

    GradientDrawable gdSubmit;
    String themeColor;

    public static OFAnnouncementFragmentModel newInstance(ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses, OFSDKSettingsTheme sdkTheme, String themeColor) {
        OFAnnouncementFragmentModel myFragment = new OFAnnouncementFragmentModel();

        Bundle args = new Bundle();
        args.putSerializable("data", getAnnouncementDetailResponses);
        args.putSerializable("theme", sdkTheme);
        args.putString("themeColor", themeColor);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        OFHelper.v(tag, "1Flow OnResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOfannouncementModelBinding.inflate(inflater);
        layoutView = binding.getRoot();
        mContext = getActivity();

        getAnnouncementDetailResponses = (ArrayList<OFGetAnnouncementDetailResponse>) getArguments().getSerializable("data");

        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        String textColor = "#2f54eb";
        if(shp.getAnnouncementResponse() != null && shp.getAnnouncementResponse().getTheme() != null){
            OFAnnouncementTheme sdkTheme = shp.getAnnouncementResponse().getTheme();
            textColor = OFHelper.handlerColor(sdkTheme.getTextColor());
            binding.tvTitle.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getTextColor())));
            themeColor = OFHelper.handlerColor(sdkTheme.getBrandColor());
        }

        if(getAnnouncementDetailResponses != null && getAnnouncementDetailResponses.get(0) != null){

            OFGetAnnouncementDetailResponse getAnnouncementDetailResponse = getAnnouncementDetailResponses.get(0);

            viewedAnnouncement(getAnnouncementDetailResponse.getId());

            handleSeen(getAnnouncementDetailResponse.getId());

            binding.tvTitle.setText(getAnnouncementDetailResponse.getTitle());

            String catColor = "#5D5FEF";
            String catName = "New";
            if(getAnnouncementDetailResponse.getCategory() != null){
                catName = getAnnouncementDetailResponse.getCategory().getName();
                catColor = getAnnouncementDetailResponse.getCategory().getColor();
            }

            int colorTitle = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(catColor)), 1.0f);
            binding.tvCategoryName.setTextColor(colorTitle);
            binding.tvCategoryName.setText(catName);
            binding.tvCategoryName.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(OFHelper.getAlphaHexColor(catColor,51))));

            binding.tvDate.setText(OFHelper.formatedDate(getAnnouncementDetailResponse.getPublishedAt(),"MMM dd, yyyy"));

            if(getAnnouncementDetailResponse.getAction() != null && getAnnouncementDetailResponse.getAction().getLink() != null){
                binding.btnSubmit.setVisibility(View.VISIBLE);
                binding.btnSubmit.setText(getAnnouncementDetailResponse.getAction().getName());
                binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String action = getAnnouncementDetailResponse.getAction().getLink();
                        if(action != null && !action.isEmpty()){
                            clickedAnnouncement(getAnnouncementDetailResponse.getId(),getAnnouncementDetailResponse.getAction().getName(),action);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
                            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.getApplicationContext().startActivity(browserIntent);
                        }
                    }
                });
            }else{
                binding.btnSubmit.setVisibility(View.GONE);
            }

            binding.webContent.getSettings().setJavaScriptEnabled(true);
            binding.webContent.setBackgroundColor(Color.TRANSPARENT);

            binding.webContent.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    // Handle URL loading within the WebView
//                view.loadUrl(request.getUrl().toString());
                    String action = request.getUrl().toString();
                    if(!action.isEmpty()){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.getApplicationContext().startActivity(browserIntent);
                    }
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.evaluateJavascript("document.readyState", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            view.evaluateJavascript("quill.root.scrollHeight", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    int height = Integer.parseInt(value);
                                    double height2 = Math.floor((height * binding.webContent.getContext().getResources().getDisplayMetrics().density));
                                    if(height2 > 1200){
                                        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1200));
                                    }else{
                                        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height2 + 45));
                                    }
                                }
                            });
                        }
                    });
                }
            });

//        String content = "[{\"insert\":\"my h2 header\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"bold string\"},{\"insert\":\"\\n\"},{\"attributes\":{\"italic\":true,\"bold\":true},\"insert\":\"bold italic\"},{\"insert\":\"\\n\"},{\"attributes\":{\"italic\":true},\"insert\":\"only italic\"},{\"insert\":\"\\nvar a = 1 + 2\"},{\"attributes\":{\"code-block\":true},\"insert\":\"\\n\"}]";
            String content = getAnnouncementDetailResponse.getContent();

//            String htmlCode = "<html><head><link href=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.css\" rel=\"stylesheet\"><link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/quill/1.3.7/quill.snow.min.css\" integrity=\"sha512-/FHUK/LsH78K9XTqsR9hbzr21J8B8RwHR/r8Jv9fzry6NVAOVIGFKQCNINsbhK7a1xubVu2r5QZcz2T9cKpubw==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\" /><link href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/atom-one-light.min.css\" rel=\"stylesheet\"><style>.ql-container {font-family: Inter;}.ql-toolbar,.ql-blank {display: none;}.ql-snow  a {color: #2f54eb;}.ql-snow .ql-editor {padding: 0;word-break: break-word;}.ql-snow .ql-editor pre.ql-syntax  {padding: 10px;color: var(--oneflow-rich-text-preview-color);background-color: rgba(0, 0, 0, 0.05);border-radius: 8px;}.ql-snow .ql-editor blockquote {margin-top: 0;margin-bottom: 0;}.ql-container.ql-snow {border: none;}.editor {color: var(--oneflow-rich-text-preview-color);}.editor-content {overflow: auto;max-height: calc(80vh - 100px);}.editor-content::-webkit-scrollbar {width: 8px;height: 8px;}.editor-content::-webkit-scrollbar-thumb  {cursor: pointer;background-color: rgba(0, 0, 0, 0.2);border-radius: 4px;}.editor-content::-webkit-scrollbar-track {background-color: transparent;}@media screen and (max-width: 568px) {.editor-content {max-height: 80vh;}}</style></head><body><div id=\"quill-editor\" class=\"editor\"><div id=\"editor-content\" class=\"editor-content\"></div></div><script src=\"https://cdn.quilljs.com/1.3.6/quill.js\"></script><script src=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/javascript.min.js\" integrity=\"sha512-H69VMoQ814lKjFuFwLImb4OwoK8Rm8fcvsqZexaxjp/VkJfEnrt5TO7oaOdNlMf/j51QUctfLTe8+rgozW7l2A==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script><script>document.getElementById('quill-editor').style.setProperty('--oneflow-rich-text-preview-color', '#2f54eb');var quill = new Quill('#editor-content', {theme: 'snow',modules: {toolbar: [],'emoji-shortname': true, syntax: {highlight: (text) => hljs.highlightAuto(text).value,},},readOnly: true});quill.setContents(" + content + ");</script></body></html>"; // Replace this with your HTML code
//            String htmlCode = "<html><head><link href=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.css\" rel=\"stylesheet\"><link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/quill/1.3.7/quill.snow.min.css\" integrity=\"sha512-/FHUK/LsH78K9XTqsR9hbzr21J8B8RwHR/r8Jv9fzry6NVAOVIGFKQCNINsbhK7a1xubVu2r5QZcz2T9cKpubw==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\" /><link href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/atom-one-light.min.css\" rel=\"stylesheet\">" + content + ");</script></body></html>"; // Replace this with your HTML code
            String htmlCode = "<html><head><link href=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.css\" rel=\"stylesheet\"><link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/quill/1.3.7/quill.snow.min.css\" integrity=\"sha512-/FHUK/LsH78K9XTqsR9hbzr21J8B8RwHR/r8Jv9fzry6NVAOVIGFKQCNINsbhK7a1xubVu2r5QZcz2T9cKpubw==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\" /><link href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/atom-one-light.min.css\" rel=\"stylesheet\"><style>.ql-container {font-family: Inter;}.ql-toolbar,.ql-blank {display: none;}.ql-snow  a {color: #2f54eb;}.ql-snow .ql-editor {padding: 0;word-break: break-word;}.ql-snow .ql-editor pre.ql-syntax  {padding: 10px;color: var(--oneflow-rich-text-preview-color);background-color: rgba(0, 0, 0, 0.05);border-radius: 8px;}.ql-snow .ql-editor blockquote {margin-top: 0;margin-bottom: 0;}.ql-container.ql-snow {border: none;}.editor {color: var(--oneflow-rich-text-preview-color);}.editor-content::-webkit-scrollbar {width: 8px;height: 8px;}.editor-content::-webkit-scrollbar-thumb  {cursor: pointer;background-color: rgba(0, 0, 0, 0.2);border-radius: 4px;}.editor-content::-webkit-scrollbar-track {background-color: transparent;}@media screen and (max-width: 568px) {.editor-content {max-height: 100vh;}}</style></head><body><div id=\"quill-editor\" class=\"editor\"><div id=\"editor-content\" class=\"editor-content\"></div></div><script src=\"https://cdn.quilljs.com/1.3.6/quill.js\"></script><script src=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/javascript.min.js\" integrity=\"sha512-H69VMoQ814lKjFuFwLImb4OwoK8Rm8fcvsqZexaxjp/VkJfEnrt5TO7oaOdNlMf/j51QUctfLTe8+rgozW7l2A==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script><script>document.getElementById('quill-editor').style.setProperty('--oneflow-rich-text-preview-color', '" + textColor + "');var quill = new Quill('#editor-content', {theme: 'snow',modules: {toolbar: [],'emoji-shortname': true,syntax: {highlight: (text) => hljs.highlightAuto(text).value,},},readOnly: true});quill.setContents(" + content + ");</script></body></html>"; // Replace this with your HTML code
            binding.webContent.loadDataWithBaseURL(null, htmlCode, "text/html", "UTF-8", null);
        }

        submitButtonBeautification();
        binding.btnSubmit.requestFocus();

        return layoutView;
    }

    private void clickedAnnouncement(String id, String text, String url){
        OFEventController ec = OFEventController.getInstance(mContext);
        HashMap<String, Object> mapValue = new HashMap<>();
        mapValue.put("announcement_id", id);
        mapValue.put("channel", "in-app");
        mapValue.put("link_text", text);
        mapValue.put("link_url", url);
        ec.storeEventsInDB(OFConstants.ANN_CLICKED, mapValue, 0);
    }

    private void viewedAnnouncement(String id){
        OFEventController ec = OFEventController.getInstance(mContext);
        HashMap<String, Object> mapValue = new HashMap<>();
        mapValue.put("announcement_id", id);
        mapValue.put("channel", "in-app");
        ec.storeEventsInDB(OFConstants.ANN_VIEWED, mapValue, 0);
    }

    private void submitButtonBeautification() {
        gdSubmit = (GradientDrawable) (binding.btnSubmit).getBackground();
        gdSubmit.setColor(Color.parseColor(themeColor));
        binding.btnSubmit.setTypeface(null, Typeface.BOLD);
    }

    public void handleSeen(String id){
        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);

        ArrayList<String> inAppIdList;
        inAppIdList = shp.getSeenInAppAnnounceList();

        inAppIdList.add(id);

        shp.setSeenInAppAnnounceList(inAppIdList);
    }
}