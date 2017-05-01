package com.baoyz.swipemenulistview;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iamchill.chill.ChillService;
import com.iamchill.chill.MainActivity;
import com.iamchill.chill.R;

/**
 * @author baoyz
 * @date 2014-8-23
 */
@SuppressWarnings("ALL")
public class SwipeMenuLayout extends FrameLayout {

    public static final int CONTENT_VIEW_ID = 1;
    public static final int MENU_VIEW_ID = 2;

    private static final int STATE_CLOSE = 0;
    private static final int STATE_OPEN = 1;

    private int mSwipeDirection;

    private View mContentView;
    public SwipeMenuView mMenuView;
    private int mDownX;
    private int state = STATE_CLOSE;
    private GestureDetectorCompat mGestureDetector;
    private OnGestureListener mGestureListener;
    private boolean isFling;
    private int MIN_FLING = dp2px(15);
    private int MAX_VELOCITYX = -dp2px(500);
    public ScrollerCompat mOpenScroller;
    public ScrollerCompat mCloseScroller;
    private int mBaseX;
    private int position;
    public Interpolator mCloseInterpolator;
    public Interpolator mOpenInterpolator;

    public SwipeMenuLayout(View contentView, SwipeMenuView menuView) {
        this(contentView, menuView, null, null);
    }

    public SwipeMenuLayout(View contentView, SwipeMenuView menuView,
                           Interpolator closeInterpolator, Interpolator openInterpolator) {
        super(contentView.getContext());
        mCloseInterpolator = closeInterpolator;
        mOpenInterpolator = openInterpolator;
        mContentView = contentView;
        mMenuView = menuView;
        mMenuView.setLayout(this);
//        try {
//            if (ChillService.chill_service.map.get(((TextView) mContentView.findViewById(R.id.label123)).getText().toString()).getString("id_contact").equals("1") && false) {
//                View v = getFirstView();
//                mMenuView.removeView(v);
//                v = getSecondView();
//                mMenuView.removeView(v);
//                removed = true;
//                removed2 = true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        setMinimumHeight((int) (90 * MainActivity.main.getResources().getDisplayMetrics().scaledDensity));
        init();
    }

    private boolean removed, removed2;

    private View getFirstView() {
        return mMenuView.findViewById(10);
    }

    private View getSecondView() {
        return mMenuView.findViewById(11);
    }

    // private SwipeMenuLayout(Context context, AttributeSet attrs, int
    // defStyle) {
    // super(context, attrs, defStyle);
    // }

    public int POROG(int d) {
        return (int) (d * 0.25);
    }

    public int getWidth(SwipeMenuView w) {
//        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
//            return w.findViewById(10).getWidth();
//        } else {
//            return w.findViewById(11).getWidth();
//        }
        return w.getWidth();
    }

