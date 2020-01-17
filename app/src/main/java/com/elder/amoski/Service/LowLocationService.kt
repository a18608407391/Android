package com.elder.amoski.Service

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.elder.zcommonmodule.DataBases.insertLocation
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.IMyAidlInterface
import com.elder.zcommonmodule.R
import com.elder.zcommonmodule.Service.*
import com.elder.amoski.Service.Mina.MinaService
import com.iflytek.speech.UtilityConfig
import com.zk.library.Utils.PreferenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID


class LowLocationService : IntentService, AMapLocationListener {
    private var mediaPlayer: MediaPlayer? = null
    var lastTime: Long = 0
    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (amapLocation != null && amapLocation.errorCode == 0) {
//            if (amapLocation.locationType == 1) {
            RxBus.default?.post(amapLocation)
            builder?.setContentText("当前位置")
            if (amapLocation.accuracy < 30 && action == "driver") {
                var aoi = ""
                var poi = ""
                if (amapLocation.aoiName != null) {
                    aoi = amapLocation.aoiName
                }
                if (amapLocation.poiName != null) {
                    poi = amapLocation.poiName
                }
                var location = Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, aoi, poi)
                insertLocation(location, userid!!)
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service)
            try {
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            PreferenceUtils.putString(context, "FinishTimeRemote", System.currentTimeMillis().toString())
            startService(Intent(this@LowLocationService, RemoteService::class.java))
            bindService(Intent(this@LowLocationService, RemoteService::class.java), this, Context.BIND_IMPORTANT)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            "Create" -> {
                //  开启双进程
//                MainService.start(context)
            }
            "start" -> {
                Log.e("result", "start")
            }
            "stop" -> {
                Log.e("result", "stop")
            }
            "driver" -> {
                Log.e("result", "driver")
            }
        }
    }

    companion object {
        var ServiceLocation = "service_location"
    }

    constructor() : super("LowLocationService")


    var action: String? = null
    var userid: String? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userid = PreferenceUtils.getString(context, USERID)
        when (intent?.action) {
            SERVICE_CREATE -> {
                lastTime = System.currentTimeMillis()
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    context.startForegroundService(Intent(this@LowLocationService, RemoteService::class.java))
                } else {
                    startService(Intent(this@LowLocationService, RemoteService::class.java))
                }
                bindService(Intent(this, RemoteService::class.java), connection, Context.BIND_IMPORTANT)
                if (mediaPlayer == null) {
                    if (android.os.Build.VERSION.SDK_INT >= 26) {
                        createNotifyCation()
                    }
                    mediaPlayer = MediaPlayer.create(this, R.raw.silents)
                    mediaPlayer?.setVolume(0f, 0f)
                    mediaPlayer?.setOnCompletionListener {
                        if (!it.isPlaying) {
                            CoroutineScope(uiContext).launch {
                                delay(10000)
                                play()
                            }
                        }
                    }
                    play()
                }
                startLocation(true)
            }
            SERVICE_START -> {
                if (mLocationClient != null) {
                    mLocationClient?.stopLocation()
                }
                startLocation(true)
            }
            SERVICE_STOP -> {
                if (mLocationClient != null) {
                    mLocationClient!!.stopLocation()
                }
            }
            SERVICE_DRIVER -> {
                if (mLocationClient != null) {
                    mLocationClient?.stopLocation()
                }
                Log.e("result", "driver4")
                startLocation(false)
            }
            SERVICE_CREATE_MINA -> {
                Log.e("result","SERVICE_CREATE_MINA")
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    context.startForegroundService(Intent(this@LowLocationService, MinaService::class.java).setAction("on"))
                } else {
                    context.startService(Intent(this@LowLocationService, MinaService::class.java).setAction("on"))
                }
            }
            SERVICE_CANCLE_MINA -> {
//                if (android.os.Build.VERSION.SDK_INT >= 26) {
//                    context.startForegroundService(Intent(this@LowLocationService, MinaService::class.java).setAction("off"))
//                } else {
                context.startService(Intent(this@LowLocationService, MinaService::class.java).setAction("off"))
//                }
            }
        }
        action = intent?.action
        return START_STICKY
    }

    var mLocationClient: AMapLocationClient? = null

    private fun startLocation(flag: Boolean): AMapLocationClient? {
        mLocationClient = AMapLocationClient(context)
        // 设置定位回调监听
        mLocationClient?.setLocationListener(this)
        // 初始化AMapLocationClientOption对象
        var mLocationOption = getDefaultOption()
        // 设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
        if (flag) {
            mLocationOption.interval = 30000
        } else {
            mLocationOption.interval = 2000
        }
        //设置定位间隔,单位毫秒,默认为2000ms
        // 获取一次定位结果： //该方法默认为false。
        //给定位客户端对象设置定位参数
        mLocationClient?.setLocationOption(mLocationOption)
        mLocationClient?.stopLocation()
        // 启动定位
        mLocationClient?.startLocation()
        return mLocationClient!!
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
        notification = Notification.Builder(this)
                .setChannelId(UtilityConfig.CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))//标题
                .setContentText("运行中...")//内容
//                .setContentIntent(pending)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)//小图标一定需要设置,否则会报错(如果不设置它启动服务前台化不会报错,但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .build();
        startForeground(1, notification);//服务前台化只能使用startForeground()方法,不能使用
    }

    var builder: Notification.Builder? = null
    var notification: Notification? = null
    var notificationManager: NotificationManager? = null
    var isCreateChannel = false
    fun buildNotification(): Notification? {
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            var channelId = getPackageName() + "001"
            if (!isCreateChannel) {
                var notificationChannel = NotificationChannel(channelId,
                        "Location", NotificationManager.IMPORTANCE_NONE)
                notificationChannel.enableLights(true)//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE) //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager?.createNotificationChannel(notificationChannel)
                isCreateChannel = true
            }
            builder = Notification.Builder(applicationContext, channelId)
            startForeground(10, builder?.build())
        } else {
            builder = Notification.Builder(applicationContext)
        }
        builder?.setSmallIcon(R.mipmap.ic_launcher)
                ?.setContentTitle(getString(R.string.app_name))
                ?.setContentText("当前位置")
                ?.setWhen(System.currentTimeMillis())

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder?.build()
        } else {
            return builder?.getNotification()!!
        }
        return notification
    }

    private fun getDefaultOption(): AMapLocationClientOption {
        var mOption = AMapLocationClientOption();
        mOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.isGpsFirst = true;//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.httpTimeOut = 30000;//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.isNeedAddress = true;//可选，设置是否返回逆地理地址信息。默认是true
        mOption.interval = 3000
        mOption.isSensorEnable = true;//可选，设置是否使用传感器。默认是false
        mOption.isWifiScan = true; //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.geoLanguage = AMapLocationClientOption.GeoLanguage.DEFAULT;//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    private var mBinder: MyBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient?.stopLocation()
        PreferenceUtils.putString(context, "FinishTimeLow", System.currentTimeMillis().toString())
        Log.e("result", javaClass.canonicalName + "onDestroy")
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

    private fun play() {
        if (mediaPlayer != null && !mediaPlayer?.isPlaying!!) {
            mediaPlayer?.start()
        }
    }

    private fun pause() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying()!!) {
            mediaPlayer?.pause()
        }
    }
}