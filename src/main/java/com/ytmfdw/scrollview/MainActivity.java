package com.ytmfdw.scrollview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ytmfdw.scrollview.widget.BannerView;
import com.ytmfdw.scrollview.widget.BannerViewGroup;
import com.ytmfdw.scrollview.widget.ScrollBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BannerViewGroup bannerView;

    List<View> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layouttest);
        initViews();
        initData();
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(Color.RED);
            tv.setText("测试数据"+i);
            data.add(tv);
        }

        bannerView.setData(data);
    }

    private void initViews() {
        bannerView = (BannerViewGroup) findViewById(R.id.bannerView);
    }
}
