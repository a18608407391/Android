package com.elder.zcommonmodule.Entity

import java.io.Serializable


class ProgressData : Serializable {


    var allTotalDis: Double = 0.0
    var allTotalTime: Long = 0
    var maxDis: Double = 0.0
    var ridingMonth: String? = null
}