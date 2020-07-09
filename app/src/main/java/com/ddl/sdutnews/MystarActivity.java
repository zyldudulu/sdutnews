package com.ddl.sdutnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.ddl.sdutnews.adapter.ListAdapter;
import com.ddl.sdutnews.bean.ListEleBean;

import org.litepal.LitePal;

import java.util.List;

public class MystarActivity extends AppCompatActivity {

    private List<ListEleBean> list;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mystar);
        listView=findViewById(R.id.liststar);
        list = LitePal.findAll(ListEleBean.class);
        setTitle("我的收藏");
        ListAdapter adapter = new ListAdapter(MystarActivity.this,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListEleBean listEleBean = list.get(position);
                Intent intent = new Intent(MystarActivity.this,NewsActivity.class);
                intent.putExtra("title",listEleBean.getTitle());
                intent.putExtra("url",listEleBean.getUrl());
                intent.putExtra("content",listEleBean.getContent());
                intent.putExtra("time",listEleBean.getTime());
                startActivityForResult(intent,1);
            }
        });
    }
}
