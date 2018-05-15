package com.example.liquanfei.xposedtest;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;
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

        if (lpparam.packageName.equals("com.ushaqi.zhuishushenqi")) {
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "setSystemUiVisibility", new Object[]{Integer.TYPE, new XC_MethodReplacement() {
                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
                    return null;
                }
            }});
        }
        if (lpparam.packageName.equals("ly.pp.justpiano2")) {
            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("ly.pp.justpiano2.JustPiano2"), "onPause", new Object[]{new XC_MethodReplacement() {
                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
                    Method var2 = Activity.class.getDeclaredMethod("onPause");
                    var2.setAccessible(true);
                    Field var3 = Activity.class.getDeclaredField("mCalled");
                    var3.setAccessible(true);
                    var3.set(var1.thisObject, true);
                    return null;
                }
            }});
        }
    }
}
