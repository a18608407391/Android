package com.example.drivermodule.Entity.RoadBook

import java.io.Serializable


class RoadDetailEntity : Serializable {
//    "id":46,
//    "startaddr":"天津市",
//    "endaddr":"太原市",
//    "day":1,
//    "allRoutepoint":"岳麓大道57号平和堂百货3层-普瑞大道与金甲冲路交叉口南200米-王台街道黄兴中路78号B1层商业空间SD4-5号",
//    "aboutdis":"525580",
//    "ridingtime":"25167",
//    "createTime":1565754673000,
//    "createUser":"system",
//    "guideId":31,
//    "startlng":"117.201538",
//    "startlat":"39.085294",
//    "endlng":"112.549717",
//    "endlat":"37.87046",
//    "pointList"
//    "pId":137,
//    "routeName":"岳麓大道57号平和堂百货3层",
//    "lng":"112.954592",
//    "lat":"28.22367",
//    "pid":137,
//    "paboutdis":"10177",
//    "pridingtime":"1478"

    var id: Int = 0
    var startaddr: String? = null
    var endaddr: String? = null
    var day = 0
//    var introduction: String? = null
    var simpIntroduction:String ? = null
    var allRoutepoint: String? = null
    var aboutdis: Long = 0
    var ridingtime: Long = 0
    var createTime: Long = 0
    var guideId = 0
    var startlng: Double = 0.0
    var startlat: Double = 0.0
    var endlng: Double = 0.0
    var endlat: Double = 0.0
    var vehicletype: String? = null
    var pId = 0
    var pointList: ArrayList<RoadDetailItem>? = null
    var routeName: String? = null
    var lng: Double = 0.0
    var lat: Double = 0.0
    var paboutdis: Long = 0
    var pridingtime: Long = 0
    var type = 0


    class RoadDetailItem : Serializable {
        var id = 0
        var routeName: String? = null
        var remake: String? = null
        var address:String ? = null
        var ridingtime: Long = 0
        var aboutdis: Long = 0
        var routeId = 0
        var lng: Double = 0.0
        var lat: Double = 0.0
        var routeIcon = 0
        var imgUrl: String = ""
    }

}