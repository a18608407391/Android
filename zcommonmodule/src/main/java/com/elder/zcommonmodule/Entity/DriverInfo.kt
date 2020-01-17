package com.elder.zcommonmodule.Entity

import com.amap.api.maps.model.LatLng
import java.io.Serializable


class DriverInfo : Serializable {

//   {var  msgvar  :var  成功var   var  codevar  :var  0var   var  datavar  :{
// var  idvar  :null var  startPositionvar  :null
//  var  passPositionvar  :null var  endPositionvar  :null
// var  createTimevar  :null
// var  createUservar  :null
// var  memberIdvar  :null
// var  ridingFileUrlvar  :null
// var  baseUrlvar  :null
// var  updateTimevar  :null
// var  updateUservar  :null
// var  totalDistancevar  :null
// var  totalTimevar  :null
// var  averageSpeedvar  :null
// var  trackImgUrlvar  :null
// var  smallImgUrlvar  :null
// var  imgUrlvar  :null
// var  imgBaseUrlvar  :null
// var  allTotalDisvar  :6430
// var  allTotalTimevar  :238
// var  allRidingCountvar  :4
// var  ridingIdvar  :null
// var  climbHeightvar  :null
// var  maxSpeedvar  :null
// ridingBendvar  :null
// var  maxAcceleratedSpeedvar  :null
// var  emergencyBrakeTimevar  :null
// var  punchPointvar  :null
// var  photoTimevar  :null
// var  degreePollutionvar  :null
// var  pmTwoFivevar  :null
// var  humidityvar  :null
// var  queryDatevar  :null
// var  fileUrlvar  :null
// var  areaRankvar  :null
// var  countryRankvar  :null}}


    var id: Int = 0

    var startPosition: LatLng? = null

    var passPosition: ArrayList<LatLng>? = null

    var endPosition: LatLng? = null

    var createTime: Long = 0

    var createUser: String? = null

    var memberId: String? = null

    var ridingFileUrl: String? = null

    var baseUrl: String? = null

    var updateTime: String? = null

    var totalDistance: Double? = null

    var totalTime: Long? = null

    var averageSpeed: String? = null

    var trackImgUrl: String? = null

    var smallImgUrl: String? = null

    var imgUrlvar: String? = null

    var imgBaseUrl: String? = null

    var allTotalDis: Double = 0.0

    var allTotalTime: Long = 0

    var allRidingCount: Int = 0

    var ridingId: String? = null

    var climbHeight: String? = null

    var maxSpeed: String? = null

    var ridingBend: String? = null

    var maxAcceleratedSpeed: String? = null

    var emergencyBrakeTime: String? = null

    var punchPoint: ArrayList<LatLng>? = null

    var photoTime: String? = null

    var degreePollution: String? = null

    var pmTwoFive: String? = null

    var humidity: String? = null

    var queryDate: String? = null

    var fileUrl: String? = null

    var areaRank: String? = null

    var countryRank: String? = null

    var ridingEndBackgroudImg:String ? = null

    var list: ArrayList<DriverInfo>? = null



}