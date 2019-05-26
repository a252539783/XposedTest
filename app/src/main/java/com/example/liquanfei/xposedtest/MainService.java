package com.example.liquanfei.xposedtest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
