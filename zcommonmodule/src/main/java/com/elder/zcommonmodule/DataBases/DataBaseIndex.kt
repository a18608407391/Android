package com.elder.zcommonmodule.DataBases

import android.databinding.ObservableArrayList
import com.amap.api.maps.model.LatLng
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.StartRidingRequest


class DataBaseIndex {


    class UserIndex {
        companion object {
            const val ID = "uid"
            const val LOGIN_NAME = "loginname"
            const val PASS_WORD = "password"
            const val SALT = "salt"
            const val TEL = "tel"
            const val NAME = "name"
            const val LOCAKED = "locked"
            const val ORGCODE = "orgCode"
            const val REMARK = "remark"
            const val BINDING_IDENTIFICATION = "bindingIdentification"
            const val LOGIN_INDENTIFICATION = "loginIdentification"
            const val UPDATE_NAME = "updateName"
            const val UPDATE_DATE = "updateDate"
            const val DAILID = "dailId"
            const val MEMBER_DAILID = "memberDailId"
            const val IDENTITY_CARD = "identityCard"
            const val MEMBER_SEX = "memberSex"
            const val HEAD_IMG_PROJECT = "headImgProject"
            const val HEAD_IMG_FILE = "headImgFile"
            const val YEAR_OF_BIRTHDAY = "yearOfBirth"
            const val ADDRESS = "address"
            const val ISATTESTATION = "isattestation"
            const val SYNOPSIS = "synopsis"
            const val WXID = "wxId"
            const val MEMBER_ID = "memberId"
            const val OPEN_UD = "openId"
            const val SUBSCRIBE = "subscribe"
            const val SUBSCRIBETIME = "subscribeTime"
            const val NICK_NAME = "nickname"
            const val SEX = "sex"
            const val COUNTRY = "country"
            const val PROVINCE = "province"
            const val CITY = "city"
            const val LANGUAGE = "language"
            const val HEAD_IMG_URL = "headImgUrl"
            const val REAL_NAME = "realName"
            const val LOCAL_IMG_HEAD = "local_head"
        }
    }

    class DRIVER_INFO {
        //骑行数据
        companion object {
            const val UID = "uid"
            const val DID = "pid"
            const val TYPE = "type"
            const val START_POSITION = "startPosition"

            const val PASS_POSITION = "passPosition"

            const val END_POSITION = "endPosition"

            const val CREATE_TIME = "createTime"

            const val CREATE_USER = "createUser"

            const val MEMBER_ID = "memberId"

            const val RIDING_FILE_URL = "ridingFileUrl"

            const val BASE_URL = "baseUrl"

            const val UPDATE_TIME = "updateTime"

            const val TOTAL_DISTANCE = "totalDistance"

            const val TOTAL_TIME = "totalTime"

            const val AVERAGESPEED = "averageSpeed"

            const val TRACKIMG_URL = "trackImgUrl"

            const val SMALL_IMG_URL = "smallImgUrl"

            const val IMG_URL_VAR = "imgUrlvar "

            const val IMG_BASE_URL = "imgBaseUrl"

            const val ALLTOTALDIS = " allTotalDis"

            const val ALLTOTALTIME = " allTotalTime"

            const val ALLRIDING_COUNT = "allRidingCount"

            const val RIDING_ID = " ridingId"

            const val CLIMB_HEIGHT = "climbHeight"

            const val MAX_SPEED = " maxSpeed"

            const val RIDING_BEND = "ridingBend"

            const val MAXACCELERATED_SPEED = " maxAcceleratedSpeed"

            const val EMERGENCY_BRAKETIME = " emergencyBrakeTime"

            const val PUNCH_POINT = " punchPoint"

            const val PHOTO_TIME = " photoTime"

            const val DEGREE_POLLUTION = " degreePollution"

            const val PM_TWO_FIVE = " pmTwoFive"

            const val HUMIDITY = " humidity"

            const val QUERY_DATE = " queryDate"

            const val FILE_URL = " fileUrl"

            const val AREA_RANK = " areaRank"

            const val COUNTRY_RANK = " countryRank"

            const val List = " list"
        }
    }

    class DriverContinue {
        companion object {
            const val UID = "uid"
            const val PID = "pid"
            const val DRIVER_OPEN = "DriverOpen"
            const val CUR_MODEL = "curModel"
            const val START_DRIVER = "startDriver"
            const val NAVIGATION_TYPE = "navigationType"
            const val SECOND = "second"
            const val DRIVER_START_POINT = "driverStartPoint"
            const val START_AOI_NAME = "startAoiName"
            const val NAVIGATION_START_AOI_NAME = "NavigationStartAoiName"
            const val NAVIGATION_END_AOI_NAME = "NavigationEndAoiName"
            const val PASS_POINT_DATAS = "passPointDatas"
            const val NAVIGATION_START_POINT = "navigationStartPoint"
            const val NAVIGATION_END_POINT = "navigationEndPoint"
            const val END_POINT = "endPoint"
            const val START_TIME = "StartTime"
            const val DRIVER_NET_RECORD = "driverNetRecord"
            const val DISTANCE = "distance"
            const val START_POINT = "startPoint"
            const val TOTAL_POINT = "totalPoint"
            const val LOCATION_LAT = "locationLat"
            const val NAVIGATION_TOTAL_DISTANCE = "navigation_distance"
            const val NAVIGATION_TOTAL_TIME = "navigation_time"
            const val ON_DESTROYSTATUS = "onDestroyStatus"
        }
    }

    class Team() {
        companion object {
            var UID = "uid"
            var TEAM_CREATE = "teamCreate"
            var TEAM_JOIN = "teamJoin"
            var TEAM_STATUES = "teamStatus"
        }
    }

    class Location() {
        companion object {
            const val UID = "uid"
            const val POINT_TYPE = "type"
            const val LATITUDE = "latitude"
            const val LONGITUDE = "longitude"
            const val TIME = "time"
            const val SPEED = "speed"
            const val HEIGHT = "height"
            const val BEARING = "bearing"
        }
    }


    class DriverPosition {
        //本地数据库存储轨迹记录  用于轨迹回放
        companion object {
            const val DID = "did"
            const val PID = "pid"
            const val LOCAL_FILE_PATH = "path"
        }
    }


    class DriverHistoryIndex {
        //地标搜索本地记录
        companion object {
            const val UID = "uid"
            const val SERACH_HISTORY = "search_history"
        }
    }
}