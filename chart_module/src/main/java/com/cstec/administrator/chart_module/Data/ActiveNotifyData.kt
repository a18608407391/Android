package com.cstec.administrator.chart_module.Data

import java.io.Serializable


class ActiveNotifyData : Serializable {

    var createTime: String? = null
    var createUser: String? = null
    var id: String? = null
    var memberId = 0
    var msgContent: String? = null
    var msgDetailUrl: String? = null
    var msgImg: String? = null
    var msgTargetId = 0
    var msgTargetIdType = 0
    var msgType = 0
    var msg_title: String? = null
    var status = 0
    var time: String? = null
}