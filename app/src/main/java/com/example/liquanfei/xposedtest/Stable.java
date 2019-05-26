package com.example.liquanfei.xposedtest;

import android.app.Activity;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Stable {
    public static void test(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        //替换追书神器应用中的所有View.setSystemUiVisibility,替换为一个空方法
        if ("com.ushaqi.zhuishushenqi".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "setSystemUiVisibility", Integer.TYPE, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
                    return null;
                }
            });
        }

        //替换极品钢琴2中JustPiano2类中的onPause方法
        if ("ly.pp.justpiano2".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("ly.pp.justpiano2.JustPiano2"), "onPause", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
                    Field var3 = Activity.class.getDeclaredField("mCalled");
                    var3.setAccessible(true);
                    var3.set(var1.thisObject, true);
                    return null;
                }
            });
        }
    }
}
