package com.elder.zcommonmodule.Entity.SoketBody

import com.amap.api.services.core.LatLonPoint
import com.elder.zcommonmodule.Entity.Location
import java.io.Serializable


class SoketNavigation : Serializable {

    var wayPoint: ArrayList<LatLonPoint>? = null

    var navigation_end: Location? = null


    var type: String? = null
}