package com.elder.zcommonmodule.Entity

import java.io.Serializable


class ActiveData : Serializable {

    var page = 0
    var limit = 0
    var pageSize = 0
    var start = 0
    var id = 0
    var code = 0
    var orgCode = 0
    var totalVisits = 0
    var title: String? = null
    var nature = 0
    var state = 0
    var describe: String? = null
    var fileNameUrl: String? = null
    var mandatoryField: String? = null
    var scopeRegistration: String? = null
    var collectionPlace: String? = null
    var collectionTime: String? = null
    var activityStart: String = ""
    var activityStop: String? = null
    var activityEnd: String = ""
    var numberLimitation: Int = 0
    var showNumber: String? = null
    var playType: String? = null
    var destination: String? = null
    var pathPoint: String? = null
    var activityIntensity = 0
    var equipmentRequirements = 0
    var detailsActivities: String? = null
    var tbActivityTimeHistoryEntity: ArrayList<String>? = null
    var tbActivityRouteEntity: ArrayList<String>? = null
    var end = " 至 "
    var space = "  "
}