package com.example.private_module.Entitiy

import com.elder.zcommonmodule.Entity.ActiveData
import com.elder.zcommonmodule.Entity.DriverInfo
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Entity.ProgressData
import java.io.Serializable


class PrivateEntity : Serializable {

    var queryUserDisCountRidingInfo: RidingInfo? = null

    var PersonalCenterDatil: PrivateCountData? = null


    class RidingInfo {
        var ridingData: ArrayList<ProgressData>? = null
        var activityData: ArrayList<ActiveData>? = null
        var ridingGuide: ArrayList<HotData>? = null
    }

    class PrivateCountData {
        var DynamicCount = 0
        var FansCount = 0
        var FollowCount = 0
        var FabulousCount = 0

    }


}