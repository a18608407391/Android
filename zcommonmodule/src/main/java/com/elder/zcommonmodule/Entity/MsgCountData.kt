package com.elder.zcommonmodule.Entity

import java.io.Serializable


class MsgCountData : Serializable {

//{"lastSystem":{"createTime":"2020-02-13 13:31:18","createUser"
//    :"system","id":2595,"memberId":80,"msgContent":"厉害了！你本周的骑行距离荣登骑行距离周榜【NO.7】快去看看吧吧"
//    ,"msgDetailUrl":"","msgImg":"","msgTargetIdType":2,"msgType":"1","msg_title":"骑行周报提醒","status":"2"
//    ,"time":"3天前"},"lastActivityCount":0,"callMeCount":0,"lastSystemCount":1,"fabulousCount":16,"commentCount":5}


    var lastSystem: SystemNotifyData? = null

    var lastActivityCount = 0

    var callMeCount = 0

    var lastSystemCount = 0

    var fabulousCount = 0

    var commentCount = 0
}