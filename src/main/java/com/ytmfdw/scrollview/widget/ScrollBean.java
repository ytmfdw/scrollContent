package com.ytmfdw.scrollview.widget;

/**
 * Created by Administrator on 2017/5/25.
 */
public class ScrollBean {
    /**
     * 图片类型
     */
    public final static int TYPE_IMG = 1;
    /**
     * 文本类型
     */
    public final static int TYPE_STRING = 2;

    /**
     * 类型,只支持两种：img和String
     */
    public int type = TYPE_STRING;
    /**
     * 内容，泛型
     */
    public Object content = null;

    /**
     * 补充说明
     */
    public String mark = null;

    private Object tag = null;

    public void setTag(Object obj) {
        this.tag = obj;
    }

    public Object getTag() {
        return this.tag;
    }
}
