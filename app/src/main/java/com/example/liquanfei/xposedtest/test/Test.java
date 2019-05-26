package com.example.liquanfei.xposedtest.test;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Test {


    public static void test(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {

        if (lpparam.packageName.equals("com.bytedance.apm.myapplication") ||
                lpparam.packageName.equals("com.example.crash.test") ||
                lpparam.packageName.equals("com.ss.android.article.news")) {
            Log.e("xx", "do test");
//        if (lpparam.packageName.equals("com.example.crash.test")) {
//        if (lpparam.packageName.equals("com.example.liquanfei.xposedtest")) {
//            XposedHelpers.findAndHookMethod(Handler.class, "sendMessageAtTime", Message.class, long.class, new XC_MethodHook() {
//                boolean inited = false;
//
//                @Override
//                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    Log.e("xx", "send msg " + param.args[0], new RuntimeException());
//                }
//            });
//            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("android.os.BinderProxy"),
//                    "transact", int.class, Parcel.class, Parcel.class, int.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            Log.e("binderHook", "binder obj: " + param.thisObject + "\n" + Log.getStackTraceString(new RuntimeException()));
//                        }
//                    });
            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.bytedance.apm.report.LogReportManager"),
                    "sendLog",long.class, String.class, byte[].class, int.class, String.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.e("sendLog", new String((byte[])param.args[2]));
                        }
                    });
            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.bytedance.crash.runtime.NpthHandlerThread"),
                    "getDefaultHandlerThread", new XC_MethodHook() {
                long time;
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            time = SystemClock.uptimeMillis();
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.e("xx", "cost " + (SystemClock.uptimeMillis() - time));
                        }
                    });
//            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.bytedance.frameworks.core.apm.SqlExecutor"),
//                    "insertBatch", List.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            List<ContentValues> cvs = (List<ContentValues>) param.args[0];
//                            for (ContentValues cv: cvs) {
//                                Log.e("sendLog", "save " + cv.toString());
//                            }
//                        }
//                    });

//            for (final Method m : lpparam.classLoader.loadClass("android.app.ActivityThread$ApplicationThread").getDeclaredMethods()) {
//                ArrayList params = new ArrayList();
//                params.addAll(Arrays.asList(m.getParameterTypes()));
//                params.add(new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        Log.e("xx",m.getName());
//                    }
//                });
//                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("android.app.ActivityThread$ApplicationThread"),
//                        m.getName(), params.toArray());
//            }
//
//
//            for (final Method m : lpparam.classLoader.loadClass("android.view.ViewRootImpl$W").getDeclaredMethods()) {
//                ArrayList params = new ArrayList();
//                params.addAll(Arrays.asList(m.getParameterTypes()));
//                params.add(new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        Log.e("xx",m.getName());
//                    }
//                });
//                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("android.view.ViewRootImpl$W"),
//                        m.getName(), params.toArray());
//            }
        }
    }
}
