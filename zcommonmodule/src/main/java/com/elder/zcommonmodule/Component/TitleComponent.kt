package com.elder.zcommonmodule.Component

import android.databinding.ObservableField
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import com.elder.zcommonmodule.R
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.getString
import java.io.Serializable


open class TitleComponent : Serializable {

    var title = ObservableField<String>()
    var arrowVisible = ObservableField<Boolean>(true)
    var rightText = ObservableField<String>(getString(R.string.finish))
    var rightVisibleType = ObservableField<Boolean>(true)
    var arrowBackVisible = ObservableField<Boolean>(true)
    var titleTextVisible = ObservableField<Boolean>(true)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var rightIcon = ObservableField<Drawable>(context.getDrawable(R.drawable.add_photo))
    var rightTextColor = ObservableField<Int>(getColor(R.color.blackTextColor))
    var leftIcon = ObservableField<Drawable>(context.getDrawable(R.drawable.arrow_black))
    var backgroundColor = ObservableField<Drawable>(context.getDrawable(R.color.white))
    fun onClick(view: View) {
        when (view.id) {
            R.id.title_arrow_btn -> {
                if (callback != null) {
                    callback?.onComponentClick(view)
                }
            }
            R.id.title_finish_btn -> {
                if (callback != null) {
                    callback?.onComponentFinish(view)
                }
            }
            R.id.title_img_right -> {
                if (callback != null) {
                    callback?.onComponentFinish(view)
                }
            }
        }
    }


    fun intTabLayout() {

    }


    var callback: titleComponentCallBack? = null

    fun setCallBack(callBack: titleComponentCallBack) {
        this.callback = callBack
    }

    interface titleComponentCallBack {
        fun onComponentClick(view: View)
        fun onComponentFinish(view: View)
    }
}