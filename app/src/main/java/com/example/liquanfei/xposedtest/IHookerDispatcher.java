package com.example.liquanfei.xposedtest;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface IHookerDispatcher {
    void dispatch(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable;
}
