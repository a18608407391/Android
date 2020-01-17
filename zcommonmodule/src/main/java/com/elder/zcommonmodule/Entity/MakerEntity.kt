package com.elder.zcommonmodule.Entity

import android.databinding.ObservableField
import java.io.Serializable


class MakerEntity : Serializable {

    var id: Int = 0

    var imgUrl: String? = null

    var createTime: Long? = null

    var createUser: String? = null

    var deptId = 0

    var status = 0

    var smallImgUrl: String? = null

    var remake: String? = null

    var type = 0

    var isChecked = ObservableField<Boolean>(false)

}