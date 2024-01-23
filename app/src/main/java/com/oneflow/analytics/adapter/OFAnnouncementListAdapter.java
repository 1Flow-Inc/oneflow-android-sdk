package com.oneflow.analytics.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oneflow.analytics.R;
import com.oneflow.analytics.controller.OFEventController;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.customwidgets.OFCustomTextViewBold;
import com.oneflow.analytics.model.announcement.OFAnnouncementIndex;
import com.oneflow.analytics.model.announcement.OFAnnouncementTheme;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class OFAnnouncementListAdapter extends RecyclerView.Adapter<OFAnnouncementListAdapter.MyViewHolder> {
    private ArrayList<OFGetAnnouncementDetailResponse> itemsList;
    private View.OnClickListener gch;
    private Context mContext;
    private OFOneFlowSHP shp;

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        OFGetAnnouncementDetailResponse getAnnouncementDetailResponse = itemsList.get(position);

        boolean isSeen = getAnnouncementDetailResponse.isSeen;
        if(!isSeen){
            holder.seenView.setVisibility(View.VISIBLE);
        }else{
            holder.seenView.setVisibility(View.INVISIBLE);
        }

        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        String textColor = "#2f54eb";
        if(shp.getAnnouncementResponse() != null && shp.getAnnouncementResponse().getTheme() != null){
            OFAnnouncementTheme sdkTheme = shp.getAnnouncementResponse().getTheme();
            textColor = OFHelper.handlerColor(sdkTheme.getTextColor());
            holder.tvTitle.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getTextColor())));
            holder.tvAction.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBrandColor())));
            holder.llMain.setBackgroundColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBackgroundColor())));
            holder.seenView.setBackgroundColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBrandColor())));
            holder.ivForward.setImageTintList(ColorStateList.valueOf(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBrandColor()))));
        }

        holder.tvTitle.setText(getAnnouncementDetailResponse.getTitle());

        int colorTitle = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(getAnnouncementDetailResponse.getCategory().getColor())), 1.0f);
        holder.tvCategoryName.setTextColor(colorTitle);
        holder.tvCategoryName.setText(getAnnouncementDetailResponse.getCategory().getName());
        holder.tvCategoryName.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(OFHelper.getAlphaHexColor(getAnnouncementDetailResponse.getCategory().getColor(),51))));

        holder.tvDate.setText(OFHelper.formatedDate(getAnnouncementDetailResponse.getPublishedAt(),"MMM dd, yyyy"));

        if(getAnnouncementDetailResponse.getAction() != null && getAnnouncementDetailResponse.getAction().getLink() != null){
            holder.llAction.setVisibility(View.VISIBLE);
            holder.tvAction.setText(getAnnouncementDetailResponse.getAction().getName());
            holder.llAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String action = getAnnouncementDetailResponse.getAction().getLink();
                    if(action != null && !action.isEmpty()){
                        clickedAnnouncement(getAnnouncementDetailResponse.getId(), getAnnouncementDetailResponse.getAction().getName(), action);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.getApplicationContext().startActivity(browserIntent);
                    }
                }
            });
        }else{
            holder.llAction.setVisibility(View.GONE);
        }

        holder.webContent.getSettings().setJavaScriptEnabled(true);
        holder.webContent.setBackgroundColor(Color.TRANSPARENT);

        holder.webContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Handle URL loading within the WebView
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
                Log.e("List : ", position + "");
                handleSeen(getAnnouncementDetailResponse.getId());
                viewedAnnouncement(getAnnouncementDetailResponse.getId());
                view.evaluateJavascript("document.readyState", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        view.evaluateJavascript("quill.root.scrollHeight", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                int height = Integer.parseInt(value);
                                double height2 = Math.floor((height * holder.webContent.getContext().getResources().getDisplayMetrics().density));
                                view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height2 + 45));
                            }
                        });
                    }
                });

//                OFEventController ec = OFEventController.getInstance(mContext);
//                HashMap<String, Object> mapValue = new HashMap<>();
//                mapValue.put("announcement_id", getAnnouncementDetailResponse.getId());
//                mapValue.put("channel", "inbox");
//                ec.storeEventsInDB(OFConstants.ANN_VIEWED, mapValue, 0);

            }
        });

//        String content = "[{\"insert\":\"my h2 header\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"bold string\"},{\"insert\":\"\\n\"},{\"attributes\":{\"italic\":true,\"bold\":true},\"insert\":\"bold italic\"},{\"insert\":\"\\n\"},{\"attributes\":{\"italic\":true},\"insert\":\"only italic\"},{\"insert\":\"\\nvar a = 1 + 2\"},{\"attributes\":{\"code-block\":true},\"insert\":\"\\n\"}]";
        String content = getAnnouncementDetailResponse.getContent();

