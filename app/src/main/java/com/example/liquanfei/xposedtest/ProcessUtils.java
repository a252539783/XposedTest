package com.example.liquanfei.xposedtest;

import android.app.ActivityManager;
import android.content.Context;

/**
 * 进程名称的获取和判断
 * <p>
 * Created by wanlipeng on 2018/5/21 下午9:58
 */
public final class ProcessUtils {
    /**
     * 当前进程的名称
     */
    private static String sCurrentProcessName;

    /**
     * 判断当前进程是否是主进程
     *
     * @return
     */
    public static boolean isMainProcess(Context context) {
        if (sCurrentProcessName == null) {
            sCurrentProcessName = getCurrentProcessName(context);
        }
        return context.getPackageName().equals(sCurrentProcessName);
    }

    /**
     * 是否是watch进程
     *
     * @param context
     * @return
     */
    public static boolean isWatchProcess(Context context) {
        if (sCurrentProcessName == null) {
            sCurrentProcessName = getCurrentProcessName(context);
        }
        return sCurrentProcessName != null && sCurrentProcessName.endsWith(":doctorx");
    }

    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return sCurrentProcessName = appProcess.processName;
            }
        }
        return null;
    }
}
