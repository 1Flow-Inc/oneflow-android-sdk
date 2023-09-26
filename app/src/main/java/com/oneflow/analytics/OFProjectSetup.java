package com.oneflow.analytics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.oneflow.analytics.customwidgets.OFCustomEditText;
import com.oneflow.analytics.utils.OFHelper;

public class OFProjectSetup extends OFSDKBaseActivity{

    OFCustomEditText projectIdEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.project_setup);
        projectIdEditText = (OFCustomEditText)findViewById(R.id.project_id);

    }

    public void submitHandle(View v){

        String projectId = "oneflow_sandbox_2Z9e492aa1qH22E2SnoSAT5broVR80RF9EXhQ0UcOTyZNgDRCsS4Y88hG4mL+IjPURFgrvCIsuNtUinVIr/ClQ==";
        if(OFHelper.validateString(projectId).equalsIgnoreCase("NA")){
            OFHelper.makeText(OFProjectSetup.this,"Please enter project id",0);
        }else{
            startActivity(new Intent(OFProjectSetup.this,OFFirstActivity.class).putExtra("project_id",projectId));
        }

    }
}
