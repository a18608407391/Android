package com.example.drivermodule.Ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.*
import com.amap.api.location.AMapLocation.GPS_ACCURACY_BAD
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.autonavi.amap.mapcore.IPoint
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.UpdateDriverStatus
import com.elder.zcommonmodule.DataBases.queryDriverStatus
import com.elder.zcommonmodule.Entity.Location
import com.example.drivermodule.AMapUtil
import com.example.drivermodule.Activity.MapActivity
import com.example.drivermodule.BR
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.zk.library.Bus.ServiceEven
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.DriverViewModel
import com.example.drivermodule.databinding.FragmentDriverBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_driver.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import io.reactivex.android.schedulers.AndroidSchedulers
import android.support.design.widget.BottomSheetBehavior
import android.databinding.ObservableArrayList
import com.elder.zcommonmodule.DataBases.deleteDriverStatus
import com.zk.library.Base.BaseApplication

@Route(path = RouterUtils.MapModuleConfig.DRIVER_FR)
class DriverFragment : BaseFragment<FragmentDriverBinding, DriverViewModel>() {
    var maxHight = 0.0
    var maxSpeed = 0F
    var hight = ArrayList<Double>()
    lateinit var mapActivity: MapActivity
    var locationDatas = ObservableArrayList<Location>()
    lateinit var localStyle: MyLocationStyle
    override fun initContentView(): Int {
        return R.layout.fragment_driver
    }

    override fun initVariableId(): Int {
        return BR.driver_model
    }

    var isUp = false
    var currentUp = 0.0
    var MaxUp = 0.0
    var UpCount = 0
    var curPoint: Location? = null
    /**
     * 激活定位
     */

    /**
     * 停止定位
     */
    override fun initMap(savedInstanceState: Bundle?) {
        super.initMap(savedInstanceState)
        mapActivity = activity as MapActivity
        mapActivity.showProgressDialog(getString(R.string.location_loading))
        initResume()
    }

