package com.elder.zcommonmodule.Entity

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.Bitmap
import com.elder.zcommonmodule.Entity.UserInfo
import java.io.Serializable


class ShareEntity:Serializable {

    var Totaldistance = ObservableField<String>()

    var Totaltime = ObservableField<String>()

    var hight = ObservableArrayList<Double>()

    var maxSpeed = ObservableField<String>()

    var averageRate = ObservableField<String>()

    var maxHight = ObservableField<String>()

    var maxUp = ObservableField<String>()

    var direct = ObservableField<String>("72°")

    var onehunSpeed = ObservableField<String>("10'")

    var userInfo = ObservableField<UserInfo>()

    var shareIcon  :String ? = null

    var secondBitmap :String ? = null


}