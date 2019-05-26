package com.example.liquanfei.xposedtest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * 崩溃情况下，Activity环境数据的收集
 */
public class ActivityDataManager {

    public static final String NAME = "name";
    public static final String TIME = "time";
    private Application mApplication;
    private Context mContext;
    // 记录当前还没销毁的Activity和创建时间
    private List<String> mAllAliveActivities = new ArrayList<>();
    private List<Long> mAllAliveActivitiesTime = new ArrayList<>();
    // 记录当前销毁的Activity和销毁时间
    private List<String> mFinishedActivities = new ArrayList<>();
    private List<Long> mFinishedActivitiesTime = new ArrayList<>();

    private final Queue<ActivityRecord> mActivityRecords = new LinkedList<>();
    private final SparseArray<Long> mCreateActivities = new SparseArray<>();
    private final SparseArray<Long> mStartActivities = new SparseArray<>();
    private final SparseArray<Long> mSavedActivities = new SparseArray<>();
    private final SparseArray<Long> mStopActivities = new SparseArray<>();
    private final SparseArray<Long> mDestroyActivities = new SparseArray<>();
    private final SparseArray<ActivityRecord> mAliveActivities = new SparseArray<>(); // 最后一次ActivityRecord记录
    //    private ActivityRecord mFrontActivity = null;
    private int mMaxCount = 30;

    private String mLastCreateActivity;
    private long mLastCreateActivityTime;
    private String mLastStartActivity;
    private long mLastStartActivityTime;
    private String mLastResumeActivity;
    private long mLastResumeActivityTime;
    private String mLastPauseActivity;
    private long mLastPauseActivityTime;
    private String mLastStopActivity;
    private long mLastStopActivityTime;

    public ActivityDataManager( Context context) {
        mContext = context;
        if (mContext instanceof Application) {
            mApplication = (Application) context;
        }
        registerActivityManager();
    }

    public void setMaxCount(int count) {
        mMaxCount = count;
    }

    private final Application.ActivityLifecycleCallbacks mLifecycleCallbacks =
            new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                    mLastCreateActivity = activity.getClass().getName();
                    mLastCreateActivityTime = System.currentTimeMillis();
                    mAllAliveActivities.add(mLastCreateActivity);
                    mAllAliveActivitiesTime.add(mLastCreateActivityTime);

                    int hashCode = System.identityHashCode(activity);
                    // onCreate时存下执行时间
                    mCreateActivities.put(hashCode, mLastCreateActivityTime);
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    mLastStartActivity = activity.getClass().getName();
                    mLastStartActivityTime = System.currentTimeMillis();

                    int hashCode = System.identityHashCode(activity);
                    // onCreate时存下执行时间
                    mStartActivities.put(hashCode, mLastStartActivityTime);
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    mLastResumeActivity = activity.getClass().getName();
                    mLastResumeActivityTime = System.currentTimeMillis();

                    int hashCode = System.identityHashCode(activity);
                    // 进入前台直接新增ActivityRecord
                    ActivityRecord record = null;
//                    if (mFrontActivity != null && mFrontActivity.mHashCode == hashCode) {
//                        record = mFrontActivity;
//                    } else {
                    Long createTime = mCreateActivities.get(hashCode, -1L);
                    Long startTime = mStartActivities.get(hashCode, -1L);
                    if (mActivityRecords.size() >= mMaxCount) {
                        record = mActivityRecords.poll();
                        if (record != null) {
                            mAliveActivities.remove(record.mHashCode);
                            mDestroyActivities.remove(record.mHashCode);
                            record.reset(mLastResumeActivity, hashCode, mLastResumeActivityTime);
                        }
                    }

                    if (record == null) {
                        record = new ActivityRecord(mLastResumeActivity, hashCode, mLastResumeActivityTime);
                    }
                    if (createTime != -1) {
                        record.addMethod("onCreate", createTime);
                        mCreateActivities.remove(hashCode);
                    }
                    if (startTime != -1) {
                        record.addMethod("onStart", startTime);
                        mStartActivities.remove(hashCode);
                    }
                    mAliveActivities.put(hashCode, record);
//                        mFrontActivity = record;
                    mActivityRecords.add(record);
//                    }
                    record.addMethod("onResume", mLastResumeActivityTime);
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    mLastPauseActivity = activity.getClass().getName();
                    mLastPauseActivityTime = System.currentTimeMillis();

