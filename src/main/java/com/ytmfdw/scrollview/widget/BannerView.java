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
        if (currentIndex >= 0 && currentIndex < data.size()) {
            ScrollBean bean = data.get(currentIndex);
            switch (bean.type) {
                case ScrollBean.TYPE_IMG: {
                    Object content = bean.content;
                    if (content instanceof Integer) {
                        //从资源中获取Bitmap
                    } else if (content instanceof String) {
                        //从文件中获取图片
                    } else if (content instanceof Bitmap) {
                        //直接绘制
                    } else if (content instanceof Drawable) {
                        //直接绘制
                    }
                }
                break;
                case ScrollBean.TYPE_STRING: {
                    String content = (String) bean.content;
                    String nextContent = null;
                    if (currentIndex + 1 < data.size()) {
                        nextContent = (String) data.get(currentIndex + 1).content;
                    } else {
                        nextContent = (String) data.get(0).content;
                    }
                    //在中间绘制字符串
                    //计算文字大小
                    int textSize = getTextSize(content);
                    mPaint.setTextSize(textSize);
                    mPaint.setColor(textColor);
                    //x开始绘制：(控件宽-字宽)/2
                    float dx = (w - mPaint.measureText(content)) / 2;
                    //y方向开始绘制：(控件高-字高)/2-assent
                    float textH = (mPaint.descent() - mPaint.ascent());
                    endValue = h - (h - textH) / 2;
                    float dy = (h - textH) / 2 - mPaint.ascent() - currentOffset;
                    canvas.drawText(content, dx, dy, mPaint);
                    //第二行文本的y方向:
                    dy = dy + textH + (h - textH) / 2;
                    canvas.drawText(nextContent, dx, dy, mPaint);
                }
                break;
            }
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
