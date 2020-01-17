package com.elder.zcommonmodule.Entity.SoketBody

import android.os.Environment
import android.util.Log
import com.elder.zcommonmodule.Point_Save_Path
import com.elder.zcommonmodule.Status_Save_Path
import com.elder.zcommonmodule.Team_Status_Save_PATH
import com.elder.zcommonmodule.Utils.FileSystem
import com.google.gson.Gson
import java.io.File
import java.io.Serializable


class SoketTeamStatus : Serializable {

    //组队相关
    var uid  =0
    var startTeam: Boolean = false   //是否组队
    var teamCreate: CreateTeamInfoDto? = null
    var teamJoin: TeamPersonnelInfoDto? = null
    var teamStatus = 0

    fun resetTeam() {
        teamCreate = null
        teamJoin = null
        teamStatus = 0
     var flag =    File(Environment.getExternalStorageDirectory().getPath() + "/Amoski" + Team_Status_Save_PATH).delete()
        if(flag){
            Log.e("result","删除成功")
        }
    }

    fun save() {
        FileSystem.writeSingString(Team_Status_Save_PATH, Gson().toJson(this))
    }
}