                    int hashCode = System.identityHashCode(activity);
                    ActivityRecord record = mAliveActivities.get(hashCode);
                    if (record != null) {
                        if (hashCode == record.mHashCode) {
                            record.addMethod("onPause", mLastPauseActivityTime);
                            record.mBackTime = mLastPauseActivityTime;
                        } else {
                            Log.e("xx", "onPause diff resume");
                        }
                    } else {
                        Log.e("xx", "onPause not found activity");
                    }
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    mLastStopActivity = activity.getClass().getName();
                    mLastStopActivityTime = System.currentTimeMillis();

                    int hashCode = System.identityHashCode(activity);
                    ActivityRecord record = mAliveActivities.get(hashCode);
                    if (record != null) {
                        if (hashCode == record.mHashCode) {
                            record.addMethod("onStop", mLastStopActivityTime);
                        } else {
                            Log.e("xx", "onStop diff resume");
                        }
                    } else {
                        Log.e("xx", "onStop not found activity");
                    }
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    String activityName = activity.getClass().getName();
                    int index = mAllAliveActivities.indexOf(activityName);
                    if (index > -1 && index < mAllAliveActivities.size()) {
                        mAllAliveActivities.remove(index);
                        mAllAliveActivitiesTime.remove(index);
                    }
                    // 保存销毁的Activity名字和时间
                    mFinishedActivities.add(activityName);
                    long time = System.currentTimeMillis();
                    mFinishedActivitiesTime.add(time);

