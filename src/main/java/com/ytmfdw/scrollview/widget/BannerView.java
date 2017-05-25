package com.ytmfdw.scrollview.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/25.
 */
public class BannerView extends View {

    /**
     * 动画持续时间，默认为2秒
     */
    private long animDuring = 2000;
    /**
     * 间隔时间，这个间隔时间是指两次动画之间的时间
     */
    private long scrollDuring = 1000;

    /**
     * 是否自动动画
     */
    private boolean isAuto = true;

    /**
     * 控件的宽
     */
    private int w;
    /**
     * 控件的高
     */
    private int h;

    /**
     * 当前显示的数据索引
     */
    private int currentIndex = 0;

    /**
     * 当前偏移量，当动画结束后，该值为0
     */
    private float currentOffset = 0f;

    /**
     * 当前显示的内容
     */
    private Object currentObj = null;
    /**
     * 下一次要显示的内容
     */
    private Object nextObj = null;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 背景颜色
     */
    private int blackgroundColor = Color.WHITE;
    /**
     * 文字颜色
     */
    private int textColor = Color.BLACK;

    private List<ScrollBean> data = new ArrayList<>();

    /**
     * 动画
     */
    ObjectAnimator objAnim = null;

    private float endValue;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        w = getMeasuredWidth();
        h = getMeasuredHeight();
        endValue = h;
    }

    public void setData(List<ScrollBean> list) {
        data.clear();
        data.addAll(list);
        //开始动画
        startBanner();
    }

    public void startBanner() {
        setDrawContent();
        post(animRunnable);
    }

    Runnable animRunnable = new Runnable() {
        @Override
        public void run() {
            if (objAnim != null) {
                objAnim.cancel();
                objAnim = null;
            }
            objAnim = ObjectAnimator.ofFloat(BannerView.this, "CurrentOffset", 0, endValue);
            objAnim.setDuration(animDuring);
//            objAnim.setInterpolator(new LinearInterpolator());
            objAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    //动画结束
                    currentOffset = 0;
                    if (data.size() > 0) {
                        currentIndex = (++currentIndex % data.size());
                    }

                    setDrawContent();

                    if (isAuto) {
                        postDelayed(animRunnable, scrollDuring);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            objAnim.start();
        }
    };

    private void setDrawContent() {
        if (currentObj != null && currentObj instanceof Bitmap) {
            ((Bitmap) currentObj).recycle();
            currentObj = null;
        }
        //下一个要显示的正好是上一个内容
        if (nextObj != null) {
            currentObj = nextObj;
        } else {
            currentObj = getBeanContent(data.get(currentIndex));
        }
        //新添加的
        if (currentIndex + 1 >= data.size()) {
            nextObj = getBeanContent(data.get(0));
        } else {
            nextObj = getBeanContent(data.get(currentIndex + 1));
        }
    }

    private Object getBeanContent(ScrollBean scrollBean) {
        switch (scrollBean.type) {
            case ScrollBean.TYPE_STRING: {
                return scrollBean.content;
            }
            case ScrollBean.TYPE_IMG: {
                final Bitmap[] bitmap = {null};
                Object obj = scrollBean.content;
                Glide.with(getContext()).load(obj).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        bitmap[0] = resource;
                    }
                });
                return bitmap[0];
            }
        }
        return null;
    }

    public List<ScrollBean> getData() {
        return data;
    }

    /**
     * 绘制内容
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        canvas.drawColor(blackgroundColor);
        if (data.size() == 0) {
            //内容为空
            return;
        }

        drawContent(canvas);

    }

    private void drawContent(Canvas canvas) {
        drawableScrollBean(canvas, false);
        drawableScrollBean(canvas, true);
    }

    private void drawableScrollBean(Canvas canvas, boolean isFirst) {
        float dy = -currentOffset;
        if (isFirst) {
            //绘制第一个
            drawableContent(canvas, currentObj, dy);
        } else {
            //绘制第二个
            dy = dy + h;
            drawableContent(canvas, nextObj, dy);
        }
    }

    /**
     * 绘制内容
     *
     * @param canvas
     * @param bean
     * @param dy
     */
    private void drawableContent(Canvas canvas, Object bean, float dy) {
        if (bean instanceof String) {
            //字符串类型
            String content = (String) bean;
            //在中间绘制字符串
            //计算文字大小
            int textSize = getTextSize(content);
            mPaint.setTextSize(textSize);
            mPaint.setColor(textColor);
            //x开始绘制：(控件宽-字宽)/2
            float dx = (w - mPaint.measureText(content)) / 2;
            //y方向开始绘制：(控件高-字高)/2-assent
            float textH = (mPaint.descent() - mPaint.ascent());
//                endValue = h - (h - textH) / 2;
            dy = dy + (h - textH) / 2 - mPaint.ascent();
            canvas.drawText(content, dx, dy, mPaint);
        } else if (bean instanceof Bitmap) {
            Bitmap bitmap = (Bitmap) bean;
            canvas.drawBitmap(bitmap, 0, dy, null);
        }

    }

    private int getTextSize(String content) {
        int len = content.length();

        return w / len > h ? h : w / len;
    }

    /**
     * 用来作属性动画
     *
     * @param offset 偏移量，偏移总量为h，即控件的高度
     */
    public void setCurrentOffset(float offset) {
        this.currentOffset = offset;
        //重绘界面
        invalidate();
    }

    public float getCurrentOffset() {
        return currentOffset;
    }


}
