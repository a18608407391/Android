package com.example.drivermodule.Controller

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.SoketBody.CreateTeamInfoDto
import com.elder.zcommonmodule.Entity.SoketBody.SoketTeamStatus
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Widget.LoginUtils.BaseDialogFragment
import com.elder.zcommonmodule.Widget.LoginUtils.LoginDialogFragment
import com.example.drivermodule.Component.DriverItemController
import com.example.drivermodule.R
import com.example.drivermodule.Sliding.SlidingUpPanelLayout
import com.example.drivermodule.Fragment.MapFragment
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.google.gson.Gson
import com.zk.library.Base.BaseApplication
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.DataBases.*
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonnelInfoDto
import com.example.drivermodule.Fragment.SearchActivity
import com.example.drivermodule.Utils.AMapUtil
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.Utils.ToastUtils
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class DriverItemModel : ItemViewModel<MapFrViewModel>(), HttpInteface.CheckTeamStatus, HttpInteface.JoinTeamResult {
    override fun JoinTeamSuccess(it: String) {
        if (it.isNullOrEmpty()) {
            Toast.makeText(mapFr.activity, "队伍解散或已失效", Toast.LENGTH_SHORT).show()
            viewModel.selectTab(0)
            return
        }
        var info = Gson().fromJson<TeamPersonnelInfoDto>(it, TeamPersonnelInfoDto::class.java)
        info.teamCode = mapFr?.teamCode
        mapFr?.getTeamController().join = info
//        viewModel?.selectTab(1)
        if (viewModel?.currentPosition == 0) {
            viewModel?.changerFragment(1)
        }
        mapFr?.getTeamController().startMinaService()
    }

    override fun JoinTeamError(ex: Throwable) {

    }

    override fun onDismiss(fr: BaseDialogFragment, value: Any) {
        if (fr is LoginDialogFragment) {
            mapFr.showProgressDialog(getString(R.string.http_loading))
            HttpRequest.instance.setCheckStatusResult(this)
            var map = HashMap<String, String>()
            HttpRequest.instance.checkTeamStatus(map)
            dialogFragment!!.functionDismiss = null
        }
        super.onDismiss(fr, value)
    }

    //                HttpRequest.instance.JoinResult = this
//                var map = HashMap<String, String>()
//                map["teamCode"] = mapFr!!.teamCode!!
//                map["joinAddr"] = viewModel?.curPosition?.latitude.toString() + "," + viewModel?.curPosition?.longitude.toString()
//                HttpRequest.instance.JoinTeam(map)
    override fun CheckTeamStatusSucccess(it: BaseResponse) {
        var info = Gson().fromJson<CreateTeamInfoDto>(Gson().toJson(it.data), CreateTeamInfoDto::class.java)
        if (it.code == 0) {
            viewModel.TeamStatus = SoketTeamStatus()
            mapFr.getTeamController().create = info
            viewModel?.changerFragment(1)
            startMinaService()
            if (mapFr.teamCode != null && info.teamCode != mapFr.teamCode) {
                Toast.makeText(mapFr.activity!!, "已有队伍，请退出队伍后，手动加入！", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (it.code == 20001) {
                //队伍不存在
                Log.e("result", "TeamColde" + mapFr.teamCode)
                viewModel.TeamStatus = SoketTeamStatus()
                if (mapFr.teamCode == null) {
                    var bundle = Bundle()
                    bundle.putSerializable(RouterUtils.MapModuleConfig.START_LOCATION, viewModel?.curPosition)
                    viewModel?.startFragment(mapFr.parentFragment!!, RouterUtils.TeamModule.TEAM_CREATE, bundle, REQUEST_CREATE_JOIN)
                } else {
                    HttpRequest.instance.JoinResult = this
                    var map = HashMap<String, String>()
                    map["teamCode"] = mapFr!!.teamCode!!
                    map["joinAddr"] = viewModel?.curPosition?.latitude.toString() + "," + viewModel?.curPosition?.longitude.toString()
                    HttpRequest.instance.JoinTeam(map)
                }
            } else if (it.code == 10009) {
//                showLoginDialogFragment(mapFr)
            }
        }
        mapFr.dismissProgressDialog()
    }

    override fun CheckTeamStatusError(ex: Throwable) {
        mapFr.dismissProgressDialog()
    }

    fun onLocation(location: AMapLocation) {
        location(location)
    }

    var timer: Observable<Long>? = null
    var timerDispose: Disposable? = null

    var panelState = ObservableField<SlidingUpPanelLayout.PanelState>(SlidingUpPanelLayout.PanelState.HIDDEN)


    //骑行界面逻辑处理
    var shareUpLoad = UpDataDriverEntitiy()


    var chart = ObservableField<DriverDataStatus>()
    //距离文本
    var driverDistance = ObservableField<String>("0M")
    //时间文本
    var driverTime = ObservableField<String>("00:00")
    //开始骑行请求结果
    var bottomLayoutVisible = ObservableField<Boolean>(true)
    //骑行用户昵称文本
    var userName = ObservableField<String>()
    //结束时间文本
    var finishTime = ObservableField<String>()
    //骑行头像
    var deivceInfo = UIdeviceInfo(ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""), ObservableField(""))

    var driverAvatar = ObservableField<String>()
    var share = ShareEntity()

    var driverStatus = ObservableField(DriverCancle)


    lateinit var mapFr: MapFragment

    var driverController = DriverItemController(this)


    override fun ItemViewModel(viewModel: MapFrViewModel): ItemViewModel<MapFrViewModel> {
        this.viewModel = viewModel
        mapFr = viewModel?.mapActivity
        mapFr.showProgressDialog(getString(R.string.location_loading))
        driverStatus.set(viewModel?.status.startDriver.get())
        initView(viewModel)
        bottomLayoutVisible.set(true)
        return super.ItemViewModel(viewModel)
    }

    var curAmapLocation: AMapLocation? = null

    var isUp = false
    var curHeight = 0.0
    var last: Location? = null

    private fun location(amapLocation: AMapLocation?) {
        //处理定位信息

        if (amapLocation != null && amapLocation.errorCode == 0) {
            mapFr.mListener?.onLocationChanged(amapLocation)// 显示系统小蓝点
            if (this.curAmapLocation == null) {
                mapFr.mAmap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(amapLocation.latitude, amapLocation.longitude)))
                viewModel?.mapActivity.dismissProgressDialog()
            }
            this.curAmapLocation = amapLocation
            viewModel?.curPosition = Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName)
            if (driverStatus.get() != DriverCancle) {
                if (viewModel.status.locationLat.size == 0) {
                    insertLocation(viewModel?.curPosition!!, mapFr?.user.data?.memberId!!)
                    addStartPoint(viewModel?.curPosition!!)
                    last = viewModel?.curPosition!!
                } else {
                    if (amapLocation.locationType == 1 || amapLocation.locationType == 5) {
                        if (curHeight < amapLocation.altitude) {
                            if (!isUp) {
                                isUp = true
                            }
                            viewModel?.status.UpValue += amapLocation.altitude - curHeight
                            viewModel?.status.UpCount++
                            if (curHeight > viewModel?.status.maxHeight) {
                                viewModel?.status.maxHeight = curHeight
                            }
                        } else {
                            //爬坡结束
                            if (isUp) {
                                isUp = false
                            }
                        }
                        curHeight = amapLocation.altitude
                        if (amapLocation.accuracy <= 30 && amapLocation.speed > 1) {
                            if (viewModel?.status.maxSpeed < amapLocation.speed) {
                                viewModel?.status.maxSpeed = amapLocation.speed
                            }

                            if (NeedPauseMath && PauseMath == 2 && isPauseDriver) {
                                //暂停以后再骑行  距离重新运算
                                //当前最后一个点，与下一个点不作为计算距离的指标
                                last = viewModel?.curPosition
                                isPauseDriver = false
                                PauseMath = 0
                            } else {
                                if (last == null) {
                                    last = viewModel?.status.locationLat.last()
                                }
                                viewModel?.status.distance += AMapUtils.calculateLineDistance(LatLng(last?.latitude!!, last?.longitude!!), LatLng(amapLocation?.latitude!!, amapLocation?.longitude!!))
                                last = viewModel?.curPosition
                            }

                            var distanceTv = ""
                            if (viewModel?.status!!.distance > 1000) {
                                distanceTv = DecimalFormat("0.0").format(viewModel?.status!!.distance / 1000) + "KM"
                            } else {
                                distanceTv = DecimalFormat("0.0").format(viewModel?.status!!.distance) + "M"
                            }
                            driverDistance.set(distanceTv)
                            viewModel.status.locationLat.add(viewModel?.curPosition!!)
                            viewModel?.mapActivity.mapUtils!!.setLocation(Location(mapFr.mAmap.myLocation.latitude, mapFr.mAmap.myLocation.longitude))
                            if (viewModel?.currentPosition == 0) {
                                mapFr.mAmap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(amapLocation.latitude, amapLocation.longitude)))
                            }
                            driverController?.setLineDatas(viewModel?.status?.locationLat, getColor(R.color.line_color))
                        }
                    }
                }
            } else {
//                if (mapFr.mAmap != null) {
//                    mapFr?.myLocationStyle?.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
//                    mapFr!!.mAmap.myLocationStyle = mapFr!!.myLocationStyle
//                }
            }
        }
    }

    var isPauseDriver = false   //骑行暂停后
    var PauseMath = 0        //骑行暂停
    var NeedPauseMath = false
    private fun addStartPoint(amapLocation: Location) {
        mapFr.dismissProgressDialog()
        mapFr.setDriverStyle()
        mapFr.mAmap.clear()
        viewModel?.status?.locationLat?.add(amapLocation)
        mapFr?.mapUtils?.createAnimationMarker(true, LatLonPoint(amapLocation.latitude, amapLocation.longitude))
//        mapActivity.getMapPointFragment().viewModel?.mapPointController?.startMaker = mapFr?.mapUtils!!.createMaker(amapLocation)
        driverController?.startMarker = mapFr?.mapUtils!!.createMaker(amapLocation)
        if (amapLocation?.aoiName != null && !amapLocation?.aoiName.isEmpty()) {
            viewModel?.status?.startAoiName = amapLocation?.aoiName
        } else {
            viewModel?.status?.startAoiName = amapLocation?.poiName
        }
        viewModel?.status?.driverStartPoint = amapLocation
        UpdateDriverStatus(viewModel?.status!!)
    }

    private fun initView(model: MapFrViewModel) {
        //进入方式判断
        if (viewModel.status?.driverStartPoint != null) {
            if (viewModel?.status?.locationLat.isNullOrEmpty()) {
                viewModel?.status?.locationLat.add(viewModel?.status?.driverStartPoint)
            }
            var end = viewModel?.status?.locationLat.last()
            if (driverStatus.get() == DriverPause) {
                isPauseDriver = true
                PauseMath = 2
            } else if (driverStatus.get() == Drivering) {
                //异常退出
                isPauseDriver = false
            }
            last = viewModel?.status?.locationLat.last()
            var distanceTv = ""
            if (viewModel?.status!!.distance > 1000) {
                distanceTv = DecimalFormat("0.0").format(viewModel?.status!!.distance / 1000) + "KM"
            } else {
                distanceTv = DecimalFormat("0.0").format(viewModel?.status!!.distance) + "M"
            }
            CoroutineScope(uiContext).launch {
                mapFr.mAmap.clear()
                mapFr.setDriverStyle()
                driverController?.startMarker = mapFr.mapUtils?.createMaker(viewModel?.status?.driverStartPoint!!)
                mapFr?.mapUtils?.createAnimationMarker(true, LatLonPoint(end.latitude, end.longitude))
//                mapFr.mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapFr?.mapUtils?.breatheMarker_center?.position, 15F))
                driverDistance!!.set(distanceTv)
                driverController?.setLineDatas(viewModel?.status?.locationLat!!, getColor(R.color.line_color))
            }
            timer = Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            timerDispose = timer?.subscribe {
                if (driverStatus.get() != DriverPause) {
                    viewModel?.status!!.second++
                    driverTime!!.set(ConvertUtils.formatTimeS(viewModel?.status!!.second))
                    UpdateDriverStatus(viewModel?.status!!)
                }
            }
        }
        if (viewModel?.mapActivity.resume == "road") {
            CoroutineScope(uiContext).launch {
                delay(200)
                viewModel?.selectTab(2)
            }
        } else if (viewModel?.mapActivity.resume == "cancle") {
            CoroutineScope(uiContext).launch {
                delay(500)
                if (viewModel?.status!!.distance > 1000) {
                    driverController.driverOver()
                } else {
                    deleteDriverStatus(PreferenceUtils.getString(org.cs.tec.library.Base.Utils.context, USERID))
                    cancleDriver(true)
                }
            }
        }
        if (mapFr!!.teamCode != null) {
            if (viewModel?.curPosition != null) {
                CoroutineScope(uiContext).launch {
                    delay(500)
                    viewModel?.selectTab(1)
                }
            }
        }
//        mapFr.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.item_start_navagation -> {
                if (driverStatus.get() == Drivering) {
                    driverStatus.set(DriverPause)
                    viewModel.status.startDriver.set(DriverPause)
                    //点击了暂停
                    PauseMath = 1
                    mapFr.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
                    mapFr.mAmap.myLocationStyle = mapFr.myLocationStyle
//                    if (viewModel?.status.driverNetRecord!!.passPosition == null) {
//                        viewModel?.status.driverNetRecord!!.passPosition = ArrayList()
//                    }
//                    viewModel?.status.driverNetRecord!!.passPosition!!.add(LatLng(viewModel?.status.locationLat.last().latitude, viewModel?.status.locationLat.last().longitude))

                    UpdateDriverStatus(viewModel?.status)
                } else if (driverStatus.get() == DriverCancle || driverStatus.get() == TeamModel) {
                    if (viewModel.status.locationLat.size == 0) {
                        if (viewModel?.curPosition != null) {
                            addStartPoint(viewModel?.curPosition!!)
                            viewModel?.startDriver(0)
                        } else {
                            return
                        }
                    }
                }
            }
            R.id.item_long_press_btn -> {
                //取消骑行
                if (BaseApplication?.MinaConnected!!) {
                    if (viewModel?.status.startDriver.get() != DriverCancle) {
                        var model = viewModel?.items[1] as TeamItemModel
                        if (viewModel?.status.distance < 1000) {
                            //小于一公里
                            //是否是队长  //成员人数
                            if (mapFr?.user?.data?.memberId == model?.teamer.toString()) {
                                //是队长
                                if (model?.TeamInfo?.redisData?.dtoList?.size == 1) {
                                    //只有自己一人
                                    dontHaveOneMetre(getString(R.string.TeamnotEnoughOneKm), getString(R.string.release_team), getString(R.string.continue_driving), 0)
                                } else {
                                    //多人
                                    dontHaveOneMetre(getString(R.string.TeamernotEnoughOneKmAndOther), getString(R.string.release_team), getString(R.string.pass_timer), 1)
                                }
                            } else {
                                //不是队长
                                //直接退出队伍
                                dontHaveOneMetre(getString(R.string.TeamnotEnoughOneKmBymember), getString(R.string.leave_team), getString(R.string.continue_driving), 2)
                            }
                        } else {
                            //超过一公里
                            if (mapFr?.user?.data?.memberId == model?.teamer.toString()) {
                                //队长
                                if (model?.TeamInfo?.redisData?.dtoList?.size == 1) {
                                    //只有自己一人
                                    dontHaveOneMetre(getString(R.string.EnoughOneKmByTeamerAndOne), getString(R.string.leave_team), getString(R.string.continue_driving), 3)
                                } else {
                                    //多人
                                    dontHaveOneMetre(getString(R.string.EnoughOneKmByTeamerAndPass), getString(R.string.release_team), getString(R.string.pass_timer), 4)
                                }
                            } else {
                                //不是队长
                                dontHaveOneMetre(getString(R.string.EnoughOneKmByTeam), getString(R.string.leave_team), getString(R.string.continue_driving), 5)
                            }
                        }
                    }
                } else {
                    if (viewModel?.status.curModel == 0) {
                        if (viewModel?.status.startDriver.get() != DriverCancle) {
                            //取消骑行
                            //结束骑行
                            if (viewModel?.status.distance < 1000) {
                                dontHaveOneMetre(getString(R.string.notEnoughOneKm), getString(R.string.cancle_riding), getString(R.string.continue_driving), 0)
                            } else {
                                driverController.driverOver()
                            }
                        }
                    }
                }
            }

            R.id.item_continue_drivering -> {
                //继续骑行
                if (PauseMath == 1) {
                    PauseMath = 2
                }
                mapFr.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
                mapFr.mAmap.myLocationStyle = mapFr.myLocationStyle
                driverStatus.set(Drivering)
                viewModel.status.startDriver.set(Drivering)
            }
            R.id.route_click -> {
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.LogRecodeConfig.PLAYER_ENTITY, deivceInfo)
                viewModel?.startFragment(mapFr.parentFragment!!, RouterUtils.LogRecodeConfig.PLAYER, bundle)
            }
        }
    }

    fun converToUiDriver(info: DriverInfo): UIdeviceInfo {
        var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var d = Date(info.createTime)
        var u = ""
        if (info.ridingEndBackgroudImg != null) {
            u = info.ridingEndBackgroudImg!!
        }
        return UIdeviceInfo(ObservableField(simple.format(d)), ObservableField(getDriverImageUrl(info.trackImgUrl, info.baseUrl)), ObservableField(DecimalFormat("0.0").format(info?.totalDistance?.toDouble()!! / 1000)), ObservableField(ConvertUtils.formatTimeS(info?.totalTime!!)), ObservableField(DecimalFormat("0.0").format((info?.totalDistance!! * 3.6 / info!!.totalTime!!))), ObservableField(info.ridingFileUrl!!), ObservableField(u), ObservableField(info.id.toString()), ObservableField(info.baseUrl!!))
    }

    fun dontHaveOneMetre(cotent: String, leftBtnTv: String, rightBtnTv: String, type: Int) {
        var dia = DialogUtils.createNomalDialog(mapFr.activity!!, cotent, leftBtnTv, rightBtnTv)
        dia.setOnBtnClickL(OnBtnClickL {
            dia.dismiss()
            if (BaseApplication.MinaConnected!!) {
                var fr = viewModel?.items[1] as TeamItemModel
                fr?.endTeam(true)
                if (viewModel?.status.distance > 1000) {
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
                var fr = viewModel?.items[1] as TeamItemModel
                if (type == 1 || type == 4) {
                    var bundle = Bundle()
                    bundle.putSerializable(RouterUtils.TeamModule.TEAM_INFO, fr?.TeamInfo)
                    viewModel.startFragment(viewModel?.mapActivity.parentFragment!!, RouterUtils.TeamModule.TEAMER_PASS, bundle)
//                    ARouter.getInstance().build(RouterUtils.TeamModule.TEAMER_PASS).withSerializable(RouterUtils.TeamModule.TEAM_INFO, fr?.TeamInfo).navigation()
                }
            }
            dia.dismiss()
        })
        dia.show()
    }

    var isResultPoint = false

    fun setResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        Log.e("result", "onFragmentResult" + requestCode)
        when (requestCode) {
            RESULT_POINT -> {
                if (data != null) {
                    if (data?.getParcelable<PoiItem>("tip") != null) {
                        var tip = data?.getParcelable("tip") as PoiItem
                        if (tip != null) {
                            if (tip.latLonPoint?.latitude != null && tip.latLonPoint?.longitude != null) {
                                mapFr?.mAmap?.isMyLocationEnabled = false
                                isResultPoint = true
                                mapFr?.mAmap?.moveCamera(CameraUpdateFactory.changeLatLng(AMapUtil.convertToLatLng(tip.latLonPoint)))
                                var opotion = MarkerOptions().title(tip.title).snippet(getString(R.string.go_there)).position(LatLng(tip.latLonPoint.latitude, tip.latLonPoint.longitude))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                var makder = mapFr?.mAmap?.addMarker(opotion)
                                makder?.showInfoWindow()
                            }
                        }
                    }
                }
            }
        }
    }

    fun onComponentFinish() {
        if (viewModel?.status.navigationType > 0) {
            var list = ArrayList<LatLng>()
            viewModel?.status.passPointDatas.forEach {
                list.add(LatLng(it.latitude, it.longitude))
            }
            viewModel?.startNavi(list, 1)
        } else {
            var fr = viewModel?.mapActivity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
            fr.startForResult((ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).navigation() as SearchActivity).setModel(0), RESULT_POINT)
        }
    }

    fun onInfoWindowClick(it: Marker?) {
        //开始导航了
        it!!.remove()
        viewModel?.status.navigationStartPoint = viewModel?.status.startPoint
        var location = Location(it!!.position.latitude, it!!.position.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, it.title, "")
        viewModel?.status.navigationEndPoint = location
        var list = ArrayList<LatLng>()
        if (driverStatus.get() == DriverCancle) {
            viewModel?.startDriver(1)
        } else {
            viewModel?.startNavi(list, 0)
        }
    }

    fun cancleDriver(b: Boolean) {
        driverStatus.set(DriverCancle)
        if (b) {
            mapFr.mAmap.clear()
        }
        driverController.cancleDriver()
        timerDispose?.dispose()
        timerDispose = null
        timer = null
        mapFr.mLocationOption?.isSensorEnable = true
        mapFr.mlocationClient?.setLocationOption(mapFr.mLocationOption)
        mapFr.myLocationStyle.showMyLocation(true)
        mapFr.mAmap.isMyLocationEnabled = true
        mapFr.mAmap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        mapFr.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        mapFr.mAmap.myLocationStyle = mapFr.myLocationStyle
        driverDistance.set("00:00")
        RxBus.default?.post(RxBusEven.getInstance(RxBusEven.DriverCancleByNavigation))
        viewModel?.status.reset()
    }


    fun onFiveBtnClick(view: View) {
        when (view.id) {
            R.id.sos_btn -> {
                var intent = Intent(Intent.ACTION_CALL)
                var data = Uri.parse("tel:120")
                intent.data = data
                mapFr.startActivity(intent)
            }
            R.id.change_map_point -> {
                changeMap_Point_btn()
            }
            R.id.team_btn -> {
                viewModel?.selectTab(1)
            }
            R.id.road_book -> {
                if (viewModel?.curPosition != null) {
                    var bundle = Bundle()
                    bundle.putSerializable(RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT, viewModel?.curPosition)
                    viewModel?.startFragment(viewModel?.mapActivity.parentFragment!!, RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY, bundle, REQUEST_LOAD_ROADBOOK)
                }
            }
        }
    }

    fun changeMap_Point_btn() {
        if (viewModel?.status.navigationType != 0) {
            //跳到导航
            var list = ArrayList<LatLng>()
            viewModel?.status.passPointDatas.forEach {
                list.add(LatLng(it.latitude, it.longitude))
            }
            viewModel?.startNavi(list, 2)
        } else {
            if (driverStatus.get() == Drivering && viewModel?.status.locationLat.size == 0) {
                return
            }
            viewModel?.changerFragment(3)
            mapFr.getMapPointController().changeMap(viewModel?.curPosition!!)
        }
    }

    open fun GoTeam() {
        //检查当前组队信息
        //检查当前进入方式
        if (BaseApplication.MinaConnected) {
            //如果当前在组队
            if (viewModel?.TeamStatus?.teamStatus == TeamStarting) {
                var model = viewModel?.items[1] as TeamItemModel
                mapFr.showProgressDialog(getString(R.string.http_loading))
                model.initInfo()
                viewModel?.changerFragment(1)
            }
        } else if (!BaseApplication.MinaConnected) {
            //如果当前未组队 检查当前个人信息
            mapFr.showProgressDialog(getString(R.string.http_loading))
            HttpRequest.instance.setCheckStatusResult(this)
            var map = HashMap<String, String>()
            HttpRequest.instance.checkTeamStatus(map)

        }
    }

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.WxLoginReLogin -> {
                if (dialogFragment != null && dialogFragment?.isVisible!!) {
                    dialogFragment?.dismiss()
                }
            }
            RxBusEven.NAVIGATION_FINISH -> {
                viewModel?.mapActivity.NavigationStart = false
                viewModel?.status.navigationType = 0
                viewModel?.status.passPointDatas.clear()
            }
        }
    }
}