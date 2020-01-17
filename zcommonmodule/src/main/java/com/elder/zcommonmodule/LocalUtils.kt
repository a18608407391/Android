package com.elder.zcommonmodule

import android.util.Log
import com.zk.library.Utils.PreferenceUtils
import org.cs.tec.library.Base.Utils.context


fun getImageUrl(url: String?): String {
    val token = PreferenceUtils.getString(context, USER_TOKEN)
    var path = Base_URL + "AmoskiActivity/userCenterManage/getImg?imgUrl=" + url + "&appToken=" + token
    return path
}


fun getRoadImgUrl(url: String?): String {
    val token = PreferenceUtils.getString(context, USER_TOKEN)
    val path = Base_URL + "AmoskiRiding/appRidingGuideManage/getImg?fileUrl=" + url + "&appToken=" + token
    return path
}


fun getDriverImageUrl(url: String?, base: String?): String {
    val token = PreferenceUtils.getString(context, USER_TOKEN)
    val path = Base_URL + "AmoskiActivity/userCenterManage/getImg?imgUrl=" + url + "&appToken=" + token + "&baseUrl=" + base
    return path
}