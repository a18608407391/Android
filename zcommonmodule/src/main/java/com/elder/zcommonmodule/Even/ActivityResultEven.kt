package com.elder.zcommonmodule.Even


class ActivityResultEven {
    var name  = 0
    var data: Any? = null

    constructor(name: Int) {
        this.name = name
    }

    constructor(name: Int, data: Any) {
        this.name = name
        this.data = data

    }
}