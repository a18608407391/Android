package com.example.drivermodule.Overlay

import android.content.Context
import android.content.res.Resources
import android.widget.ImageView
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.AttributeSet
import com.example.drivermodule.R


class NextTurnTipView : ImageView {
    private var customIconTypeDrawables: IntArray? = null
    private var nextTurnBitmap: Bitmap? = null
    private var mLastIconType: Long = -1
    private var customRes: Resources? = null
    private val defaultIconTypes = intArrayOf(R.drawable.caricon,
            R.drawable.caricon,
            R.drawable.action2,
            R.drawable.action3,
            R.drawable.action4,
            R.drawable.action5,
            R.drawable.action6,
            R.drawable.action7,
            R.drawable.action8,
            R.drawable.action9,
            R.drawable.action10,
            R.drawable.action11,
            R.drawable.action12,
            R.drawable.action13,
            R.drawable.action14,
            R.drawable.action_end
//            R.drawable.action16,  //涵洞
//            R.drawable.action17,                //进入环岛，注意，这个是左侧通行地区的顺时针环岛
//            R.drawable.action18,                //驶出环岛，注意，这个是左侧通行地区的顺时针环岛
//            R.drawable.amap_navi_lbs_sou19,    //右转掉头图标 ，注意，这个是左侧通行地区的掉头
//            R.drawable.amap_navi_lbs_sou20
    )   //顺行图标

    /**
     * 用于设置自定义的icon图片数组。
     *
     *
     * 此方法与NextTurnTipView.setIconType(int)相互配合。
     * 导航过程中，SDK会调用NextTurnTipView.setIconType(int)设置转向图片，其中图片
     * 就是通过customIconTypes[]数组对应index下标获取的。
     *
     * @param res             Resource资源
     * @param customIconTypes icon数组，数组中存放图片的顺序需要与IconType依次对应
     */
    fun setCustomIconTypes(res: Resources?, customIconTypes: IntArray?) {
        try {
            if (res == null || customIconTypes == null) {
                return
            }
            this.customRes = res
            customIconTypeDrawables = IntArray(customIconTypes.size + 2)
            for (i in customIconTypes.indices) {
                customIconTypeDrawables!![i + 2] = customIconTypes[i]
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    /**
     * @exclude
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    /**
     * @exclude
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /**
     * @exclude
     */
    constructor(context: Context) : super(context) {

    }

    /**
     * 释放图片资源
     */
    fun recycleResource() {
        try {
            if (nextTurnBitmap != null) {
                nextTurnBitmap!!.recycle()
                nextTurnBitmap = null
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    /**
     * 设置自定义的转向图标，请参考[com.amap.api.navi.enums.IconType]
     *
     * @param iconType 图片的id
     */
    fun setIconType(iconType: Int) {
        try {
            if (iconType > 20 || mLastIconType == iconType.toLong()) {
                return
            }
            recycleResource()
            if (customIconTypeDrawables != null && customRes != null) {
                nextTurnBitmap = BitmapFactory.decodeResource(customRes, customIconTypeDrawables!![iconType])
            } else {
                nextTurnBitmap = BitmapFactory.decodeResource(resources, defaultIconTypes[iconType])
            }
            setImageBitmap(nextTurnBitmap)
            mLastIconType = iconType.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
