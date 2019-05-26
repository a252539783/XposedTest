package com.example.liquanfei.xposedtest.test;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;
import android.util.SparseArray;

import com.example.liquanfei.xposedtest.ActivityDataManager;
import com.example.liquanfei.xposedtest.ProcessUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TTMain {

    static int activityIndex = 0;
    public static void test(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException, NoSuchFieldException {
        if (false || (!lpparam.packageName.equals("com.ss.android.article.news") &&
                !lpparam.packageName.equals("asdcom.bytedance.apm.myapplication"))) {
            return;
        }//

            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                boolean inited = false;
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (!inited) {
                        inited = true;
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                shouldWrite = true;
                            }
                        }, 5000);
                        final Application app = (Application) param.thisObject;
                        curProcessName = ProcessUtils.getCurrentProcessName(app);
                        if (ProcessUtils.isMainProcess(app)) {
//                            CrashTrigger.install(app);
                            final Handler handler = new Handler();
                            final ActivityDataManager manager = new ActivityDataManager(app);
                            manager.setMaxCount(10);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SystemClock.sleep(15000);
//                                    ((Handler)handler).postDelayed(this, 45000);
//                                    NativeCrashImpl.NativeCrashMapT();
                                }
                            }, 5000);
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    JSONArray array = manager.getRecentRecord();
//                                    activityIndex++;
//                                    try {
//                                        writeToFile("/sdcard/test/" + activityIndex + ".txt", array.toString().getBytes());
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    handler.postDelayed(this, 50000);
//                                }
//                            }, 50000);
                        }
                    }
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


        XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.bytedance.crash.nativecrash.NativeCrashCollector"),
                "onNativeCrash", String.class, String.class, String[].class, String[].class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e("xx", "native crash");
                        return false;
                    }

//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        Log.e("xx", "setMessageLogging\n" + Log.getStackTraceString(new RuntimeException()));
//                    }
                });


            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.bytedance.crash.k.d"),
                    "a", File.class, String.class, boolean.class, new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
//                            ((File)param.args[0]).getParentFile().mkdirs();
                            Log.e("xx", "before writeFile\n" + Log.getStackTraceString(new RuntimeException()));
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
//                            if (Looper.getMainLooper() == Looper.myLooper()) {
//                                Log.e("mainLooper", "error \n" + Log.getStackTraceString(new RuntimeException()));
//                            }
//                            String key = param.args[0].toString();
                        }
                    });
        startTime = SystemClock.uptimeMillis();
        if (true) // binder记录
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
//                            synchronized (sBinderList) {
//                                LinkedList<String> list;
//                                if ((list = sBinderList.get(send.dataSize() + recv.dataSize())) == null) {
//                                    list = new LinkedList<>();
//                                    sBinderList.put(send.dataSize() + recv.dataSize(), list);
//                                }
//                                list.add(info);
//                            }

                            String threadKey = Thread.currentThread().getName() + "_" + Process.myTid();
                            SparseArray<LinkedList<String>> binderList = sThreadBinderList.get(threadKey);
                            if (binderList == null) {
                                synchronized (sThreadBinderList) {
                                    binderList = sThreadBinderList.get(threadKey);
                                    if (binderList == null) {
                                        binderList = new SparseArray<>();
                                        sThreadBinderList.put(threadKey, binderList);
                                    }
                                }
                            }
                            LinkedList<String> list = binderList.get(send.dataSize() + recv.dataSize());
                            if (list == null) {
                                synchronized (binderList) {
                                    list = binderList.get(send.dataSize() + recv.dataSize());
                                    if (list == null) {
                                        list = new LinkedList<>();
                                        binderList.put(send.dataSize() + recv.dataSize(), list);
                                    }
                                    list.add(info);
                                }
                            }
                            list.add(info);

                            if (shouldWrite) {
//                                synchronized (sBinderList) {
//                                    if (shouldWrite) {
//                                        shouldWrite = false;
//                                        long all = 0;
//                                        StringBuilder res = new StringBuilder();
//                                        for (int i = sBinderList.size() - 1; i >= 0; i--) {
//                                            LinkedList<String> list = sBinderList.valueAt(i);
//                                            all += list.size();
//                                            for (String s : list) {
//                                                res.append(s).append("\n");
//                                            }
//                                        }
//                                        res.append("all : ").append(all).append("\n");
//                                        Log.e("binderWrite", "/sdcard/binderHook/binderMap_" + Process.myPid());
//                                        writeToFile("/sdcard/binderHook/binderMap_" + Process.myPid(), res.toString().getBytes());
//                                    }
//                                }
                                synchronized (sThreadBinderList) {
                                    if (shouldWrite) {
                                        shouldWrite = false;
                                        long all = 0;
                                        for (Map.Entry<String, SparseArray<LinkedList<String>>> entry : sThreadBinderList.entrySet()) {
                                            StringBuilder res = new StringBuilder();
                                            for (int i = entry.getValue().size() - 1; i >= 0; i--) {
                                                LinkedList<String> lists = entry.getValue().valueAt(i);
                                                all += lists.size();
                                                for (String s : lists) {
                                                    res.append(s).append("\n");
                                                }
                                            }
                                            res.append("all : ").append(all).append("\n");
                                            Log.e("binderWrite", "/sdcard/binderHook/" + curProcessName + "/" + entry.getKey() + " : " + entry.getValue().size());
                                            writeToFile("/sdcard/binderHook/" + curProcessName + "/" + entry.getKey(), res.toString().getBytes());
                                        }
                                    }
                                }
                            }
                        }
                    });
    }

    static HashMap<String, Integer> sBinderMap = new HashMap<>();
    static HashMap<String, SparseArray<LinkedList<String>>> sThreadBinderList = new HashMap<>();
    static SparseArray<LinkedList<String>> sBinderList = new SparseArray<>();
    static String curProcessName = "";
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

