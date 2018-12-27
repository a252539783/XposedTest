package com.example.liquanfei.xposedtest;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Resources;

public class MApp extends Application {
    Application mApp;
    Resources mRes;
    public MApp(Application app, Resources res) {
        mApp = app;
        mRes = res;
    }

    @Override
    public Resources getResources() {
        return super.getResources();
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        mApp.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        mApp.unregisterComponentCallbacks(callback);
    }

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        mApp.registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        mApp.unregisterActivityLifecycleCallbacks(callback);
    }

    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        mApp.registerOnProvideAssistDataListener(callback);
    }

    @Override
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        mApp.unregisterOnProvideAssistDataListener(callback);
    }
}
