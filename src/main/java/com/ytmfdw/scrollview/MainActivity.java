package com.ytmfdw.scrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ytmfdw.scrollview.widget.BannerView;
import com.ytmfdw.scrollview.widget.ScrollBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BannerView bannerView;

    List<ScrollBean> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
    }

    private void initData() {
        for (int i = 0; i < 3; i++) {
            ScrollBean bean = new ScrollBean();
            bean.type = ScrollBean.TYPE_STRING;
            bean.content = "测试数据" + i;
            data.add(bean);
        }

        bannerView.setData(data);
    }

    private void initViews() {
        bannerView = (BannerView) findViewById(R.id.bannerView);
    }
}
