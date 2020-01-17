package com.elder.zcommonmodule.Entity

import android.os.Parcel
import android.os.Parcelable
import com.amap.api.maps.model.LatLng
import java.io.Serializable


data class Location(
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var time: String = "",
        var speed: Float = 0F,
        var height: Double = 0.0,
        var bearing: Float = 0F,
        var aoiName: String = "",
        var poiName: String = "") : Serializable {

}