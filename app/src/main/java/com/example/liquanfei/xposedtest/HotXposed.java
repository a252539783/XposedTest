package com.example.liquanfei.xposedtest;


import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HotXposed {
    public static void hook(Class clazz, XC_LoadPackage.LoadPackageParam lpparam)
            throws Exception {
        String packageName = clazz.getName().replace("." + clazz.getSimpleName(), "");
        File apkFile = getApkFile(packageName);

        if (!apkFile.exists()) {
            Log.e("error", "apk file not found");
            return;
        }

        filterNotify(lpparam);

        PathClassLoader classLoader =
                new PathClassLoader(apkFile.getAbsolutePath(), getLibFile(apkFile).getAbsolutePath(), lpparam.classLoader);
        try {
            Field parentField = ClassLoader.class.getDeclaredField("parent");
            parentField.setAccessible(true);
//            parentField.set(lpparam.getClass().getClassLoader(), parentField.get(lpparam.classLoader));
            parentField.set(lpparam.classLoader, lpparam.getClass().getClassLoader());
//            parentField.set(classLoader, lpparam.classLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Class cls = classLoader.loadClass(clazz.getName());
        Method method = cls.getDeclaredMethod("dispatch", XC_LoadPackage.LoadPackageParam.class);
        method.setAccessible(true);
        method.invoke(cls.newInstance(), lpparam);
//        XposedHelpers.callMethod(classLoader.loadClass(clazz.getName()).newInstance(), "dispatch", lpparam);

    }

    private static void filterNotify(XC_LoadPackage.LoadPackageParam lpparam)
            throws ClassNotFoundException {
        if ("de.robv.android.xposed.installer".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("de.robv.android.xposed.installer.util.NotificationUtil"),
                    "showModulesUpdatedNotification", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(new Object());
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    });
        }
    }


    private static File getApkFile(String packageName) {
        String filePath = String.format("/data/app/%s-%s/base.apk", packageName, 1);
        File apkFile = new File(filePath);
        if (!apkFile.exists()) {
            filePath = String.format("/data/app/%s-%s/base.apk", packageName, 2);
            apkFile = new File(filePath);
        }
        return apkFile;
    }

    private static File getLibFile(File apkFile) {
        String appFile = apkFile.getParent();

        File libFile = new File(appFile + "/lib/arm/");
        if (libFile.exists()) {
            return libFile;
        }

        libFile = new File(appFile + "/lib/arm64/");
        if (libFile.exists()) {
            return libFile;
        }

        return libFile;
    }
}
