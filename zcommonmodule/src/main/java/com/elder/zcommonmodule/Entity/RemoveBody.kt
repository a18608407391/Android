package com.elder.zcommonmodule.Entity

import java.io.Serializable


class RemoveBody : Serializable {

//    06-29 18:11:53.925 25529-26349/com.elder.amoski E/result: 移除消息{"latitude":"0.0","longitude":"0.0","userIds":"79,"}


    var latitude = 0.0
    var longitude = 0.0
    var userIds: String? = null
}