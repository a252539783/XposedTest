package com.example.liquanfei.xposedtest;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ThreadHook {

    static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            if (!lpparam.packageName.startsWith("android")) {
                if (sHandleField == null) {
                    sHandleField = Thread.class.getDeclaredField("uncaughtExceptionHandler");
                    sHandleField.setAccessible(true);

                    XposedHelpers.findAndHookMethod(Thread.class, "start", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);

                            Thread t = (Thread) param.thisObject;
                            t.setUncaughtExceptionHandler(t.getUncaughtExceptionHandler());
                        }
                    });
                    XposedHelpers.findAndHookMethod(Thread.class, "setUncaughtExceptionHandler",
                            Thread.UncaughtExceptionHandler.class, new ExceptionHandlerHook());
                    XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class,
                            new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    if (Looper.getMainLooper() == null) {
                                        Log.e("ExceptionHandlerHook", "main looper is null");
                                        return;
                                    }
                                    Thread t = Looper.getMainLooper().getThread();
                                    if (t == null) {
                                        Log.e("ExceptionHandlerHook", "main thread is null");
                                        return;
                                    }

                                    t.setUncaughtExceptionHandler(t.getUncaughtExceptionHandler());
                                }
                            });
                }
            }
        } catch (Throwable e) {
            Log.e("ExceptionHandlerHook", Log.getStackTraceString(e));
        }
    }

    static Field sHandleField = null;

    private static class ExceptionHandlerHook extends XC_MethodHook {
        private Thread.UncaughtExceptionHandler defaultHandler = null;
        HashMap<Object, Thread.UncaughtExceptionHandler> handlerMap = new HashMap<>();
        private Thread.UncaughtExceptionHandler mHandler = new PrintHandler();

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Thread.UncaughtExceptionHandler handler = null;
            if (param.args[0] != null) {
                handler = (Thread.UncaughtExceptionHandler) param.args[0];
            }

            if (handler instanceof PrintHandler) {
                handlerMap.put(param.thisObject, handler);
            }
            param.args[0] = mHandler;
        }

        private class PrintHandler implements Thread.UncaughtExceptionHandler {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("ExceptionHandlerHook", Log.getStackTraceString(e));

                Thread.UncaughtExceptionHandler handler = handlerMap.get(t);
                if (handler == null) {
                    handler = t.getThreadGroup();
                }
                if (handler != null) {
                    handler.uncaughtException(t, e);
                }
            }
        }
    }
}
