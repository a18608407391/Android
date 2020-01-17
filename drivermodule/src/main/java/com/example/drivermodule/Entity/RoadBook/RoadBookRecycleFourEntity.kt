package com.example.drivermodule.Entity.RoadBook

import android.databinding.ObservableField
import java.io.Serializable


class RoadBookRecycleFourEntity : Serializable {

    var type = ObservableField(0)

    var title = ObservableField<String>()

}