    public int wd(SwipeMenuView w) {
        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
            return removed ? 0 : getFirstView().getWidth();
        } else {
            return removed2 ? 0 : getSecondView().getWidth();
        }
    }

    private SwipeMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private SwipeMenuLayout(Context context) {
        super(context);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        mMenuView.setPosition(position);
    }

    public void setSwipeDirection(int swipeDirection) {
        mSwipeDirection = swipeDirection;
    }

    private void init() {
        setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
                (int) (MainActivity.main.getResources().getDisplayMetrics().scaledDensity * 90)));
        mGestureListener = new SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                isFling = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                // TODO
                if (Math.abs(e1.getX() - e2.getX()) > MIN_FLING
                        && velocityX < MAX_VELOCITYX) {
                    isFling = true;
                }
                // Log.i("byz", MAX_VELOCITYX + ", velocityX = " + velocityX);
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };
        mGestureDetector = new GestureDetectorCompat(getContext(),
                mGestureListener);

        // mScroller = ScrollerCompat.create(getContext(), new
        // BounceInterpolator());
        if (mCloseInterpolator != null) {
            mCloseScroller = ScrollerCompat.create(getContext(),
                    mCloseInterpolator);
        } else {
            mCloseScroller = ScrollerCompat.create(getContext());
        }
        if (mOpenInterpolator != null) {
            mOpenScroller = ScrollerCompat.create(getContext(),
                    mOpenInterpolator);
        } else {
            mOpenScroller = ScrollerCompat.create(getContext());
        }

        LayoutParams contentParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mContentView.setLayoutParams(contentParams);
        if (mContentView.getId() < 1) {
            mContentView.setId(1);
        }

        mMenuView.setId(2);
        mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        addView(mContentView);
        addView(mMenuView);

        // if (mContentView.getBackground() == null) {
        // mContentView.setBackgroundColor(Color.WHITE);
        // }

        // in android 2.x, MenuView height is MATCH_PARENT is not work.
        // getViewTreeObserver().addOnGlobalLayoutListener(
        // new OnGlobalLayoutListener() {
        // @Override
        // public void onGlobalLayout() {
        // setMenuHeight(mContentView.getHeight());
        // // getViewTreeObserver()
        // // .removeGlobalOnLayoutListener(this);
        // }
        // });

        ((TextView) mContentView.findViewById(R.id.label123)).setTypeface(MainActivity.font_bold);
        if (!removed)
            ((TextView) mMenuView.findViewById(1123)).setTypeface(MainActivity.font);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public boolean onSwipe(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
//                System.out.println("down " + mDownX);
                isFling = false;
                break;
            case MotionEvent.ACTION_MOVE:
//                System.out.println("move");
                if (mDownX + 1 == 0) mDownX = (int) event.getX();
                // Log.i("byz", "downX = " + mDownX + ", moveX = " + event.getX());
                int dis = (int) (mDownX - event.getX());
//                System.out.println(mDownX + " " + event.getX());
//                System.out.println("move");

                if (dis > 0) {
                    if (!(mSwipeDirection == SwipeMenuListView.DIRECTION_RIGHT && mContentView.getLeft() != 0)) {
                        mSwipeDirection = SwipeMenuListView.DIRECTION_LEFT;
//                        ViewGroup.LayoutParams pre = mMenuView.findViewById(10).getLayoutParams();
//                        pre.width = dp2px(90);
//                        mMenuView.findViewById(10).setLayoutParams(pre);
//                        pre = mMenuView.findViewById(11).getLayoutParams();
//                        pre.width = 0;
//                        mMenuView.findViewById(11).setLayoutParams(pre);
//                        mMenuView.postInvalidate();
//                        mMenuView.refreshDrawableState();
//                        System.out.println("move");
//                        System.out.println("Changed direction from right to left");
                    }
                } else {
                    if (!(mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT && mContentView.getLeft() != 0)) {
                        mSwipeDirection = SwipeMenuListView.DIRECTION_RIGHT;
//                        DisplayMetrics metrics = new DisplayMetrics();
//                        MainActivity.main.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                        ViewGroup.LayoutParams pre = mMenuView.findViewById(11).getLayoutParams();
//                        pre.width = metrics.widthPixels - dp2px(90);
//                        mMenuView.findViewById(11).setLayoutParams(pre);
//                        pre = mMenuView.findViewById(10).getLayoutParams();
//                        pre.width = 0;
//                        mMenuView.findViewById(10).setLayoutParams(pre);
//                        mMenuView.postInvalidate();
//                        mMenuView.refreshDrawableState();
//                        System.out.println("move");
//                        System.out.println(mContentView.getLeft());
//                        System.out.println("Changed direction from left to right");
                    }
                }
//                mMenuView.removeAllViews();
//                mMenuView.mMenu.clear();
//                if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
////                            System.out.println(deleteItem.getWidth() + " width remove");
//                    mMenuView.mMenu.addMenuItem(MainActivity.deleteItem);
////                            deleteItem.setWidth(dp2px(90));
////                            anotherItem.setWidth(500);
//                } else {
//                    mMenuView.mMenu.addMenuItem(MainActivity.anotherItem);
////                            deleteItem.setWidth(500);
////                            anotherItem.setWidth(metrics.widthPixels - dp2px(90));
//                }
//                mMenuView.addViews();
//                if (mSwipeDirection == SwipeMenuListView.DIRECTION_RIGHT) {
//                } else {
//                }

//                System.out.println("swipe " + dis);
                if (state == STATE_OPEN) {
                    if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT && mContentView.getLeft() < 0)
                        state = STATE_CLOSE;
                    else
                        dis += wd(mMenuView) * mSwipeDirection;
                }
//                System.out.println("swipe " + dis);
                swipe(dis);
                break;
            case MotionEvent.ACTION_UP:
