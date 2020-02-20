package com.elder.zcommonmodule.Entity

import java.io.Serializable


class SystemNotifyData : Serializable {
    var createtime: String = ""
    var createUser: String? = null
    var id = 0
    var memberId = 0
    var msgContent: String? = null
    var msgDetailUrl: String? = null
    var msgImg: String? = null
    var msgTargetIdType = 0
    var msgType = 0
    var msg_title: String? = null
    var status = 0
    var time: String? = null

    var msgTargetId  =  0
}