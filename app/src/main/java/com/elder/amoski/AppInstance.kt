package com.elder.amoski

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
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
import com.cstec.administrator.chart_module.Utils.StorageUtil
import com.elder.zcommonmodule.Service.SERVICE_CREATE
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection
import com.liulishuo.filedownloader.FileDownloader
import com.zk.library.Bus.DataEven
import com.zk.library.Bus.event.RxBusEven


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
            ARouter.printStackTrace()
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
                        context.startForegroundService(Intent(context, LowLocationService::class.java).setAction(SERVICE_CREATE))
                    } else {
                        context.startService(Intent(context, LowLocationService::class.java).setAction(SERVICE_CREATE))
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
                    Log.e("result", "发送指令" + it.gson)
                    var session = SessionManager.getInstance().writeToServer(it.gson)
                }
                "HomeStop" -> {
                    Log.e("result", "HomeStop")
                    context.startService(Intent(context, LowLocationService::class.java).setAction("stop"))
                }
                "MsgCount" -> {
                    var count = JMessageClient.getAllUnReadMsgCount()
                    var data = DataEven()
                    data.value = count
                    RxBus.default?.post(data)
                }
            }
        }
        RxSubscriptions.add(postEven)
        JMessageClient.init(getApplicationContext(), true);
        StorageUtil.init(context, null)
        JMessageClient.setDebugMode(true);
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND or JMessageClient.FLAG_NOTIFY_WITH_LED or JMessageClient.FLAG_NOTIFY_WITH_VIBRATE)
        //注册Notification点击的接收器
        NotificationClickEventReceiver(applicationContext)
        initReceiver()
    }

    var mReceiver: NetworkReceiver? = null
    fun initReceiver() {
        mReceiver = NetworkReceiver()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(mReceiver, filter)
    }

    inner class NetworkReceiver : BroadcastReceiver() {


        var TimeOut = 0L
        override fun onReceive(context: Context, intent: Intent?) {
            if (System.currentTimeMillis() - TimeOut < 1000) {
                return
            } else {
                TimeOut = System.currentTimeMillis()
            }
            Log.e("result", "网络状态监听")
            if (intent != null && intent.action == "android.net.conn.CONNECTIVITY_CHANGE") {
                val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeInfo = manager.activeNetworkInfo
                if (null == activeInfo) {
                    RxBus.default?.post(RxBusEven.getInstance(RxBusEven.NET_WORK_ERROR))
                } else {
                    RxBus.default?.post(RxBusEven.getInstance(RxBusEven.NET_WORK_SUCCESS))
                }
            }
        }
    }


    override fun onTerminate() {
        super.onTerminate()
        RxSubscriptions.remove(postEven)
    }
}