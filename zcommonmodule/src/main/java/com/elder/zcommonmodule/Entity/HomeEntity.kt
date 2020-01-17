package com.elder.zcommonmodule.Entity

import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import java.io.Serializable


class HomeEntity : Serializable {

//    {"msg":"成功","code":"0","data":{"masterCount":29,"masterDis":185319.86355550948,"selfDis":40711.76064220071,"selfCount":340,"selfAllDis":78339}}

    var masterCount: Int? = 0

    var masterDis: Double? = 0.0

    var selfDis: Double? = 0.0

    var selfCount = 0

    var selfAllDis: Double? = 0.0

    var ridingTeam: CreateTeamInfoDto? = null

    var ridingGuide: ArrayList<HotData>? = null

    var activityData: ArrayList<ActiveData>? = null

    var ridingData :ArrayList<ProgressData> ? =null

}