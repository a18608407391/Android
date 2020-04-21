package com.elder.amoski

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zk.library.Utils.RouterUtils
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.getScaleUpAnimation
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.google.gson.Gson
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.OSUtil
import com.zk.library.Utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_splash.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.http.NetworkUtil
import kotlin.concurrent.thread
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.*
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Http.NetWorkManager
import com.elder.zcommonmodule.Service.Login.LoginService
import com.elder.zcommonmodule.Service.SERVICE_AUTO_BOOT_COMPLETED
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadSampleListener
import com.liulishuo.filedownloader.FileDownloader
import com.zk.library.Base.AppManager
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Bus.event.RxBusEven.Companion.BrowserSendTeamCode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.APP_CREATE
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import java.io.File


class SplashActivity : Activity(), RouteSearch.OnRouteSearchListener {

    var teamCode: String? = null
    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
        var id = PreferenceUtils.getString(context, USERID)
        if (p1 == 1000) {
            p0?.paths!![0].steps.forEach {
                it.polyline.forEach {
                    insertLocation(Location(it.latitude, it.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F), id)
                }
            }
            var status = queryDriverStatus(id)
            status[0].second = (System.currentTimeMillis() - status[0].StartTime) / 1000
            UpdateDriverStatus(status[0])
            doStatus(queryDriverStatus(id))
        }
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {

    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {

    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {

    }

    var dispose: Disposable? = null


    @RequiresApi(android.os.Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        OSUtil.jumpStartInterface(context)

        if (intent != null) {
            if (!intent.scheme.isNullOrEmpty()) {
                var uri = intent.data
                teamCode = uri.getQueryParameter("teamCode")
            }
        }


        if (!NetworkUtil.isNetworkAvailable(this)) {
//            ARouter.getInstance().build(RouterUtils.MapModuleConfig.SMOOTH_ACTIVITY).navigation()
            Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
            return
        }

        var list = queryAllUserInfo()
        if (PreferenceUtils.getString(context, USERID) != null) {
            var location = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!location.isProviderEnabled(LocationManager.GPS_PROVIDER) || !location.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Toast.makeText(context, getString(R.string.get_point_time_out), Toast.LENGTH_SHORT).show()
                var intent = Intent()
                intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            } else if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, getString(R.string.permisstion_error), Toast.LENGTH_SHORT).show()
                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } else {
            }
        }
        var flag = checkDriverStatus()
        if (flag) {
            var pos = ServiceEven()
            pos.type = "splashCreate"
            RxBus.default?.post(pos)
            dispose = RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
                var status = queryDriverStatus(PreferenceUtils.getString(context, USERID))
                if (status[0].locationLat.size != 0) {
                    var driver = status[0].locationLat
                    var dis = AMapUtils.calculateLineDistance(LatLng(driver[driver.size - 1].latitude, driver[driver.size - 1].longitude), LatLng(it.latitude, it.longitude))
                    if (dis < 1000) {
                        if (driver[driver.size - 1].time.toLong() - status[0].StartTime > 0) {
                            status[0].second = (driver[driver.size - 1].time.toLong() - status[0].StartTime) / 1000
                        }
                        UpdateDriverStatus(status[0])
                        doStatus(status)
                    } else {
                        var mRoutePath = RouteSearch(this)
                        mRoutePath.setRouteSearchListener(this)
                        var fromAndTo = RouteSearch.FromAndTo(LatLonPoint(driver[driver.size - 1].latitude, driver[driver.size - 1].longitude), LatLonPoint(it.latitude, it.longitude))
                        var query = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION,
                                null, null, "")// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
                        mRoutePath.calculateDriveRouteAsyn(query)// 异步路径规划驾车模式查询
                    }
                } else {
                    doStatus(status)
                }
                dispose?.dispose()
                RxSubscriptions.remove(dispose)
            }
            RxSubscriptions.add(dispose)

        } else {
            if (BaseApplication.getInstance().curActivity == 0) {
                goHome()
            } else {
                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withOptionsCompat(getScaleUpAnimation(splash_layout)).navigation()
                if (teamCode != null) {
                    RxBus.default?.post(RxBusEven.getInstance(RxBusEven.BrowserSendTeamCode, teamCode!!))
                }
                finish()
            }
        }
    }


    fun Nomal() {
        var info = context.packageManager.getPackageInfo(context.packageName, 0)
        var map = HashMap<String, String>()
        map.put("version", info.versionCode.toString())
        NetWorkManager.instance.getOkHttpRetrofit()?.create(LoginService::class.java)?.getVession(NetWorkManager.instance.getBaseRequestBody(map)!!)?.map {
            return@map it
        }?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.doOnError {
            Toast.makeText(context, org.cs.tec.library.Base.Utils.getString(com.elder.blogin.R.string.network_error) + it.message, Toast.LENGTH_SHORT).show()
        }?.onErrorResumeNext(Observable.empty())?.subscribe {
            if (it.code == 0) {
                var entity = Gson().fromJson<Entity>(Gson().toJson(it.data), Entity::class.java)
                if (entity.version) {
                    if (entity.force) {
                        var dialog = DialogUtils.createNomalStyleOneDialog(this, entity.versionDesc!!, getString(R.string.go_update))
                        dialog.title(getString(R.string.update_notice))
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.setCancelable(false)
                        dialog.show()
                        dialog.setOnBtnClickL(OnBtnClickL {
                            dialog.dismiss()
                            CoroutineScope(uiContext).launch {
                                delay(500)
                                goUpdate(entity)
                            }
                        })
                    } else {
                        var s = entity.versionDesc!!.replace("\\n", "\n")
                        var dialog = DialogUtils.createNomalDialog(this, s, getString(R.string.ingnore), getString(R.string.go_update))
                        dialog.title(getString(R.string.update_notice))
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.setCancelable(false)
                        dialog.show()
                        dialog.setOnBtnClickL(OnBtnClickL {
                            dialog.dismiss()
                            goHome()
                        }, OnBtnClickL {
                            dialog.dismiss()
                            CoroutineScope(uiContext).launch {
                                delay(500)
                                goUpdate(entity)
                            }
//                            finish()
                        })
                    }
                } else {

                    thread {
                        Thread.sleep(3000)
                        var flag = PreferenceUtils.getBoolean(context, APP_CREATE, false)
                        if (flag) {
                            ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(this, object : NavCallback() {
                                override fun onArrival(postcard: Postcard?) {
                                    finish()
                                }
                            })
                        } else {
                            PreferenceUtils.putBoolean(context, APP_CREATE, true)
                            ARouter.getInstance().build(RouterUtils.ActivityPath.GUIDE).navigation(this, object : NavCallback() {
                                override fun onArrival(postcard: Postcard?) {
                                    finish()
                                }
                            })
                        }
                    }
                }
            } else {
                Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun goUpdate(entity: Entity) {
        curPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Amoski" + File.separator + "Amoski-" + entity.versionName + ".apk"
        var file = File(curPath)
        if (file.exists()) {
            startInstall(file)
        } else {
            var task = downLoadApk(entity.versionName!!)
            task!!.start()
        }
    }


    fun goHome() {
        var wm1 = this.windowManager
        BaseApplication.getInstance().getWidthPixels = wm1.defaultDisplay.width
        BaseApplication.getInstance().getHightPixels = wm1.defaultDisplay.height
        RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE).subscribe {
            if (it) {
                Nomal()
            } else {
                Toast.makeText(context, getString(R.string.permisstion_error), Toast.LENGTH_SHORT).show()
                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            9999 -> {
            }
            6666 -> {
                if (!curPath.isNullOrEmpty()) {
                    startInstall(File(curPath))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }


    fun doStatus(status: ArrayList<DriverDataStatus>) {
        if (BaseApplication.getInstance().curActivity == 0) {
            var time = "0"
            if (status[0].locationLat.size != 0) {
                var location = status[0].locationLat[status[0].locationLat.size - 1]
                time = location.time
            }
            if (System.currentTimeMillis() - time.toLong() > 60000) {
                var flag = PreferenceUtils.getBoolean(context, SERVICE_AUTO_BOOT_COMPLETED)
                if (!flag || !OSUtil.checkIgnoreBattery(this)) {
                    var dialog = DialogUtils.createNomalDialog(this, getString(R.string.checked_exception_out), getString(R.string.finish_driver), getString(R.string.continue_driving))
                    dialog.setOnBtnClickL(OnBtnClickL {
                        dialog.dismiss()
                        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withOptionsCompat(getScaleUpAnimation(splash_layout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "cancle").withString(RouterUtils.MapModuleConfig.RESUME_MAP_TEAMCODE, teamCode).navigation()
                    }, OnBtnClickL {
                        dialog.dismiss()
                        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withOptionsCompat(getScaleUpAnimation(splash_layout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue").withString(RouterUtils.MapModuleConfig.RESUME_MAP_TEAMCODE, teamCode).navigation(this, object : NavCallback() {
                            override fun onArrival(postcard: Postcard?) {
                                this@SplashActivity.finish()
                            }
                        })
                    })
                    dialog.show()
                } else {
                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withOptionsCompat(getScaleUpAnimation(splash_layout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "resume").withString(RouterUtils.MapModuleConfig.RESUME_MAP_TEAMCODE, teamCode).navigation(this, object : NavCallback() {
                        override fun onArrival(postcard: Postcard?) {
                            this@SplashActivity.finish()
                        }
                    })
                }
            } else {
                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withOptionsCompat(getScaleUpAnimation(splash_layout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "resume").withString(RouterUtils.MapModuleConfig.RESUME_MAP_TEAMCODE, teamCode).navigation(this, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            }
        } else {
            if (BaseApplication.getInstance().curActivity == 1) {
                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withOptionsCompat(getScaleUpAnimation(splash_layout)).navigation(this, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        if (teamCode != null) {
                            RxBus.default?.post(RxBusEven.getInstance(RxBusEven.BrowserSendTeamCode, teamCode!!))
                        }
                        finish()
                    }
                })
            } else if (BaseApplication.getInstance().curActivity == 3) {
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.NAVIGATION).withOptionsCompat(getScaleUpAnimation(splash_layout)).navigation(this, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (dispose != null) {
            dispose?.dispose()
            RxSubscriptions.remove(dispose)
        }
    }

    var curPath = ""


//    var defaltPath =  Environment.getExternalStorageDirectory().getPath() + File.separator + "Amoski" + File.separator + "Amoski-" + versionCode + ".apk"

    fun downLoadApk(versionCode: String): BaseDownloadTask? {
        var dialog = MaterialDialog.Builder(this).title("下载中......").content("当前版本号:优摩游" + versionCode + ",下载完成后，将自动打开优摩游安装页面,请确保app自动安装应用权限已开启！").progress(false, 100, true).progressIndeterminateStyle(false)
        dialog.cancelable(false)
        dialog.canceledOnTouchOutside(false)
        var d = dialog.show()
        d.maxProgress = 100
        var task = FileDownloader.getImpl().create("http://amoski.net/Amoski.apk").setPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "Amoski" + File.separator + "Amoski-" + versionCode + ".apk", false).setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400).setListener(object : FileDownloadSampleListener() {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        super.progress(task, soFarBytes, totalBytes)
                        d.setProgress(((soFarBytes * 1.0 / totalBytes) * 100).toInt())
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        super.completed(task)
                        d.setProgress(100)
                        d.setContent("下载完成！")
                        CoroutineScope(uiContext).launch {
                            delay(500)
                            d.dismiss()
                            startInstall(File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Amoski" + File.separator + "Amoski-" + versionCode + ".apk"))
                        }
                    }
                })
        return task
    }

    fun startInstall(file: File) {
        var intent = Intent()
        intent.action = Intent.ACTION_VIEW
        if (Build.VERSION.SDK_INT >= 24) {
            var apkUri = FileProvider.getUriForFile(context, "com.elder.zcommonmodule.fileprovider", file)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                var hasInstallPermission = getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
                    //请求安装未知应用来源的权限
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES), 6666)
                }
            }
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivityForResult(intent, 9999)
    }


//    private void startInstall(String filePath){
//        //分别进行7.0以上和7.0以下的尝试
//        File apkfile = new File(filePath);
//        if (!apkfile.exists()) {
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            //7.0以上
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),"你的包名.FileProvider",apkfile);
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        }else{
//            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
//        }
//        startActivity(intent);
//    }

}
