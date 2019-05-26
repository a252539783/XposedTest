package com.example.liquanfei.xposedtest.test;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class QQFeiche {
    public static void test(final XC_LoadPackage.LoadPackageParam lpparam) {

        if ("com.tencent.tmgp.speedmobile".equals(lpparam.packageName)) {
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
        }
    }
}