    fun resumeDriver(mapActivity: MapActivity) {
//        KeepLiveHelper.getDefault().onActivityCreate(activity!!)

        Observable.just("").map(Function<String, ArrayList<Location>> {
            if (viewModel?.status?.locationLat?.isEmpty()!!) {
                viewModel?.status?.locationLat!!?.add(viewModel?.status!!.driverStartPoint)
            } else {
                viewModel?.status?.locationLat?.forEach {
                    hight.add(it.height)
                }
            }
//            viewModel?.mapPointController?.startPoint = converLatPoint(viewModel?.status?.locationLat[viewModel?.status?.locationLat.size - 1])

//            if (viewModel?.status?.locationLat != null && !viewModel?.status?.locationLat!!.isEmpty()) {
//                viewModel?.status!!.distance = 0.0
//                viewModel?.status?.locationLat!!.forEachIndexed { index, lat ->
//                    if (index != 0) {
//                        viewModel?.status!!.distance += AMapUtils.calculateLineDistance(LatLng(viewModel?.status?.locationLat!![index - 1].latitude, viewModel?.status?.locationLat!![index - 1].longitude), LatLng(viewModel?.status?.locationLat!![index].latitude, viewModel?.status?.locationLat!![index].longitude))
//                    }
//                }
//            }
            viewModel?.status?.curModel = 0
            mapActivity.mAmap.clear()
            viewModel?.driverController?.startMarker = mapActivity?.mapUtils?.createMaker(Location(viewModel?.status!!.driverStartPoint!!.latitude, viewModel?.status!!.driverStartPoint!!.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F))
//            viewModel?.driverController?.startMarker = mapActivity?.mapUtils?.createAnimationMarker(true, LatLonPoint(viewModel?.status!!.driverStartPoint!!.latitude, viewModel?.status!!.driverStartPoint!!.longitude))
            var end = viewModel?.status?.locationLat!![viewModel?.status?.locationLat!!.size - 1]

//            viewModel?.driverController?.movemaker = mapActivity.mAmap.addMarker(MarkerOptions().position(LatLng(end.latitude, end.longitude)).zIndex(2f)
//                    .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))
            mapActivity?.mapUtils?.createAnimationMarker(true, LatLonPoint(end.latitude, end.longitude))


            mapActivity.mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapActivity?.mapUtils?.breatheMarker_center?.position, 15F))
            curPoint = viewModel?.status!!.locationLat[viewModel?.status!!.locationLat.size - 1]
            viewModel?.status!!.onDestroyStatus = 2
            return@Function viewModel?.status!!.locationLat
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            mapActivity.setDriverStyle()
            mapActivity.mViewModel?.component?.Drivering?.set(true)
            viewModel?.status?.startDriver?.set(Drivering)
            viewModel?.linearBg!!.set(getColor(R.color.white))
            var distanceTv = ""
            if (viewModel?.status!!.distance > 1000) {
                distanceTv = DecimalFormat("0.00").format(viewModel?.status!!.distance / 1000) + "KM"
            } else {
                distanceTv = DecimalFormat("0.00").format(viewModel?.status!!.distance) + "M"
            }

            viewModel?.driverDistance!!.set(distanceTv)
//            var s = mapActivity.mViewModel?.mFragments!![1] as NavigationFragment

            viewModel?.driverController?.setLineDatas(viewModel?.status?.locationLat!!, getColor(R.color.line_color))

//            SPUtils.getInstance().put("Action", "driver")
//            KeepLiveHelper.getDefault().setAction("driver")
//            KeepLiveHelper.getDefault().startBindService(context)
        }
        viewModel?.timer = Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        viewModel?.timerDispose = viewModel?.timer?.subscribe {
            viewModel?.status!!.second++
            viewModel?.driverTime!!.set(ConvertUtils.formatTimeS(viewModel?.status!!.second))
            UpdateDriverStatus(viewModel?.status!!)
//                s.viewModel?.totalTime?.set(ConvertUtils.formatTimeS(viewModel?.status!!.second))
        }
        if (mapActivity.resume == "cancle") {
            CoroutineScope(uiContext).launch {
                delay(500)
                if (viewModel?.status!!.distance > 1000) {
                    viewModel?.driverController!!.driverOver()
                } else {
                    deleteDriverStatus(PreferenceUtils.getString(org.cs.tec.library.Base.Utils.context, USERID))
                    viewModel?.cancleDriver(true)
                }
            }
        }
    }

    lateinit var behaviors: BottomSheetBehavior<LinearLayout>
    private fun setUpBehavior() {
        behaviors = BottomSheetBehavior.from<LinearLayout>(behavior)
        behaviors.state = BottomSheetBehavior.STATE_HIDDEN
        viewModel?.addChildView(horizontalLinear)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        super.initData()
        viewModel?.inject(this)
        setUpBehavior()
    }

    fun setResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data?.extras != null) {
            if (data?.extras!!["tip"] != null) {
                var tip = data?.extras!!["tip"] as PoiItem
                if (tip != null) {
                    if (resultCode == RESULT_POINT) {
                        if (tip.latLonPoint?.latitude != null && tip.latLonPoint?.longitude != null) {
                            mapActivity?.mAmap?.isMyLocationEnabled = false
                            isResultPoint = true
                            mapActivity?.mAmap?.moveCamera(CameraUpdateFactory.changeLatLng(AMapUtil.convertToLatLng(tip.latLonPoint)))
                            var opotion = MarkerOptions().title(tip.title).snippet(getString(R.string.go_there)).position(LatLng(tip.latLonPoint.latitude, tip.latLonPoint.longitude))
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            var makder = mapActivity?.mAmap?.addMarker(opotion)
                            makder?.showInfoWindow()
                        }
                    }
                }
            } else {
                if (resultCode == TO_NAVIGATION) {
//                    Log.e("result", "RESULT_回调")
//                    var t = data.getIntExtra("CurrentState", 0)
//                    if (t == 1) {
//                        //
//                        viewModel?.mapPointController?.endPoint = null
//                         viewModel?.status?.navigationType = 0
//                        if (viewModel?.status?.distance!! < 1000) {
//
//                        } else {
//
//                        }
//                    } else {
//                        viewModel?.status?.navigationType = 0
//                        viewModel?.status!!.startDriver.set(DriverCancle)
//                    }
                }
            }
        }
    }


    fun initResume() {
        var t: DriverDataStatus? = null

        var list = queryDriverStatus(PreferenceUtils.getString(context, USERID))
        if (list.size != 0) {
            t = list[0]
        }
        var pos = ServiceEven()
        pos.type = "HomeDriver"
        RxBus.default?.post(pos)
//        context!!.startService(Intent(context, LowLocationService::class.java).setAction("driver"))

        if (mapActivity?.resume == "nomal") {
            if (t == null) {
                viewModel?.status = DriverDataStatus()
            } else {
                viewModel?.status = t
                if (viewModel?.status?.navigationType == Driver_Navigation) {
                    var wayPoint = ArrayList<LatLng>()
                    viewModel!!.status.passPointDatas.forEach {
                        wayPoint.add(LatLng(it.latitude, it.longitude))
                    }
                    viewModel?.startNavi(viewModel?.status?.navigationStartPoint!!, viewModel?.status?.navigationEndPoint!!, wayPoint, false)
                } else {
                    viewModel?.status?.passPointDatas?.clear()
                }
                resumeDriver(mapActivity!!)
            }
            viewModel?.status?.uid = PreferenceUtils.getString(context, USERID)
//            viewModel?.startDrive(false)
        } else if (mapActivity?.resume == "myroad") {

            if (t == null) {
                viewModel?.status = DriverDataStatus()
            } else {
                viewModel?.status = t
                if (viewModel?.status?.navigationType == Driver_Navigation) {
                    var wayPoint = ArrayList<LatLng>()
                    viewModel!!.status.passPointDatas.forEach {
                        wayPoint.add(LatLng(it.latitude, it.longitude))
                    }
                    viewModel?.startNavi(viewModel?.status?.navigationStartPoint!!, viewModel?.status?.navigationEndPoint!!, wayPoint, false)
                } else {
                    viewModel?.status?.passPointDatas?.clear()
                }
                resumeDriver(mapActivity!!)
            }
            viewModel?.status?.uid = PreferenceUtils.getString(context, USERID)
            CoroutineScope(uiContext).async {
                delay(200)
                mapActivity.mViewModel?.selectTab(2)
                mapActivity.mViewModel?.changerFragment(3)
                if (PreferenceUtils.getString(mapActivity, PreferenceUtils.getString(context, USERID) + "hot") == null && mapActivity.hotData == null) {
                    mapActivity.getRoadBookFragment().viewModel?.doLoadDatas(mapActivity.hotData!!)
                }
            }
        } else if (mapActivity?.resume == "fastTeam") {
            Log.e("result", "fastTeam")
            if (t == null) {
                viewModel?.status = DriverDataStatus()
            } else {
                viewModel?.status = t
                if (viewModel?.status?.navigationType == Driver_Navigation) {
                    var wayPoint = ArrayList<LatLng>()
                    viewModel!!.status.passPointDatas.forEach {
                        wayPoint.add(LatLng(it.latitude, it.longitude))
                    }
                    viewModel?.startNavi(viewModel?.status?.navigationStartPoint!!, viewModel?.status?.navigationEndPoint!!, wayPoint, false)
                } else {
                    viewModel?.status?.passPointDatas?.clear()
                }
                resumeDriver(mapActivity!!)
            }
            viewModel?.status = DriverDataStatus()
            viewModel?.status?.uid = PreferenceUtils.getString(context, USERID)
            CoroutineScope(uiContext).launch {
                delay(500)
                mapActivity.mViewModel?.selectTab(1)
            }
        } else {
            viewModel?.status = queryDriverStatus(PreferenceUtils.getString(context, USERID))[0]
            if (mapActivity?.resume == "resume") {
                if (viewModel?.status?.navigationType == Driver_Navigation) {
                    var wayPoint = ArrayList<LatLng>()
                    viewModel!!.status.passPointDatas.forEach {
                        wayPoint.add(LatLng(it.latitude, it.longitude))
                    }
                    viewModel?.startNavi(viewModel?.status?.navigationStartPoint!!, viewModel?.status?.navigationEndPoint!!, wayPoint, false)
                } else {
                    viewModel?.status?.passPointDatas?.clear()
                }

            } else if (mapActivity?.resume == "continue" || mapActivity?.resume == "cancle") {
                viewModel?.status?.startDriver?.set(Drivering)
            }
            resumeDriver(mapActivity!!)
        }
    }

    var isResultPoint = false

    var curPosition: Location? = null

    var lastDistance: Location? = null

    fun loacation(amapLocation: AMapLocation) {
        if (mapActivity?.mListener != null && amapLocation != null) {
//            Log.e("result", "当前定位" + amapLocation.toStr())
//            取值范围：【0，360】，其中0度表示正北方向，90度表示正东，180度表示正南，270度表示正西
//            3.1.0之前的版本只有定位类型为 AMapLocation.LOCATION_TYPE_GPS时才有值
//            自3.1.0版本开始，不限定定位类型，当定位类型不是AMapLocation.LOCATION_TYPE_GPS时，可以通过 AMapLocationClientOption.setSensorEnable(boolean) 控制是否返回方向角，当设置为true时会通过手机传感器获取方向角,如果手机没有对应的传感器会返回0.0 注意：
//            定位类型为AMapLocation.LOCATION_TYPE_GPS时，方向角指的是运动方向
//            定位类型不是AMapLocation.LOCATION_TYPE_GPS时，方向角指的是手机朝向
            if (amapLocation != null && amapLocation.errorCode == 0) {
                if (amapLocation?.gpsAccuracyStatus == GPS_ACCURACY_BAD) {
                    if (isAdded && mapActivity?.onStart!!) {
                        CoroutineScope(uiContext).launch {
                            Toast.makeText(activity, getString(R.string.gsp_bad), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                mapActivity?.mListener?.onLocationChanged(amapLocation)// 显示系统小蓝点
                if (viewModel?.status?.navigationType != 1) {
                    viewModel?.status?.navigationStartPoint = Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName)
                }
                if (curPosition == null) {
                    mapActivity.dismissProgressDialog()
                }
                curPosition = Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing)
                curPoint = Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing)
                if (viewModel?.status?.startDriver?.get() == Drivering) {
                    if (mapActivity?.mapUtils?.breatheMarker_center != null) {
                        mapActivity?.mapUtils?.breatheMarker_center!!.rotateAngle = amapLocation.bearing
                    }
                    if (viewModel?.status?.locationLat?.size == 0) {
//                        viewModel?.dismissDialog()
//                        mapActivity.mAmap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(amapLocation.latitude,amapLocation.longitude)))
                        hight.add(amapLocation.altitude)
                        viewModel?.status?.locationLat?.add(Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName))
                        addStartPoint(Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName))
                    } else {
                        if (amapLocation.locationType == 1) {
                            hight.add(amapLocation.altitude)
                            if (maxSpeed < amapLocation.speed) {
                                maxSpeed = amapLocation.speed
                            }
                            if (maxHight < amapLocation.altitude) {
                                if (!isUp) {
                                    isUp = true
                                    currentUp = amapLocation.altitude
                                }
                                maxHight = amapLocation.altitude
                            } else if (maxHight == amapLocation.altitude) {
                                if (isUp) {
                                    //如果之前是在升高状态
                                    UpCount++
                                    if (amapLocation.altitude >= currentUp) {
                                        MaxUp += amapLocation.altitude - currentUp
                                    }
                                    isUp = false
                                }
                            }
                            if (amapLocation.accuracy <= 30 && amapLocation.speed > 1) {
                                if (viewModel?.status?.startDriver!!.get() == Drivering || viewModel?.status?.navigationType == 1) {
                                    if (mapActivity.mViewModel?.currentPosition!! < 2) {
                                        mapActivity.mAmap.moveCamera(CameraUpdateFactory.changeLatLng(mapActivity?.mapUtils?.breatheMarker_center?.position))
                                    }
//                                    var lastLat = LatLng(viewModel?.status?.locationLat?.get(viewModel?.status?.locationLat?.size!! - 1)!!.latitude, viewModel?.status?.locationLat?.get(viewModel?.status?.locationLat?.size!! - 1)!!.longitude)
//                                    var thisLat = LatLng(amapLocation.latitude, amapLocation.longitude)
//                                    var s = AMapUtils.calculateLineDistance(lastLat, thisLat)
//                                    if (s > 1) {
//                                        val lastP = IPoint()
//                                        val thisP = IPoint()
//                                        MapProjection.lonlat2Geo(lastLat.latitude, lastLat.longitude, lastP)
//                                        MapProjection.lonlat2Geo(thisLat.latitude, thisLat.longitude, thisP)
//                                        var aroute = getRotate(lastP, thisP)
//                                        viewModel?.driverController?.movemaker?.rotateAngle = 360 - aroute + mapActivity.mAmap.cameraPosition.bearing
//                                        viewModel?.driverController?.movemaker?.period = 1
//                                    }

//                                    viewModel?.driverController?.movemaker?.rotateAngle = amapLocation.bearing


                                    if (lastDistance == null) {
                                        lastDistance = viewModel?.status!!.locationLat[viewModel?.status!!.locationLat.size - 1]
                                    }
                                    viewModel?.status!!.distance += AMapUtils.calculateLineDistance(LatLng(lastDistance?.latitude!!, lastDistance?.longitude!!), LatLng(amapLocation?.latitude!!, amapLocation?.longitude!!));
                                    var distanceTv = ""
                                    if (viewModel?.status!!.distance > 1000) {
                                        distanceTv = DecimalFormat("0.0").format(viewModel?.status!!.distance / 1000) + "KM"
                                    } else {
                                        distanceTv = DecimalFormat("0.0").format(viewModel?.status!!.distance) + "M"
                                    }
                                    viewModel?.driverDistance!!.set(distanceTv)
                                    lastDistance = Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName)
                                    locationDatas.add(Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName))

                                    mapActivity?.mapUtils?.setLocation(Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName))
//                                    viewModel?.driverController?.movemaker?.position = LatLng(amapLocation.latitude, amapLocation.longitude)
                                    viewModel?.status?.locationLat?.add(Location(amapLocation.latitude, amapLocation.longitude, System.currentTimeMillis().toString(), amapLocation.speed, amapLocation.altitude, amapLocation.bearing, amapLocation.aoiName, amapLocation.poiName))
                                    viewModel?.driverController?.setLineDatas(viewModel?.status?.locationLat, getColor(R.color.line_color))
                                }
                            }
                        }
                    }
                } else {
                    if (mapActivity!!.mAmap != null) {
                        mapActivity?.myLocationStyle?.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
                        mapActivity!!.mAmap.myLocationStyle = mapActivity!!.myLocationStyle
                    }
                }
            } else {
                val errText = "定位失败," + amapLocation.errorCode + ": " + amapLocation.errorInfo
                Log.e("AmapErr", errText)
            }
        }
    }

    private fun getRotate(var1: IPoint?, var2: IPoint?): Float {
        if (var1 != null && var2 != null) {
            var var3 = var2.y.toDouble()
            var var5 = var1.y.toDouble()
            var var7 = var1.x.toDouble()
            return (Math.atan2(var2.x.toDouble() - var7, var5 - var3) / 3.141592653589793 * 180.0).toFloat()
        } else {
            return 0.0f
        }
    }


    fun addStartPoint(amapLocation: Location) {
        mapActivity.dismissProgressDialog()
        mapActivity?.setDriverStyle()
        mapActivity.mAmap.clear()
        mapActivity?.mapUtils?.createAnimationMarker(true, LatLonPoint(amapLocation.latitude, amapLocation.longitude))

//        mapActivity.getMapPointFragment().viewModel?.mapPointController?.startMaker = mapActivity?.mapUtils?.createAnimationMarker(true, LatLonPoint(amapLocation.latitude, amapLocation.longitude))
        mapActivity.getMapPointFragment().viewModel?.mapPointController?.startMaker = mapActivity?.mapUtils!!.createMaker(amapLocation)


        viewModel?.driverController?.startMarker = mapActivity.getMapPointFragment().viewModel?.mapPointController?.startMaker


//        viewModel?.driverController?.movemaker = mapActivity.mAmap.addMarker(MarkerOptions().position(LatLng(amapLocation.latitude, amapLocation.longitude)).zIndex(2f)
//                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))


        if (amapLocation?.aoiName != null && !amapLocation?.aoiName.isEmpty()) {
            viewModel?.status?.startAoiName = amapLocation?.aoiName
        } else {
            viewModel?.status?.startAoiName = amapLocation?.poiName
        }
        locationDatas.add(amapLocation)
        viewModel?.status?.driverStartPoint = Location(amapLocation.latitude, amapLocation.longitude, amapLocation.time.toString(), amapLocation.speed, amapLocation.height, amapLocation.bearing)
        if (mapActivity.getTeamFragment()!=null &&mapActivity.getTeamFragment().isAdded && BaseApplication?.MinaConnected!!) {
            mapActivity.getTeamFragment().viewModel?.markerListNumber?.forEach {
                mapActivity.getTeamFragment().viewModel?.markerList!![it]!!.remove()
            }
            mapActivity.getTeamFragment().viewModel?.markerListNumber!!.clear()
            mapActivity.getTeamFragment().viewModel?.markerList!!.clear()
            mapActivity.getTeamFragment().viewModel?.initInfo()
        }
        UpdateDriverStatus(viewModel?.status!!)
    }

    fun onInfoWindowClick(it: Marker) {
        if (viewModel?.status?.locationLat?.size == 0) {
            viewModel?.status?.locationLat!!.add(Location(curPosition?.latitude!!, curPosition?.longitude!!, curPosition?.time.toString(), curPosition?.speed!!, curPosition?.height!!, curPosition?.bearing!!))
            addStartPoint(curPosition!!)
        }
        if (viewModel?.status!!.startDriver.get() == Drivering) {
            var wayPoint = ArrayList<LatLng>()
            viewModel?.status?.navigationStartPoint = Location(curPosition?.latitude!!, curPosition?.longitude!!, curPosition?.time.toString(), curPosition?.speed!!, curPosition?.height!!, curPosition?.bearing!!)
            viewModel?.status?.navigationEndPoint = Location(it.position.latitude, it.position?.longitude!!, "", 0F, 0.0, 0F)
            viewModel?.startNavi(viewModel?.status?.navigationStartPoint!!, viewModel?.status?.navigationEndPoint!!, wayPoint, false)
        } else {
            viewModel?.status?.navigationStartPoint = Location(curPosition?.latitude!!, curPosition?.longitude!!, curPosition?.time.toString(), curPosition?.speed!!, curPosition?.height!!, curPosition?.bearing!!)
            viewModel?.status?.navigationEndPoint = Location(it.position.latitude, it.position?.longitude!!, "", 0F, 0.0, 0F)
            viewModel?.startDrive(true)
        }
    }
}