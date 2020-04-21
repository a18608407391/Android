package com.example.drivermodule;

import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.example.drivermodule.Sliding.ScrollableViewHelper;

public class NestedScrollableViewHelper extends ScrollableViewHelper {

    public int getScrollableViewScrollPosition(View mScrollableView, boolean isSlidingUp) {
        if (mScrollableView instanceof NestedScrollView) {
            if (isSlidingUp) {
                return mScrollableView.getScrollY();
            } else {
                NestedScrollView nsv = ((NestedScrollView) mScrollableView);
                View child = nsv.getChildAt(0);
                return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
            }
        } else {
            return 0;
        }
    }
}