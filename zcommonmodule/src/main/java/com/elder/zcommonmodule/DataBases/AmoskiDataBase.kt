package com.elder.zcommonmodule.DataBases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.amap.api.services.core.LatLonPoint
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import com.elder.zcommonmodule.Entity.SoketBody.SoketTeamStatus
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonnelInfoDto
import com.google.gson.Gson
import com.zk.library.Utils.OSUtil
import org.cs.tec.library.Base.Utils.context
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import java.nio.charset.Charset


class AmoskiDataBase(mContext: Context = context) : ManagedSQLiteOpenHelper(mContext, DB_NAME, null, 4) {
    companion object {
        var mDb: AmoskiDataBase? = null
        fun getIncetance(): AmoskiDataBase? {
            if (mDb == null) {
                synchronized(AmoskiDataBase::class.java) {
                    if (mDb == null) {
                        mDb = AmoskiDataBase()
                    }
                }
            }
            return mDb
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(User)
        db!!.execSQL(driverDatas)
        db!!.execSQL(driverStatusTable)
        db!!.execSQL(location)
        db!!.execSQL(team)
        db!!.execSQL(history)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.e("result", "oldVersion" + oldVersion + "newVersion" + newVersion)
        if (newVersion > oldVersion) {
            if (newVersion > 2) {
                var result = checkColumnExist(db!!, DRIVER_STATUS_TABLE, DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_DISTANCE)
                if (!result) {
                    var upgradeGoods = "alter table $DRIVER_STATUS_TABLE add column ${DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_DISTANCE} REAL"
                    var upgradeGoods1 = "alter table $DRIVER_STATUS_TABLE add column ${DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_TIME} INTEGER"
                    db?.execSQL(upgradeGoods)
                    db?.execSQL(upgradeGoods1)
                }
                if(newVersion==4){
                    var result = checkColumnExist(db!!, DRIVER_STATUS_TABLE, DataBaseIndex.DriverContinue.MAX_SPEED)
                    if(!result){
                        var Goods = "alter table $DRIVER_STATUS_TABLE add column ${DataBaseIndex.DriverContinue.MAX_SPEED} REAL"
                        var Goods1 = "alter table $DRIVER_STATUS_TABLE add column ${DataBaseIndex.DriverContinue.MAX_HEIGHT} REAL"
                        var upgradeGoods2 = "alter table $DRIVER_STATUS_TABLE add column ${DataBaseIndex.DriverContinue.UP_COUNT} INTEGER"
                        var upgradeGoods3 = "alter table $DRIVER_STATUS_TABLE add column ${DataBaseIndex.DriverContinue.UP_VALUE} REAL"
                        db?.execSQL(Goods)
                        db?.execSQL(Goods1)
                        db?.execSQL(upgradeGoods2)
                        db?.execSQL(upgradeGoods3)
                    }
                }
            }
        }
    }


    var User = "CREATE TABLE " + USER_TABLE + "(_id integer primary KEY AUTOINCREMENT," +
            DataBaseIndex.UserIndex.LOGIN_NAME + " VARCHAR(11)," +              //设备ID即蓝牙地址
            DataBaseIndex.UserIndex.PASS_WORD + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.UserIndex.ID + " VARCHAR(20)," +             //是否是新设备
            DataBaseIndex.UserIndex.NAME + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.UserIndex.TEL + " VARCHAR(11)," +             //是否是新设备
            DataBaseIndex.UserIndex.LOCAKED + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.ORGCODE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.REMARK + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.BINDING_IDENTIFICATION + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.LOGIN_INDENTIFICATION + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.UPDATE_NAME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.UPDATE_DATE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.DAILID + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.MEMBER_DAILID + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.IDENTITY_CARD + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.MEMBER_SEX + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.HEAD_IMG_PROJECT + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.HEAD_IMG_FILE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.YEAR_OF_BIRTHDAY + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.ADDRESS + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.UserIndex.ISATTESTATION + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.SYNOPSIS + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.WXID + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.MEMBER_ID + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.OPEN_UD + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.SUBSCRIBE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.SUBSCRIBETIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.NICK_NAME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.SEX + " INTEGER," +             //是否是新设备
            DataBaseIndex.UserIndex.COUNTRY + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.UserIndex.PROVINCE + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.UserIndex.CITY + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.LANGUAGE + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.UserIndex.HEAD_IMG_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.REAL_NAME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.UserIndex.SALT + " INTEGER)"


    var driverDatas = "CREATE TABLE " + DRIVER_INFO_TABLE + "(_id integer primary KEY AUTOINCREMENT," +
            DataBaseIndex.DRIVER_INFO.UID + " VARCHAR(20)," +              //设备ID即蓝牙地址
            DataBaseIndex.DRIVER_INFO.DID + " VARCHAR(20)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.CREATE_TIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.RIDING_FILE_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.BASE_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.UPDATE_TIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.TRACKIMG_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.ALLTOTALDIS + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.ALLTOTALTIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.ALLRIDING_COUNT + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.MEMBER_ID + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.QUERY_DATE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.AREA_RANK + " INTEGER," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.List + " BLOB," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.COUNTRY_RANK + " INTEGER)"


