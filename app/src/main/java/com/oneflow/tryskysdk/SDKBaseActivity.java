package com.oneflow.tryskysdk;

import androidx.appcompat.app.AppCompatActivity;

import com.oneflow.tryskysdk.model.survey.GetSurveyListResponse;
import com.oneflow.tryskysdk.sdkdb.OneFlowSHP;
import com.oneflow.tryskysdk.utils.Helper;

import java.util.ArrayList;

public class SDKBaseActivity extends AppCompatActivity {
    /**
     * This method will check if trigger name is available in the list or not
     *
     * @param type
     * @return
     */
    //private ArrayList<SurveyScreens> checkSurveyTitleAndScreens(String type){
    public GetSurveyListResponse checkSurveyTitleAndScreens(String type) {
        ArrayList<GetSurveyListResponse> slr = new OneFlowSHP(this).getSurveyList();
        GetSurveyListResponse gslr = null;
        //ArrayList<SurveyScreens> surveyScreens = null;
        int counter = 0;
        String tag = this.getClass().getName();
        Helper.v(tag, "OneFlow list size[" + slr.size() + "]type[" + type + "]");
        for (GetSurveyListResponse item : slr) {
            if (item.getTrigger_event_name().equalsIgnoreCase(type)) {
                gslr = item;
                /*surveyScreens = item.getScreens();
                selectedSurveyId = item.get_id();
                themeColor = item.getThemeColor();*/
                // Helper.v(tag,"OneFlow survey found at ["+(counter++)+"]triggerName["+item.getTrigger_event_name()+"]queSize["+item.getScreens().size()+"]");
                // Helper.v(tag,"OneFlow survey queSize["+new Gson().toJson(item.getScreens())+"]");
                /*int i=0;
                while(i<item.getScreens().size()) {
                    try {
                        if(item.getScreens().get(i).getInput()!=null) {
                            Helper.v(tag, "OneFlow input type["+i+"][" + item.getScreens().get(i).getInput().getInput_type() + "]");
                        }else{
                            Helper.v(tag,"OneFlow found null");
                        }
                    }catch(Exception ex){

                    }
                i++;
                }*/
                break;
            }
        }
        return gslr;
    }
}
