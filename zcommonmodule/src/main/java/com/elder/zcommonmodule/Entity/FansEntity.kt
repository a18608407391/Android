package com.elder.zcommonmodule.Entity

import java.io.Serializable


class FansEntity : Serializable {


    var data :ArrayList<FansBean> ? = null





    class FansBean : Serializable {
        var createDate: String? = null
        var fansMemberId: String? = null
        var followed: Int = 0
        var id: String? = null
        var memberId: String? = null
        var memberImage: String? = null
        var memberName: String? = null
        var pageSize: String? = null
    }


}