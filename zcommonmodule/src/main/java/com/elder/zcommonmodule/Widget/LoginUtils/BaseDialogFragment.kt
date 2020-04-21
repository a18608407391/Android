package com.elder.zcommonmodule.Widget.LoginUtils

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.support.annotation.StyleRes
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.View
import com.elder.zcommonmodule.R
import android.view.Gravity
import android.view.WindowManager
import android.view.KeyEvent.KEYCODE_BACK
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import com.amap.api.maps2d.MapFragment
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Widget.CityPicker.util.ScreenUtil


open class BaseDialogFragment : DialogFragment() {
    private var mAnimStyle = R.style.DefaultCityPickerAnimation
    var mContentView: View? = null
    private var height: Int = 0
    private var width: Int = 0
    var dismissValue: Any? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CityPickerStyle)
    }

    @SuppressLint("ResourceType")
    fun setAnimationStyle(@StyleRes resId: Int) {
        this.mAnimStyle = if (resId <= 0) mAnimStyle else resId
    }

    private fun measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val dm = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getRealMetrics(dm)
            height = dm.heightPixels
            width = dm.widthPixels
        } else {
            val dm = resources.displayMetrics
            height = dm.heightPixels
            width = dm.widthPixels
        }
    }

    override fun onStart() {
        super.onStart()
        var dialog = dialog
        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (functionDismiss != null) {
                        functionDismiss?.onDismiss(this@BaseDialogFragment,dismissValue!!)
                    }
                }
                return false
            }
        })

        measure()
        val window = dialog.window
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            window.setGravity(Gravity.BOTTOM)
            window.setLayout(width, height - ScreenUtil.getStatusBarHeight(getActivity()))
            window.setWindowAnimations(mAnimStyle)
        }
    }

    override fun dismiss() {
        super.dismiss()
        functionDismiss?.onDismiss(this,dismissValue!!)
    }

    var progress: Dialog? = null

    fun showProgress(title: String) {
        if (progress == null) {
            progress = DialogUtils.showProgress(this.activity!!, title)
        } else if (progress!!.isShowing) {
            progress!!.show()
        }
    }

    fun dissmissProgress() {
        if (progress != null && progress!!.isShowing) {
            progress!!.dismiss()
        }
    }

    var functionDismiss: DismissListener? = null

    interface DismissListener {
        fun onDismiss(fr: BaseDialogFragment, value: Any)
    }
}