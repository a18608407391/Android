package com.example.drivermodule.Entity.RoadBook

import android.databinding.ObservableField

class RoadBookListEntity {
    var type = ObservableField(1)

    var title =  ObservableField<String>()

    var city = ObservableField<String>()

    var visible = ObservableField<Boolean>(false)
}
