package org.cs.tec.library.Utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.getText

import java.lang.ref.WeakReference

/**
 * Created by goldze on 2017/5/14.
 * 吐司工具类
 */
class ToastUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        private val DEFAULT_COLOR = 0x12000000
        private var sToast: Toast? = null
        private var gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        private var xOffset = 0
        private var yOffset = (64 * context.resources.displayMetrics.density + 0.5).toInt()
        private var mbackgroundColor = DEFAULT_COLOR
        private var mbgResource = -1
        private var mmessageColor = DEFAULT_COLOR
        private var sViewWeakReference: WeakReference<View>? = null
        private val sHandler = Handler(Looper.getMainLooper())

        /**
         * 设置吐司位置
         *
         * @param gravity 位置
         * @param xOffset x偏移
         * @param yOffset y偏移
         */
        fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
            ToastUtils.gravity = gravity
            ToastUtils.xOffset = xOffset
            ToastUtils.yOffset = yOffset
        }

        /**
         * 设置吐司view
         *
         * @param layoutId 视图
         */
        fun setView(@LayoutRes layoutId: Int) {
            val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            sViewWeakReference = WeakReference(inflate.inflate(layoutId, null))
        }

        /**
         * 设置吐司view
         *
         * @param view 视图
         */
        fun setView(view: View?) {
            sViewWeakReference = if (view == null) null else WeakReference(view)
        }

        /**
         * 获取吐司view
         *
         * @return view
         */
        val view: View?
            get() {
                if (sViewWeakReference != null) {
                    val view = sViewWeakReference!!.get()
                    if (view != null) {
                        return view
                    }
                }
                return if (sToast != null) sToast!!.view else null
            }

        /**
         * 设置背景颜色
         *
         * @param backgroundColor 背景色
         */
        fun setBackgroundColor(@ColorInt backgroundColor: Int) {
            ToastUtils.mbackgroundColor = backgroundColor
        }

        /**
         * 设置背景资源
         *
         * @param bgResource 背景资源
         */
        fun setBgResource(@DrawableRes bgResource: Int) {
            ToastUtils.mbgResource = bgResource
        }

        /**
         * 设置消息颜色
         *
         * @param messageColor 颜色
         */
        fun setMessageColor(@ColorInt messageColor: Int) {
            ToastUtils.mmessageColor = messageColor
        }

        /**
         * 安全地显示短时吐司
         *
         * @param text 文本
         */
        fun showShortSafe(text: CharSequence) {
            sHandler.post { show(text, Toast.LENGTH_SHORT) }
        }

        /**
         * 安全地显示短时吐司
         *
         * @param resId 资源Id
         */
        fun showShortSafe(@StringRes resId: Int) {
            sHandler.post { show(resId, Toast.LENGTH_SHORT) }
        }

        /**
         * 安全地显示短时吐司
         *
         * @param resId 资源Id
         * @param args  参数
         */
        fun showShortSafe(@StringRes resId: Int, vararg args: Any) {
            sHandler.post { show(resId, Toast.LENGTH_SHORT, *args) }
        }

        /**
         * 安全地显示短时吐司
         *
         * @param format 格式
         * @param args   参数
         */
        fun showShortSafe(format: String, vararg args: Any) {
            sHandler.post { show(format, Toast.LENGTH_SHORT, *args) }
        }

        /**
         * 安全地显示长时吐司
         *
         * @param text 文本
         */
        fun showLongSafe(text: CharSequence) {
            sHandler.post { show(text, Toast.LENGTH_LONG) }
        }

        /**
         * 安全地显示长时吐司
         *
         * @param resId 资源Id
         */
        fun showLongSafe(@StringRes resId: Int) {
            sHandler.post { show(resId, Toast.LENGTH_LONG) }
        }

        /**
         * 安全地显示长时吐司
         *
         * @param resId 资源Id
         * @param args  参数
         */
        fun showLongSafe(@StringRes resId: Int, vararg args: Any) {
            sHandler.post { show(resId, Toast.LENGTH_LONG, *args) }
        }

        /**
         * 安全地显示长时吐司
         *
         * @param format 格式
         * @param args   参数
         */
        fun showLongSafe(format: String, vararg args: Any) {
            sHandler.post { show(format, Toast.LENGTH_LONG, *args) }
        }

        /**
         * 显示短时吐司
         *
         * @param text 文本
         */
        fun showShort(text: CharSequence) {
            show(text, Toast.LENGTH_SHORT)
        }

        /**
         * 显示短时吐司
         *
         * @param resId 资源Id
         */
        fun showShort(@StringRes resId: Int) {
            show(resId, Toast.LENGTH_SHORT)
        }

        /**
         * 显示短时吐司
         *
         * @param resId 资源Id
         * @param args  参数
         */
        fun showShort(@StringRes resId: Int, vararg args: Any) {
            show(resId, Toast.LENGTH_SHORT, *args)
        }

        /**
         * 显示短时吐司
         *
         * @param format 格式
         * @param args   参数
         */
        fun showShort(format: String, vararg args: Any) {
            show(format, Toast.LENGTH_SHORT, *args)
        }

        /**
         * 显示长时吐司
         *
         * @param text 文本
         */
        fun showLong(text: CharSequence) {
            show(text, Toast.LENGTH_LONG)
        }

        /**
         * 显示长时吐司
         *
         * @param resId 资源Id
         */
        fun showLong(@StringRes resId: Int) {
            show(resId, Toast.LENGTH_LONG)
        }

        /**
         * 显示长时吐司
         *
         * @param resId 资源Id
         * @param args  参数
         */
        fun showLong(@StringRes resId: Int, vararg args: Any) {
            show(resId, Toast.LENGTH_LONG, *args)
        }

        /**
         * 显示长时吐司
         *
         * @param format 格式
         * @param args   参数
         */
        fun showLong(format: String, vararg args: Any) {
            show(format, Toast.LENGTH_LONG, *args)
        }

        /**
         * 安全地显示短时自定义吐司
         */
        fun showCustomShortSafe() {
            sHandler.post { show("", Toast.LENGTH_SHORT) }
        }

        /**
         * 安全地显示长时自定义吐司
         */
        fun showCustomLongSafe() {
            sHandler.post { show("", Toast.LENGTH_LONG) }
        }

        /**
         * 显示短时自定义吐司
         */
        fun showCustomShort() {
            show("", Toast.LENGTH_SHORT)
        }

        /**
         * 显示长时自定义吐司
         */
        fun showCustomLong() {
            show("", Toast.LENGTH_LONG)
        }

        /**
         * 显示吐司
         *
         * @param resId    资源Id
         * @param duration 显示时长
         */
        private fun show(@StringRes resId: Int, duration: Int) {
            show(getText(resId).toString(), duration)
        }

        /**
         * 显示吐司
         *
         * @param resId    资源Id
         * @param duration 显示时长
         * @param args     参数
         */
        private fun show(@StringRes resId: Int, duration: Int, vararg args: Any) {
            show(String.format(getString(resId), *args), duration)
        }

        /**
         * 显示吐司
         *
         * @param format   格式
         * @param duration 显示时长
         * @param args     参数
         */
        private fun show(format: String, duration: Int, vararg args: Any) {
            show(String.format(format, *args), duration)
        }

        /**
         * 显示吐司
         *
         * @param text     文本
         * @param duration 显示时长
         */
        private fun show(text: CharSequence, duration: Int) {
            cancel()
            var isCustom = false
            if (sViewWeakReference != null) {
                val view = sViewWeakReference!!.get()
                if (view != null) {
                    sToast = Toast(context)
                    sToast!!.view = view
                    sToast!!.duration = duration
                    isCustom = true
                }
            }
            if (!isCustom) {
                if (mmessageColor != DEFAULT_COLOR) {
                    val spannableString = SpannableString(text)
                    val colorSpan = ForegroundColorSpan(mmessageColor)
                    spannableString.setSpan(colorSpan, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    sToast = Toast.makeText(context, spannableString, duration)
                } else {
                    sToast = Toast.makeText(context, text, duration)
                }
            }
            val view = sToast!!.view
            if (mbgResource != -1) {
                view.setBackgroundResource(mbgResource)
            } else if (mbackgroundColor != DEFAULT_COLOR) {
                view.setBackgroundColor(mbackgroundColor)
            }
            sToast!!.setGravity(gravity, xOffset, yOffset)
            sToast!!.show()
        }

        /**
         * 取消吐司显示
         */
        fun cancel() {
            if (sToast != null) {
                sToast!!.cancel()
                sToast = null
            }
        }
    }
}