package com.jiek.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.jiek.jglide.JGlide;
import com.jiek.jglide.JRequestListener;
import com.jiek.jglide.RequestManager;
import com.jiek.logger.Logger;

public class MainActivity extends AppCompatActivity {

    ImageView url_imageView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url_imageView = findViewById(R.id.url_imageView);


        getLifecycle().addObserver(new LifecyleObserverImpl());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.e(TAG, "onResume 图片下载: ");

        JGlide.with(this).load("http://p1.itc.cn/images01/20200723/bd771601e4d3455abe336db80a36b635.png")
                .setjRequestListener(new JRequestListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap, int sourceType) {
                        Logger.e(TAG, "图片来源: " + sourceType);
                    }

                    @Override
                    public void onFail(String msg) {
                        Logger.e(TAG, msg);
                    }
                })
                .placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(url_imageView);
    }

    public void jumpNextPage(View view) {
        startActivity(new Intent(this, RecycleViewActivity.class));
    }

    @Override
    protected void onDestroy() {
//        RequestManager.getInstance().stop();
        super.onDestroy();
    }

    private class LifecyleObserverImpl implements LifecycleEventObserver {
        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            Lifecycle.State currentState = source.getLifecycle().getCurrentState();
            Logger.e(TAG, currentState.toString());
            switch (currentState) {
                case DESTROYED:
                    RequestManager.getInstance().stop();
                    break;
            }
        }
    }
}
