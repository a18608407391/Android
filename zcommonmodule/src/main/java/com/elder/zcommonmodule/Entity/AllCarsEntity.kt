package com.elder.zcommonmodule.Entity

import com.zyyoona7.wheel.IWheelEntity


class AllCarsEntity() {

    var msg: String? = null
    var code: Int? = 0
    var data: ArrayList<AllCarsTypeBean>? = null


    class AllCarsTypeBean : IWheelEntity {
        override fun getWheelText(): String {
            return if (brandName == null) name!! else brandName!!
        }
        var id: Int? = null
        var brandName: String? = null
        var name: String? = null
        var createTime: String? = null
        var createUser: String? = null
        var list: ArrayList<AllCarsTypeBean>? = null
    }
}