    var driverTable = "CREATE TABLE " + DRIVER_INFO_TABLE + "(_id integer primary KEY AUTOINCREMENT," +
            DataBaseIndex.DRIVER_INFO.UID + " VARCHAR(20)," +              //设备ID即蓝牙地址
            DataBaseIndex.DRIVER_INFO.DID + " VARCHAR(20)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.TYPE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.START_POSITION + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.PASS_POSITION + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.END_POSITION + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.CREATE_TIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.CREATE_USER + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.RIDING_FILE_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.BASE_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.UPDATE_TIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.TOTAL_DISTANCE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.AVERAGESPEED + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.TRACKIMG_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.SMALL_IMG_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.IMG_URL_VAR + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.IMG_BASE_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.ALLTOTALDIS + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.RIDING_ID + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.ALLTOTALTIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.ALLRIDING_COUNT + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.CLIMB_HEIGHT + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.MEMBER_ID + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.MAX_SPEED + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.RIDING_BEND + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.MAXACCELERATED_SPEED + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.EMERGENCY_BRAKETIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.PUNCH_POINT + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.PHOTO_TIME + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.DEGREE_POLLUTION + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.PM_TWO_FIVE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.HUMIDITY + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.QUERY_DATE + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.FILE_URL + " VARCHAR(50)," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.AREA_RANK + " INTEGER," +             //是否是新设备
            DataBaseIndex.DRIVER_INFO.COUNTRY_RANK + " INTEGER)"


