package com.elder.zcommonmodule.Entity.HttpResponseEntitiy

import java.io.Serializable


open class BaseResponse : Serializable {

    var code: Int = 0

    var msg: String? = null

    var data: Any? = null
}