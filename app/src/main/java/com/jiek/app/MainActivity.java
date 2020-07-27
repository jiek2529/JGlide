package com.jiek.app;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.jiek.jglide.JGlide;

public class MainActivity extends AppCompatActivity {

    ImageView url_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url_imageView = findViewById(R.id.url_imageView);

        JGlide.with(this).load("http://p1.itc.cn/images01/20200723/bd771601e4d3455abe336db80a36b635.png").placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(url_imageView);
    }
}
