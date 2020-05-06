package com.elder.zcommonmodule.Component

import android.databinding.ObservableField
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import com.elder.zcommonmodule.R
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import java.io.Serializable

class DriverComponent : TitleComponent(), Serializable {
    var type = ObservableField<Int>(1)

    var titleVisible = ObservableField<Boolean>(false)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setHomeStyle() {
        type.set(1)
        title.set(getString(R.string.driver))
        rightText.set("")
        arrowVisible.set(false)
        rightVisibleType.set(false)
        rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
    }


    fun onFiveClick(view: View) {
        if (fiveListener != null) {
            fiveListener?.FiveBtnClick(view)
        }
    }




    var isTeam  = ObservableField<Boolean>(false)            //以前用于判断组队是否开启，现在作用不大


    var Drivering = ObservableField<Boolean>(true)       //用于判断界面右边5个按钮内显示方式

    var fiveListener: onFiveClickListener? = null

    fun setOnFiveClickListener(fiveListener: onFiveClickListener) {
        this.fiveListener = fiveListener
    }

    interface onFiveClickListener {
        fun FiveBtnClick(view: View)
    }


}