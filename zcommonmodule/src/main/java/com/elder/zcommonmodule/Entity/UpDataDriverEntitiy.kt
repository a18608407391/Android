package com.elder.zcommonmodule.Entity

import com.amap.api.maps.model.LatLng
import java.io.Serializable


class UpDataDriverEntitiy : Serializable {
    //里程
    var mileage: Double? = null

    //时间戳
    var dateTimer: String? = null

    //配速
    var minkm: String? = null

    //累计用时
    var totalTime: Long = 0

    //爬坡距离
    var climbing: String ? = null

    //极速
    var topspeed: Float = 0F

    //压弯
    var bendAngle = 0

    //百米时速
    var metre100Sprint = 0
    //天气预报
    var weatherArray: ArrayList<Weather>? = null
    //海拔
    var elevationArray: ArrayList<Double>? = null

    var locationArray: ArrayList<Location>? = null

    var highestPoint = 0.0

    class Weather : Serializable {

    }
}