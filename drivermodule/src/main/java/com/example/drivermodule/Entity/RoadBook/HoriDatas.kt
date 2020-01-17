package com.example.drivermodule.Entity.RoadBook

import android.databinding.ObservableField
import java.io.Serializable
import java.util.*


class HoriDatas : Serializable {

    var title = ObservableField<String>("")
    var isCheck = ObservableField<Boolean>(false)
}