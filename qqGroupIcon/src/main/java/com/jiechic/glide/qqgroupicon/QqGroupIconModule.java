package com.jiechic.glide.qqgroupicon;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;
import java.util.List;

/**
 * Created by jiechic on 5/18/16.
 */
public class QqGroupIconModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        Class<List<String>> clazz = (Class) List.class;
        glide.register(clazz, InputStream.class, new QqGroupIconLoader.Factory());
    }
}
