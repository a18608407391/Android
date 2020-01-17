package com.example.drivermodule.ViewModel

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.*
import android.widget.*
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Component.DriverComponent
import com.elder.zcommonmodule.DataBases.UpdateDriverStatus
import com.elder.zcommonmodule.DataBases.deleteDriverStatus
import com.elder.zcommonmodule.DataBases.insertDriverStatus
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.UIdeviceInfo
import com.elder.zcommonmodule.Even.ActivityResultEven
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Widget.Chart.SuitLines
import com.elder.zcommonmodule.Widget.Chart.Unit
import com.example.drivermodule.Activity.MapActivity
import com.example.drivermodule.Adapter.AddPointAdapter
import com.example.drivermodule.Component.DriverController
import com.elder.zcommonmodule.Entity.ShareEntity
import com.elder.zcommonmodule.Entity.UpDataDriverEntitiy
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import com.elder.zcommonmodule.Entity.SoketBody.SoketTeamStatus
import com.elder.zcommonmodule.Entity.WeatherEntity
import com.elder.zcommonmodule.Even.NomalPostStickyEven
import com.elder.zcommonmodule.Entity.StartRidingRequest
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.drivermodule.Component.SimpleRecycleComponent
import com.example.drivermodule.R
import com.example.drivermodule.Ui.DriverFragment
import com.example.drivermodule.Ui.TeamFragment
import com.google.gson.Gson
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


class DriverViewModel : BaseViewModel, DriverComponent.onFiveClickListener, HttpInteface.CheckTeamStatus {


    override fun CheckTeamStatusSucccess(it: BaseResponse) {
//        Log.e("result", it + "查询返回消息")
        Log.e("result", "发送查询返回消息" + Gson().toJson(it))
        var info = Gson().fromJson<CreateTeamInfoDto>(Gson().toJson(it.data), CreateTeamInfoDto::class.java)
        if (it.code == 0) {
            //存在队伍，并在队伍中，直接进入
//            mapActivity.getTeamFragment().viewModel?.addDispose()
            if (status.startDriver.get() == 2) {
                status.startDriver.set(TeamModel)
            }
            TeamStatus = SoketTeamStatus()
            mapActivity.getTeamFragment().create = info
            mapActivity.mViewModel?.component!!.isTeam.set(true)
            mapActivity.mViewModel?.component!!.Drivering.set(true)
            mapActivity?.mViewModel?.changerFragment(1)
            var pos = ServiceEven()
            pos.type = "splashContinue"
            RxBus.default?.post(pos)
        } else {
            mapActivity.dismissProgressDialog()
            if (it.code == 20001) {
                //队伍不存在
                TeamStatus = SoketTeamStatus()
                ARouter.getInstance().build(RouterUtils.TeamModule.TEAM_CREATE).navigation(mapActivity, REQUEST_CREATE_JOIN)
            } else {
                ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(mapActivity, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        mapActivity.finish()
                    }
                })
            }
        }
    }

    override fun CheckTeamStatusError(ex: Throwable) {
        mapActivity.dismissProgressDialog()
        mapActivity.mViewModel?.selectTab(0)
        Toast.makeText(context, getString(R.string.net_error), Toast.LENGTH_SHORT).show()
    }

    lateinit var status: DriverDataStatus
    //骑行控制器
    var driverController = DriverController(this)
    //地图控制器
    var share = ShareEntity()
    var deivceInfo = UIdeviceInfo(ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""))
    //上传数据传递
    var shareUpLoad = UpDataDriverEntitiy()
    //地图选点控制器

    //地图工具类
    var showShareInfo = ObservableField<Boolean>(false)

    //右侧位置消失隐藏控制
    var linearBg = ObservableField<Int>(Color.TRANSPARENT)
    //距离文本
    var driverDistance = ObservableField<String>("0M")
    //时间文本
    var driverTime = ObservableField<String>("00:")
    //开始骑行请求结果

    //骑行头像
    var driverAvatar = ObservableField<String>()
    //骑行用户昵称文本
    var userName = ObservableField<String>()
    //结束时间文本
    var finishTime = ObservableField<String>()
    //底部布局显示参数
    var bottomLayoutVisible = ObservableField<Boolean>(true)
