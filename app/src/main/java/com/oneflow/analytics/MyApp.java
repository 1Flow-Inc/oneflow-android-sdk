package com.oneflow.analytics;

import android.app.Application;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;


public class MyApp extends Application implements LifecycleObserver {

    String tag = this.getClass().getName();

    @Override
    public void onCreate() {
        super.onCreate();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }
}
