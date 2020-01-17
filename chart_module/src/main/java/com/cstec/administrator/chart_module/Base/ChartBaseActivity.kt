package com.cstec.administrator.chart_module.Base

import android.content.Context
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.cstec.administrator.chart_module.Base.Swipe.SwipeBackActivityHelper
import com.cstec.administrator.chart_module.Base.Swipe.SwipeInteface
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseViewModel
import com.cstec.administrator.chart_module.View.SwipeBackLayout
import com.cstec.administrator.chart_module.Base.Swipe.SwipeUtils
import android.view.inputmethod.InputMethodManager


abstract class ChartBaseActivity<V : ViewDataBinding, VM : BaseViewModel> : BaseActivity<V, VM>(), SwipeInteface {

    lateinit var mHelper: SwipeBackActivityHelper
    override fun initData() {
        super.initData()
        mHelper = SwipeBackActivityHelper(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper.onPostCreate()
    }

    override fun <T : View> findViewById(id: Int): T {
        val v = super.findViewById<View>(id)
        return if (v == null && mHelper != null) mHelper.findViewById(id) as T else v as T
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mHelper.getSwipeBackLayout()!!
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        getSwipeBackLayout().setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        SwipeUtils.convertActivityToTranslucent(this)
        getSwipeBackLayout().scrollToFinishActivity()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (null != this.currentFocus) {
            val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return mInputMethodManager!!.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.onTouchEvent(event)
    }




}