//    var component = DriverComponent()


    var recycleComponent = SimpleRecycleComponent(this)

    lateinit var pointAdapter: AddPointAdapter

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var weatherDatas = ObservableArrayList<WeatherEntity>().apply {
        for (i in 0..24) {
            if (i < 10) {
                this.add(WeatherEntity(ObservableField(context.getDrawable(R.drawable.ic_sun)), ObservableField("0$i:00"), ObservableField("14℃")))
            } else {
                this.add(WeatherEntity(ObservableField(context.getDrawable(R.drawable.ic_sun)), ObservableField("$i:00"), ObservableField("14℃")))
            }
        }
    }


    var StringDispose: Disposable? = null

    constructor() {
        StringDispose = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "startNavigation") {
                status?.curModel = 1
                startNavigation()
            } else if (it == "NavigationFinish") {
                status.navigationType = 0
                status.passPointDatas.clear()
            } else if (it == "fastTeam") {
                mapActivity.mViewModel?.selectTab(1)
            }
        }
        RxSubscriptions.add(StringDispose)
    }

    var CurrentClickTime = 0L
    @SuppressLint("MissingPermission")
    override fun FiveBtnClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        when (view.id) {
            R.id.sos_btn -> {
                var intent = Intent(Intent.ACTION_CALL)
                var data = Uri.parse("tel:120")
                intent.data = data
                mapActivity.startActivity(intent)
            }
            R.id.camera_btn -> {

            }
            R.id.change_map_point -> {
                changeMap_Point_btn()
            }
            R.id.team_btn -> {
                if (mapActivity.mViewModel?.currentPosition == 1) {
                    mapActivity.mViewModel?.selectTab(0)
//                    mapActivity.getTeamFragment().viewModel?.backToDriver()
                } else {
                    mapActivity.mViewModel?.selectTab(1)
//                    GoTeam()
                }
            }
            R.id.setting_btn -> {
                if (fr.curPoint != null) {
                    mapActivity.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(fr.curPoint!!.latitude, fr!!.curPoint!!.longitude), 15F), 1000, object : AMap.CancelableCallback {
                        override fun onFinish() {
                        }

                        override fun onCancel() {
                        }
                    })
                }
            }
            R.id.road_book -> {
//                recycleComponent.initDatas(mapActivity.getRoadBookFragment().road_book_hori_linear)
                if (fr.curPosition != null) {
                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT, fr.curPosition).navigation(mapActivity, REQUEST_LOAD_ROADBOOK)
                }
            }
        }
    }

    var TeamStatus: SoketTeamStatus? = null
    fun GoTeam() {
        if (mapActivity.getTeamFragment()?.viewModel?.minaConnectedDispose == null|| mapActivity.getTeamFragment()?.viewModel?.minaConnectedDispose!!.isDisposed) {
            mapActivity.getTeamFragment()?.viewModel?.setMina()
        }
        if (TeamStatus == null || TeamStatus?.teamStatus == TeamNomal) {
            //开始组队
            HttpRequest.instance.setCheckStatusResult(this)
//            ARouter.getInstance().build(RouterUtils.TeamModule.TEAM_CREATE).navigation(mapActivity, REQUEST_CREATE_JOIN)
            mapActivity.showProgressDialog(getString(R.string.http_loading))
            var map = HashMap<String, String>()
            Log.e("result", "获取网络请求")
            HttpRequest.instance.checkTeamStatus(map)
        } else if (TeamStatus?.teamStatus == TeamStarting) {
            //直接加入
            mapActivity.mViewModel?.component!!.isTeam.set(true)
            if (status.startDriver.get() == 2) {
                status.startDriver.set(TeamModel)
            }
            mapActivity.showProgressDialog(getString(R.string.http_loading))
            mapActivity.mViewModel?.component!!.Drivering.set(true)
            var team = mapActivity?.mViewModel?.mFragments!![1] as TeamFragment
            team.viewModel?.initInfo()
//            if (team?.viewModel?.markerList?.size != 0) {
//                team?.viewModel?.markerListNumber?.forEach {
//                    team?.viewModel?.markerList!![it]?.isVisible = true
//                }
//            }
            mapActivity?.mViewModel?.changerFragment(1)
        }
//        else if (TeamStatus?.teamStatus == TeamOffline) {
//
//            if (status.startDriver.get() == 2) {
//                status.startDriver.set(TeamModel)
//            }
//            component.isTeam.set(true)
//            component.Drivering.set(true)
//            HttpRequest.instance.setCheckStatusResult(this)
//            var map = HashMap<String, String>()
//            if (TeamStatus?.teamCreate != null) {
//                map["teamCode"] = TeamStatus?.teamCreate?.teamCode!!
//            } else if (TeamStatus?.teamJoin != null) {
//                map["teamCode"] = TeamStatus?.teamJoin?.teamCode!!
//            }
//            mapActivity.showDialog(getString(R.string.http_loading))
//            HttpRequest.instance.checkTeamStatus(map)
//        }
        backStatus = false
    }

    var isCanclePrepare = false
    fun changeMap_Point_btn() {
        if (mapActivity.mViewModel?.currentPosition == 1) {
//            mapActivity.mViewModel?.changerFragment(0)
            mapActivity.getTeamFragment()?.viewModel?.backToDriver()
            backStatus = true
        }

        if (status.startDriver.get() == Drivering) {
//                    startDriver.set(DriverType.DrivingContinue)
//                    dontHaveOneMetre(getString(R.string.onDriverModel), getString(R.string.cancle), getString(R.string.confirm), 1)

            UiChange(1)
        } else if (status.startDriver.get() == DriverCancle) {
            UiChange(1)
        } else if (status.startDriver.get() == DriverPause) {
            UiChange(1)
        }
    }

    private fun changeMap() {
        if (status.navigationStartPoint == null) {
            if (status.locationLat.size == 0) {
                return
            }
            status.navigationStartPoint = status.locationLat[status.locationLat.size - 1]
        }
        mapActivity.mAmap.clear()
        mapActivity.getMapPointFragment().viewModel?.mapPointController!!.startMaker =  mapActivity.mapUtils?.createMaker(Location(status.navigationStartPoint!!.latitude,status.navigationStartPoint!!.longitude))
//        mapActivity.getMapPointFragment().viewModel?.mapPointController!!.startMaker = mapActivity.mapUtils?.createAnimationMarker(false, LatLonPoint(status.navigationStartPoint!!.latitude, status.navigationStartPoint!!.longitude))
        mapActivity.getMapPointFragment().viewModel?.mapPointController!!.screenMaker = mapActivity.mapUtils?.createScreenMarker()
        //添加可移动marker
        mapActivity.mapUtils?.queryGeocoder(LatLonPoint(status.navigationStartPoint!!.latitude, status.navigationStartPoint!!.longitude))
    }

    fun UiChange(i: Int) {
        if (i == 1) {
            //跳到地图选点
            if (status.navigationType == 1) {
//                ARouter.getInstance().build(RouterUtils.MapModuleConfig.NAVIGATION).navigation()
                var list = ArrayList<LatLng>()
                if (!status.passPointDatas.isEmpty()) {
                    status.passPointDatas.forEach {
                        list.add(LatLng(it.latitude, it.longitude))
                    }
                }
                startNavi(status!!.navigationStartPoint!!, status!!.navigationEndPoint!!, list, false)
            } else {
                if (status.startDriver.get() == Drivering && status.locationLat.size == 0) {
                    return
                }
                mapActivity.mViewModel?.changerFragment(2)
//                component.type.set(0)
//                component.title.set(getString(R.string.line_route))
//                component.rightVisibleType.set(true)
//                component.rightText.set(getString(R.string.road_detail))
//                status.curModel = 1
                changeMap()
            }
        } else {
            //回到骑行页面
        }
    }


    fun onComponentClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 1500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        if (mapActivity.mViewModel?.component!!.type.get() == 1) {
            if (status.startDriver.get() == Drivering || status.startDriver.get() == DriverPause || BaseApplication?.MinaConnected!!) {
                if (!fr.isResultPoint) {
                    RxBus.default?.postSticky(status)
                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
                } else {
                    fr.isResultPoint = false
                    UiChange(0)
                }
            } else {
                if (mapActivity.resume != null) {
                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
                }
//                SPUtils.getInstance().put("Action", "start")
//                KeepLiveHelper.getDefault().setAction("start")
//                KeepLiveHelper.getDefault().startBindService(context)
                var pos = ServiceEven()
                pos.type = "HomeStart"
                RxBus.default?.post(pos)

//                context.startService(Intent(context, LowLocationService::class.java).setAction("start"))
                RxBus.default?.post(status)

                finish()
                timerDispose?.dispose()
                timer = null
            }
        } else {
            UiChange(0)
        }
        CoroutineScope(uiContext).launch {
            delay(200)
            RxBus.default?.post(ActivityResultEven(CALL_BACK_STATUS, status.startDriver.get()!!))
        }
    }

    fun onComponentFinish(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        ComponentFinish()
    }


    fun ComponentFinish() {
        if (status.curModel == 0) {
            ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).withInt(RouterUtils.MapModuleConfig.SEARCH_MODEL, status.curModel).navigation(mapActivity, RESULT_POINT)
        } else if (status.curModel == 1) {

        }
    }


    fun onClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        when (view.id) {
//            R.id.bottom_navigation_btn -> {
//                changeMap_Point_btn()
//            }
//            R.id.bottom_team_btn -> {
//                GoTeam()
//            }
            R.id.continue_drivering -> {
                status.startDriver.set(Drivering)
            }
            R.id.route_click -> {
                if (deivceInfo != null && deivceInfo?.id?.get() != null) {
                    Log.e("result", Gson().toJson(deivceInfo))
                    var even = NomalPostStickyEven(106, deivceInfo!!)
                    ARouter.getInstance().build(RouterUtils.LogRecodeConfig.PLAYER).navigation()
                    RxBus.default?.postSticky(even)
                }
            }
            R.id.start_navagation -> {
                startNavigation()
            }
            R.id.stop_navagation -> {

            }
            R.id.team_btn -> {
                if (mapActivity.mViewModel?.currentPosition == 1) {
                    mapActivity.getTeamFragment().viewModel?.backToDriver()
                } else {
                    GoTeam()
                }
            }
            R.id.long_press_btn -> {
                if (BaseApplication?.MinaConnected!!) {
                    if (status.startDriver.get() != DriverCancle) {
                        var model = mapActivity.getTeamFragment().viewModel
                        if (status.distance < 1000) {
                            //小于一公里
                            //是否是队长  //成员人数
                            if (model?.user?.data?.memberId == model?.teamer.toString()) {
                                //是队长
                                if (model?.TeamInfo?.redisData?.dtoList?.size == 1) {
                                    //只有自己一人
                                    dontHaveOneMetre(org.cs.tec.library.Base.Utils.getString(R.string.TeamnotEnoughOneKm), org.cs.tec.library.Base.Utils.getString(R.string.release_team), org.cs.tec.library.Base.Utils.getString(R.string.continue_driving), 0)
                                } else {
                                    //多人
                                    dontHaveOneMetre(org.cs.tec.library.Base.Utils.getString(R.string.TeamernotEnoughOneKmAndOther), org.cs.tec.library.Base.Utils.getString(R.string.release_team), org.cs.tec.library.Base.Utils.getString(R.string.pass_timer), 1)
                                }
                            } else {
                                //不是队长
                                //直接退出队伍
                                dontHaveOneMetre(org.cs.tec.library.Base.Utils.getString(R.string.TeamnotEnoughOneKmBymember), org.cs.tec.library.Base.Utils.getString(R.string.leave_team), org.cs.tec.library.Base.Utils.getString(R.string.continue_driving), 2)
                            }
                        } else {
                            //超过一公里
                            if (model?.user?.data?.memberId == model?.teamer.toString()) {
                                //队长
                                if (model?.TeamInfo?.redisData?.dtoList?.size == 1) {
                                    //只有自己一人
                                    dontHaveOneMetre(org.cs.tec.library.Base.Utils.getString(R.string.EnoughOneKmByTeamerAndOne), org.cs.tec.library.Base.Utils.getString(R.string.leave_team), org.cs.tec.library.Base.Utils.getString(R.string.continue_driving), 3)
                                } else {
                                    //多人
                                    dontHaveOneMetre(org.cs.tec.library.Base.Utils.getString(R.string.EnoughOneKmByTeamerAndPass), org.cs.tec.library.Base.Utils.getString(R.string.release_team), org.cs.tec.library.Base.Utils.getString(R.string.pass_timer), 4)
                                }
                            } else {
                                //不是队长
                                dontHaveOneMetre(org.cs.tec.library.Base.Utils.getString(R.string.EnoughOneKmByTeam), org.cs.tec.library.Base.Utils.getString(R.string.leave_team), org.cs.tec.library.Base.Utils.getString(R.string.continue_driving), 5)
                            }
                        }
                    }
                } else {
                    if (status.curModel == 0) {
                        if (status.startDriver.get() != DriverCancle) {
                            //取消骑行
//                        mapActivity.queryTrack()
                            //结束骑行
                            if (status.distance < 1000) {
                                dontHaveOneMetre(org.cs.tec.library.Base.Utils.getString(R.string.notEnoughOneKm), org.cs.tec.library.Base.Utils.getString(R.string.cancle_riding), org.cs.tec.library.Base.Utils.getString(R.string.continue_driving), 0)
                            } else {
                                driverController.driverOver()
                            }
                        }
                    }
                }
            }
        }
    }

    var timer: Observable<Long>? = null
    var timerDispose: Disposable? = null
    var backStatus: Boolean = false
    fun startNavigation() {
        if (mapActivity.mViewModel?.currentPosition == 0 || mapActivity.mViewModel?.currentPosition == 1) {
            if (status.startDriver.get() == Drivering) {
                status.startDriver.set(DriverPause)
            } else if (status.startDriver.get() == DriverCancle || status.startDriver.get() == TeamModel) {
                startDrive(false)
            } else if (status.startDriver.get() == DriverPause) {
                status.startDriver.set(Drivering)
            }
        } else if (mapActivity.mViewModel?.currentPosition == 2) {
            if (status.startDriver.get() == DriverCancle || status.startDriver.get() == TeamModel) {
                var wayPoint = ArrayList<LatLng>()
                status.passPointDatas.forEach {
                    wayPoint.add(LatLng(it.latitude, it.longitude))
                }
                status.navigationStartPoint = fr.curPoint
                startDrive(true)
            } else {
                if (status.startDriver.get() == Prepare_Navigation) {
                    if (isCanclePrepare) {
                        var wayPoint = ArrayList<LatLng>()
                        status.passPointDatas.forEach {
                            wayPoint.add(LatLng(it.latitude, it.longitude))
                        }
                        status.navigationStartPoint = fr.curPoint
                        startDrive(true)
                    }
                }
                DriverNavigation()
            }
        }
    }

    fun DriverNavigation() {
        if (status.locationLat.size == 0) {
            if (fr.curPoint != null) {
                status.locationLat.add(fr.curPoint)
                fr.addStartPoint(fr.curPosition!!)
            } else {
                return
            }
        }
        status?.NavigationEndAoiName = mapActivity.getMapPointFragment().viewModel?.finalyText!!.get()
        status.startDriver.set(Drivering)
        //开启导航
        var wayPoint = ArrayList<LatLng>()
        status.passPointDatas.forEach {
            wayPoint.add(LatLng(it.latitude, it.longitude))
        }
        var star: Location
        if (fr.locationDatas.size != 0) {
            star = fr.locationDatas[fr.locationDatas.size - 1]
            if (star.aoiName != null && !star.aoiName.isEmpty()) {
                status?.NavigationStartAoiName = star.aoiName
            } else {
                status?.NavigationStartAoiName = star.poiName
            }
        }
//        s.viewModel?.startNavi(NaviLatLng(mapPointController!!.startPoint!!.latitude, mapPointController.startPoint!!.longitude), NaviLatLng(mapPointController.endPoint!!.latitude, mapPointController!!.endPoint!!.longitude), wayPoint)
        startNavi(status.navigationStartPoint!!, status.navigationEndPoint!!, wayPoint, false)
        status.passPointDatas.clear()
        mapActivity.getMapPointFragment().viewModel?.mapPointController!!.finallyMarker?.remove()
        mapActivity.getMapPointFragment().viewModel?.mapPointController!!.finallyMarker = null
    }

    fun startDrive(flag: Boolean) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
            return
        }
        var token = PreferenceUtils.getString(org.cs.tec.library.Base.Utils.context, USER_TOKEN)
        CoroutineScope(uiContext).launch {
            var pro = mapActivity.showProgressDialog(getString(R.string.start_driver))
            delay(20000)
            if (fr.isAdded && pro != null && pro!!.isShowing) {
                var location = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (location.isProviderEnabled(LocationManager.GPS_PROVIDER) && location.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    if (Build.VERSION.SDK_INT >= 23 && mapActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, getString(R.string.permisstion_error), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, getString(R.string.exception_time_out_error), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, getString(R.string.get_point_time_out), Toast.LENGTH_SHORT).show()
                    var intent = Intent()
                    intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    mapActivity.startActivity(intent)
                }
                cancleDriver(true)
                mapActivity.dismissProgressDialog()
            }
        }
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiRiding/ridingManage/recordRidingData").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(mapActivity) {
            override fun onNext(t: String) {
                super.onNext(t)
                var k = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
                if (k.code == 0) {
                    insertDriverStatus(status)
                    status.driverNetRecord = Gson().fromJson<StartRidingRequest>(Gson().toJson(k.data), StartRidingRequest::class.java)
                    status.startDriver.set(Drivering)
                    //设置骑行起始点
                    timer = Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    linearBg.set(Color.WHITE)
                    status.StartTime = System.currentTimeMillis()
                    mapActivity.mViewModel?.component!!.Drivering.set(true)
                    timerDispose = timer?.subscribe {
                        status.second++
//                        status.saveAsLocal()
                        driverTime.set(ConvertUtils.formatTimeS(status.second))
                        UpdateDriverStatus(status)
//                        s.viewModel?.totalTime?.set(ConvertUtils.formatTimeS(status.second))
                    }
                    status.onDestroyStatus = 2

                    if (flag) {
                        if (status.navigationStartPoint != null && status.navigationEndPoint != null) {
                            var wayPoint = ArrayList<LatLng>()
                            if (!status.passPointDatas!!.isEmpty()) {
                                status.passPointDatas.forEach {
                                    wayPoint.add(LatLng(it.latitude, it.longitude))
                                }
                            }
                            startNavi(status.navigationStartPoint!!, status.navigationEndPoint!!, wayPoint, false)
                        } else {
                            DriverNavigation()
                        }
                    }
                } else {
                    cancleDriver(true)
                    Toast.makeText(context, k.msg, Toast.LENGTH_SHORT).show()
                }
                if (flag) {
                    mapActivity.dismissProgressDialog()
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                cancleDriver(true)
                mapActivity.dismissProgressDialog()
            }
        })
    }

    fun dontHaveOneMetre(cotent: String, leftBtnTv: String, rightBtnTv: String, type: Int) {
        var dia = DialogUtils.createNomalDialog(mapActivity, cotent, leftBtnTv, rightBtnTv)
        dia.setOnBtnClickL(OnBtnClickL {
            dia.dismiss()
            if (BaseApplication.MinaConnected!!) {
                var fr = mapActivity?.mViewModel?.mFragments!![1] as TeamFragment
                fr.viewModel?.endTeam(true)
                if (status.distance > 1000) {
                    driverController.driverOver()
                } else {
                    deleteDriverStatus(PreferenceUtils.getString(context, USERID))
                    cancleDriver(true)
                }
            } else {
                deleteDriverStatus(PreferenceUtils.getString(context, USERID))
                cancleDriver(true)
            }
            dia.dismiss()

        }, OnBtnClickL {
            if (BaseApplication?.MinaConnected!!) {
                if (type == 1 || type == 4) {
                    ARouter.getInstance().build(RouterUtils.TeamModule.TEAMER_PASS).withSerializable(RouterUtils.TeamModule.TEAM_INFO, mapActivity.getTeamFragment().viewModel?.TeamInfo).navigation()
                }
            }
            dia.dismiss()
        })
        dia.show()
    }

    fun cancleDriver(flag: Boolean) {
        status.startDriver.set(DriverCancle)
        if (flag) {
            mapActivity.mAmap.clear()
        }
        driverController.cancleDriver()
        timerDispose?.dispose()
        timerDispose = null
        timer = null
        mapActivity.mViewModel?.component!!.Drivering.set(true)
        mapActivity.mLocationOption?.isSensorEnable = false
        mapActivity.mlocationClient?.setLocationOption(mapActivity.mLocationOption)
        mapActivity.myLocationStyle.showMyLocation(true)
        mapActivity.mAmap.isMyLocationEnabled = true
        mapActivity.mAmap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        mapActivity.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        mapActivity.mAmap.myLocationStyle = mapActivity.myLocationStyle
        linearBg.set(Color.TRANSPARENT)
        status.reset()
        driverDistance.set("00:00")
        RxBus.default?.post("DriverCancle")
    }

    lateinit var fr: DriverFragment
    lateinit var mapActivity: MapActivity


    var roadbookModel: RoadBookViewModel? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun inject(fr: DriverFragment) {
        this.mapActivity = fr.activity as MapActivity
        this.fr = fr
        roadbookModel = this.mapActivity.getRoadBookFragment().viewModel
        mapActivity.mViewModel?.component!!.setHomeStyle()
//          mapActivity.mViewModel?.component!!.setCallBack(this)
//        component.setOnFiveClickListener(this)

    }


    var alertDialog: AlertDialog? = null

    fun addChart(chart: SuitLines?, list: ArrayList<Double>, time: Long) {
        if (!list.isNotEmpty() && list.size < 6) {
            return
        }
        var k = 0
        var size = list.size
        if ((size - 1) % 15 != 0) {
            k = (size - 1) % 15
        }
        var n = (size - k) / 15
        if (n == 0) {
            return
        }
//        fr.basesult.maxOfVisible = n * 6
        var baseTime = time.toInt() / 15.0
        var count = 0
        var lines = ArrayList<Unit>()
        for (i in 0..(size - 1)) {
            if (i % n == 0) {
                count++
                var t = Unit(list[i].toFloat() + 1, (DecimalFormat("0.0").format(baseTime * count) + "s"))
                lines.add(t)
            } else if (i == size - 1) {
                var t = Unit(list[i].toFloat() + 1, "")
                lines.add(t)
            } else {
                lines.add(Unit(list[i].toFloat() + 1))
            }
        }
        chart?.feed(lines)
    }

    fun addChildView(layout: LinearLayout?) {
        weatherDatas.forEachIndexed { i, entity ->
            var inflater = mapActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.horizontal_weather_child, layout, false)
            var img = view.findViewById<ImageView>(R.id.weather_icon)
            var time = view.findViewById<TextView>(R.id.weather_time)
            var temperature = view.findViewById<TextView>(R.id.weather_temperature)
            img.setImageDrawable(entity.icon.get())
            time.text = entity.time.get()
            temperature.text = entity.temperature.get()
            layout?.addView(view)
            layout?.invalidate()
        }
    }


    fun startNavi(s: Location, e: Location, wayPoint: ArrayList<LatLng>, flag: Boolean) {
        //跳往导航页面
        isCanclePrepare = false
        if (s == null || e == null) {
            return
        }
        var list = ArrayList<LatLng>()
        list.add(LatLng(s.latitude, s.longitude))
        if (wayPoint != null && wayPoint.size != 0) {
            wayPoint.forEach {
                list.add(it)
            }
        }
        list.add(LatLng(e.latitude, e.longitude))
        status.startDriver.set(Drivering)
        linearBg.set(Color.WHITE)
        if (status.locationLat.isEmpty()) {
            status.locationLat.add(s)
            fr.addStartPoint(s)
        }
        if (!flag && mapActivity.getTeamFragment()?.isAdded && mapActivity.getTeamFragment()?.viewModel?.TeamInfo != null) {
            if (mapActivity.getTeamFragment()?.viewModel?.TeamInfo?.redisData?.teamer.toString() == PreferenceUtils.getString(context, USERID) && status.navigationType == 0) {
                //队长
                mapActivity.getTeamFragment()?.viewModel?.sendNavigationNotify()
            }
        }
        status.navigationType = 1
        if (mapActivity.mViewModel?.currentPosition == 2) {
            mapActivity.getMapPointFragment().viewModel?.returnDriverFr()
        }
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.NAVIGATION)
                .withSerializable(RouterUtils.MapModuleConfig.NAVIGATION_DATA, list)
                .withInt(RouterUtils.MapModuleConfig.NAVIGATION_TYPE, status.navigationType)
                .withFloat(RouterUtils.MapModuleConfig.NAVIGATION_DISTANCE, status.navigationDistance)
                .withLong(RouterUtils.MapModuleConfig.NAVIGATION_TIME, status.navigationTime)
                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                .navigation()
    }
}