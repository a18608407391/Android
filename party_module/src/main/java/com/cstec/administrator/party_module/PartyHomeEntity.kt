package com.cstec.administrator.party_module

import android.arch.lifecycle.LiveData
import android.databinding.ObservableArrayList
import java.io.Serializable


class PartyHomeEntity : Serializable {
    var motoActivityList: ArrayList<MBRecommend>? = null
    var topActivityList: ArrayList<HotRecommend>? = null
    var selectedActivityList: ArrayList<WonderfulActive>? = null
    var clockActivityList: ArrayList<ClockActive>? = null
    var topCityList: ArrayList<TopCity>? = null


    class TopCity {
        var SUBORDINATE_CITY: String? = null
        var N = 0
    }


    class HotRecommend : Serializable {
        var ID = 0
        var CODE = 0
        var TITLE: String? = null
        var STATE = 0
        var DAILY_VISITS = 0
        var TOTAL_VISITS = 0
        var TYPE: String? = null
        var CREATE_DATA: String? = null
        var RECOMMEND_ORDER = 0
        var FILE_NAME_URL: String? = null
        var ACTIVITY_START: String? = null
        var ACTIVITY_STOP: String? = null
        var ACTIVITY_END: String? = null
        var TICKET_PRICE: String?  = null
        var DACT_X_AXIS: Double = 0.0
        var DACT_Y_AXIS: Double = 0.0
        var SQRTVALUE: Long = 0
    }

    class ClockActive : Serializable {
        var ID = 0
        var CODE = 0
        var TITLE: String? = null
        var STATE = 0
        var TYPE: String? = null
        var BIG_TYPE = 0
        var ACTIVITY_START: String? = null
        var ACTIVITY_STOP: String? = null
        var FILE_NAME_URL: String? = null
        var DISTANCE: String? = null
        var DACT_X_AXIS: Double = 0.0
        var DACT_Y_AXIS: Double = 0.0
        var SUBORDINATE_CITY: String? = null
        var SQRTVALUE: Long = 0
        var MAN_COUNT: String? = ""
    }

    class HotDistination {

    }

    class MBRecommend {
//        "ID": 651, //活动ID
//        "CODE": "89",
//        "TITLE": "2019哈尔滨滚雷骑行节", //活动标题
//        "STATE": "2", //未失效
//        "CREATE_DATA": "2019-12-05 17:56:48", //创建时间
//        "RECOMMEND_ORDER": "2", //权重
//        "RIDING_OFFICER_MEMBER_ID": "111", //骑行官会员id
//        "NAME": "测试wedded", //骑行官会员昵称
//        "HEAD_IMG_FILE": "/Activity/userHeaderPicoriginalImg/2020Y/02M/19D/11120200219154532012.jpg", //骑行官会员头像
//        "FILE_NAME_URL": "/home/uploadFile/images/createActivity/2019/09/27/5/fileNameUrl0f915224a9954ad5aa2349832462cc69.jpg", //活动图片
//        "ACTIVITY_START": "2020-03-04 00:00:00", //活动开始时间
//        "ACTIVITY_STOP": "2020-03-06 00:00:00", //活动结束时间
//        "DAY": 2, //活动共几天
//        "DISTANCE": null, //行程距离
//        "PATH_POINT": "机场（哈尔滨太平国际机场）——世界欢乐城——大巡游——龙鸿山庄", //途经点
//        "SUBORDINATE_CITY": "广东省-广州市", //活动所属城市
//        "TICKET_PRICE": 0.01, //票价
//        "Y_AXIS": "44.79912", //集合点经度
//        "X_AXIS": "125.502976", ////集合点纬度
//        "SQRTVALUE": 3831 //距离我多远

        var ID = 0
        var CODE = 0
        var TITLE: String? = null
        var STATE = 0
        var CREATE_DATA: String? = null
        var RECOMMEND_ORDER = 0
        var RIDING_OFFICER_MEMBER_ID = 0
        var NAME: String? = null
        var HEAD_IMG_FILE: String? = null
        var FILE_NAME_URL: String? = null
        var ACTIVITY_START: String? = null
        var ACTIVITY_STOP: String? = null
        var DAY = 0
        var DISTANCE: String? = null
        var PATH_POINT: String? = null
        var SUBORDINATE_CITY: String? = null
        var TICKET_PRICE: Double = 0.0
        var Y_AXIS: Double = 0.0
        var X_AXIS: Double = 0.0
        var SQRTVALUE: Long = 0
    }

    class WonderfulActive {
        var ID = 0
        var CODE = 0
        var TITLE: String? = null
        var STATE = 0
        var CREATE_DATA: String? = null
        var BIG_TYPE = 0
        var RECOMMEND_ORDER = 0
        var FILE_NAME_URL: String? = null
        var ACTIVITY_START: String? = null
        var ACTIVITY_STOP: String? = null
        var ACTIVITY_END: String? = null
        var DAY = 0
        var PATH_POINT: String? = null
        var SUBORDINATE_CITY: String? = null
        var TICKET_PRICE:String ? = null
        var DACT_Y_AXIS: Double = 0.0
        var DACT_X_AXIS: Double = 0.0
        var SQRTVALUE: Long = 0
    }

}