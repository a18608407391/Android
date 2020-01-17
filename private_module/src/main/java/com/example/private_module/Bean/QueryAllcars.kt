package com.example.private_module.Bean

import java.io.Serializable


class QueryAllcars : Serializable {
//   {"msg":"成功","code":"0"
// ,"data":[{
// "id":21,
// "memberId":55,
// "carName":"卡罗拉",
// "carImg":"/userVehiclePic/originalImg/2019/04/28/Ymd4HO20190428091354372.jpg",
// "baseUrl":"D:/testProject/images",
// "carBrandId":1,
// "createTime":"2019-04-28",
// "createUser":"18608407391",
// "status":"1",
// "isDefault":"1",
// "brandName":"本田",
// "brandTypeName":"战驭 XR150"}]}


    var msg: String? = null

    var code: Int = 0

    var data: ArrayList<AllcarsResult>? = null

    class AllcarsResult {
        var id: Int = 0

        var memberId: Int = 0

        var carName: String? = null

        var carImg: String? = null

        var baseUrl: String? = null

        var carBrandId: String ? = null

        var createTime: String? = null

        var createUser: String? = null

        var status: Int = 0

        var isDefault: Int = 0

        var brandName: String? = null

        var brandTypeName: String? = null
    }
}