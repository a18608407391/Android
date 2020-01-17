package com.example.drivermodule.Entity

import android.databinding.ObservableField
import java.io.Serializable


class RouteEntity : Serializable {

    var title = ObservableField<String>()

    var time = ObservableField<String>()

    var distance = ObservableField<String>()

    var money = ObservableField<String>()

    var select  = ObservableField<Boolean>(false)


}