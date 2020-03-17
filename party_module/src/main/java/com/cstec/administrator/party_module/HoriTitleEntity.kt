package com.cstec.administrator.party_module

import java.io.Serializable


class HoriTitleEntity : Serializable {

    var title: String? = null

    var check = false

    constructor(title:String,check:Boolean){
        this.title = title
        this.check = check
    }
}