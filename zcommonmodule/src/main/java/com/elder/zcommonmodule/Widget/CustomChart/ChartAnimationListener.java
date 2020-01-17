package com.elder.zcommonmodule.Widget.CustomChart;


import java.util.ArrayList;


/**
 */
public interface ChartAnimationListener {

    /**
     *
     * @param data Chart data to be used in the next view invalidation.
     */
    boolean onAnimationUpdate(ArrayList<ChartSet> data);
}
