package com.zk.library.Bus.event

class RxBusEven {

    companion object {
        var LocationReceive = 0x9999
        var WxLoginReLogin = 0x9998
        var DriverReturnRequest = 0x1001
        var PartyWebViewReturn = 0x1002
        var DriverMapPointRegeocodeSearched = 0x1003
        var MapCameraChangeFinish = 0x1004
        //组队相关
        var TeamSocketConnectSuccess = 0x1005
        var TeamSocketDisConnect = 0x1006
        var MinaDataReceive = 0x1007
        var Team_reject_even = 0x1008
        var BrowserSendTeamCode = 0x1011
        //骑行导航相关
        var DriverCancleByNavigation = 0x1009
        var DriverNavigationRouteChange = 0x1010
        val NAVIGATION_FINISH = 0x1012
        val DriverNavigationChange = 0x1011
        val SHARE_SUCCESS = 0x1013
        val SHARE_CANCLE = 0x1014

        //跳转类型
        var ENTER_TO_SEARCH = 0x6000
        var ENTER_TO_ROAD_HOME = 0x6001
        var CHAT_ROOM_ACTIVITY_RETURN = 0x6002
        var ACTIVE_WEB_GO_TO_APP = 0x6003
        val RELOGIN = 0x6004
        val RELOGIN_SUCCESS = 0x6005
        var NAVIGATION_DATA = 0x6006
        var NET_WORK_ERROR = 0x6007
        var NET_WORK_SUCCESS = 0x6008

        fun getInstance(type: Int): RxBusEven {
            var even = RxBusEven()
            even.type = type
            return even
        }

        fun getInstance(type: Int, value: Any): RxBusEven {
            var even = RxBusEven()
            even.type = type
            even.value = value
            return even
        }

        fun getInstance(type: Int, value: Any, value2: Any): RxBusEven {
            var even = RxBusEven()
            even.type = type
            even.value = value
            even.value2 = value2
            return even
        }


    }


    var type = 0

    var value: Any? = null

    var value2: Any? = null

    var secondValue: Any? = null

    var parameter: HashMap<Any, Any>? = null


}