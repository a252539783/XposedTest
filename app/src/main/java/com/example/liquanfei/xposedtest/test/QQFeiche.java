package com.example.liquanfei.xposedtest.test;

import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.InputQueue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.liquanfei.xposedtest.ProcessUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class QQFeiche {

    private static MotionEvent sCurrentMotionEvent = null;
    private static long sLastDownMills = -1;
    private static Application sApplication;

    private static WindowManager.LayoutParams params;
    private static Point mMaxSize = new Point();
    private static View sView = null;
    private static Handler sMainHandler = null;
    private static InputQueue sInputQueue = null;
    private static Method sSendMotionEvent = null;
    private static long sInputQueuePtr = -1;

    public static void test(final XC_LoadPackage.LoadPackageParam lpparam) {

        if ("com.tencent.tmgp.speedmobile".equals(lpparam.packageName)) {

            try {
                final Class viewRootClass = lpparam.classLoader.loadClass("android.view.ViewRootImpl");
                XposedHelpers.findAndHookMethod(viewRootClass,
                        "setView", View.class, WindowManager.LayoutParams.class, View.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                if (sInputQueue == null) {
                                    Field f = viewRootClass.getDeclaredField("mInputQueue");
                                    f.setAccessible(true);
                                    sInputQueue = (InputQueue) f.get(param.thisObject);
                                    if (sInputQueue != null) {
                                        Method m = InputQueue.class.getDeclaredMethod("getNativePtr");
                                        m.setAccessible(true);
                                        sInputQueuePtr = (long) m.invoke(sInputQueue);
                                        sSendMotionEvent = InputQueue.class.getDeclaredMethod("nativeSendMotionEvent", long.class, MotionEvent.class);
                                        sSendMotionEvent.setAccessible(true);
                                        Log.e("xx", "find InputQueue");
                                    }
                                }
                            }
                        });
            } catch (ClassNotFoundException e) {
                Log.e("xx", e.toString());
            }
            XposedHelpers.findAndHookMethod(View.class, "dispatchTouchEvent", MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (sCurrentMotionEvent == param.args[0]) {
                        return;
                    }
                    sCurrentMotionEvent = (MotionEvent) param.args[0];
                    doEvent(sCurrentMotionEvent);
                }
            });
            XposedHelpers.findAndHookMethod(ViewGroup.class, "dispatchTouchEvent", MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (sCurrentMotionEvent == param.args[0]) {
                        return;
                    }
                    sCurrentMotionEvent = (MotionEvent) param.args[0];
                    doEvent(sCurrentMotionEvent);
                }
            });
//            try {
//                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("android.view.ViewRootImpl$NativePostImeInputStage"),
//                        "onProcess", "android.view.ViewRootImpl$QueuedInputEvent", new XC_MethodReplacement() {
//                    @Override
//                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                        return 3;
//                    }
//                });
//            } catch (ClassNotFoundException e) {
//                Log.e("xx", e.toString());
//            }
            XposedHelpers.findAndHookMethod(InputQueue.class, "sendInputEvent", long.class, MotionEvent.class,  new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return true;
                }
            });
