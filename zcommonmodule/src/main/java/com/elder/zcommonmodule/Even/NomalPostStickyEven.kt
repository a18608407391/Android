package com.elder.zcommonmodule.Even

import android.graphics.Bitmap
import com.elder.zcommonmodule.Entity.Location


class NomalPostStickyEven {
    var type = 0
    var any: Any? = null
    var img: Any? = null

    var list: Any? = null

//    constructor(type: Int, any: Any) {
//        this.type = type
//        this.any = any
//    }

    constructor(type: Int, list: Any) {
        this.type = type
        this.list = list
    }


    constructor(type: Int, any: Any?, img: Any) {
        this.img = img
        this.type = type
        this.any = any
    }
}