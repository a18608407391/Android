package com.elder.amoski

import android.content.Intent
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.amoski.Service.LocationService.Companion.ServiceLocation
import com.elder.amoski.Service.LowLocationService
import com.elder.amoski.Service.Mina.SessionManager
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Service.SERVICE_CANCLE_MINA
import com.elder.zcommonmodule.Service.SERVICE_CREATE_MINA
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.PreferenceUtils
import com.zk.organ.Service.InitService
import io.reactivex.disposables.Disposable
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import cn.jpush.im.android.api.JMessageClient
import com.cstec.administrator.chart_module.Receiver.NotificationClickEventReceiver
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection
import com.liulishuo.filedownloader.FileDownloader


class AppInstance : BaseApplication() {
    var width = 0
    var hight = 0

    companion object {
        private lateinit var instan: AppInstance
        fun getInstance(): AppInstance {
            return instan
        }
    }

    var postEven: Disposable? = null
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
//            // 打印日志
            ARouter.openLog()
            //开启调试模式
            ARouter.openDebug()
        }
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
        PreferenceUtils.putBoolean(context, ServiceLocation, false)
//        KeepLiveHelper.getDefault().init(this, "", "")
////        NotificationUtils().setNotification("测试变化标题", "测试变化内容", R.mipmap.ic_launcher)
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            KeepLiveHelper.getDefault().bindService(LocationService::class.java)
//        } else {
//            KeepLiveHelper.getDefault().bindService(LocationLowService::class.java)
//        }
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(FileDownloadUrlConnection.Creator(FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15000) // set connection timeout.
                        .readTimeout(15000) // set read timeout.
                ))
                .commit()
        ARouter.init(this)
        postEven = RxBus.default?.toObservable(ServiceEven::class.java)!!.subscribe {
            when (it.type) {
                "splashCreate" -> {
                    Log.e("result", "splashCreate")
                    if (android.os.Build.VERSION.SDK_INT >= 26) {
                        context.startForegroundService(Intent(context, LowLocationService::class.java).setAction("Create"))
                    } else {
                        context.startService(Intent(context, LowLocationService::class.java).setAction("Create"))
                    }
                }
                "splashContinue" -> {
                    Log.e("result", "splashContinue")
                    if (!MinaConnected) {
                        context?.startService(Intent(context, LowLocationService::class.java).setAction(SERVICE_CREATE_MINA))
                    }
                }
                "MinaServiceCancle" -> {
                    Log.e("result", "MinaServiceCancle")
                    isClose = true
                    context?.startService(Intent(context, LowLocationService::class.java).setAction(SERVICE_CANCLE_MINA))
                }
                "HomeStart" -> {
                    Log.e("result", "HomeStart")
                    context.startService(Intent(context, LowLocationService::class.java).setAction("start"))
                }
                "HomeDriver" -> {
                    Log.e("result", "HomeDriver")
                    context.startService(Intent(context, LowLocationService::class.java).setAction("driver"))
                }
                "sendData" -> {
                    Log.e("result", "sendData")
                    var session = SessionManager.getInstance().writeToServer(it.gson)
                }
                "HomeStop" -> {
                    Log.e("result", "HomeStop")
                    context.startService(Intent(context, LowLocationService::class.java).setAction("stop"))
                }


            }
        }
        RxSubscriptions.add(postEven)
        JMessageClient.init(getApplicationContext(), true);
        JMessageClient.setDebugMode(true);
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND or JMessageClient.FLAG_NOTIFY_WITH_LED or JMessageClient.FLAG_NOTIFY_WITH_VIBRATE)
        //注册Notification点击的接收器
        NotificationClickEventReceiver(applicationContext)
    }


    override fun onTerminate() {
        super.onTerminate()
        RxSubscriptions.remove(postEven)
    }
}