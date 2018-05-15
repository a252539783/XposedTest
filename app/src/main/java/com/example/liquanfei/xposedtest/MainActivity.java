package com.example.liquanfei.xposedtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;

import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            getClassLoader().loadClass("aaaa");
//        } catch (ClassNotFoundException e) {
//            Log.e("xx",e.toString());
//        }

//
//
//        ClassLoader cl = getClassLoader();
//        while (cl != null && !(cl.getParent().getParent() == null)) {
//            cl = cl.getParent();
//        }
//
//        try {
//            Field parent = ClassLoader.class.getDeclaredField("parent");
//            parent.setAccessible(true);
//            ClassLoader parentLoader = (ClassLoader) parent.get(cl);        //bootcl
//            parent.set(cl, ccl);
//            parent.set(ccl, parentLoader);
//        } catch (NoSuchFieldException e) {
//            Log.e("xx",e.toString());
//        } catch (IllegalAccessException e) {
//            Log.e("xx",e.toString());
//        }
//
//        try {
//            getClassLoader().loadClass("com.ss.android.article.news.ArticleApplication");
//        } catch (ClassNotFoundException e) {
//            Log.e("xx",e.toString());
//        }
    }
}
