package com.idlestar.tabindicator;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hxw on 2017-01-08.
 */
public class SamplePagerAdapter extends PagerAdapter {

    private Activity mActivity;

    public SamplePagerAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Item " + (position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.pager_item,
                container, false);

        TextView title = (TextView) view.findViewById(R.id.item_title);
        title.setText(String.valueOf((position + 1)));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
