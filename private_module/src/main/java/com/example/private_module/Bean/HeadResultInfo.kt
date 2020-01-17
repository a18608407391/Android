package com.example.private_module.Bean

import java.io.Serializable


class HeadResultInfo : Serializable {

    var msg: String? = null
    var code: Int = 0
    var data: HeadResult? = null


    class HeadResult : Serializable {
        var smallImgPath: String? = null

        var originaImgPath: String? = null

    }


}