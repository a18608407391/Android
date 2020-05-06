package com.elder.amoski.Service

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.support.annotation.RequiresApi
import android.util.Log
import com.elder.amoski.Service.Mina.MinaService
import com.elder.zcommonmodule.IMyAidlInterface
import com.elder.zcommonmodule.R
import com.iflytek.speech.UtilityConfig
import com.zk.library.Utils.PreferenceUtils
import org.cs.tec.library.Base.Utils.context


/**
 * Created by leigong2 on 2018-06-07 007.
 */


class RemoteService : Service() {

    private var mBinder = LocalBinder()
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val service: RemoteService
            get() = this@RemoteService
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("result", "RemoteService 销毁")
        PreferenceUtils.putString(context, "FinishTimeRemote", System.currentTimeMillis().toString())
    }
}