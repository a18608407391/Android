package com.elder.zcommonmodule

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
import android.provider.Settings
import com.amap.api.maps.model.LatLng
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.services.core.LatLonPoint
import com.elder.zcommonmodule.DataBases.queryDriverStatus
import com.zk.library.Utils.PreferenceUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.USERID
//const val Base_URL = "http://ly532915135.vicp.cc/"
//const val Base_URL = "http://yomoy.com.cn/"
//const val Base_URL = "http://192.168.5.242/"
const val Base_URL = "http://192.168.5.253/"
//const val Base_URL = "http://192.168.5.155/"
//const val Base_URL = "http://192.168.5.178/"
//
const val USER_PHONE = "user_phone"
const val USER_PASS = "user_pass"
const val USER_TOKEN = "user_token"
const val RE_LOGIN = "re_login"
const val TEMINAL_ID = "teminalId"
const val TEMINAL_NAME = "teminalName"
const val SERVICE_ID = "serviceId"
const val REGISTER = "register"
const val DB_NAME = "amoski_database"
const val USER_TABLE = "user_db_table"
const val DRIVER_INFO_TABLE = "user_driver_table"
const val DRIVER_STATUS_TABLE = "driver_status_table"
const val LOCATION_TABLE = "location_table"
const val TEAM_TABLE = "team_table"
const val REAL_NAME = "real_name"
const val REAL_CODE = "real_code"
const val HISTORY_TABLE = "history_table"
const val TEAM_CODE = "team_code"
const val TEAM_ID = "team_id"

const val MSG_RETURN_REQUEST = 125
const val MSG_RETURN_REFRESH_REQUEST = 12
var ROOT_PATH = Environment.getExternalStorageDirectory().absolutePath
var GRADE_IMG = ROOT_PATH + File.separator + "Amoski/camera" + File.separator
const val REAL_PATH = "real_path"
const val REQUEST_CODE_CAMERA = 101
const val REQUEST_IMG_WARTER_MARK = 102
const val PICK_IMAGE_ACTIVITY_REQUEST_CODE = 103

const val CHECK_PERMISSION = 104
const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 105
const val GET_NICKNAME = 106
const val GET_USERINFO = 108
const val REQUEST_CODE_CROP_IMAGE = 107
const val INSERT_CARS = 108
const val RESULT_STR = 109
const val RESULT_POINT = 110
const val ENTER_TO_SELECTE = 111
const val REQUEST_BOND_CAR = 112
const val REQUEST_CERTIFICATION_CODE = 113
const val EDIT_CARS = 109
const val CALL_BACK_STATUS = 110
const val EDIT_FINAL_POINT = 114
const val FINISH = 115
const val REQUEST_SHARE = 116
const val START_DRIVER = 117
const val TO_NAVIGATION = 118
//普通状态

const val SOCIAL_DETAIL_RETURN = 130

const val PRIVATE_DATA_RETURN = 131
const val CURRENT_WEATHER = "local_weather"
const val CURRENT_AOI = "local_aoi"

const val SHARE_PICTURE = 121

const val REQUEST_CREATE_JOIN = 120

const val REQUEST_LOAD_ROADBOOK = 121

const val REQUEST_MY_ROADBOOK = 123

const val REQUEST_SEARCH_ROADBOOK = 122

const val SOCIAL_SELECT_PHOTOS = 123

const val SELECT_USER_CALLBACK = 124
const val DriverCancle = 2

//骑行中
const val Drivering = 0

//查看线路
const val DriverPause = 1

const val TeamModel = 4


//选点成功

const val Prepare_Navigation = 3

const val NotNavigation = 0
const val Driver_Navigation = 1
const val Nomal_Navigation = 2
const val Status_Save_Path = "/Status.txt"
const val Team_Status_Save_PATH = "/TeamStatus.txt"
const val Point_Save_Path = "/DriverLocation.txt"
const val ROUTE_TIME = "route_time"
const val ROUTE_DISTANCE = "route_distance"

fun converLatPoint(t: LatLng): LatLonPoint {
    return LatLonPoint(t.latitude, t.longitude)
}

fun converNaviLatLngPoint(t: LatLonPoint): NaviLatLng {
    return NaviLatLng(t.latitude, t.longitude)
}


fun converViewToBitmap() {


}

private val REQUEST_IGNORE_BATTERY_CODE = 1001
fun gotoSettingIgnoringBatteryOptimizations(context: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            val intent = Intent()
            val packageName = context.packageName
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivityForResult(intent, REQUEST_IGNORE_BATTERY_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun checkDriverStatus(): Boolean {
    var uid = PreferenceUtils.getString(context, USERID)
    return if (uid == null) {
        false
    } else {
        var list = queryDriverStatus(uid)
        if (list.size == 0) {
            false
        } else {
            var status = list.get(0)
            status.startDriver.get() != DriverCancle
        }
    }
}
