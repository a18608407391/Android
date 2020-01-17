package com.cstec.administrator.social.Entity

import android.databinding.ObservableField
import org.cs.tec.library.binding.command.ViewAdapter.Banner.MultiItemEntity
import java.io.Serializable


data class PhotoEntitiy(
        var path: ObservableField<String>,
        var createTime: ObservableField<String>,
        var type: ObservableField<Int> = ObservableField(0),
        var ids : ObservableField<String> =  ObservableField("a")
) : MultiItemEntity, Serializable {
    override fun getItemType(): Int {
        return type.get()!!
    }

    var isCheced = ObservableField<Boolean>(false)

    constructor(bean: PhotoBean.Photo) : this(ObservableField(bean.imgUrl!!), ObservableField(bean.createTime!!))
}