package com.elder.zcommonmodule.Entity

import java.io.Serializable


class PhotoBean : Serializable {

    var msg: String? = null
    var code: Int = 0
    var data: ResultPhoto? = null

    class ResultPhoto {
        var data: ArrayList<Photo>? = null
        var recordsTotal = 0
        var recordsFiltered = 0
        var draw = 0
    }
//    {"msg":"成功","code":"0","data":{
// "data":
// [{
// "id":181,
// "imgUrl":"/Activity/userPhotoPicoriginalImg/2019Y/05M/20D/8020190520170048472.jpg",
// "createTime":1558342848000,"createUser":"18608407391",
// "smallUrl":"/Activity/userPhotoPicsmallImg/2019Y/05M/20D/8020190520170048472.jpg",
// "status":"1",
// "baseUrl":"/uploadFile/images",
// "photoId":null,"memberId":80,"imgType":1}]
// ,"recordsTotal":1,"recordsFiltered":1,"draw":0}}

    class Photo {
        var id: String? = null
        var imgUrl: String? = null
        var createTime: String? = null
        var createDate:String ? = null
        var createUser: String? = null
        var smallUrl: String? = null
        var status: Int = 0
        var baseUrl: String? = null
        var photoId: String? = null
        var memberId: Int = 0
        var imgType: Int = 0
        var filePathUrl: String? = null
        var filePath: String? = null
        var projectUrl: String? = null
        var scheduleId = 0
        var basicsId = 0
    }
}