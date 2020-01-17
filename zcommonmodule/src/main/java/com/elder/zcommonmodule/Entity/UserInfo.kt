package com.elder.zcommonmodule.Entity

import java.io.Serializable


class UserInfo : Serializable {

    var code: Int = 1000

    var msg: String? = null

    var data: Result? = null


    class Result:Serializable{
        var id: String? = null
        var name: String? = null
        var loginname: String? = null
        var password: String? = null
        var salt: String? = null
        var tel: String? = null
        var locked: String? = null
        var orgCode: String? = null
        var remark: String? = null
        var bindingIdentification: String? = null
        var loginIdentification: String? = null
        var backgroundImages :String ? = null
        var updateName: String? = null
        var updateDate: String? = null
        var dailId: String? = null
        var memberDailId: String? = null
        var identityCard: String? = null
        var memberSex: String? = null
        var headImgProject: String? = null
        var headImgFile: String? = null
        var yearOfBirth: String? = null
        var address: String? = null
        var isattestation: String? = null
        var synopsis: String? = null
        var wxId: String? = null
        var memberId: String? = null
        var openId: String? = null
        var subscribe: String? = null
        var subscribeTime: String? = null
        var nickname: String? = null
        var sex: String ? = null
        var country: String? = null
        var province: String? = null
        var city: String? = null
        var language: String? = null
        var headImgUrl: String? = null
        var realName:String? = null
    }

}