package com.example.drivermodule.Entity

import com.amap.api.services.core.LatLonPoint
import java.io.Serializable


class PointEntity : Serializable {

    constructor(address: String, point: LatLonPoint) {
        this.address = address
        this.point = point
    }

    var address: String? = null
    var point: LatLonPoint? = null


}