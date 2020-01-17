package com.example.drivermodule.Component

import com.amap.api.maps.model.Marker
import com.amap.api.services.core.LatLonPoint
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.ViewModel.DriverViewModel
import com.example.drivermodule.ViewModel.MapPointViewModel


class MapPointController {
    var screenMaker: Marker? = null
    var finallyMarker: Marker? = null
//    var startPoint: LatLonPoint? = null
//    var endPoint: LatLonPoint? = null
    var startMaker: Marker? = null
    var pointList = ArrayList<PointEntity>()
    var CurState = 0  //列表状态，收缩状态为0  展示状态为1
    var SingleList = ArrayList<PointEntity>().apply {
        this.add(PointEntity("", LatLonPoint(0.0, 0.0)))
    }

    constructor(model: MapPointViewModel)

    fun reset() {
        if (screenMaker != null) {
            screenMaker?.remove()
        }
        screenMaker = null
        if (finallyMarker != null) {
            finallyMarker?.remove()
        }

        finallyMarker = null
//        startPoint = null
        pointList.clear()
        if (startMaker != null) {
            startMaker?.remove()
        }
        startMaker = null
    }

}