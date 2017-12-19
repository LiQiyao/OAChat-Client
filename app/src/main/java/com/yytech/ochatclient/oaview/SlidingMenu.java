package com.yytech.ochatclient.oaview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.yytech.ochatclient.R;


/**
 * Created by admin on 2017/12/4.
 */

public class SlidingMenu extends HorizontalScrollView {

    private LinearLayout mWrapper; //该控件下的布局
    private ViewGroup mMenu;    //mWrapper下的第一个内容；
    private ViewGroup mContent; //mWrapper下的第二个内容；

    private  int mMenuWidth;       //左边菜单的宽度

    private int mScreenWidth;    //屏幕宽度
    private int mMenuRightPadding ;//dp   //菜单右侧的padding

    private boolean once;  //Measure方法是否第一次调用，默认不是

    public SlidingMenu(Context context) {
        this(context,null);
    }

    //未使用自定义属性时调用
    public SlidingMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,63);



    }

    //当时用了自定义属性时调用
    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu,defStyleAttr,0);

        int count = a.getIndexCount();
        for(int i = 0 ; i < count ; i ++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(attr,(int) TypedValue.
                            applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            73,context.getResources().getDisplayMetrics()));
                    break;
            }
        }


        a.recycle();


        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(!once){
            mWrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWrapper.getChildAt(0);
            mContent = (ViewGroup) mWrapper.getChildAt(1);

            //给子控件指定宽度
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        //隐藏菜单
        if(changed){
            this.scrollTo(mMenuWidth,0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX >= mMenuWidth / 2){
                    this.smoothScrollTo(mMenuWidth,0);
                }else {
                    this.smoothScrollTo(0,0);
                }
                return true;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        mMenu.setTranslationX(l);

    }
}
