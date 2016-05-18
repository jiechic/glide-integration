package com.jiechic.glide.qqgroupicon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiechic on 5/18/16.
 */
public class QqGroupIconStreamFetcher implements DataFetcher<InputStream> {
    private final List<String> strings;
    private final int width;
    private final int height;
    private final Context context;

    public QqGroupIconStreamFetcher(Context context, List<String> strings, int width, int height) {
        this.context = context;
        this.strings = strings;
        this.width = width;
        this.height = height;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (String string : strings) {
            bitmaps.add(Glide.with(context).load(string).asBitmap().into(width, height).get());
        }

        int dimension = width;

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        if (bitmaps.size() == 0)
            return null;

        int count = Math.min(bitmaps.size(), JoinLayout.max());
        float[] size = JoinLayout.size(count);
        // 旋转角度
        float[] rotation = JoinLayout.rotation(count);
        // paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Matrix matrixJoin = new Matrix();
        // scale as join size
        matrixJoin.postScale(size[0], size[0]);

        canvas.save();
        canvas.drawColor(Color.TRANSPARENT);

        for (int index = 0; index < bitmaps.size(); index++) {
            Bitmap bitmap = bitmaps.get(index);
            //如何该bitmap被回收了,则跳过
            if (bitmap == null || bitmap.isRecycled()) {
                continue;
            }
            // MATRIX
            Matrix matrix = new Matrix();
            // scale as destination
            matrix.postScale((float) dimension / bitmap.getWidth(),
                    (float) dimension / bitmap.getHeight());

            canvas.save();

            matrix.postConcat(matrixJoin);

            float[] offset = JoinLayout.offset(count, index, dimension, size);
            canvas.translate(offset[0], offset[1]);

            // 缩放
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            // 裁剪
            Bitmap bitmapOk = createMaskBitmap(newBitmap, newBitmap.getWidth(),
                    newBitmap.getHeight(), (int) rotation[index], 0.15f);

            canvas.drawBitmap(bitmapOk, 0, 0, paint);
            canvas.restore();
        }
        return bitmap2InputStream(output);

    }

    @Override
    public void cleanup() {

    }

    @Override
    public String getId() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    @Override
    public void cancel() {

    }


