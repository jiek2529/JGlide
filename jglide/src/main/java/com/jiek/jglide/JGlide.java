package com.jiek.jglide;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class JGlide {

    private volatile static JGlide instance;
    private static volatile boolean isInitializing;
    Context applicationContext;

    private JGlide(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static JGlide get(Context context) {
        if (instance == null) {
            synchronized (JGlide.class) {
                if (instance == null) {
                    if (isInitializing) {
//                        throw new IllegalAccessException("初始化异常");
                    }
                    isInitializing = true;
                    instance = new JGlide(context.getApplicationContext());
                    isInitializing = false;
                }
            }
        }
        return instance;
    }

    public static RequestManager with(Context context) {
        checkContext(context);
        return RequestManager.getInstance();
    }

    public static RequestManager with(Fragment fragment) {
        checkContext(fragment.getContext());
        return RequestManager.getInstance();
    }

    private static void checkContext(Context context) {
        Context applicationContext;
        if (context instanceof FragmentActivity || context instanceof Activity) {
            applicationContext = context.getApplicationContext();
        } else {
            applicationContext = context;
        }
        get(applicationContext);
    }

    public static Context getApplicationContext() {
        return instance.applicationContext;
    }
}