//                System.out.println("up");
                if (mDownX + 1 == 0) mDownX = (int) event.getX();
                if ((isFling || Math.abs(mDownX - event.getX()) > (POROG(wd(mMenuView)))) &&
                        Math.signum(mDownX - event.getX()) == mSwipeDirection) {
                    // open
//                    System.out.println("open menu");
                    smoothOpenMenu();
                } else {
                    // close
//                    System.out.println("close menu");
                    smoothCloseMenu();
                    return false;
                }
                break;
        }
        return true;
    }

    public boolean isOpen() {
        return state == STATE_OPEN;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void swipe(int dis) {
        if (removed && mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
            clearSwipe();
            mSwipeDirection = SwipeMenuListView.DIRECTION_RIGHT;
        }
//        System.out.println(dis + " " + getWidth(mMenuView));
//        boolean sd = false;
        if (mSwipeDirection == SwipeMenuListView.DIRECTION_RIGHT) {
            if (!removed2)
                if (Math.abs(dis) > getSecondView().getWidth()) {
                    dis = -getSecondView().getWidth();
                }
        } else {
            if (!removed)
                if (Math.abs(dis) > getFirstView().getWidth()) {
                    dis = getFirstView().getWidth();
                }
        }
        if (Math.signum(dis) != mSwipeDirection) {
            dis = 0;
//            mSwipeDirection = SwipeMenuListView.DIRECTION_LEFT;
//            ViewGroup.LayoutParams pre = mMenuView.findViewById(10).getLayoutParams();
//            pre.width = dp2px(90);
//            mMenuView.findViewById(10).setLayoutParams(pre);
//            pre = mMenuView.findViewById(11).getLayoutParams();
//            pre.width = 0;
//            mMenuView.findViewById(11).setLayoutParams(pre);
        } else if (Math.abs(dis) >= wd(mMenuView)) {
            dis = wd(mMenuView) * mSwipeDirection;
            if (mSwipeDirection == SwipeMenuListView.DIRECTION_RIGHT && !removed2) {
//                dis = 0;
                MainActivity.swipe = this;
                MainActivity.main.sendGO(mContentView);
                //  dis = 0;
//                sd = true;
            }
        }

        mContentView.layout(-dis, mContentView.getTop(),
                mContentView.getWidth() - dis, getMeasuredHeight());

        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
            mMenuView.layout(mContentView.getWidth() - dis, mMenuView.getTop(),
                    mContentView.getWidth() + getWidth(mMenuView) - dis,
                    mMenuView.getBottom());
        } else {
            mMenuView.layout(-getWidth(mMenuView) - dis, mMenuView.getTop(),
                    -dis, mMenuView.getBottom());
        }

        MainActivity.swipe = this;
    }

    public void clearSwipe() {
        int dis = 0;
        state = STATE_CLOSE;
        mContentView.layout(-dis, mContentView.getTop(),
                mContentView.getWidth() - dis, getMeasuredHeight());

        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
            mMenuView.layout(mContentView.getWidth() - dis, mMenuView.getTop(),
                    mContentView.getWidth() + getWidth(mMenuView) - dis,
                    mMenuView.getBottom());
        } else {
            mMenuView.layout(-getWidth(mMenuView) - dis, mMenuView.getTop(),
                    -dis, mMenuView.getBottom());
        }
    }

    @Override
    public void computeScroll() {
        if (state == STATE_OPEN) {
            if (mOpenScroller.computeScrollOffset()) {
                swipe(mOpenScroller.getCurrX() * mSwipeDirection);
                postInvalidate();
            }
        } else {
            if (mCloseScroller.computeScrollOffset()) {
                swipe((mBaseX - mCloseScroller.getCurrX()) * mSwipeDirection);
                postInvalidate();
            }
        }
    }

    public void smoothCloseMenu() {
        state = STATE_CLOSE;
        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
            mBaseX = -mContentView.getLeft();
            mCloseScroller.startScroll(0, 0, wd(mMenuView), 0, 350);
        } else {
            mBaseX = mMenuView.getRight();
            mCloseScroller.startScroll(0, 0, wd(mMenuView), 0, 350);
        }
        postInvalidate();
    }

    public void smoothOpenMenu() {
        state = STATE_OPEN;
        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
            mOpenScroller.startScroll(-mContentView.getLeft(), 0, wd(mMenuView), 0, 350);
        } else {
            mOpenScroller.startScroll(mContentView.getLeft(), 0, wd(mMenuView), 0, 350);
        }
        postInvalidate();
    }

    public void closeMenu() {
        if (mCloseScroller.computeScrollOffset()) {
            mCloseScroller.abortAnimation();
        }
        if (state == STATE_OPEN) {
            state = STATE_CLOSE;
            mDownX = -1;
            swipe(0);
        }
    }

    public void openMenu() {
        if (state == STATE_CLOSE) {
            state = STATE_OPEN;
            swipe(wd(mMenuView) * mSwipeDirection);
        }
    }

    public View getContentView() {
        return mContentView;
    }

    public SwipeMenuView getMenuView() {
        return mMenuView;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMenuView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mContentView.layout(0, 0, getMeasuredWidth(),
                mContentView.getMeasuredHeight());
        if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
            mMenuView.layout(getMeasuredWidth(), 0,
                    getMeasuredWidth() + mMenuView.getMeasuredWidth(),
                    mContentView.getMeasuredHeight());
        } else {
            mMenuView.layout(-mMenuView.getMeasuredWidth(), 0,
                    0, mContentView.getMeasuredHeight());
        }
    }

    public void setMenuHeight(int measuredHeight) {
        Log.i("byz", "pos = " + position + ", height = " + measuredHeight);
        LayoutParams params = (LayoutParams) mMenuView.getLayoutParams();
        if (params.height != measuredHeight) {
            params.height = measuredHeight;
            mMenuView.setLayoutParams(mMenuView.getLayoutParams());
        }
    }
}
