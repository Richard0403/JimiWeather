package me.jimi.weather.tools;

import android.app.Activity;
import android.app.Application;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.jimi.weather.App;
import me.jimi.weather.App;


public class Tool {
    /**
     * 文本
     */
    private String mText;
    /**
     * 字体颜色，十六进制形式，例如：0xAEAEAEAE
     */
    private int mTextColor;
    /**
     * 字体大小，单位为sp
     */
    private float mTextSize;
    /**
     * 旋转角度
     */
    private float mRotation;
    private static Tool sInstance;

    private Tool() {
        mText = ascii2Native("\\u5409\\u7c73\\u5f00\\u6e90");
        mTextColor = 0xA0AEAEAE;
        mTextSize = 18;
        mRotation = -35;
    }

    public static Tool getInstance() {
        if (sInstance == null) {
            synchronized (Tool.class) {
                sInstance = new Tool();
            }
        }
        return sInstance;
    }

    /**
     * 设置文本
     *
     * @param text 文本
     * @return Watermark实例
     */
    public Tool setText(String text) {
        mText = text;
        return sInstance;
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色，十六进制形式，例如：0xAEAEAEAE
     * @return Watermark实例
     */
    public Tool setTextColor(int color) {
        mTextColor = color;
        return sInstance;
    }

    /**
     * 设置字体大小
     *
     * @param size 大小，单位为sp
     * @return Watermark实例
     */
    public Tool setTextSize(float size) {
        mTextSize = size;
        return sInstance;
    }

    /**
     * 设置旋转角度
     *
     * @param degrees 度数
     * @return Watermark实例
     */
    public Tool setRotation(float degrees) {
        mRotation = degrees;
        return sInstance;
    }

    /**
     * 显示，铺满整个页面
     *
     * @param activity 活动
     */
    public void show(Activity activity) {
        show(activity, mText);
    }

    /**
     * 显示，铺满整个页面
     *
     * @param activity 活动
     * @param text     
     */
    public void show(Activity activity, String text) {
        MarkDrawable drawable = new MarkDrawable();
        drawable.mText = text;
        drawable.mTextColor = mTextColor;
        drawable.mTextSize = mTextSize;
        drawable.mRotation = mRotation;

        ViewGroup rootView = activity.findViewById(android.R.id.content);
        FrameLayout layout = new FrameLayout(activity);
        layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setBackground(drawable);
        rootView.addView(layout);
    }

    public void registerLife(Application app) {
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Tool.getInstance().show(activity);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    private class MarkDrawable extends Drawable {
        private Paint mPaint;
        /**
         * 文本
         */
        private String mText;
        /**
         * 字体颜色，十六进制形式，例如：0xAEAEAEAE
         */
        private int mTextColor;
        /**
         * 字体大小，单位为sp
         */
        private float mTextSize;
        /**
         * 旋转角度
         */
        private float mRotation;

        private MarkDrawable() {
            mPaint = new Paint();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            int width = getBounds().right;
            int height = getBounds().bottom;
            int diagonal = (int) Math.sqrt(width * width + height * height); // 对角线的长度

            mPaint.setColor(mTextColor);
            mPaint.setTextSize(sp2px(mTextSize)); // ConvertUtils.spToPx()这个方法是将sp转换成px，ConvertUtils这个工具类在我提供的demo里面有
            mPaint.setAntiAlias(true);
            float textWidth = mPaint.measureText(mText);

            canvas.drawColor(0x00000000);
            canvas.rotate(mRotation);

            int index = 0;
            float fromX;
            // 以对角线的长度来做高度，这样可以保证竖屏和横屏整个屏幕都能布满
            for (int positionY = diagonal / 10; positionY <= diagonal; positionY += diagonal / 10) {
                fromX = -width + (index++ % 2) * textWidth; // 上下两行的X轴起始点不一样，错开显示
                for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                    canvas.drawText(mText, positionX, positionY, mPaint);
                }
            }

            canvas.save();
            canvas.restore();
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }


        public int sp2px(float spValue){
            final float fontScale = App.Companion.instance().getResources().getDisplayMetrics().scaledDensity;
            return (int)(spValue * fontScale +0.5f);
        }

    }

    public static String ascii2Native(String str) {
        final String PREFIX = "\\u";
        StringBuilder sb = new StringBuilder();
        int begin = 0;
        int index = str.indexOf(PREFIX);
        while (index != -1) {
            sb.append(str.substring(begin, index));
            sb.append(ascii2Char(str.substring(index, index + 6)));
            begin = index + 6;
            index = str.indexOf(PREFIX, begin);
        }
        sb.append(str.substring(begin));
        return sb.toString();
    }

    /**
     * Ascii to native character.
     *
     * @param str
     * ascii string
     * @return native character
     */
    private static char ascii2Char(String str) {
        final String PREFIX = "\\u";
        if (str.length() != 6) {
            throw new IllegalArgumentException("Ascii string of a native character must be 6 character.");
        }
        if (!PREFIX.equals(str.substring(0, 2))) {
            throw new IllegalArgumentException("Ascii string of a native character must start with \"\\u\".");
        }
        String tmp = str.substring(2, 4);
        int code = Integer.parseInt(tmp, 16) << 8;
        tmp = str.substring(4, 6);
        code += Integer.parseInt(tmp, 16);
        return (char) code;
    }


}
