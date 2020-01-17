package com.elder.zcommonmodule

import android.databinding.ObservableField
import org.cs.tec.library.binding.command.ViewAdapter.Banner.MultiItemEntity
import java.io.Serializable


class PictureInfo(var name: ObservableField<String>, var time: ObservableField<Long>, var path: ObservableField<String>, var size: ObservableField<Int>) : Serializable {
    var isCheced = ObservableField<Boolean>(false)
}