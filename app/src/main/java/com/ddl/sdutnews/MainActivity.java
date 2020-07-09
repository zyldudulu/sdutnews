package com.ddl.sdutnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ddl.sdutnews.ui.main.SectionsPagerAdapter;
import com.ddl.sdutnews.utils.HtmlUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    int cnt=0;
    int colorPrimary=0xFF0000FF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            init_list();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(cnt<4){ Log.i("cnt",Integer.toString(cnt));};

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.tostar);
        fab.setBackgroundColor(colorPrimary);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MystarActivity.class);
                startActivity(intent);
            }
        });

    }



    //初始化新闻列表
    public void init_list() throws IOException {         //并行加载4列表新闻
        cnt=0;
        for (int i = 1; i <= 4; i++) {
            final int finalI = i;
            new Thread() {
                public void run() {
                    try {
                        HtmlUtil.init(finalI,1);
                        cnt++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }





}