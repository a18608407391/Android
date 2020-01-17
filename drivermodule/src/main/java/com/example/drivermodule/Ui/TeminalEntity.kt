package com.example.drivermodule.Ui

import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import java.io.Serializable


class TeminalEntity : Serializable {


    var msg: String? = null
    var code: Int? = 0
    var data: TeminalResult? = null

    class TeminalResult : Serializable {
        //高德地图key
        var mapKey: String? = null
        //服务id
        var serverId: Long = 0
        //服务名称
        var serverName: String? = null
        //服务描述
        var serverDesc: String? = null
        //创建时间
        var createTime: String? = null
        //创建人
        var createUser: String? = null

        //终端id
        var terminalId: Long = 0
        //终端名称
        var terminalName: String? = null
        //终端描述
        var terminalDesc: String? = null
    }


}