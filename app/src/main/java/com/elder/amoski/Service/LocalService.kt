package com.elder.amoski.Service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.content.ServiceConnection
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import com.elder.zcommonmodule.IMyAidlInterface


class LocalService : Service() {
    private var mBinder: MyBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
            try {
                Log.i("LocalService", "connected with " + iMyAidlInterface.getServiceName())
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        }

        override fun onServiceDisconnected(name: ComponentName) {
            Toast.makeText(this@LocalService, "链接断开，重新启动 RemoteService", Toast.LENGTH_LONG).show()
            startService(Intent(this@LocalService, RemoteService::class.java))
            bindService(Intent(this@LocalService, RemoteService::class.java), this, Context.BIND_IMPORTANT)
        }
    }


    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "LocalService 启动", Toast.LENGTH_LONG).show()
        Log.e("result","LocalService 启动")
        startService(Intent(this@LocalService, RemoteService::class.java))
        bindService(Intent(this, RemoteService::class.java), connection, Context.BIND_IMPORTANT)
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        mBinder = MyBinder()
        return mBinder
    }

    private inner class MyBinder : IMyAidlInterface.Stub() {
        override fun getServiceName(): String {
            return javaClass.name
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e("result","LocalService 销毁")
    }
}