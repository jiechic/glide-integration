package com.jiechic.glide.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.image);

        List<String> list = new ArrayList<>();

        list.add("http://list.image.baidu.com/t/image_category/galleryimg/womenstar/hk/shu_qi.jpg");
        list.add("http://list.image.baidu.com/t/image_category/galleryimg/womenstar/hk/jia_jing_wen.jpg");
        list.add("http://list.image.baidu.com/t/image_category/galleryimg/womenstar/hk/xu_ruo_xuan.jpg");
        list.add("http://list.image.baidu.com/t/image_category/galleryimg/womenstar/hk/guo_shu_yao.jpg");
        list.add("http://list.image.baidu.com/t/image_category/galleryimg/womenstar/hk/li_jia_xin.jpg");

        Glide.with(this)
                .load(list)
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }
}
