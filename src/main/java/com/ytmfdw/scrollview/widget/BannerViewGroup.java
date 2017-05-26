package com.ytmfdw.scrollview.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/25.
 */
public class BannerViewGroup extends LinearLayout {

    /**
     * 动画持续时间，默认为2秒
     */
    private long animDuring = 1000;
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
    private int currentOffset = 0;

    /**
     * 当前显示的内容
     */
    private View currentObj = null;
    /**
     * 下一次要显示的内容
     */
    private View nextObj = null;


    private List<View> data = new ArrayList<>();
    private boolean[] isMeasure;

    /**
     * 动画
     */
    ObjectAnimator objAnim = null;

    /**
     * 子控件的大小
     */
    LayoutParams params;

    private int endValue;

    public BannerViewGroup(Context context) {
        this(context, null);
    }

    public BannerViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        //垂直方向
        setOrientation(VERTICAL);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("ytmfdw", "测量子控件");
        //确定所有子控件的宽度
        w = getMeasuredWidth();
        h = getMeasuredHeight();
        endValue = h;
        params = new LayoutParams(w, h);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
        /*    if (isMeasure[data.indexOf(view)]) {
                break;
            }*/
            Log.d("ytmfdw", "测量子控件" + data.indexOf(view));
            measureChild(view, MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            isMeasure[data.indexOf(view)] = true;
        }
    }

    public void setData(List<View> list) {
        data.clear();
        data.addAll(list);
        isMeasure = new boolean[data.size()];
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
            objAnim = ObjectAnimator.ofInt(BannerViewGroup.this, "CurrentOffset", 0, endValue);
            objAnim.setDuration(animDuring);
//            objAnim.setInterpolator(new LinearInterpolator());
            objAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    Log.d("ytmfdw", "动画开始");
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
        Log.d("ytmfdw", "加载内容，先移除上一个");
        //先移除上一个视图
        if (currentObj != null) {
            removeView(currentObj);
            currentObj = null;
        }
        //下一个要显示的正好是上一个内容
        if (nextObj != null) {
            currentObj = nextObj;
        } else {
            currentObj = data.get(currentIndex);
            addView(currentObj, params);
        }
        //新添加的
        if (currentIndex + 1 >= data.size()) {
            nextObj = data.get(0);
        } else {
            nextObj = data.get(currentIndex + 1);
        }
        addView(nextObj, params);
    }


    public List<View> getData() {
        return data;
    }

    /**
     * 用来作属性动画
     *
     * @param offset 偏移量，偏移总量为h，即控件的高度
     */
    public void setCurrentOffset(int offset) {
        this.currentOffset = offset;
        for (int i = 0; i < getChildCount(); i++) {
            LinearLayout.LayoutParams tmp = (LayoutParams) getChildAt(i).getLayoutParams();
            tmp.topMargin = tmp.topMargin + offset;
            getChildAt(i).setLayoutParams(tmp);
        }
    }

    public int getCurrentOffset() {
        return currentOffset;
    }


}
