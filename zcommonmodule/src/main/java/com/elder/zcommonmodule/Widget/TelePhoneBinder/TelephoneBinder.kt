package com.elder.zcommonmodule.Widget.TelePhoneBinder

import android.support.annotation.StyleRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import java.lang.ref.WeakReference


class TelephoneBinder {

    lateinit var mContext: WeakReference<FragmentActivity>
    lateinit var mFragment: WeakReference<Fragment>
    lateinit var mFragmentManager: WeakReference<FragmentManager>

    var enableAnim: Boolean = true
    var mAnimStyle: Int = 0


    constructor(fragment: Fragment) {
        mFragmentManager = WeakReference(fragment.childFragmentManager)
    }

    constructor(activity: FragmentActivity, fragment: Fragment) {
        mContext = WeakReference(activity)
        mFragment = WeakReference(fragment)
    }

    constructor(activity: FragmentActivity) {
        TelephoneBinder(activity, null!!)
        mFragmentManager = WeakReference(activity.supportFragmentManager)
    }


    companion object {
        var TAG = "TelephoneBinder"

        fun from(fragment: Fragment): TelephoneBinder {
            return TelephoneBinder(fragment)
        }

        fun from(activity: FragmentActivity): TelephoneBinder {
            return TelephoneBinder(activity)
        }
    }

    fun setAnimationStyle(@StyleRes animStyle: Int): TelephoneBinder {
        this.mAnimStyle = animStyle
        return this@TelephoneBinder
    }

    fun enableAnimation(enable: Boolean): TelephoneBinder {
        this.enableAnim = enable
        return this@TelephoneBinder
    }

    fun show(): TelephoneBinderDialogFragment {
        var ft = mFragmentManager.get()!!.beginTransaction()
        val prev = mFragmentManager.get()!!.findFragmentByTag(TAG)
        if (prev != null) {
            ft.remove(prev).commit()
            ft = mFragmentManager.get()?.beginTransaction()!!
        }
        ft.addToBackStack(null)
        var cityPickerFragment = TelephoneBinderDialogFragment.newInstance(enableAnim)
        cityPickerFragment.setAnimationStyle(mAnimStyle)
        cityPickerFragment.isCancelable = false
        cityPickerFragment.show(ft, TAG)
        return cityPickerFragment
    }

}