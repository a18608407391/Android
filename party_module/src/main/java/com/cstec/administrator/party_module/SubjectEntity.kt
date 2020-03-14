package com.cstec.administrator.party_module

import java.io.Serializable


class SubjectEntity  :Serializable{
    var TITLE: String? = null
    var FILE_NAME_URL: String? = null
    var ACTIVITY_START: String? = null
    var ACTIVITY_STOP: String? = null
    var DAY = 0
    var TICKET_PRICE: Double = 0.0
    var SQRTVALUE: Long = 0
    var MAN_COUNT :String ? = ""
    var DISTANCE: String? = null
    var PATH_POINT: String? = null
    var NAME: String? = null
    var HEAD_IMG_FILE: String? = null
}