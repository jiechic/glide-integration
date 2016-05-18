package com.jiechic.glide.qqgroupicon;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;
import java.util.List;

/**
 * Created by jiechic on 5/18/16.
 */
public class QqGroupIconLoader implements ModelLoader<List<String>, InputStream> {
    private final Context context;

    public QqGroupIconLoader(Context context) {
        this.context = context;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(List<String> model, int width, int height) {
        return new QqGroupIconStreamFetcher(context, model, width, height);
    }

    public static class Factory implements ModelLoaderFactory<List<String>, InputStream> {

        @Override
        public ModelLoader<List<String>, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new QqGroupIconLoader(context);
        }

        @Override
        public void teardown() {

        }
    }
}
