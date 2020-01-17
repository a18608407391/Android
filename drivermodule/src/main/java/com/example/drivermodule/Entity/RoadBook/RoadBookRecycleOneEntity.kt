package com.example.drivermodule.Entity.RoadBook

import android.databinding.ObservableField
import java.io.Serializable


class RoadBookRecycleOneEntity :Serializable{


    var title = ObservableField<String>()

    var address = ObservableField<String>()

    var addressContent = ObservableField<String>()

    var driverTime =  ObservableField<String>()

}