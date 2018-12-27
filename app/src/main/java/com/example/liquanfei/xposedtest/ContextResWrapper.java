package com.example.liquanfei.xposedtest;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

public class ContextResWrapper extends ContextWrapper {
    Resources mRes;
    public ContextResWrapper(Context base, Resources res) {
        super(base);
        mRes = res;
    }

    @Override
    public Resources getResources() {
        return mRes == null ? super.getResources() : mRes;
    }
}
