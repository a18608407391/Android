package com.elder.zcommonmodule.Widget.RoadBook

import android.databinding.ObservableField
import java.io.Serializable
import java.util.*


class HoriDatas : Serializable {

    var title = ObservableField<String>("")
    var isCheck = ObservableField<Boolean>(false)
}