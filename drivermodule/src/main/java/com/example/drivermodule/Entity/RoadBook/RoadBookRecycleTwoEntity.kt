package com.example.drivermodule.Entity.RoadBook

import android.databinding.ObservableField
import java.io.Serializable


class RoadBookRecycleTwoEntity : Serializable {

    var type = ObservableField(0)

    var title =  ObservableField<String>()

    var routeId = ObservableField<Int>()

    var distanceTv = ObservableField<String>()

    var timeTv  =ObservableField<String>()

    var imgUrl = ObservableField<String>()

    var descripte = ObservableField<String>()
}