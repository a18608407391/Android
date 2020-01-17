package com.elder.zcommonmodule

import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import org.cs.tec.library.Base.Utils.context
import java.io.Serializable


class OningData : Serializable {

    var visibleType = ObservableField<Boolean>(false)

    var bgIcon = ObservableField<Int>(0)

    var title = ObservableField<String>()

    var count = ObservableField<String>()

    var unit = ObservableField<String>("人")

    var addTitle = ObservableField<String>()

    var type = 0

}