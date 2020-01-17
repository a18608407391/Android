package com.elder.zcommonmodule.Widget

import android.content.Context
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.elder.zcommonmodule.R
import org.cs.tec.library.Utils.ConvertUtils


class CustomPopuWindow(context: Context) : PopupWindow(context) {
    init {
        height = ConvertUtils.dp2px(267F)
        width = ConvertUtils.dp2px(50F)
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(context.resources.getDrawable(R.drawable.float_layout_bg))
        var view = LayoutInflater.from(context).inflate(R.layout.popuwindow_custom, null, false)
        contentView = view
    }

}