//        String htmlCode = "<html><head><link href=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.css\" rel=\"stylesheet\"><link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/quill/1.3.7/quill.snow.min.css\" integrity=\"sha512-/FHUK/LsH78K9XTqsR9hbzr21J8B8RwHR/r8Jv9fzry6NVAOVIGFKQCNINsbhK7a1xubVu2r5QZcz2T9cKpubw==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\" /><link href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/atom-one-light.min.css\" rel=\"stylesheet\"><style>.ql-container {font-family: Inter;}.ql-toolbar,.ql-blank {display: none;}.ql-snow  a {color: #2f54eb;}.ql-snow .ql-editor {padding: 0;word-break: break-word;}.ql-snow .ql-editor pre.ql-syntax  {padding: 10px;color: var(--oneflow-rich-text-preview-color);background-color: rgba(0, 0, 0, 0.05);border-radius: 8px;}.ql-snow .ql-editor blockquote {margin-top: 0;margin-bottom: 0;}.ql-container.ql-snow {border: none;}.editor {color: var(--oneflow-rich-text-preview-color);}.editor-content {overflow: auto;max-height: calc(80vh - 100px);}.editor-content::-webkit-scrollbar {width: 8px;height: 8px;}.editor-content::-webkit-scrollbar-thumb  {cursor: pointer;background-color: rgba(0, 0, 0, 0.2);border-radius: 4px;}.editor-content::-webkit-scrollbar-track {background-color: transparent;}@media screen and (max-width: 568px) {.editor-content {max-height: 80vh;}}</style></head><body><div id=\"quill-editor\" class=\"editor\"><div id=\"editor-content\" class=\"editor-content\"></div></div><script src=\"https://cdn.quilljs.com/1.3.6/quill.js\"></script><script src=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/javascript.min.js\" integrity=\"sha512-H69VMoQ814lKjFuFwLImb4OwoK8Rm8fcvsqZexaxjp/VkJfEnrt5TO7oaOdNlMf/j51QUctfLTe8+rgozW7l2A==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script><script>document.getElementById('quill-editor').style.setProperty('--oneflow-rich-text-preview-color', '#2f54eb');var quill = new Quill('#editor-content', {theme: 'snow',modules: {toolbar: [],'emoji-shortname': true, syntax: {highlight: (text) => hljs.highlightAuto(text).value,},},readOnly: true});quill.setContents(" + content + ");</script></body></html>"; // Replace this with your HTML code
        String htmlCode = "<html><head><link href=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.css\" rel=\"stylesheet\"><link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/quill/1.3.7/quill.snow.min.css\" integrity=\"sha512-/FHUK/LsH78K9XTqsR9hbzr21J8B8RwHR/r8Jv9fzry6NVAOVIGFKQCNINsbhK7a1xubVu2r5QZcz2T9cKpubw==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\" /><link href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/atom-one-light.min.css\" rel=\"stylesheet\"><style>.ql-container {font-family: Inter;}.ql-toolbar,.ql-blank {display: none;}.ql-snow  a {color: #2f54eb;}.ql-snow .ql-editor {padding: 0;word-break: break-word;}.ql-snow .ql-editor pre.ql-syntax  {padding: 10px;color: var(--oneflow-rich-text-preview-color);background-color: rgba(0, 0, 0, 0.05);border-radius: 8px;}.ql-snow .ql-editor blockquote {margin-top: 0;margin-bottom: 0;}.ql-container.ql-snow {border: none;}.editor {color: var(--oneflow-rich-text-preview-color);}.editor-content::-webkit-scrollbar {width: 8px;height: 8px;}.editor-content::-webkit-scrollbar-thumb  {cursor: pointer;background-color: rgba(0, 0, 0, 0.2);border-radius: 4px;}.editor-content::-webkit-scrollbar-track {background-color: transparent;}@media screen and (max-width: 568px) {.editor-content {max-height: 100vh;}}</style></head><body><div id=\"quill-editor\" class=\"editor\"><div id=\"editor-content\" class=\"editor-content\"></div></div><script src=\"https://cdn.quilljs.com/1.3.6/quill.js\"></script><script src=\"https://cdn.jsdelivr.net/npm/quill-emoji@0.2.0/dist/quill-emoji.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/javascript.min.js\" integrity=\"sha512-H69VMoQ814lKjFuFwLImb4OwoK8Rm8fcvsqZexaxjp/VkJfEnrt5TO7oaOdNlMf/j51QUctfLTe8+rgozW7l2A==\" crossorigin=\"anonymous\" referrerpolicy=\"no-referrer\"></script><script>document.getElementById('quill-editor').style.setProperty('--oneflow-rich-text-preview-color', '" + textColor + "');var quill = new Quill('#editor-content', {theme: 'snow',modules: {toolbar: [],'emoji-shortname': true,syntax: {highlight: (text) => hljs.highlightAuto(text).value,},},readOnly: true});quill.setContents(" + content + ");</script></body></html>"; // Replace this with your HTML code
        holder.webContent.loadDataWithBaseURL(null, htmlCode, "text/html", "UTF-8", null);