                    int hashCode = System.identityHashCode(activity);
                    ActivityRecord record = mAliveActivities.get(hashCode);
                    if (record != null) {
                        if (hashCode == record.mHashCode) {
                            record.addMethod("onDestroy", time);
                        } else {
                            Log.e("xx", "onDestroy diff resume");
                        }
                    } else {
                        Log.e("xx", "onDestroy not found activity");
                    }
                    mDestroyActivities.put(hashCode, time);
                    mAliveActivities.remove(hashCode);
                    mCreateActivities.remove(hashCode);
                    mStartActivities.remove(hashCode);
                }
            };

    private void registerActivityManager() {
        if (Build.VERSION.SDK_INT >= 14 && mApplication != null) {
            mApplication.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
        }
    }

    /**
     * 获取所有活动的Activity，使用|隔开
     *
     * @return 不存在的时候返回空字符串
     */
    private JSONArray getAllAliveActivities() {
        JSONArray activities = new JSONArray();
        if (mAllAliveActivities == null || mAllAliveActivities.isEmpty()) {
            return activities;
        }
        for (int i = 0; i < mAllAliveActivities.size(); i++) {
            final String name = mAllAliveActivities.get(i);
            final long time = mAllAliveActivitiesTime.get(i);
            activities.put(getActivityJson(name, time));
        }
        return activities;
    }

    /**
     * 返回所有访问过，执行了onDestroyed的Activity
     *
     * @return
     */
    private JSONArray getAllFinishedActivities() {
        JSONArray activities = new JSONArray();
        if (mFinishedActivities == null || mFinishedActivities.isEmpty()) {
            return activities;
        }
        for (int i = 0; i < mFinishedActivities.size(); i++) {
            final String name = mFinishedActivities.get(i);
            final long time = mFinishedActivitiesTime.get(i);
            activities.put(getActivityJson(name, time));
        }
        return activities;
    }

    private JSONObject getActivityJson(String name, long time) {
        JSONObject json = new JSONObject();
        try {
            json.put(NAME, name);
            json.put(TIME, time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 返回Activity的调用时间
     *
     * @return
     */
    public JSONObject getActivityTrace() {
        JSONObject trace = new JSONObject();
//        try {
//            // Activity的生命周期
//            trace.put(LAST_CREATE_ACTIVITY, getActivityJson(mLastCreateActivity, mLastCreateActivityTime));
//            trace.put(LAST_START_ACTIVITY, getActivityJson(mLastStartActivity, mLastStartActivityTime));
//            trace.put(LAST_RESUME_ACTIVITY, getActivityJson(mLastResumeActivity, mLastResumeActivityTime));
//            trace.put(LAST_PAUSE_ACTIVITY, getActivityJson(mLastPauseActivity, mLastPauseActivityTime));
//            trace.put(LAST_STOP_ACTIVITY, getActivityJson(mLastStopActivity, mLastStopActivityTime));
//
//            // 存活的Activity信息
//            trace.put(ALIVE_ACTIVITIES, getAllAliveActivities());
//
//            // 所有已经销毁的Activity信息
//            trace.put(FINISH_ACTIVITIES, getAllFinishedActivities());
//        } catch (JSONException ignored) {
//        }
        return trace;
    }

    /**
     * 获取运行时进程的信息，高版本只能拿到自己进程的
     *
     * @return
     */
    public JSONArray getRunningTasks() {
        JSONArray runningTaskJson = new JSONArray();
        try {
            ActivityManager mgr = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            if (mgr == null) {
                return runningTaskJson;
            }
            List<ActivityManager.RunningTaskInfo> list = mgr.getRunningTasks(5);
            if (list == null) {
                return runningTaskJson;
            }
            for (ActivityManager.RunningTaskInfo task : list) {
                if (task == null || task.baseActivity == null) {
                    continue;
                }
                try {
                    JSONObject taskJson = new JSONObject();
                    taskJson.put("id", task.id);
                    taskJson.put("package_name", task.baseActivity.getPackageName());
                    taskJson.put("description", task.description);
                    taskJson.put("number_of_activities", task.numActivities);
                    taskJson.put("number_of_running_activities", task.numRunning);
                    taskJson.put("topActivity", task.topActivity.toString());
                    taskJson.put("baseActivity", task.baseActivity.toString());
                    runningTaskJson.put(taskJson);
                } catch (JSONException ignored) {
                }
            }
        } catch (Exception ignored) {
            // 上面的调用有可能有运行时异常抛出
        }
        return runningTaskJson;
    }

    public JSONArray getRecentRecord() {
        JSONArray array = new JSONArray();
        for (ActivityRecord record : mActivityRecords) {
            array.put(record.toJson());
        }
        return array;
    }

    private int mIdAutoIncrement = 0;
    private class ActivityRecord {

        String mName;
        int mId;
        int mHashCode;
        long mFrontTime;
        long mBackTime = -1;
        boolean mAlive = true;

        List<MethodRecord> mMethods = new ArrayList<>();

        ActivityRecord(String name, int hashCode, long frontTime) {
            reset(name, hashCode, frontTime);
        }

        public final void reset(String name, int hashCode, long createTime) {
            mId = mIdAutoIncrement;
            mIdAutoIncrement++;
            mName = name;
            mHashCode = hashCode;
            mFrontTime = createTime;

            if (createTime == -1) {
                Log.e("xx", "ActivityDataManager_Alive activity not found \n" +
                        Log.getStackTraceString(new RuntimeException()));
//                Ensur.ensureNotReachHere("ActivityDataManager_Alive activity not found");
            }
            if (!mMethods.isEmpty()) {
                mMethods.clear();
            }
        }

        public JSONObject toJson() {
            mAlive = mDestroyActivities.get(mHashCode, -1L) == -1;

            JSONObject json = new JSONObject();
            try {
                json.put("name", mName);
                json.put("id", mId);
                json.put("front_time", mFrontTime);
                json.put("back_time", mBackTime);
                json.put("alive", mAlive);
                json.put("hash_code", mHashCode);

                JSONArray methods = new JSONArray();
                for (MethodRecord method : mMethods){
                    methods.put(method.toJSON());
                }
                json.put("life_cycle", methods);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

        void addMethod(String name, long time) {
            mMethods.add(new MethodRecord(name, time));
        }
    }

    private static class MethodRecord {
        String mName;
        long mTime;

        MethodRecord(String name, long time) {
            mName = name;
            mTime = time;
        }

        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            try {
                json.put("name", mName);
                json.put("time", mTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
    }
}
