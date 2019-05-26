package com.example.liquanfei.xposedtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    HandlerThread mHandlerThread = new HandlerThread("test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandlerThread.start();
        IntentFilter filter = new IntentFilter();
        filter.addAction("a.a.a.a.a.a.a.a");
        registerReceiver(new TestReceiver(), filter);

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        array.put(null);
        try {
            json.put("a", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("xxj", json.toString());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
//        new Handler(mHandlerThread.getLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("xx", "startService");
//                startService(new Intent(MainActivity.this, MainService.class));
//                startActivity(new Intent(MainActivity.this, MainActivity.class));
////                sendBroadcast(new Intent("a.a.a.a.a.a.a.a"));
//            }
//        }, 3000);
    }

    class TestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("xx", "onReceive");
        }
    }
}
