package com.example.liquanfei.xposedtest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by liquanfei on 2018/4/19.
 */

public class MainXP implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.startsWith("android") ||
                lpparam.packageName.startsWith("com.android") ||
                lpparam.packageName.startsWith("com.mi") ||
                lpparam.packageName.startsWith("com.qti") ||
                lpparam.packageName.startsWith("com.qualcomm") ||
                lpparam.packageName.startsWith("com.xiaomi") ||
                lpparam.packageName.startsWith("de.robv.android") ||
                lpparam.packageName.startsWith("system")) {
            XposedBridge.log("XposedTest skip " + lpparam.packageName);
            return;
        }
        Log.e("xx", "do xposed " + lpparam.packageName + " : " + lpparam.processName);
        HotXposed.hook(HookDispatcher.class, lpparam);
    }
}
