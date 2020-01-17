package com.elder.zcommonmodule.Even

import java.io.Serializable


class RequestErrorEven  :Serializable{

    var errorCode :Int  =  0

    constructor(code:Int){
        this.errorCode = code
    }
}