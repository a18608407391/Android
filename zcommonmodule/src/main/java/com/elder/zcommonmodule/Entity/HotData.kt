package com.elder.zcommonmodule.Entity

import android.databinding.ObservableField
import java.io.Serializable


class HotData : Serializable {
    //    "id":25,
//    "title":"dssdsd",
//    "bill":"D:/home/uploadFile/images/createActivity/2019/08/13/18/fileNameUrl9d6445602f624bb5889b6241fb77799a.jpg",
//    "feature":"亲子",
//    "startaddr":"湖南省,长沙市,未来漫城",
//    "routepoint":"fdfdfdd-gfgfgf-gfgfgf",
//    "strengthgrade":"2",
//    "scenerygrad":"4",
//    "experience":"4",
//    "season":"夏,秋",
//    "status":2,
//    "lookcount":0,
//    "vehicletype":"旅行,山地",
//    "routepointcount":1,
//    "introduction":"<p>dsdsdsds</p>",
//    "createTime":1565682635000,
//    "createUser":"system",
//    "guideType":"1",
//    "lng":"113.086818",
//    "lat":"28.260114"


    var id: Int = 0
    var title: String? = null
    var bill: String? = null
    var feature: String? = null
    var ridingtime: Int? = 0
    var startaddr: String? = null
    var routepoint: String? = null
    var strengthgrade: Int = 0
    var scenerygrad: Int = 0
    var experience: Int = 0
    var season: String? = null
    var status: Int? = 0
    var lookcount: Int = 0
    var vehicletype: String? = null
    var routepointcount: Int? = 0
    var createTime: Long = 0L
    var simpIntroduction: String? = null
    var createUser: String? = null
    var guideType = 0
    var distance = 0F
    var aboutdis: Double = 0.0
    var distanceTv: String? = null
    var lng: Double = 0.0
    var lat: Double = 0.0

    var height = 0
    var width = 0
//    var id: ObservableField<Int> = ObservableField(0)
//    var title: ObservableField<String> = ObservableField()
//
//    var bill: ObservableField<String> = ObservableField()
//    var feature: ObservableField<String> = ObservableField()
//    var ridingtime: ObservableField<Int> = ObservableField(0)
//
//    var startaddr: ObservableField<String> = ObservableField()
//    var routepoint: ObservableField<String> = ObservableField()
//
//    var strengthgrade: ObservableField<Int> = ObservableField(0)
//    var scenerygrad: ObservableField<Int> = ObservableField(0)
//    var experience: ObservableField<Int> = ObservableField(0)
//
//    var season: ObservableField<String> = ObservableField()
//    var status: ObservableField<Int> = ObservableField(0)
//    var lookcount: ObservableField<Int> = ObservableField(0)
//    var vehicletype: ObservableField<String> = ObservableField()
//    var routepointcount: ObservableField<Int> = ObservableField(0)
//    var introduction: ObservableField<String> = ObservableField()
//    var createTime: ObservableField<Long> = ObservableField(0)
//    var createUser: ObservableField<String> = ObservableField()
//    var guideType: ObservableField<Int> = ObservableField(0)
//
//    var lng: Double = 0.0
//    var lat: Double = 0.0


}