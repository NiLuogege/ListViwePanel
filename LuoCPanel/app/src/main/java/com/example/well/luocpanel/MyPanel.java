package com.example.well.luocpanel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Well on 2016/11/29.
 */

public class MyPanel extends ListView implements AbsListView.OnScrollListener {
    private View mMyPanel;
    private Animation mInAnim;
    private Animation mOutAnim;
    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;
    private int mMyPanelTop;
    private int mPosition;
    private OnPositionChangedListener onPositionChangedListener;

    public MyPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //设置滑动简体
        super.setOnScrollListener(this);

        //获取自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExtendedListView);
        int resourceId = a.getResourceId(R.styleable.ExtendedListView_scrollBarPanel, -1);
        int inAnimation = a.getResourceId(R.styleable.ExtendedListView_scrollBarPanelInAnimation, R.anim.in);
        int outAnimation = a.getResourceId(R.styleable.ExtendedListView_scrollBarPanelOutAnimation, R.anim.out);
        a.recycle();

        setMyPanelViewId(resourceId);


        mInAnim = AnimationUtils.loadAnimation(context, inAnimation);
        mOutAnim = AnimationUtils.loadAnimation(context, outAnimation);
        int scrollBarFadeDuration = ViewConfiguration.getScrollBarFadeDuration();//滑动条消失事件
        mOutAnim.setDuration(scrollBarFadeDuration);
        mOutAnim.setFillAfter(true);
        mOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束的时候,指示器隐藏
                if (null != mMyPanel) {
                    mMyPanel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //系统已经测量完了自己
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.mWidthMeasureSpec = widthMeasureSpec;
        this.mHeightMeasureSpec = heightMeasureSpec;
        //测量我们要加进去的子View
        measureChild(mMyPanel, widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //系统已经布局完了ListView中的控件
        super.onLayout(changed, l, t, r, b);

        int left = getMeasuredWidth() - mMyPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
        int top = mMyPanelTop;
        int right = getMeasuredWidth() - getVerticalScrollbarWidth();
        int bottom = top + mMyPanel.getMeasuredHeight();

        mMyPanel.layout(left, top, right, bottom);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //绘制全部的子控件
        super.dispatchDraw(canvas);
        //绘制自己要加的指示器
        drawChild(canvas, mMyPanel, getDrawingTime());
    }

    //监听到系统给滑动条出来的情况
    @Override
    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        boolean isAwaken = super.awakenScrollBars(startDelay, invalidate);
        if (isAwaken && null != mMyPanel) {
            if (mMyPanel.getVisibility() == View.VISIBLE) {
                if (null != mInAnim) {
                    mMyPanel.startAnimation(mInAnim);
                }
            }
            mHandler.removeCallbacks(mRunnable);
            mHandler.postAtTime(mRunnable, 1000);
        }


        return isAwaken;
    }

    Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mMyPanel.startAnimation(mOutAnim);
        }
    };

    private void setMyPanelViewId(int resourceId) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, this, false);
//        View view = View.inflate(getContext(), resourceId, this);
        mMyPanel = view;
        mMyPanel.setVisibility(View.GONE);

        //因为新加进来一个View所以要重新进行测量
        requestLayout();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//        computeVerticalScrollRange()//View整个滑动整体的大小
//        computeVerticalScrollOffset()//移除屏幕的大小
//        computeVerticalScrollExtent()//可视区域的大小

        if (mMyPanel != null && onPositionChangedListener != null) {


            //获取滑动条的高度
            int scrollBarHeight = computeVerticalScrollExtent() * getMeasuredHeight() / computeVerticalScrollRange();
            //获取滑动条中点距离顶部的大小
            int thunmOffset = scrollBarHeight * computeVerticalScrollOffset() / computeVerticalScrollExtent() + scrollBarHeight / 2;

            //我们自己控件距离顶部的距离
            mMyPanelTop = thunmOffset - mMyPanel.getHeight() / 2;
            //进行重新布局
            int left = getMeasuredWidth() - mMyPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
            int top = mMyPanelTop;
            int right = getMeasuredWidth() - getVerticalScrollbarWidth();
            int bottom = top + mMyPanel.getMeasuredHeight();

            mMyPanel.layout(left, top, right, bottom);


            //给指示器设置下标
            int count = getChildCount();
            for (int j = 0; j < count; j++) {
                View childAt = getChildAt(j);
                if (childAt.getTop() < thunmOffset && childAt.getBottom() >= thunmOffset) {//说明这个item和指示器对齐
                    if (mPosition != getFirstVisiblePosition() + j) {
                        mPosition = getFirstVisiblePosition() + j;
                        onPositionChangedListener.onPositionChanged(this, mPosition, mMyPanel);
                        //因为text内容长度不一样所以还需要measure
                        measureChild(mMyPanel, mWidthMeasureSpec, mHeightMeasureSpec);
                    }
                }
            }
        }

    }

    public interface OnPositionChangedListener {
        void onPositionChanged(MyPanel listView, int positon, View ScroBarPanel);
    }

    public void setOnPositionChangedListener(OnPositionChangedListener listener) {
        this.onPositionChangedListener = listener;

    }
}
