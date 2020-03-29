package com.elder.zcommonmodule.Entity

import java.io.Serializable


class CollectionEntity : Serializable {


    var data: ArrayList<Collection>? = null


    class Collection : Serializable {
        var CREATE_DATE: String? = null
        var createDate: String? = null
        var DYNAMIC_ID: String? = null
        var publishContent: String? = null
        var ID: String? = null
        var isRead: String? = null
        var MEMBER_ID: String? = null
        var memberImages: String? = null
        var MEMBER_NAME: String? = null
        var memberName: String? = null
        var releaseDynamicParent: DynamicsCategoryEntity.Dynamics? = null
        var TARGET_ID: String? = null
        var TARGET_ID_TYPE: String? = null
        var PUBLISH_CONTENT: String? = null
        var FILE_PATH: String? = null
        var TITLE: String? = null
        var CODE = 0
        var BIG_TYPE: Int = 0
        var STATE = 0
        var FILE_NAME_URL: String? = null
        var COLLECTION_PLACE: String? = null
        var COLLECTION_TIME: String? = null
        var ACTIVITY_START: String? = null
        var ACTIVITY_STOP: String? = null
        var ACTIVITY_END: String? = null
        var DAY = 0
        var DESTINATION: String? = null
        var PATH_POINT: String? = null
        var DACT_X_AXIS: Double = 0.0
        var DACT_Y_AXIS: Double = 0.0
        var SUBORDINATE_CITY: String? = null
        var TICKET_PRICE: String? = null
        var X_AXIS: Double = 0.0
        var Y_AXIS: Double = 0.0
        var DISTANCE: String? = null
        var MAN_COUNT  = 0
        var SQRTVALUE = 0
        var SQRTVALUE1 = 0
        var RN = 0
        var TIME: String? = null
    }


}