//        holder.webContent.loadUrl("file:///android_asset/sample_code.html");
    }

    public boolean checkSeenStatus(String id){
//        boolean isSeen = false;
        boolean isAvailable = false;
        for (int i1 = 0; i1 < shp.getSeenInboxAnnounceList().size(); i1++) {
            if(shp.getSeenInboxAnnounceList().get(i1).equals(id)){
                isAvailable = true;
                break;
            }
        }

        return isAvailable;

//        if(shp.getAnnouncementResponse() != null && shp.getAnnouncementResponse().getAnnouncements() != null){
//            for (int i = 0; i < shp.getAnnouncementResponse().getAnnouncements().getInbox().size(); i++) {
//                OFAnnouncementIndex announcementIndex = shp.getAnnouncementResponse().getAnnouncements().getInbox().get(i);
//                if(announcementIndex.getId().equalsIgnoreCase(id)){
//                    isSeen = announcementIndex.getSeen();
//                }
//            }
//        }
//
//        return isSeen;
    }

    private void clickedAnnouncement(String id, String text, String url){
        OFEventController ec = OFEventController.getInstance(mContext);
        HashMap<String, Object> mapValue = new HashMap<>();
        mapValue.put("announcement_id", id);
        mapValue.put("channel", "inbox");
        mapValue.put("link_text", text);
        mapValue.put("link_url", url);
        ec.storeEventsInDB(OFConstants.ANN_CLICKED, mapValue, 0);
    }

    private void viewedAnnouncement(String id){
        OFEventController ec = OFEventController.getInstance(mContext);
        HashMap<String, Object> mapValue = new HashMap<>();
        mapValue.put("announcement_id", id);
        mapValue.put("channel", "inbox");
        ec.storeEventsInDB(OFConstants.ANN_VIEWED, mapValue, 0);
    }

    public void handleSeen(String id){
        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);

        ArrayList<String> inboxIdList;
        inboxIdList = shp.getSeenInboxAnnounceList();

        if(!inboxIdList.contains(id)){
            inboxIdList.add(id);
            shp.setSeenInboxAnnounceList(inboxIdList);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        WebView webContent;
        OFCustomTextViewBold tvCategoryName;
        OFCustomTextView tvDate;
        OFCustomTextViewBold tvTitle;
        OFCustomTextViewBold tvAction;
        View seenView;
        LinearLayoutCompat llMain;
        LinearLayoutCompat llAction;
        ImageView ivForward;

        public MyViewHolder(View view) {
            super(view);
            webContent = view.findViewById(R.id.webView);
            tvCategoryName = view.findViewById(R.id.tvCategoryName);
            tvDate = view.findViewById(R.id.tvDate);
            tvAction = view.findViewById(R.id.tvAction);
            tvTitle = view.findViewById(R.id.tvTitle);
            seenView = view.findViewById(R.id.seenView);
            llMain = view.findViewById(R.id.llMain);
            llAction = view.findViewById(R.id.llAction);
            ivForward = view.findViewById(R.id.ivForward);
        }

    }

    public OFAnnouncementListAdapter(Context context, ArrayList<OFGetAnnouncementDetailResponse> itemsList) {
        OFHelper.v(this.getClass().getName(), "OneFlow size[" + itemsList.size() + "]");
        this.mContext = context;
        this.itemsList = itemsList;
        shp = OFOneFlowSHP.getInstance(mContext);
    }

    public void notifyMyList(ArrayList<OFGetAnnouncementDetailResponse> arrayList) {
        this.itemsList = arrayList;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_list_single_item, parent, false);


        return new MyViewHolder(itemView);
    }


    @Override
    public int getItemCount() {

        return itemsList.size();
    }
}