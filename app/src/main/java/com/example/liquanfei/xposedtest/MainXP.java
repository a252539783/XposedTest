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
        HotXposed.hook(HookDispatcher.class, lpparam);
    }
}
