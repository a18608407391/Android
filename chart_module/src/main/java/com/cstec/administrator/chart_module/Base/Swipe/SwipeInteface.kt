package com.cstec.administrator.chart_module.Base.Swipe

import com.cstec.administrator.chart_module.View.SwipeBackLayout


interface SwipeInteface{


    abstract fun getSwipeBackLayout(): SwipeBackLayout

    abstract fun setSwipeBackEnable(enable: Boolean)

    /**
     * Scroll out contentView and finish the activity
     */
    abstract fun scrollToFinishActivity()


}