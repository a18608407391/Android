package com.zk.organ.Service

import android.app.IntentService
import android.content.Context
import android.content.Intent

class InitService : IntentService {

    constructor() : super("InitService")

    companion object {
        private const val Action_WHEN_APP_CREATE: String = "org.cs.tec.library.Service.Music.Init.InitService"
        fun start(context: Context) {
            var intent = Intent(context, InitService::class.java)
            intent.action = Action_WHEN_APP_CREATE
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            if (intent.action == Action_WHEN_APP_CREATE) {
                initApplication()
            }
        }
    }

    fun initApplication() {
        //初始化操作
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}