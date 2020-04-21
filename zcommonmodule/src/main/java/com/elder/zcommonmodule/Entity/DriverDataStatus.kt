package com.elder.zcommonmodule.Entity

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.elder.zcommonmodule.DataBases.deleteDriverStatus
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonnelInfoDto
import com.elder.zcommonmodule.Point_Save_Path
import com.elder.zcommonmodule.Status_Save_Path
import com.elder.zcommonmodule.Utils.FileSystem
import com.google.gson.Gson
import com.zk.library.Utils.PreferenceUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.USERID
import java.io.File
import java.io.Serializable
import java.util.HashMap


class DriverDataStatus() : Serializable {


    var uid = ""
    //骑行相关
    var DriverOpen: Int = 0     //默认0 开启  1为未开启
    var curModel: Int = 0    //当前的界面状态   0为骑行 1地图选点 2 导航
    var startDriver = ObservableField<Int>(DriverCancle)//当前的骑行状态  0为骑行中 1为暂停  2为cancle


    var navigationType: Int = 0 //0 没导航  1 正在导航   2无骑行导航
    var second: Long = 0
    var distance: Double = 0.0
    var driverPointPath: String = Environment.getExternalStorageDirectory().getPath() + "/Amoski" + Point_Save_Path //本地骑行路径点集合
    var driverStartPoint: Location? = null  //骑行起始点
    var startAoiName: String? = null


    var NavigationStartAoiName: String? = null
    var NavigationEndAoiName: String? = null


    var passPointDatas = ArrayList<Location>()


    var navigationStartPoint: Location? = null //导航起点
    var navigationEndPoint: Location? = null //导航终点


    var endPoint: Location? = null         //导航终点
    var StartTime: Long = 0          //已骑行时间
    var CurentDistance: Double? = 0.0    //已骑行距离
    var driverNetRecord: StartRidingRequest? = null
    //DriverController参数
    var startPoint: Location? = null
    var totalPoint = ArrayList<Location>()
    var locationLat = ObservableArrayList<Location>()
    var onDestroyStatus = 0          //0表示未销毁   1表示骑行中正常销毁  2表示骑行中未进行销毁


    var navigationDistance = 0F
    var navigationTime = 0L
//
//    fun saveAsLocal(): Boolean {
//        if (locationLat.size == 0) {
//            return false
//        }
//        try {
//            return FileSystem.writeSingString(Status_Save_Path, Gson().toJson(this))
//        } catch (ex: ConcurrentModificationException) {
//            ex.printStackTrace()
//            return false
//        }
//    }

//    fun deleteFile(): Boolean {
//        Log.e("result", "删除本地文件")
//        File(Environment.getExternalStorageDirectory().getPath() + "/Amoski" + Point_Save_Path).delete()
//        return File(Environment.getExternalStorageDirectory().getPath() + "/Amoski" + Status_Save_Path).delete()
//    }


    fun reset() {
        DriverOpen = 0     //默认0 开启  1为未开启
        curModel = 0    //当前的界面状态   0为骑行 1地图选点 2 导航
        startDriver = ObservableField<Int>(DriverCancle)//当前的骑行状态  0为骑行中 1为暂停  2为cancle
        navigationType = 0 //0 没导航  1 正在导航   2无骑行导航
        second = 0
        distance = 0.0
        driverPointPath = Environment.getExternalStorageDirectory().getPath() + "/Amoski" + Point_Save_Path //本地骑行路径点集合
        driverStartPoint = null  //骑行起始点
        startAoiName = null
        NavigationStartAoiName = null
        NavigationEndAoiName = null
        passPointDatas = ArrayList<Location>()
        navigationStartPoint = null //导航起点
        navigationEndPoint = null
        endPoint = null         //导航终点
        StartTime = 0          //已骑行时间
        CurentDistance = 0.0    //已骑行距离
        driverNetRecord = null
        //DriverController参数

        startPoint = null
        totalPoint = ArrayList()
        locationLat = ObservableArrayList()
        onDestroyStatus = 0
        //newData
    }


    var UpValue = 0.0   //累计爬坡高度
    var maxHeight = 0.0  //最高海拔
    var maxSpeed: Float = 0F //最高速度
    var UpCount = 0        //爬坡次数
}