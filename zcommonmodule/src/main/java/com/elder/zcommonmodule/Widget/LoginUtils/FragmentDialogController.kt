package com.elder.zcommonmodule.Widget.LoginUtils

import android.support.annotation.StyleRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import java.lang.ref.WeakReference


class FragmentDialogController {

    private val TAG = "CityPicker"

    private lateinit var mContext: WeakReference<FragmentActivity>
    private lateinit var mFragment: WeakReference<Fragment>
    private var mFragmentManager: WeakReference<FragmentManager>? = null
    private var enableAnim: Boolean = true
    private var mAnimStyle: Int = 0

    constructor(fragment: Fragment) {
        FragmentDialogController(fragment.activity, fragment)
        mFragmentManager = WeakReference(fragment.childFragmentManager)
    }

    constructor(activity: FragmentActivity) {
        FragmentDialogController(activity, null)
        mFragmentManager = WeakReference(activity.supportFragmentManager)
    }

    constructor(activity: FragmentActivity?, fragment: Fragment?) {
        mContext = WeakReference<FragmentActivity>(activity)
        mFragment = WeakReference<Fragment>(fragment)
    }

    fun from(fragment: Fragment): FragmentDialogController {
        return FragmentDialogController(fragment)
    }

    fun from(activity: FragmentActivity): FragmentDialogController {
        return FragmentDialogController(activity)
    }

    /**
     * 设置动画效果
     * @param animStyle
     * @return
     */
    fun setAnimationStyle(@StyleRes animStyle: Int): FragmentDialogController {
        this.mAnimStyle = animStyle
        return this@FragmentDialogController
    }

    /**
     * 启用动画效果，默认为false
     * @param enable
     * @return
     */
    fun enableAnimation(enable: Boolean): FragmentDialogController {
        this.enableAnim = enable
        return this
    }

    /**
     * 设置选择结果的监听器
     * @param listener
     * @return
     */

    fun show(dialog: BaseDialogFragment): BaseDialogFragment {
        var ft = mFragmentManager!!.get()?.beginTransaction()
        val prev = mFragmentManager!!.get()?.findFragmentByTag(TAG)
        if (prev != null) {
            ft?.remove(prev)!!.commit()
            ft = mFragmentManager!!.get()!!.beginTransaction()
        }
        dialog.isCancelable = false
        ft?.addToBackStack(null)
        dialog.setAnimationStyle(mAnimStyle)
        dialog.show(ft, TAG)
        return dialog
    }

    /**
     * 定位完成
     * @param location
     * @param state
     */

}