    public static final Bitmap createMaskBitmap(Bitmap bitmap, int viewBoxW, int viewBoxH,
                                                int rotation, float gapSize) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);// 抗锯齿
        paint.setFilterBitmap(true);
        int center = Math.round(viewBoxW / 2f);
        canvas.drawCircle(center, center, center, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if (rotation != 360) {
            Matrix matrix = new Matrix();
            // 根据原图的中心位置旋转
            matrix.setRotate(rotation, viewBoxW / 2, viewBoxH / 2);
            canvas.setMatrix(matrix);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawCircle(viewBoxW * (1.5f - gapSize), center, center, paint);
        }
        return output;
    }

    public InputStream bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static class JoinLayout {

        public static final String TAG = JoinLayout.class.getSimpleName();

        public static int max() {
            return 5;
        }

        private static final float[][] rotations = {new float[]{360}, new float[]{45, 360},
                new float[]{120, 0, -120}, new float[]{90, 180, -90, 0},
                new float[]{144, 72, 0, -72, -144},};

        public static float[] rotation(int count) {
            return count > 0 && count <= rotations.length ? rotations[count - 1] : null;
        }

        private static final float[][] sizes = {new float[]{0.9f, 0.9f},
                new float[]{0.5f, 0.65f}, new float[]{0.45f, 0.8f},
                new float[]{0.45f, 0.91f}, new float[]{0.38f, 0.80f}};

        public static float[] size(int count) {
            return count > 0 && count <= sizes.length ? sizes[count - 1] : null;
        }

        public static float[] offset(int count, int index, float dimension, float[] size) {
            switch (count) {
                case 1:
                    return offset1(index, dimension, size);
                case 2:
                    return offset2(index, dimension, size);
                case 3:
                    return offset3(index, dimension, size);
                case 4:
                    return offset4(index, dimension, size);
                case 5:
                    return offset5(index, dimension, size);
                default:
                    break;
            }
            return new float[]{0f, 0f};
        }

        /**
         * 5个头像
         *
         * @param index     下标
         * @param dimension 画布边长（正方形）
         * @param size      size[0]缩放 size[1]边距
         * @return 下标index X，Y轴坐标
         */
        private static float[] offset5(int index, float dimension, float[] size) {
            // 圆的直径
            float cd = (float) dimension * size[0];
            // 边距
            float s1 = -cd * size[1];

            float x1 = 0;
            float y1 = s1;

            float x2 = (float) (s1 * Math.cos(19 * Math.PI / 180));
            float y2 = (float) (s1 * Math.sin(18 * Math.PI / 180));

            float x3 = (float) (s1 * Math.cos(54 * Math.PI / 180));
            float y3 = (float) (-s1 * Math.sin(54 * Math.PI / 180));

            float x4 = (float) (-s1 * Math.cos(54 * Math.PI / 180));
            float y4 = (float) (-s1 * Math.sin(54 * Math.PI / 180));

            float x5 = (float) (-s1 * Math.cos(19 * Math.PI / 180));
            float y5 = (float) (s1 * Math.sin(18 * Math.PI / 180));

            // Log.d(TAG, "x1:" + x1 + "/y1:" + y1);
            // Log.d(TAG, "x2:" + x2 + "/y2:" + y2);
            // Log.d(TAG, "x3:" + x3 + "/y3:" + y3);
            // Log.d(TAG, "x4:" + x4 + "/y4:" + y4);
            // Log.d(TAG, "x5:" + x5 + "/y5:" + y5);

            // 居中 Y轴偏移量
            float xx1 = (dimension - cd - y3 - s1) / 2;
            // 居中 X轴偏移量
            float xxc1 = (dimension - cd) / 2;
            // xx1 = xxc1 = -s1;
            // xx1 = xxc1 = 0;
            switch (index) {
                case 0:
                    // return new float[] { s1 + xxc1, xx1 };
                    return new float[]{x1 + xxc1, y1 + xx1};
                case 1:
                    return new float[]{x2 + xxc1, y2 + xx1};
                case 2:
                    return new float[]{x3 + xxc1, y3 + xx1};
                case 3:
                    return new float[]{x4 + xxc1, y4 + xx1};
                case 4:
                    return new float[]{x5 + xxc1, y5 + xx1};
                default:
                    break;
            }
            return new float[]{0f, 0f};
        }

        /**
         * 4个头像
         *
         * @param index     下标
         * @param dimension 画布边长（正方形）
         * @param size      size[0]缩放 size[1]边距
         * @return 下标index X，Y轴坐标
         */
        private static float[] offset4(int index, float dimension, float[] size) {
            // 圆的直径
            float cd = (float) dimension * size[0];
            // 边距
            float s1 = cd * size[1];

            float x1 = 0;
            float y1 = 0;

            float x2 = s1;
            float y2 = y1;

            float x3 = s1;
            float y3 = s1;

            float x4 = x1;
            float y4 = y3;

            // Log.d(TAG, "x1:" + x1 + "/y1:" + y1);
            // Log.d(TAG, "x2:" + x2 + "/y2:" + y2);
            // Log.d(TAG, "x3:" + x3 + "/y3:" + y3);
            // Log.d(TAG, "x4:" + x4 + "/y4:" + y4);

            // 居中 X轴偏移量
            float xx1 = (dimension - cd - s1) / 2;
            switch (index) {
                case 0:
                    return new float[]{x1 + xx1, y1 + xx1};
                case 1:
                    return new float[]{x2 + xx1, y2 + xx1};
                case 2:
                    return new float[]{x3 + xx1, y3 + xx1};
                case 3:
                    return new float[]{x4 + xx1, y4 + xx1};
                default:
                    break;
            }
            return new float[]{0f, 0f};
        }

        /**
         * 3个头像
         *
         * @param index     下标
         * @param dimension 画布边长（正方形）
         * @param size      size[0]缩放 size[1]边距
         * @return 下标index X，Y轴坐标
         */
        private static float[] offset3(int index, float dimension, float[] size) {
            // 圆的直径
            float cd = (float) dimension * size[0];
            // 边距
            float s1 = cd * size[1];
            // 第二个圆的 Y坐标
            float y2 = s1 * (3 / 2);
            // 第二个圆的 X坐标
            float x2 = s1 - y2 / 1.73205f;
            // 第三个圆的 X坐标
            float x3 = s1 * 2 - x2;
            // 居中 Y轴偏移量
            float xx1 = (dimension - cd - y2) / 2;
            // 居中 X轴偏移量
            float xxc1 = (dimension - cd) / 2 - s1;
            // xx1 = xxc1 = 0;
            switch (index) {
                case 0:
                    return new float[]{s1 + xxc1, xx1};
                case 1:
                    return new float[]{x2 + xxc1, y2 + xx1};
                case 2:
                    return new float[]{x3 + xxc1, y2 + xx1};
                default:
                    break;
            }
            return new float[]{0f, 0f};
        }

        /**
         * 2个头像
         *
         * @param index     下标
         * @param dimension 画布边长（正方形）
         * @param size      size[0]缩放 size[1]边距
         * @return 下标index X，Y轴坐标
         */
        private static float[] offset2(int index, float dimension, float[] size) {
            // 圆的直径
            float cd = (float) dimension * size[0];
            // 边距
            float s1 = cd * size[1];

            float x1 = 0;
            float y1 = 0;

            float x2 = s1;
            float y2 = s1;

            // Log.d(TAG, "x1:" + x1 + "/y1:" + y1);
            // Log.d(TAG, "x2:" + x2 + "/y2:" + y2);

            // 居中 X轴偏移量
            float xx1 = (dimension - cd - s1) / 2;
            switch (index) {
                case 0:
                    return new float[]{x1 + xx1, y1 + xx1};
                case 1:
                    return new float[]{x2 + xx1, y2 + xx1};
                default:
                    break;
            }
            return new float[]{0f, 0f};
        }

        /**
         * 1个头像
         *
         * @param index     下标
         * @param dimension 画布边长（正方形）
         * @param size      size[0]缩放 size[1]边距
         * @return 下标index X，Y轴坐标
         */
        private static float[] offset1(int index, float dimension, float[] size) {
            // 圆的直径
            float cd = (float) dimension * size[0];
            float offset = (dimension - cd) / 2;
            return new float[]{offset, offset};
        }
    }
}