//            final Field var3 = Activity.class.getDeclaredField("mCalled");
//            var3.setAccessible(true);
//            final Method onPause = Activity.class.getDeclaredMethod("onPause"),
//                    onStop = Activity.class.getDeclaredMethod("onStop");
//            onPause.setAccessible(true);
//            onStop.setAccessible(true);
//
//            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.tmgp.speedmobile.speedmobile"), "onPause", new XC_MethodReplacement() {
//                @Override
//                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
//                    SuperMethodUtil.invoke(onPause, var1.thisObject);
//                    return null;
//                }
//            });
//            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.tmgp.speedmobile.speedmobile"), "onStop", new XC_MethodReplacement() {
//                @Override
//                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
//                    SuperMethodUtil.invoke(onStop, var1.thisObject);
//                    return null;
//                }
//            });
//            XposedHelpers.findAndHookMethod(Application.class, "dispatchActivityPaused", Activity.class, new XC_MethodReplacement() {
//                @Override
//                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
//                    return null;
//                }
//            });
//            XposedHelpers.findAndHookMethod(Application.class, "dispatchActivityStopped", Activity.class, new XC_MethodReplacement() {
//                @Override
//                protected Object replaceHookedMethod(MethodHookParam var1) throws Throwable {
//                    return null;
//                }
//            });
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    sApplication = (Application) param.thisObject;
                    if (ProcessUtils.isMainProcess(sApplication)) {
                        (sMainHandler = new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                WindowManager wm = (WindowManager) sApplication.getSystemService(Context.WINDOW_SERVICE);

                                wm.getDefaultDisplay().getSize(mMaxSize);
                                params = new WindowManager.LayoutParams();
                                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.) {
//                                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//                                } else {
                                params.type = WindowManager.LayoutParams.TYPE_PHONE;
//                                }
                                params.width = mMaxSize.x / 3;
                                params.height = 20;
                                params.gravity = Gravity.LEFT | Gravity.TOP;
                                params.format = PixelFormat.RGBA_8888;
                                params.y = mMaxSize.y / 8;
                                params.x = mMaxSize.x / 3;
                                View v = new ProcessView(sApplication);
                                sView = v;
                                v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                wm.addView(v, params);
                                Log.e("xx", "addView " + params.width + " : " + params.height + " :: " + params.x + " : " + params.y);

                                params = new WindowManager.LayoutParams();
                                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.) {
//                                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//                                } else {
                                params.type = WindowManager.LayoutParams.TYPE_PHONE;
//                                }
                                params.width = 100;
                                params.height = 100;
                                params.gravity = Gravity.LEFT | Gravity.TOP;
                                params.format = PixelFormat.RGBA_8888;
                                params.y = mMaxSize.y - 100;
                                params.x = mMaxSize.x / 2 - 100;
                                v = new TextView(sApplication);
                                v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                v.setBackgroundColor(0x8800ff00);
                                v.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        long time = SystemClock.uptimeMillis();
                                        MotionEvent event = MotionEvent.obtain(time, time, MotionEvent.ACTION_POINTER_DOWN, 580, 850, 0);
                                        injectEvent(event);

                                        sMainHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                sLastDownMills = -1;
                                                long time = SystemClock.uptimeMillis();
                                                MotionEvent event = MotionEvent.obtain(time, time, MotionEvent.ACTION_POINTER_DOWN, 580, 850, 0);
                                                injectEvent(event);
                                            }
                                        }, 480);
                                    }
                                });
//                                wm.addView(v, params);
                                Log.e("xx", "addView " + params.width + " : " + params.height + " :: " + params.x + " : " + params.y);
                            }
                        }, 5000);
                    }
                }
            });
        }
    }

    private static class ProcessView extends View {

        Paint p = new Paint();
        Paint p2 = new Paint();
        float bar = -1;

        public ProcessView(Context context) {
            super(context);
            p.setColor(Color.RED);
            p2.setColor(Color.BLUE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (sLastDownMills <= 0) {
                return;
            }
            long pressTime = SystemClock.uptimeMillis() - sLastDownMills;
            if (pressTime <= 0 || pressTime >= 1000) {
                return;
            }
            if (bar == -1) {
                bar = getWidth() /4 * 3;
            }
            // 475 - 505
            canvas.drawRect(0, 0,
                     bar * pressTime / 485f,
                    getHeight(), p);
            canvas.drawRect(bar, 0, bar + 10, getHeight(), p2);
            invalidate();
        }
    }

    private static void doEvent(MotionEvent event) {
        int size = event.getPointerCount();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                for (int i = 0; i < size; i++) {
                    if (event.getX(i) < 960) {
                        // 发现左边按键按下
                        sLastDownMills = SystemClock.uptimeMillis();
                        if (sView != null) {
                            sView.invalidate();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                for (int i = 0; i < size; i++) {
                    if (event.getX(i) < 960) {
                        // 发现左边按键抬起
                        Log.e("xx", "press time " + (SystemClock.uptimeMillis() - sLastDownMills));
                        sLastDownMills = -1;
                        if (sView != null) {
                            sView.invalidate();
                        }
                    }
                }
                break;
            default:
        }
    }

    private static void injectEvent(MotionEvent event) {
        if (sInputQueue != null) {
            try {
                sSendMotionEvent.invoke(sInputQueue, sInputQueuePtr, event);
            } catch (Throwable e) {
                Log.e("xx",  e.toString());
            }
        }
    }
}
