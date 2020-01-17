package com.elder.amoski.Service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.JobIntentService
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
import kotlin.collections.ArrayList

class LocationService : JobIntentService(), AMapLocationListener {

    var data = ArrayList<Location>()
    var isStart = false
    override fun onHandleWork(intent: Intent) {
        if (!isStart) {
            isStart = true
        }
        if (!PreferenceUtils.getBoolean(context, ServiceLocation)) {
            if (action == "start") {
                startLocation(false)
            } else if (action == "stop") {
                mLocationClient?.stopLocation()
            } else if (action == "driver") {
                startLocation(true)
            }
        }
    }

    var address: String? = null
    override fun onLocationChanged(amapLocation: AMapLocation?) {
        RxBus.default?.post(amapLocation!!)
        if (amapLocation?.accuracy!! < 30) {
            var location = Location(amapLocation?.latitude!!, amapLocation.longitude, amapLocation.time.toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing)
            FileSystem.writeAsString(Point_Save_Path, Gson().toJson(location) + "&")
        }
    }

    var builder: Notification.Builder? = null

    companion object {
        const val Action_LOCATION: String = "org.cs.tec.library.Service.Init.LocationSerivce"
        var ServiceLocation = "service_location"
        fun start() {
            var intent = Intent(context, InitService::class.java)
            intent.action = LocationService.Action_LOCATION
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    var action: String? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("result", "onStartCommandHight")
        if (Build.VERSION.SDK_INT >= 26) {
            createNotifyCation()
        }
        action = intent?.action
        if (action == "start") {
            startLocation(false)
        } else if (action == "stop") {
            PreferenceUtils.putBoolean(context, ServiceLocation, false)
            mLocationClient?.stopLocation()
        } else if (action == "driver") {
            startLocation(true)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    var mLocationClient: AMapLocationClient? = null
    private fun startLocation(flag: Boolean): AMapLocationClient? {
        if(!flag){
            PreferenceUtils.putBoolean(context, ServiceLocation, true)
        }
        mLocationClient = AMapLocationClient(context)
        // 设置定位回调监听
        mLocationClient?.setLocationListener(this)
        // 初始化AMapLocationClientOption对象
        var mLocationOption = getDefaultOption()
        // 设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.interval = 3000
        // 获取一次定位结果： //该方法默认为false。
        mLocationOption.isOnceLocation = flag
        mLocationOption.isMockEnable = false
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotifyCation() {
        var manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var Channel = NotificationChannel(CHANNEL_ID, "主服务", NotificationManager.IMPORTANCE_MIN)
        Channel.enableLights(true)
        Channel.lightColor = Color.RED//设置提示灯颜色
        Channel.setShowBadge(true)//显示logo
        Channel.description = "ytzn"//设置描述
        Channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC//设置锁屏可见 VISIBILITY_PUBLIC=可见
        manager.createNotificationChannel(Channel)
        var notification = Notification.Builder(this)
                .setChannelId(CHANNEL_ID)
                .setContentTitle("主服务")//标题
                .setContentText("运行中...")//内容
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)//小图标一定需要设置,否则会报错(如果不设置它启动服务前台化不会报错,但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .build();
        startForeground(1, notification);//服务前台化只能使用startForeground()方法,不能使用
    }

    var notificationManager: NotificationManager? = null
    var isCreateChannel = false
    fun buildNotification(): Notification {
        Log.e("result", "开启通知")
        var notification: Notification? = null
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            var channelId = packageName + "001"
            if (!isCreateChannel) {
                var notificationChannel = NotificationChannel(channelId,
                        "Location", NotificationManager.IMPORTANCE_MIN)
                notificationChannel.enableLights(false)//是否在桌面icon右上角展示小圆点
                notificationChannel.lightColor = Color.BLUE //小圆点颜色
                notificationChannel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
                Log.e("result", Uri.parse("android.resource://zune.keeplivelibrary/raw/sample.png").toString())
                notificationChannel.setSound(null, null)
                notificationManager?.createNotificationChannel(notificationChannel)
                isCreateChannel = true
            }
            builder = Notification.Builder(applicationContext, channelId).setSound(null)
        } else {
            builder = Notification.Builder(applicationContext)
        }
        builder?.setSmallIcon(R.mipmap.ic_launcher)
                ?.setContentTitle(getString(R.string.app_name))
                ?.setContentText(getString(R.string.location_address))
                ?.setWhen(System.currentTimeMillis())
                ?.setOnlyAlertOnce(true)
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder?.build()
        } else {
            return builder?.notification!!
        }
        return notification!!
    }

    /**
     * 防止后台2个小时后就休眠
     */
    public fun startAlarm() {
        //首先获得系统服务
        var am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //设置闹钟的意图，我这里是去调用一个服务，该服务功能就是获取位置并且上传
        var intentAlarm = Intent(this, LocationService.javaClass)
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

    var count = 0
    override fun onDestroy() {
        super.onDestroy()
        count++
        isStart = false/**/
        Log.e("result", "定位服务已关闭" + count)
        PreferenceUtils.putLong(context, "onDestroyTime", System.currentTimeMillis())
    }
}