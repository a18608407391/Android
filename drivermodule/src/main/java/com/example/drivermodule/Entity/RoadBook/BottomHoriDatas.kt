package com.example.drivermodule.Entity.RoadBook

import android.databinding.ObservableField
import java.io.Serializable
import java.util.*


class BottomHoriDatas : Serializable {

    var topTv = ObservableField<String>("")
    var isCheck = ObservableField<Boolean>(false)
    var describe = ObservableField<String>()
    var roadid = ""
    var frondtype = ObservableField<Boolean>(false)
    var endtype = ObservableField<Boolean>(false)
    var imgUrl = ObservableField<String>()
    var lat = 0.0
    var lon = 0.0
    var number = ObservableField<String>()
}