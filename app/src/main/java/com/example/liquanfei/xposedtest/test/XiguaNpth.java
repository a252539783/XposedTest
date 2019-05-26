package com.example.liquanfei.xposedtest.test;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;

import com.example.liquanfei.xposedtest.ActivityDataManager;
import com.example.liquanfei.xposedtest.ProcessUtils;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author liquanfei
 */
public class XiguaNpth {

    static int activityIndex = 0;
    public static void test(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException, NoSuchFieldException {
        if (false || (!lpparam.packageName.equals("com.ss.android.article.video")
        && (!lpparam.packageName.equals("com.lemon.faceu")))) {
            return;
        }//

        Log.e("xx", "doXiGua");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            boolean inited = false;
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (!inited) {
                    inited = true;
                    final Application app = (Application) param.thisObject;
                    if (!ProcessUtils.isMainProcess(app)) {
                        final Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((Handler) null).post(this);
                            }
                        });
                    }
                }
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
//                final Handler handler = new Handler();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((Handler) null).post(this);
//                    }
//                });
            }
        });

//        boolean porguard = false;
//        Class LaunchMonitor;
//        if (!porguard) {
//            LaunchMonitor = lpparam.classLoader.loadClass("com.bytedance.framwork.core.monitor.MonitorUtils");
//        } else {
//            LaunchMonitor = lpparam.classLoader.loadClass("com.bytedance.framwork.core.monitor.MonitorUtils");
//        }
//
//        if (!porguard) {
//            XposedHelpers.findAndHookMethod(LaunchMonitor,
//                    "monitorDirectOnTimer", String.class, String.class, float.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            String key = (String) param.args[0];
//                            if (key.equals("applicationToFeedShown")) {
//                                Log.e("app_launch", key + ":" + param.args[2]);
//                            }
//                        }
//                    });
//        } else {
//            XposedHelpers.findAndHookMethod(LaunchMonitor,
//                    "monitorDirectOnTimer", String.class, String.class, float.class, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            String key = (String) param.args[0];
//                            if (key.equals("applicationToFeedShown")) {
//                                Log.e("app_launch", key + ":" + param.args[2]);
//                            }
//                        }
//                    });
//        }


        XposedHelpers.findAndHookMethod(Thread.class,
                "setDefaultUncaughtExceptionHandler", Thread.UncaughtExceptionHandler.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.e("xx", "setDefaultUncaughtExceptionHandler\n" + Log.getStackTraceString(new RuntimeException()));
                    }
                });


//            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.bytedance.crash.e.a"),
//                    "a", new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            Log.e("npth", "call getParamsMap: \n" + Log.getStackTraceString(new RuntimeException()));
//                            ((Handler)null).post(null);
////                            String key = param.args[0].toString();
//                        }
//                    });
        startTime = SystemClock.uptimeMillis();
        if (false) // binder记录
            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("android.os.BinderProxy"),
                    "transact", int.class, Parcel.class, Parcel.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Parcel send = (Parcel) param.args[1], recv = (Parcel) param.args[2];
                            String key = "" + (SystemClock.uptimeMillis() - startTime);//Thread.currentThread().getStackTrace()[5].toString();
                            String info = "thread id: " + Process.myPid() + "  name: " + Thread.currentThread().getName() +
                                    "send :" + send.dataSize() + " recv :" + recv.dataSize() + " total :" + (send.dataSize() + recv.dataSize()) + "\n" +
                                    Log.getStackTraceString(new RuntimeException());
                            if (false) {
                                Log.w("binderHook", info);
                            }
                            int time = (int) (SystemClock.uptimeMillis() - startTime);
                            synchronized (sBinderList) {
                                LinkedList<String> list;
                                if ((list = sBinderList.get(send.dataSize() + recv.dataSize())) == null) {
                                    list = new LinkedList<>();
                                    sBinderList.put(send.dataSize() + recv.dataSize(), list);
                                }
                                list.add(info);
                            }

                            if (shouldWrite) {
                                synchronized (sBinderList) {
                                    if (shouldWrite) {
                                        shouldWrite = false;
                                        long all = 0;
                                        StringBuilder res = new StringBuilder();
                                        for (int i = sBinderList.size() - 1; i >= 0; i--) {
                                            LinkedList<String> list = sBinderList.valueAt(i);
                                            all += list.size();
                                            for (String s : list) {
                                                res.append(s).append("\n");
                                            }
                                        }
                                        res.append("all : ").append(all).append("\n");
                                        Log.e("binderWrite", "/sdcard/binderHook/binderMap_" + Process.myPid());
                                        writeToFile("/sdcard/binderHook/binderMap_" + Process.myPid(), res.toString().getBytes());
                                    }
                                }
                            }
                        }
                    });
    }

    static HashMap<String, Integer> sBinderMap = new HashMap<>();
    static SparseArray<LinkedList<String>> sBinderList = new SparseArray<>();
    static long startTime = 0;
    static long endTime = 0;
    static boolean shouldWrite = false;


    public static void writeToFile(String file, byte[] src) throws IOException {
        File f = new File(file);
        f.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(src);
        fos.close();
    }
}
