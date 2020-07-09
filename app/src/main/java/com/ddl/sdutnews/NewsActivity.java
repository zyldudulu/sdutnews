package com.ddl.sdutnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ddl.sdutnews.bean.ListEleBean;
import com.ddl.sdutnews.utils.HtmlUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    private Intent intent;
    int flag=0;
    ImageView star;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        final WebView test = findViewById(R.id.content);
        star = findViewById(R.id.starNews);
        final String[] imgItems = new String[1];
        intent = getIntent();
        setTitle(intent.getStringExtra("title"));
        List<ListEleBean> listEleBeans = LitePal.where("Url = ?",intent.getStringExtra("url")).find(ListEleBean.class);
        if(listEleBeans.size()!=0) {
            flag = 1;
            star.setImageResource(R.drawable.star_on);
        }
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==0) {
                    ListEleBean listEleBean = new ListEleBean();
                    listEleBean.setContent(intent.getStringExtra("content"));
                    listEleBean.setTime(intent.getStringExtra("time"));
                    listEleBean.setTitle(intent.getStringExtra("title"));
                    listEleBean.setUrl(intent.getStringExtra("url"));
                    listEleBean.save();
                    if (listEleBean.save()) {
                        Toast.makeText(NewsActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewsActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                    }
                    star.setImageResource(R.drawable.star_on);
                }
                else {
                    LitePal.deleteAll(ListEleBean.class,"url = ?",intent.getStringExtra("url"));
                    Toast.makeText(NewsActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(2,intent);
                    star.setImageResource(R.drawable.star_off);
                }
                flag^=1;
            }
        });
        new Thread(){
            public void run() {
                try {
                    imgItems[0] = HtmlUtil.get_news(intent.getStringExtra("url"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        test.loadData(imgItems[0],"text/html; charset=UTF-8;",null);
                    }
                });
            }
        }.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "山东理工大学新闻网新闻：《"+intent.getStringExtra("title")+"》\n\n"
                                +"请点击下方链接查看详细信息："+intent.getStringExtra("url"));
                shareIntent = Intent.createChooser(shareIntent, "分享给你的朋友吧");
                startActivity(shareIntent);
                break;
            default:
        }
        return true;
    }

}
