package com.idlestar.tabindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by hxw on 2017-01-08.
 */
public final class TabIndicator extends LinearLayout {
    private ViewPager mViewPager;

    private Paint mIndicatorPaint = new Paint();
    private Paint mDividerPaint = new Paint();
    private Paint mBottomLinePaint = new Paint();

    private int mIndicatorHeight = 8;
    private float mDividerWidth = 2f;
    private float mDividerPadding = 16f;
    private int mIndicatorColor = 0xff00b0ff;
    private int mDividerColor = 0xffeeeeee;
    private int mBottomLineColor = 0xffeeeeee;
    private boolean mEnableDivider = true;

    private int mSelectedPosition = 0;
    private float mIndicatorOffset = 0f;

    private ViewPager.OnPageChangeListener mOuterPageListener;

    public TabIndicator(Context context) {
        this(context, null);
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        mIndicatorPaint.setColor(mIndicatorColor);
        mDividerPaint.setColor(mDividerColor);
        mBottomLinePaint.setColor(mBottomLineColor);

        // ensure the call of onDraw().
        setWillNotDraw(false);
        setGravity(Gravity.CENTER_VERTICAL);

        // use bottom padding for the bottom indicator drawing. Or childView will cover our drawings.
        setPadding(0, 0, 0, mIndicatorHeight);
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;

        if (viewPager != null) {
            // now the ViewPager has addOnPageChangeListener()... never mind.
            viewPager.setOnPageChangeListener(new PageChangeListener());
            buildTabStrip();
        }
    }

    private void buildTabStrip() {
        removeAllViews();

        PagerAdapter adapter = mViewPager.getAdapter();
        TabClickListener tabClickListener = new TabClickListener();

        int tabCount = adapter.getCount();
        int dividerWidth = (int) mDividerWidth;
        for (int i = 0; i < tabCount; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            params.weight = 1;

            if (dividerWidth > 0) {
                if (i != 0) {
                    // use marginRight to make space for divider line.
                    params.setMargins(dividerWidth, 0, 0, 0);
                }
            }

            TextView tabTitleView = createTabTitleView(params);
            tabTitleView.setText(adapter.getPageTitle(i));
            tabTitleView.setOnClickListener(tabClickListener);

            addView(tabTitleView);
        }
    }

    private TextView createTabTitleView(LinearLayout.LayoutParams params) {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(Color.WHITE);
        textView.setLayoutParams(params);
        return textView;
    }

    private class PageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabCount = getChildCount();
            if ((tabCount == 0) || (position < 0) || (position >= tabCount)) {
                return;
            }

            onViewPagerPageChanged(position, positionOffset);

            if (mOuterPageListener != null) {
                mOuterPageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mOuterPageListener != null) {
                mOuterPageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            // this is called before the onPageScrolled progress finished.
            // do not conflict with drag or setting-scroll.
            // ViewPager.setCurrentItem(index, animating) may need this?
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                onViewPagerPageChanged(position, 0f);
            }

            if (mOuterPageListener != null) {
                mOuterPageListener.onPageSelected(position);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("TabIndicator","onDraw");

        View selectedTitle = getChildAt(mSelectedPosition);
        int tabCount = getChildCount();
        int tabWidth = selectedTitle.getWidth();
        int tabHostHeight = getHeight();

        // draw bottom line, be ware of the bottom line is draw within the View, so marginBottom = 1px
        canvas.drawLine(getLeft(), tabHostHeight - 1, getRight(), tabHostHeight - 1, mBottomLinePaint);

        // draw bottom tab indicator.
        if (tabCount > 0) {
            // all tabViews have same width & height.
            // we can also calculate the distance of current child and target child in horizontal.
            int left = selectedTitle.getLeft();
            int right = selectedTitle.getRight();

            if (mIndicatorOffset > 0f && mSelectedPosition < (tabCount - 1)) {
                int offsetPixels = (int) (tabWidth * mIndicatorOffset);
                left += offsetPixels;
                right += offsetPixels;
            }

            canvas.drawRect(left, tabHostHeight - mIndicatorHeight, right,
                    tabHostHeight, mIndicatorPaint);
        }

        // draw the divider.
        if (mEnableDivider && mDividerWidth > 0 && tabCount > 1) {
            View tab = getChildAt(0);

            if (mDividerPadding > tab.getHeight()) {
                mDividerPadding = tab.getHeight() / 2.0f;
            }

            float startY = tab.getY() + mDividerPadding;
            float stopY = tab.getY() + tab.getHeight() - mDividerPadding;

            mDividerPaint.setStrokeWidth(mDividerWidth);
            float halfDividerWidth = mDividerWidth / 2.0f;

            for (int i = 0; i < tabCount - 1; i++) {
                tab = getChildAt(i);

                canvas.drawLine(tab.getRight() + halfDividerWidth,
                        startY, tab.getRight() + halfDividerWidth,
                        stopY,
                        mDividerPaint);
            }
        }
    }

    public void onViewPagerPageChanged(int position, float positionOffset) {
        if (mSelectedPosition == position
                && mIndicatorOffset == positionOffset) return;

        mSelectedPosition = position;
        mIndicatorOffset = positionOffset;
        invalidate();
    }

    private class TabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            for (int i = 0; i < getChildCount(); i++) {
                if (v == getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

    /**
     * Call this to set OnPageChangeListener for the associate ViewPager.
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mOuterPageListener = listener;
    }
}
