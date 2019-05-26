package com.example.liquanfei.xposedtest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;

import com.example.liquanfei.xposedtest.test.TTMain;
import com.example.liquanfei.xposedtest.test.Test;
import com.example.liquanfei.xposedtest.test.XiguaNpth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookDispatcher implements IHookerDispatcher {
    @Override
    public void dispatch(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Stable.test(lpparam);
        TTMain.test(lpparam);
//        Test.test(lpparam);
        XiguaNpth.test(lpparam);
//        ThreadHook.hook(lpparam);
    }
}
