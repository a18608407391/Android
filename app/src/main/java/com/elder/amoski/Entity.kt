package com.elder.amoski

import java.io.Serializable


class Entity : Serializable {

    var version: Boolean = false

    var versionDesc: String? = null

    var force: Boolean = false

    var versionName: String? = null

}