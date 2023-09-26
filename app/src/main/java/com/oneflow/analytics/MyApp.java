package com.oneflow.analytics;

import android.app.Application;
import android.os.StrictMode;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.oneflow.analytics.utils.OFHelper;

public class MyApp extends Application implements LifecycleObserver {

    String tag = this.getClass().getName();

    @Override
    public void onCreate() {
        super.onCreate();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }
}
