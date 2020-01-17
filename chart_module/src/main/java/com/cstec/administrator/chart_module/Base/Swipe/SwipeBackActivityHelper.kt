package com.cstec.administrator.chart_module.Base.Swipe

import com.cstec.administrator.chart_module.View.SwipeBackLayout
import android.view.LayoutInflater
import android.graphics.drawable.ColorDrawable
import android.app.Activity
import android.graphics.Color
import android.view.View
import com.cstec.administrator.chart_module.R


class SwipeBackActivityHelper{

      var mActivity: Activity

    private var mSwipeBackLayout: SwipeBackLayout? = null

    constructor(activity: Activity){
        mActivity = activity
    }


    fun onActivityCreate() {
        mActivity.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mActivity.window.decorView.setBackgroundDrawable(null)
        mSwipeBackLayout = LayoutInflater.from(mActivity).inflate(
                R.layout.swipeback_layout, null) as SwipeBackLayout
        mSwipeBackLayout!!.addSwipeListener(object : SwipeBackLayout.SwipeListener {
            override fun onScrollStateChange(state: Int, scrollPercent: Float) {}

            override fun onEdgeTouch(edgeFlag: Int) {
                SwipeUtils.convertActivityToTranslucent(mActivity)
            }

            override fun onScrollOverThreshold() {

            }
        })
    }

    fun onPostCreate() {
        mSwipeBackLayout!!.attachToActivity(mActivity)
    }

    fun findViewById(id: Int): View? {
        return if (mSwipeBackLayout != null) {
            mSwipeBackLayout!!.findViewById<View>(id)
        } else null
    }

    fun getSwipeBackLayout(): SwipeBackLayout? {
        return mSwipeBackLayout
    }
}