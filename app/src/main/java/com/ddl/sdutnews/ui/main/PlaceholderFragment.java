package com.ddl.sdutnews.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ddl.sdutnews.NewsActivity;
import com.ddl.sdutnews.R;
import com.ddl.sdutnews.adapter.ListAdapter;
import com.ddl.sdutnews.bean.ListEleBean;
import com.ddl.sdutnews.utils.HtmlUtil;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PageViewModel pageViewModel;

    int index;
    int cnt;
    int page;
    private List<ListEleBean> list,newlist;
    View root;
    ListView listView;
    ListAdapter listAdapter;


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

    }

    //初始化新闻列表
    public void init_list(int index) throws IOException {         //并行加载4列表新闻
        cnt=0;
        for (int i = index; i <= index; i++) {
            final int finalI = i;
            new Thread() {
                public void run() {
                    try {
                        HtmlUtil.init(finalI,page);
                        cnt++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    //显示数据
    public void showdata(int index)
    {
        list=HtmlUtil.get_list(index);
        listAdapter=new ListAdapter(getActivity(),list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListEleBean listEleBean = list.get(position);
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                intent.putExtra("title",listEleBean.getTitle());
                intent.putExtra("url",listEleBean.getUrl());
                intent.putExtra("content",listEleBean.getContent());
                intent.putExtra("time",listEleBean.getTime());
                startActivityForResult(intent,1);
            }
        });
    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        listView=root.findViewById(R.id.listview);
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {
                index=Integer.parseInt(s);
                page=1;
                showdata(index);
                //下拉刷新
                RefreshLayout refreshLayout = (RefreshLayout)root.findViewById(R.id.refreshLayout);
                refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(RefreshLayout refreshlayout) {
                        page=1;
                        try {
                            init_list(index);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        while(cnt<1){Log.i("cnt",Integer.toString(cnt));};
                        showdata(index);
                        refreshlayout.finishRefresh(2000/*,false*/);
                    }
                });
                //下拉加载下一页
                refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(RefreshLayout refreshlayout) {
                        page++;
                        try {
                            init_list(index);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        while(cnt<1){Log.i("cnt",Integer.toString(cnt));};
                        newlist=HtmlUtil.get_list(index);
                        for(ListEleBean data:newlist){
                            list.add(data);
                        }
                        listAdapter.notifyDataSetChanged();
                        refreshlayout.finishLoadMore(1500/*,false*/);//传入false表示加载失败
                    }
                });
            }
        });
        return root;
    }
}