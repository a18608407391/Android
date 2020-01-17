package com.elder.zcommonmodule.Entity

import com.amap.api.maps.model.LatLng
import java.io.Serializable


class StartRidingRequest : Serializable {

    var id: Long = 0

    var startPosition: LatLng? = null

    var passPosition: ArrayList<LatLng>? = null

    var endPosition: LatLng? = null

    var createTime: String? = null

    var createUser: String? = null

    var memberId: String? = null

    var ridingFileUrl: String? = null

    var baseUrl: String? = null

    var updateTime: String? = null

    var totalDistance: String? = null

    var totalTime: String? = null

    var averageSpeed: String? = null

    var trackImgUrl: String? = null

}