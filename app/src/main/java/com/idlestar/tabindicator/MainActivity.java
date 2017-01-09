package com.idlestar.tabindicator;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabIndicator tab_indicator = (TabIndicator) findViewById(R.id.tab_indicator);

        SamplePagerAdapter adapter = new SamplePagerAdapter(this);
        ViewPager view_pager = (ViewPager) findViewById(R.id.view_pager);
        view_pager.setAdapter(adapter);
        tab_indicator.setViewPager(view_pager);
    }
}
