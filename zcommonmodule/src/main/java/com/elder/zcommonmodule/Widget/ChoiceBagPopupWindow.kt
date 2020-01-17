package com.ucar.myapplication.Wedge

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.R
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Utils.ConvertUtils.Companion.dp2px
import org.w3c.dom.Text


class ChoiceBagPopupWindow(context: Context) : PopupWindow(context) {

    init {
        height = dp2px(88F)
        width = dp2px(88F)
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(context.resources.getDrawable(R.drawable.corner_dialog_fivedp))
        var view = LayoutInflater.from(context).inflate(R.layout.popupbag, null, false)
        view.findViewById<TextView>(R.id.pop_search).setOnClickListener {
            ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY).navigation(context as Activity, REQUEST_LOAD_ROADBOOK)
            this.dismiss()
        }
//        view.findViewById<TextView>(R.id.middle_bag_price).typeface = BaseApplication.getInstance().WeChatSansStd
        contentView = view
    }
}