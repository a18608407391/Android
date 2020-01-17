package com.elder.amoski.Service

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.support.annotation.RequiresApi
import android.util.Log
import com.elder.zcommonmodule.IMyAidlInterface
import com.elder.zcommonmodule.R
import com.iflytek.speech.UtilityConfig
import com.zk.library.Utils.PreferenceUtils
import org.cs.tec.library.Base.Utils.context


/**
 * Created by leigong2 on 2018-06-07 007.
 */


class RemoteService : Service() {

    private var mBinder: MyBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
            try {
                Log.e("RemoteService", "connected with " + iMyAidlInterface.serviceName)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            PreferenceUtils.putString(context,"FinishTimeLow",System.currentTimeMillis().toString())
            startService(Intent(this@RemoteService, LocalService::class.java))
            bindService(Intent(this@RemoteService, LocalService::class.java), this, Context.BIND_IMPORTANT)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("result","RemoteService 启动")
        bindService(Intent(this, LocalService::class.java), connection, Context.BIND_IMPORTANT)
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            createNotifyCation()
        }
        return START_STICKY
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotifyCation() {
        var manager = getSystemService(IntentService.NOTIFICATION_SERVICE) as NotificationManager
        var Channel = NotificationChannel(UtilityConfig.CHANNEL_ID, "主服务", NotificationManager.IMPORTANCE_HIGH)
        Channel.enableLights(true)
        Channel.lightColor = Color.RED//设置提示灯颜色
        Channel.setShowBadge(true)//显示logo
        Channel.description = "ytzn"//设置描述
        Channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC//设置锁屏可见 VISIBILITY_PUBLIC=可见
        manager.createNotificationChannel(Channel)
        var notification = Notification.Builder(this)
                .setChannelId(UtilityConfig.CHANNEL_ID)
                .setContentTitle("主服务")//标题
                .setContentText("运行中...")//内容
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)//小图标一定需要设置,否则会报错(如果不设置它启动服务前台化不会报错,但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .build();
        startForeground(1, notification);//服务前台化只能使用startForeground()方法,不能使用
    }

    override fun onBind(intent: Intent): IBinder? {
        mBinder = MyBinder()
        return mBinder
    }

    private inner class MyBinder : IMyAidlInterface.Stub() {

        override fun getServiceName(): String {
            return RemoteService::class.java.name
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("result","RemoteService 销毁")
        PreferenceUtils.putString(context,"FinishTimeRemote",System.currentTimeMillis().toString())
    }
}