    fun checkColumnExist(db: SQLiteDatabase, tableName: String, columnName: String): Boolean {
        var result = false
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $tableName LIMIT 0", null)
            result = cursor != null && cursor.getColumnIndex(columnName) != -1
        } catch (e: Exception) {
//            log.error("checkColumnExists...", e)
        } finally {
            if (null != cursor && !cursor.isClosed) {
                cursor.close()
            }
        }
        return result
    }


    fun checkTableExist(db: SQLiteDatabase, tableName: String): Boolean {
        var result = false
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT count(*) FROM sqlite_master where type='table' AND name='$tableName'", null)
            cursor!!.moveToFirst()
            result = cursor.getInt(0) > 0
        } catch (e: Exception) {
            Log.e("checkTableExist...", e.toString())
        } finally {
            if (null != cursor && !cursor.isClosed) {
                cursor.close()
            }
        }
        return result
    }


    var driverStatusTable = "CREATE TABLE " + DRIVER_STATUS_TABLE + "(_id integer primary KEY AUTOINCREMENT," +
            DataBaseIndex.DriverContinue.UID + " VARCHAR(20)," +              //设备ID即蓝牙地址
            DataBaseIndex.DriverContinue.DRIVER_OPEN + " INTEGER," +             //是否是新设备
            DataBaseIndex.DriverContinue.CUR_MODEL + " INTEGER," +             //是否是新设备
            DataBaseIndex.DriverContinue.START_DRIVER + " INTEGER," +             //是否是新设备
            DataBaseIndex.DriverContinue.NAVIGATION_TYPE + " INTEGER," +             //是否是新设备
            DataBaseIndex.DriverContinue.SECOND + " INTEGER," +             //是否是新设备
            DataBaseIndex.DriverContinue.DRIVER_START_POINT + " BLOB," +             //是否是新设备
            DataBaseIndex.DriverContinue.START_AOI_NAME + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.DriverContinue.NAVIGATION_START_AOI_NAME + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.DriverContinue.NAVIGATION_END_AOI_NAME + " VARCHAR(30)," +             //是否是新设备
            DataBaseIndex.DriverContinue.PASS_POINT_DATAS + " BLOB," +             //是否是新设备
            DataBaseIndex.DriverContinue.NAVIGATION_START_POINT + " BLOB," +             //是否是新设备
            DataBaseIndex.DriverContinue.END_POINT + " BLOB," +             //是否是新设备
            DataBaseIndex.DriverContinue.START_TIME + " VARCHAR(20)," +             //是否是新设备
            DataBaseIndex.DriverContinue.DRIVER_NET_RECORD + " BLOB," +             //是否是新设备
            DataBaseIndex.DriverContinue.DISTANCE + " REAL," +             //是否是新设备
            DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_TIME + " INTEGER," +             //是否是新设备
            DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_DISTANCE + " REAL," +             //是否是新设备
            DataBaseIndex.DriverContinue.UP_COUNT + " INTEGER," +             //是否是新设备
            DataBaseIndex.DriverContinue.UP_VALUE + " REAL," +             //是否是新设备
            DataBaseIndex.DriverContinue.MAX_HEIGHT + " REAL," +             //是否是新设备
            DataBaseIndex.DriverContinue.MAX_SPEED + " REAL," +             //是否是新设备
            DataBaseIndex.DriverContinue.START_POINT + " BLOB," +             //是否是新设备




            DataBaseIndex.DriverContinue.NAVIGATION_END_POINT + " VARCHAR(255))"

    var location = "CREATE TABLE " + LOCATION_TABLE + "(_id integer primary KEY AUTOINCREMENT," +
            DataBaseIndex.Location.UID + " INTEGER," +              //设备ID即蓝牙地址
            DataBaseIndex.Location.POINT_TYPE + " INTEGER," +             //是否是新设备
            DataBaseIndex.Location.LATITUDE + " REAL," +             //是否是新设备
            DataBaseIndex.Location.LONGITUDE + " REAL," +             //是否是新设备
            DataBaseIndex.Location.TIME + " VARCHAR(20)," +             //是否是新设备.
            DataBaseIndex.Location.SPEED + " REAL," +             //是否是新设备
            DataBaseIndex.Location.HEIGHT + " REAL," +             //是否是新设备
            DataBaseIndex.Location.BEARING + " REAL)"

    var team = "CREATE TABLE " + TEAM_TABLE + "(_id integer primary KEY AUTOINCREMENT," +
            DataBaseIndex.Team.UID + " INTEGER," +              //设备ID即蓝牙地址
            DataBaseIndex.Team.TEAM_CREATE + " BLOB," +             //是否是新设备
            DataBaseIndex.Team.TEAM_STATUES + " INTEGER," +             //是否是新设备
            DataBaseIndex.Team.TEAM_JOIN + " BLOB)"

    var history = "CREATE TABLE " + HISTORY_TABLE + "(_id integer primary KEY AUTOINCREMENT," +
            DataBaseIndex.DriverHistoryIndex.UID + " INTEGER," +              //设备ID即蓝牙地址
            DataBaseIndex.DriverHistoryIndex.SERACH_HISTORY + " VARCHAR(50))"

    fun getDriverStatus(cursor: Cursor): DriverDataStatus {
        var status = DriverDataStatus()
        status.uid = cursor.getString(cursor.getColumnIndex(DataBaseIndex.DriverContinue.UID))
        status.DriverOpen = cursor.getInt(cursor.getColumnIndex(DataBaseIndex.DriverContinue.DRIVER_OPEN))
        status.curModel = cursor.getInt(cursor.getColumnIndex(DataBaseIndex.DriverContinue.CUR_MODEL))
        status.startDriver.set(cursor.getInt(cursor.getColumnIndex(DataBaseIndex.DriverContinue.START_DRIVER)))
        status.navigationType = cursor.getInt(cursor.getColumnIndex(DataBaseIndex.DriverContinue.NAVIGATION_TYPE))
        status.second = cursor.getInt(cursor.getColumnIndex(DataBaseIndex.DriverContinue.SECOND)).toLong()
        var a = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.DRIVER_START_POINT)))
        if (a != null) {
            status.driverStartPoint = a as Location
        }


        status.startAoiName = cursor.getString(cursor.getColumnIndex(DataBaseIndex.DriverContinue.START_AOI_NAME))



        Log.e("result", status.startAoiName + "startAoiName")
        status.NavigationStartAoiName = cursor.getString(cursor.getColumnIndex(DataBaseIndex.DriverContinue.NAVIGATION_START_AOI_NAME))
        status.NavigationEndAoiName = cursor.getString(cursor.getColumnIndex(DataBaseIndex.DriverContinue.NAVIGATION_END_AOI_NAME))


        if (OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.PASS_POINT_DATAS))) != null) {
            if (OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.PASS_POINT_DATAS))) as ArrayList<Location> == null) {
                status.passPointDatas = ArrayList()
            }
        } else {
            status.passPointDatas = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.PASS_POINT_DATAS))) as ArrayList<Location>
        }

        var b = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.NAVIGATION_START_POINT)))
        if (b != null) {
            status.navigationStartPoint = b as Location
        }
        var c = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.END_POINT)))
        if (c != null) {
            status.endPoint = c as Location
        }
        status.StartTime = cursor.getLong(cursor.getColumnIndex(DataBaseIndex.DriverContinue.START_TIME))
        if (OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.DRIVER_NET_RECORD))) != null) {
            status.driverNetRecord = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.DRIVER_NET_RECORD))) as StartRidingRequest
        }
        status.distance = cursor.getDouble(cursor.getColumnIndex(DataBaseIndex.DriverContinue.DISTANCE))
        var d = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.START_POINT)))
        if (d != null) {
            status.startPoint = d as Location
        }
        status.navigationDistance = cursor.getFloat(cursor.getColumnIndex(DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_DISTANCE))
        status.navigationTime = cursor.getLong(cursor.getColumnIndex(DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_TIME))

        var e = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.DriverContinue.NAVIGATION_END_POINT)))
        if (e != null) {
            status.navigationEndPoint = e as Location
        }
        use {
            var cursor = this.query(LOCATION_TABLE, null, DataBaseIndex.Location.UID + "=?", arrayOf(status.uid.toString()), null, null, null)
            while (!cursor.isClosed && cursor.moveToNext()) {
                var loc = getLocation(cursor)
                status.locationLat.add(loc)
            }
        }
        return status
    }


    fun getLocation(cursor: Cursor): Location {
        var location = Location()
        location.latitude = cursor.getDouble(cursor.getColumnIndex(DataBaseIndex.Location.LATITUDE))
        location.longitude = cursor.getDouble(cursor.getColumnIndex(DataBaseIndex.Location.LONGITUDE))
        location.time = cursor.getString(cursor.getColumnIndex(DataBaseIndex.Location.TIME))
        location.speed = cursor.getFloat(cursor.getColumnIndex(DataBaseIndex.Location.SPEED))
        location.height = cursor.getDouble(cursor.getColumnIndex(DataBaseIndex.Location.HEIGHT))
        location.bearing = cursor.getFloat(cursor.getColumnIndex(DataBaseIndex.Location.BEARING))
        return location
    }

    fun setDriverStatus(status: DriverDataStatus): ContentValues {
        var valuse = ContentValues()
        valuse.put(DataBaseIndex.DriverContinue.UID, status.uid)
        valuse.put(DataBaseIndex.DriverContinue.DRIVER_OPEN, status.DriverOpen)
        valuse.put(DataBaseIndex.DriverContinue.CUR_MODEL, status.curModel)
        valuse.put(DataBaseIndex.DriverContinue.START_DRIVER, status.startDriver.get())
        valuse.put(DataBaseIndex.DriverContinue.NAVIGATION_TYPE, status.navigationType)
        valuse.put(DataBaseIndex.DriverContinue.SECOND, status.second)
        valuse.put(DataBaseIndex.DriverContinue.DRIVER_START_POINT, OSUtil.toByteArray(status.driverStartPoint))
        valuse.put(DataBaseIndex.DriverContinue.START_AOI_NAME, status.startAoiName)
        valuse.put(DataBaseIndex.DriverContinue.NAVIGATION_START_AOI_NAME, status.NavigationStartAoiName)
        valuse.put(DataBaseIndex.DriverContinue.NAVIGATION_END_AOI_NAME, status.NavigationStartAoiName)
        valuse.put(DataBaseIndex.DriverContinue.PASS_POINT_DATAS, OSUtil.toByteArray(status.passPointDatas))
        valuse.put(DataBaseIndex.DriverContinue.NAVIGATION_START_POINT, OSUtil.toByteArray(status.navigationStartPoint))
        valuse.put(DataBaseIndex.DriverContinue.END_POINT, OSUtil.toByteArray(status.endPoint))
        valuse.put(DataBaseIndex.DriverContinue.START_TIME, status.StartTime)
        valuse.put(DataBaseIndex.DriverContinue.DRIVER_NET_RECORD, OSUtil.toByteArray(status.driverNetRecord))
        valuse.put(DataBaseIndex.DriverContinue.DISTANCE, status.distance)
        valuse.put(DataBaseIndex.DriverContinue.START_POINT, OSUtil.toByteArray(status.startPoint))
        valuse.put(DataBaseIndex.DriverContinue.NAVIGATION_END_POINT, OSUtil.toByteArray(status.navigationEndPoint))

        valuse.put(DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_DISTANCE, status.navigationDistance)

        valuse.put(DataBaseIndex.DriverContinue.NAVIGATION_TOTAL_TIME, status.navigationTime)


        valuse.put(DataBaseIndex.DriverContinue.MAX_HEIGHT ,status.maxHeight)
        valuse.put(DataBaseIndex.DriverContinue.MAX_SPEED,status.maxSpeed)
        valuse.put(DataBaseIndex.DriverContinue.UP_COUNT,status.UpCount)
        valuse.put(DataBaseIndex.DriverContinue.UP_VALUE,status.UpValue)
        return valuse
    }

    fun setUserInfo(user: UserInfo.Result): ContentValues {
        var valuse = ContentValues()

        valuse.put(DataBaseIndex.UserIndex.ID, user?.id)

        valuse.put(DataBaseIndex.UserIndex.PASS_WORD, user?.password)

        valuse.put(DataBaseIndex.UserIndex.TEL, user?.tel)

        valuse.put(DataBaseIndex.UserIndex.NAME, user?.name)

        valuse.put(DataBaseIndex.UserIndex.LOCAKED, user?.locked)

        valuse.put(DataBaseIndex.UserIndex.ORGCODE, user?.orgCode)

        valuse.put(DataBaseIndex.UserIndex.REMARK, user?.remark)

        valuse.put(DataBaseIndex.UserIndex.BINDING_IDENTIFICATION, user?.bindingIdentification)

        valuse.put(DataBaseIndex.UserIndex.LOGIN_INDENTIFICATION, user?.loginIdentification)

        valuse.put(DataBaseIndex.UserIndex.UPDATE_NAME, user?.updateName)

        valuse.put(DataBaseIndex.UserIndex.UPDATE_DATE, user?.updateDate)

        valuse.put(DataBaseIndex.UserIndex.DAILID, user?.dailId)

        valuse.put(DataBaseIndex.UserIndex.MEMBER_DAILID, user?.memberDailId)

        valuse.put(DataBaseIndex.UserIndex.IDENTITY_CARD, user?.identityCard)

        valuse.put(DataBaseIndex.UserIndex.MEMBER_SEX, user?.memberSex)

        valuse.put(DataBaseIndex.UserIndex.HEAD_IMG_PROJECT, user?.headImgProject)

        valuse.put(DataBaseIndex.UserIndex.HEAD_IMG_FILE, user?.headImgFile)

        valuse.put(DataBaseIndex.UserIndex.YEAR_OF_BIRTHDAY, user?.yearOfBirth)

        valuse.put(DataBaseIndex.UserIndex.ADDRESS, user?.address)

        valuse.put(DataBaseIndex.UserIndex.ISATTESTATION, user?.isattestation)

        valuse.put(DataBaseIndex.UserIndex.SYNOPSIS, user?.synopsis)

        valuse.put(DataBaseIndex.UserIndex.WXID, user?.wxId)

        valuse.put(DataBaseIndex.UserIndex.MEMBER_ID, user?.memberId)

        valuse.put(DataBaseIndex.UserIndex.OPEN_UD, user?.openId)

        valuse.put(DataBaseIndex.UserIndex.SUBSCRIBE, user?.subscribe)

        valuse.put(DataBaseIndex.UserIndex.SUBSCRIBETIME, user?.subscribeTime)

        valuse.put(DataBaseIndex.UserIndex.NICK_NAME, user?.nickname)
        valuse.put(DataBaseIndex.UserIndex.SEX, user?.sex)
        valuse.put(DataBaseIndex.UserIndex.COUNTRY, user?.country)
        valuse.put(DataBaseIndex.UserIndex.PROVINCE, user?.province)
        valuse.put(DataBaseIndex.UserIndex.CITY, user?.city)
        valuse.put(DataBaseIndex.UserIndex.LANGUAGE, user?.language)
        valuse.put(DataBaseIndex.UserIndex.HEAD_IMG_URL, user?.headImgUrl)
        valuse.put(DataBaseIndex.UserIndex.REAL_NAME, user?.realName)
        valuse.put(DataBaseIndex.UserIndex.SALT, user?.salt)
        Log.e("result", Gson().toJson(valuse) + "VALUES")
        return valuse
    }


    fun getUserInfo(valuse: Cursor): UserInfo {
        var user = UserInfo()
        user.data = UserInfo.Result()
        user?.data?.id = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.ID))
        user?.data?.tel = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.TEL))
        user?.data?.password = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.PASS_WORD))
        user?.data?.name = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.NAME))
        user?.data?.locked = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.LOCAKED))
        user?.data?.orgCode = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.ORGCODE))
        user?.data?.remark = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.REMARK))
        user?.data?.bindingIdentification = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.BINDING_IDENTIFICATION))
        user?.data?.loginIdentification = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.LOGIN_INDENTIFICATION))
        user?.data?.updateName = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.UPDATE_NAME))
        user?.data?.updateDate = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.UPDATE_DATE))
        user?.data?.dailId = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.DAILID))
        user?.data?.memberDailId = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.MEMBER_DAILID))
        user?.data?.identityCard = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.IDENTITY_CARD))
        user?.data?.memberSex = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.MEMBER_SEX))
        user?.data?.headImgProject = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.HEAD_IMG_PROJECT))
        user?.data?.headImgFile = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.HEAD_IMG_FILE))
        user?.data?.yearOfBirth = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.YEAR_OF_BIRTHDAY))
        user?.data?.address = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.ADDRESS))
        user?.data?.isattestation = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.ISATTESTATION))
        user?.data?.synopsis = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.SYNOPSIS))
        user?.data?.wxId = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.WXID))
        user?.data?.memberId = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.MEMBER_ID))
        user?.data?.openId = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.OPEN_UD))
        user?.data?.subscribe = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.SUBSCRIBE))
        user?.data?.subscribeTime = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.SUBSCRIBETIME))
        user?.data?.nickname = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.NICK_NAME))

        user?.data?.sex = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.SEX))

        user?.data?.country = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.COUNTRY))
        user?.data?.province = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.PROVINCE))
        user?.data?.city = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.CITY))
        user?.data?.language = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.LANGUAGE))
        user?.data?.headImgUrl = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.HEAD_IMG_URL))
        user?.data?.realName = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.REAL_NAME))
        user?.data?.salt = valuse.getString(valuse.getColumnIndex(DataBaseIndex.UserIndex.SALT))
        return user
    }

    fun getDriverDatas(valuse: Cursor): DriverInfo {
        var driver = DriverInfo()
        driver.memberId = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.UID))
        driver.id = valuse.getInt(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.DID))
        driver.createTime = valuse.getLong(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.CREATE_TIME))
        driver.ridingFileUrl = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.RIDING_FILE_URL))
        driver.baseUrl = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.BASE_URL))
        driver.updateTime = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.UPDATE_TIME))
        driver.trackImgUrl = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.TRACKIMG_URL))
        driver.allTotalDis = valuse.getDouble(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.ALLTOTALDIS))
        driver.allTotalTime = valuse.getLong(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.ALLTOTALTIME))
        driver.allRidingCount = valuse.getInt(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.ALLRIDING_COUNT))
        driver.memberId = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.MEMBER_ID))
        driver.queryDate = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.QUERY_DATE))
        driver.areaRank = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.AREA_RANK))
        driver.list = OSUtil.toObject(valuse.getBlob(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.List))) as ArrayList<DriverInfo>
        driver.countryRank = valuse.getString(valuse.getColumnIndex(DataBaseIndex.DRIVER_INFO.COUNTRY_RANK))
        return driver
    }


    fun setDriverDatas(driver: DriverInfo): ContentValues {
        var valuse = ContentValues()
        valuse.put(DataBaseIndex.DRIVER_INFO.UID, driver.memberId)
        valuse.put(DataBaseIndex.DRIVER_INFO.DID, driver.id)
        valuse.put(DataBaseIndex.DRIVER_INFO.CREATE_TIME, driver.createTime)
        valuse.put(DataBaseIndex.DRIVER_INFO.RIDING_FILE_URL, driver.ridingFileUrl)
        valuse.put(DataBaseIndex.DRIVER_INFO.BASE_URL, driver.baseUrl)
        valuse.put(DataBaseIndex.DRIVER_INFO.UPDATE_TIME, driver.updateTime)
        valuse.put(DataBaseIndex.DRIVER_INFO.TRACKIMG_URL, driver.trackImgUrl)
        valuse.put(DataBaseIndex.DRIVER_INFO.ALLTOTALDIS, driver.allTotalDis)
        valuse.put(DataBaseIndex.DRIVER_INFO.ALLTOTALTIME, driver.allTotalTime)
        valuse.put(DataBaseIndex.DRIVER_INFO.ALLRIDING_COUNT, driver.allRidingCount)
        valuse.put(DataBaseIndex.DRIVER_INFO.MEMBER_ID, driver.memberId)
        valuse.put(DataBaseIndex.DRIVER_INFO.QUERY_DATE, driver.queryDate)
        valuse.put(DataBaseIndex.DRIVER_INFO.AREA_RANK, driver.areaRank)
        valuse.put(DataBaseIndex.DRIVER_INFO.List, OSUtil.toByteArray(driver.list))
        valuse.put(DataBaseIndex.DRIVER_INFO.COUNTRY_RANK, driver.countryRank)
        return valuse
    }

    fun setLocation(location: Location, uid: String): ContentValues {
        var valuse = ContentValues()
        valuse.put(DataBaseIndex.Location.UID, uid)
        valuse.put(DataBaseIndex.Location.BEARING, location.bearing)
        valuse.put(DataBaseIndex.Location.HEIGHT, location.height)
        valuse.put(DataBaseIndex.Location.LATITUDE, location.latitude)
        valuse.put(DataBaseIndex.Location.LONGITUDE, location.longitude)
        valuse.put(DataBaseIndex.Location.POINT_TYPE, 0)
        valuse.put(DataBaseIndex.Location.SPEED, location.speed)
        valuse.put(DataBaseIndex.Location.TIME, location.time)
        return valuse
    }

    fun setTeam(teamStatus: SoketTeamStatus, uid: String): ContentValues {
        var values = ContentValues()
        values.put(DataBaseIndex.Team.UID, uid)
        values.put(DataBaseIndex.Team.TEAM_CREATE, OSUtil.toByteArray(teamStatus.teamCreate))
        values.put(DataBaseIndex.Team.TEAM_JOIN, OSUtil.toByteArray(teamStatus.teamJoin))
        values.put(DataBaseIndex.Team.TEAM_STATUES, teamStatus.teamStatus)
        return values
    }

    fun getTeam(cursor: Cursor): SoketTeamStatus {
        var status = SoketTeamStatus()
        status.teamCreate = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.Team.TEAM_CREATE))) as CreateTeamInfoDto?
        status.teamJoin = OSUtil.toObject(cursor.getBlob(cursor.getColumnIndex(DataBaseIndex.Team.TEAM_JOIN))) as TeamPersonnelInfoDto?
        status.teamStatus = cursor.getInt(cursor.getColumnIndex(DataBaseIndex.Team.TEAM_STATUES))
        return status
    }

}