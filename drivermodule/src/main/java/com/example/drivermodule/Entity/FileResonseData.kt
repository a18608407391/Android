package com.example.drivermodule.Entity

import java.io.Serializable


class FileResonseData  :Serializable{

//{"msg":"成功","code":"0","data":{"littlefilePath":"/RidingOverPiclittle/originalImg/2020Y/04M/22D/90_20200422214728.jpg","bigfilePath":"/RidingOverPicbig/originalImg/2020Y/04M/22D/90_20200422214728.jpg"}}


    var littlefilePath:String ? = null

    var bigfilePath :String ? = null
}