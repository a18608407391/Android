package com.elder.amoski.Service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.amap.api.location.AMapLocation
import com.elder.amoski.R
import com.iflytek.speech.UtilityConfig.CHANNEL_ID
import com.zk.organ.Service.InitService
import org.cs.tec.library.Base.Utils.context
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Point_Save_Path
import com.elder.zcommonmodule.Utils.FileSystem
import com.google.gson.Gson
import com.zk.library.Utils.PreferenceUtils
import org.cs.tec.library.Bus.RxBus

class LocationLowService : IntentService, AMapLocationListener {
    var isStart = false
    override fun onHandleIntent(intent: Intent?) {
    }

    constructor() : super("LocationService")

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (amapLocation != null && amapLocation.errorCode == 0) {
//            if (amapLocation.locationType == 1) {
            RxBus.default?.post(amapLocation)
            if (amapLocation.accuracy < 30 && action == "driver") {
                var location = Location(amapLocation.latitude, amapLocation.longitude, amapLocation.time.toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing)
                FileSystem.writeAsString(Point_Save_Path, Gson().toJson(location) + "&")
            }
        }
    }

    companion object {
        const val Action_LOCATION: String = "org.cs.tec.library.Service.Init.LocationSerivce"
        fun start() {
            var intent = Intent(context, InitService::class.java)
            intent.action = LocationLowService.Action_LOCATION
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    var startTime: Long = 0
    var action: String? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        action = intent?.action
        if (action == "start") {
            if (!isStart) {
                isStart = true
                startTime = System.currentTimeMillis()
//                if (!PreferenceUtils.getBoolean(context, LocationService.ServiceLocation)) {
                startLocation(true)
//                }

            }
        } else if (action == "stop") {
            if (mLocationClient != null) {
                PreferenceUtils.putBoolean(context, LocationService.ServiceLocation, false)
                mLocationClient?.stopLocation()
            }
        } else if (action == "driver") {
            if (mLocationClient != null) {
                mLocationClient?.stopLocation()
            }
            if (!isStart) {
                isStart = true
                startTime = System.currentTimeMillis()
                startLocation(false)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    var mLocationClient: AMapLocationClient? = null
    private fun startLocation(flag: Boolean): AMapLocationClient? {
        Log.e("result", "开始定位")
        if (!flag) {
            PreferenceUtils.putBoolean(context, LocationService.ServiceLocation, true)
        }
        mLocationClient = AMapLocationClient(context)
        // 设置定位回调监听
        mLocationClient?.setLocationListener(this)
        // 初始化AMapLocationClientOption对象
        var mLocationOption = getDefaultOption()
        // 设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.interval = 2000
        // 获取一次定位结果： //该方法默认为false。
        mLocationOption.isOnceLocation = flag
        mLocationOption.isMockEnable = false
        mLocationOption.isSensorEnable = true;//可选，设置是否使用传感器。默认是false
        mLocationOption.isOnceLocationLatest = false
        mLocationClient?.enableBackgroundLocation(2004, buildNotification())
        //给定位客户端对象设置定位参数
        mLocationClient?.setLocationOption(mLocationOption)
        mLocationClient?.stopLocation()
        // 启动定位
        mLocationClient?.startLocation()
        return mLocationClient!!
    }

    private fun getDefaultOption(): AMapLocationClientOption {
        var mOption = AMapLocationClientOption();
        mOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.isGpsFirst = true;//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.httpTimeOut = 30000;//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.interval = 2000;//可选，设置定位间隔。默认为2秒
        mOption.isNeedAddress = true;//可选，设置是否返回逆地理地址信息。默认是true
        mOption.isOnceLocation = false;//可选，设置是否单次定位。默认是false
        mOption.isOnceLocationLatest = false;//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.isSensorEnable = false;//可选，设置是否使用传感器。默认是false
        mOption.isWifiScan = true; //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.isLocationCacheEnable = true; //可选，设置是否使用缓存定位，默认为true
        mOption.geoLanguage = AMapLocationClientOption.GeoLanguage.DEFAULT;//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
    }


    var notificationManager: NotificationManager? = null
    var isCreateChannel = false
    var builder: Notification.Builder? = null
    var notification: Notification? = null
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
        } else {
            builder = Notification.Builder(applicationContext)
        }
        builder?.setSmallIcon(R.mipmap.ic_launcher)
                ?.setContentTitle(getString(R.string.app_name))
                ?.setContentText(getString(R.string.location_address))
                ?.setWhen(System.currentTimeMillis())

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder?.build()
        } else {
            return builder?.getNotification()!!
        }
        return notification
    }

    /**
     * 防止后台2个小时后就休眠
     */
    public fun startAlarm() {
        //首先获得系统服务
        var am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //设置闹钟的意图，我这里是去调用一个服务，该服务功能就是获取位置并且上传
        var intentAlarm = Intent(this, LocationLowService.javaClass)
        var pendSender = PendingIntent.getService(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
//        am.cancel(pendSender);
        //AlarmManager.RTC_WAKEUP ;这个参数表示系统会唤醒进程；设置的间隔时间是20分钟
        var triggerAtTime = System.currentTimeMillis() + 20 * 60 * 1000
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtTime,
                    pendSender)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime,
                    pendSender)
        } else {
            am.set(AlarmManager.RTC_WAKEUP, triggerAtTime,
                    pendSender)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isStart = false
        Log.e("result", "onLowDestroy")
        PreferenceUtils.putLong(context, "onDestroyTime", System.currentTimeMillis())
//        if (SPUtils.getInstance().getString( "Action") == "stop") {
//            Log.e("result","StopLocation")
//            mLocationClient?.stopLocation()
//        }
//        NotificationUtils().setNotification("测试变化标题", "测试变化内容", R.mipmap.ic_launcher)
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            KeepLiveHelper.getDefault().bindService(LocationService::class.java)
//        } else {
//            KeepLiveHelper.getDefault().bindService(LocationLowService::class.java)
//        }
    }

}