package com.example.private_module.Entitiy

import android.databinding.ObservableField
import java.io.Serializable


class RequestEntity : Serializable {

    var code = 0
    var msg: String? = null
    var data: ArrayList<RequestIdeaEntity>? = null


    class RequestIdeaEntity {
        var id = 0
        var name: String? = null
        var pId = 0
    }


}