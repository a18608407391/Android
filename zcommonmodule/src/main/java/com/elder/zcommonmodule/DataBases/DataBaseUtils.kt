package com.elder.zcommonmodule.DataBases

import android.util.Log
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.Entity.DriverInfo
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.SoketBody.SoketTeamStatus
import com.elder.zcommonmodule.Entity.UserInfo
import org.cs.tec.library.Base.Utils.context
import org.jetbrains.anko.db.transaction


fun queryUserInfo(id: String): ArrayList<UserInfo> {
    var list = ArrayList<UserInfo>()
    context.database.use {
        transaction {
            var cursor = this.query(USER_TABLE, null, DataBaseIndex.UserIndex.ID + "=?", arrayOf(id), null, null, null)
            while (!cursor.isClosed && cursor.moveToNext()) {
                var info = context.database.getUserInfo(cursor)
                list.add(info)
            }
        }
    }
    return list
}

fun queryAllUserInfo(): ArrayList<UserInfo> {
    var list = ArrayList<UserInfo>()
    context.database.use {
        transaction {
            var cursor = this.query(USER_TABLE, null, null, null, null, null, null)
            while (!cursor.isClosed && cursor.moveToNext()) {
                var info = context.database.getUserInfo(cursor)
                list.add(info)
            }
        }
    }
    return list
}

fun insertUserInfo(user: UserInfo.Result): Long {
    if(user==null){
        return 0
    }
    var values: Long = -1
    var list = queryUserInfo(user?.id!!)
//    Log.e("result","执行"+ list.size )
    if (list.size == 0) {
        context.database.use {
            transaction {
                values = this.insert(USER_TABLE, null, context.database.setUserInfo(user))
            }
        }
        Log.e("result", "执行" + values)
        return values
    } else {
        Log.e("result", "执行" + values)
        UpdateUserInfo(user)
        return 1
    }
    return values
}

fun deleteUserInfo(id: String) {
    context.database.use {
        transaction {
            this.delete(USER_TABLE, DataBaseIndex.UserIndex.ID + "=?", arrayOf(id))
        }
    }
}

fun UpdateUserInfo(user: UserInfo.Result) {
    context.database.use {
        transaction {
            this.update(USER_TABLE, context.database.setUserInfo(user), DataBaseIndex.UserIndex.ID + "=?", arrayOf(user?.id))
        }
    }
}


fun queryDriverInfo(uid: String): ArrayList<DriverInfo> {
    var list = ArrayList<DriverInfo>()
    context.database.use {
        transaction {
            var cursor = this.query(DRIVER_INFO_TABLE, null, DataBaseIndex.DRIVER_INFO.DID + "=?", arrayOf(uid), null, null, null)
            while (!cursor.isClosed && cursor.moveToNext()) {
                var info = context.database.getDriverDatas(cursor)
                list.add(info)
            }
        }
    }
    return list
}

fun insertDriverInfo(driverInfo: DriverInfo): Long {
    var values: Long = -1
    context.database.use {
        transaction {
            values = this.insert(DRIVER_INFO_TABLE, null, context.database.setDriverDatas(driverInfo))
        }
    }
    return values
}

fun deleteDriverInfo(uid: String) {
    context.database.use {
        transaction {
            this.delete(DRIVER_INFO_TABLE, DataBaseIndex.DRIVER_INFO.DID + "=?", arrayOf(uid))
        }
    }
}

fun UpdateDriverInfo(driverInfo: DriverInfo) {
    context.database.use {
        transaction {
            this.update(DRIVER_INFO_TABLE, context.database.setDriverDatas(driverInfo), DataBaseIndex.DRIVER_INFO.UID + "=?", arrayOf(driverInfo?.memberId))
        }
    }
}


fun queryDriverStatus(uid: String): ArrayList<DriverDataStatus> {
    var list = ArrayList<DriverDataStatus>()
    context.database.use {
        transaction {
            var cursor = this.query(DRIVER_STATUS_TABLE, null, DataBaseIndex.DriverContinue.UID + "=?", arrayOf(uid), null, null, null)
            while (!cursor.isClosed && cursor.moveToNext()) {
                var info = context.database.getDriverStatus(cursor)
                list.add(info)
            }
        }
    }
    return list
}


fun insertDriverStatus(driverInfo: DriverDataStatus): Long {
    var values: Long = -1
    context.database.use {
        transaction {
            values = this.insert(DRIVER_STATUS_TABLE, null, context.database.setDriverStatus(driverInfo))
        }
    }
    return values
}


fun deleteDriverStatus(uid: String) {
    context.database.use {
        transaction {
            this.delete(DRIVER_STATUS_TABLE, DataBaseIndex.DriverContinue.UID + "=?", arrayOf(uid))
        }
    }
}

fun deleteLocation(uid: String) {
    context.database.use {
        transaction {
            this.delete(LOCATION_TABLE, DataBaseIndex.Location.UID + "=?", arrayOf(uid))
        }
    }
}


fun UpdateDriverStatus(driverInfo: DriverDataStatus) {
    context.database.use {
        transaction {
            this.update(DRIVER_STATUS_TABLE, context.database.setDriverStatus(driverInfo), DataBaseIndex.DriverContinue.UID + "=?", arrayOf(driverInfo?.uid.toString()))
        }
    }
}

fun insertLocation(location: Location, uid: String) {
    context.database.use {
        this.insert(LOCATION_TABLE, null, context.database.setLocation(location, uid))
    }
}

fun queryLocation(uid: String): ArrayList<Location> {
    var list = ArrayList<Location>()
    context.database.use {
        transaction {
            var cursor = this.query(LOCATION_TABLE, null, DataBaseIndex.Location.UID + "=?", arrayOf(uid), null, null, null)
            while (!cursor.isClosed && cursor.moveToNext()) {
                var info = context.database.getLocation(cursor)
                list.add(info)
            }
        }
    }
    return list
}


fun queryTeamStatus(uid: String): ArrayList<SoketTeamStatus> {
    var list = ArrayList<SoketTeamStatus>()
    context.database.use {
        transaction {
            var cursor = this.query(TEAM_TABLE, null, DataBaseIndex.UserIndex.MEMBER_ID + "=?", arrayOf(uid), null, null, null)
            while (!cursor.isClosed && cursor.moveToNext()) {
                var info = context.database.getTeam(cursor)
                list.add(info)
            }
        }
    }
    return list
}


fun insertTeamStatus(driverInfo: SoketTeamStatus): Long {
    var values: Long = -1
    context.database.use {
        transaction {
            values = this.insert(TEAM_TABLE, null, context.database.setTeam(driverInfo, driverInfo.uid.toString()))
        }
    }
    return values
}


fun deleteTeamStatus(uid: String) {
    context.database.use {
        transaction {
            this.delete(TEAM_TABLE, DataBaseIndex.Team.UID + "=?", arrayOf(uid))
        }
    }
}


fun UpdateTeamStatus(driverInfo: SoketTeamStatus) {
    context.database.use {
        transaction {
            this.update(TEAM_TABLE, context.database.setTeam(driverInfo, driverInfo.uid.toString()), DataBaseIndex.Team.UID + "=?", arrayOf(driverInfo?.uid.toString()))
        }
    }
}
