package com.zk.library.Utils

import com.google.gson.Gson

class GsonUtils {

    companion object {
        val instance: GsonUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GsonUtils()
        }
    }

    private var gson: Gson

    constructor() {
        gson = Gson()
    }

    fun toJson(t: Any): String? {
        return gson.toJson(t)
    }

}