package com.example.liquanfei.xposedtest;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

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

        //替换追书神器应用中的所有View.setSystemUiVisibility,替换为一个空方法
        if (lpparam.packageName.equals("com.ushaqi.zhuishushenqi")) {
            XposedHelpers.findAndHookMethod("android.view.View", lpparam.classLoader, "setSystemUiVisibility", Integer.TYPE, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
                    return null;
                }
            });
        }

        //替换极品钢琴2中JustPiano2类中的onPause方法
        if (lpparam.packageName.equals("ly.pp.justpiano2")) {
            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("ly.pp.justpiano2.JustPiano2"), "onPause", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
                    Method var2 = Activity.class.getDeclaredMethod("onPause");
                    var2.setAccessible(true);
                    Field var3 = Activity.class.getDeclaredField("mCalled");
                    var3.setAccessible(true);
                    var3.set(var1.thisObject, true);
                    return null;
                }
            });
        }

        if (lpparam.packageName.equals("com.ss.android.article.news")) {

//            XposedHelpers.findAndHookMethod(
//                    lpparam.classLoader.loadClass("com.bytedance.framwork.core.monitor.internal.MonitorTraffic"),
//                    "reportTrafficOfLastTime", new XC_MethodHook() {
//                        Field field = lpparam.classLoader.loadClass("com.bytedance.framwork.core.monitor.internal.MonitorTraffic").getDeclaredField("TRAFFIC_OF_BG_MOBILE_THRESHOLD");
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            super.beforeHookedMethod(param);
//                            Log.e("xx", "reportTrafficOfLastTime");
//
//                            field.setAccessible(true);
//                            field.set(null, -1);
//                        }
//                    });

            XposedHelpers.findAndHookMethod(
                    lpparam.classLoader.loadClass("com.bytedance.framwork.core.monitor.internal.MonitorTraffic"),
                    "collectOnTimer", new XC_MethodHook() {
                        Method method = lpparam.classLoader.loadClass("com.bytedance.framwork.core.monitor.internal.MonitorTraffic").getDeclaredMethod("collectTrafficInfo");
                        Field field = lpparam.classLoader.loadClass("com.bytedance.framwork.core.monitor.internal.MonitorTraffic").getDeclaredField("COLLECT_TRAFFIC_INTERVAL");
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            Log.e("xx", "handleTrafficInfoUpdate");

                            method.setAccessible(true);
                            method.invoke(param.thisObject);
                        }
                    });

            XposedHelpers.findAndHookMethod(
                    lpparam.classLoader.loadClass("com.bytedance.framwork.core.monitor.MonitorUtils"),
                    "isBackground", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) {
                            return true;
                        }
                    });

            XposedHelpers.findAndHookMethod(
                    lpparam.classLoader.loadClass("com.bytedance.frameworks.core.monitor.MonitorContentProvider"),
                    "delete", Uri.class, String.class, String[].class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
                            var1.setResult(1);
                            return 1;
                        }
                    });

            XposedHelpers.findAndHookMethod(
                    lpparam.classLoader.loadClass("com.bytedance.frameworks.core.monitor.LogTaskManager"),
                    "logSend", String.class, String.class, JSONObject.class, boolean.class, boolean.class, new XC_MethodHook() {
                        Context c = null;
                        String aid = null;

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            param.args[3] = true;
                        }
                    });

            XposedHelpers.findAndHookMethod(
                    lpparam.classLoader.loadClass("com.bytedance.frameworks.core.monitor.LogReportManager"),
                    "sendLog",
                    String.class, JSONArray.class, JSONArray.class, JSONArray.class, long.class, boolean.class,
                    new XC_MethodHook() {
                        String aid = null;

                        ConcurrentHashMap impl = null;
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            boolean isFetch = (boolean) param.args[5];
                            Log.e("xx", "send data type: " + param.args[0] + ": " + isFetch);
                            Log.e("xx", "send data type: " + param.args[0] + " : data " + isFetch +
                                    param.args[1] != null ? param.args[1].toString() + " : " : "" +
                                    param.args[2] != null ? param.args[2].toString() + " : " : "" +
                                    param.args[3] != null ? param.args[3].toString() + " : " : "");
                            if (isFetch) {
                            }
                            super.afterHookedMethod(param);
                        }
                    });
        }
//        ThreadHook.hook(lpparam);
    }
}
