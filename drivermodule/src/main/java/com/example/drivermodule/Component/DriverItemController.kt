package com.example.drivermodule.Component

import android.support.design.widget.BottomSheetBehavior
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.elder.zcommonmodule.DataBases.deleteDriverStatus
import com.elder.zcommonmodule.DataBases.deleteLocation
import com.elder.zcommonmodule.Drivering
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Douglas
import com.example.drivermodule.Controller.DriverItemModel
import com.example.drivermodule.R
import com.example.drivermodule.Utils.MapControllerUtils
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.getWindowWidth
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import java.io.Serializable
import java.text.DecimalFormat
import kotlin.collections.ArrayList


class DriverItemController : Serializable {
    var model: DriverItemModel

    constructor(model: DriverItemModel) {
        this.model = model
    }


    //骑行暂停
//    var startPoint: Location? = null
//
//    var totalPoint = ArrayList<Location>()
//
//    var DriverLine = ArrayList<HashMap<String, ArrayList<Location>>>()
//
//    var locationLat = ObservableArrayList<Location>()


//    var movemaker: Marker? = null
    //骑行点  起始点

    var startMarker: Marker? = null
    var line: Polyline? = null

    //骑行时所有地图点集合
//    var pointDatas: ArrayList<AMapLocation>? = null
//
//    var
    //骑行中
    fun resetDriver(flag: Boolean) {
        //flag 骑行中  暂停/继续
        //恢复默认骑行状态
    }


    fun cancleDriver() {
        model.viewModel?.mapActivity.mapUtils!!.clearMarker()
        if (startMarker != null) {
            startMarker?.remove()
            startMarker = null
        }
        if (line != null) {
            line?.remove()
            line = null
        }
        deleteDriverStatus(PreferenceUtils.getString(context, USERID))
        deleteLocation(PreferenceUtils.getString(context, USERID))
    }

    fun continueDriver() {

        model.bottomLayoutVisible.set(true)
        model.viewModel.status.startDriver.set(Drivering)
//        model.second += naviTime / 1000
//        model.fr.distance += naviDistance
        var distanceTv = ""
        if (model.viewModel.status.distance > 1000) {
            distanceTv = DecimalFormat("0.0").format(model.viewModel.status.distance / 1000) + "KM"
        } else {
            distanceTv = DecimalFormat("0.0").format(model.viewModel.status.distance) + "M"
        }
        model?.driverDistance!!.set(distanceTv)
        var move: Location = model.viewModel.status.locationLat[model.viewModel.status.locationLat.size - 1]
        model.viewModel.mapActivity.mAmap.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(move.latitude, move.longitude)))
        var start = startMarker?.position

        startMarker?.remove()
        startMarker = null
        if (start != null) {
            model.viewModel.mapActivity.mAmap.clear()
//            startMarker = model.mapActivity.mapUtils?.createAnimationMarker(true, AMapUtil.convertToLatLonPoint(start))
            startMarker = model.viewModel.mapActivity?.mapUtils!!.createMaker(Location(start.latitude, start.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F))
        }
//        movemaker?.remove()
//        movemaker = null

//        movemaker = model.mapActivity.mAmap.addMarker(MarkerOptions().zIndex(2f).position(LatLng(move.latitude, move.longitude))
//                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked)))
        model.viewModel.mapActivity?.mapUtils!!.clearMarker()
        model.viewModel.mapActivity?.mapUtils!!.createAnimationMarker(true, LatLonPoint(move.latitude, move.longitude))

        line?.remove()
        line = null
        setLineDatas(model.viewModel.status.locationLat, getColor(R.color.line_color))
    }


    fun setLineDatas(datas: MutableList<Location>?, color: Int) {


        if (line == null) {
            line = model.viewModel.mapActivity.mAmap.addPolyline(PolylineOptions()
                    .color(R.color.line_color)
                    .width(18f).zIndex(0f).color(getColor(R.color.line_color)))
        } else {
            if (!line?.isVisible!!) {
                line?.remove()
                line = null
                line = model.viewModel.mapActivity.mAmap.addPolyline(PolylineOptions()
                        .color(R.color.line_color)
                        .width(18f).zIndex(0f).color(getColor(R.color.line_color)))
            }
        }
        if (datas == null) {
            return
        }

        var list = ArrayList<LatLng>()
        datas.forEach {
            list.add(LatLng(it.latitude, it.longitude))
        }
        line?.points = list
    }

    fun driverOver() {
        //获取当前所有的点的集合
        if (model.viewModel.status.distance > 1000) {
            model.bottomLayoutVisible.set(false)
            model.viewModel.mapActivity.viewModel?.component!!.Drivering.set(false)
            model.viewModel.mapActivity.viewModel?.selectTab(0)
//            model.mapActivity.mViewModel?.changerFragment(0)
            model.viewModel.mapActivity.showProgressDialog(getString(R.string.upload_to_Net))
            Observable.just("").subscribeOn(Schedulers.io()).map(Function<String, String> {
                if (model.viewModel.status.locationLat.size > 1) {
                    model.viewModel.status.locationLat.forEach {
                        model.viewModel.status.totalPoint.add(it)
                    }
                }
                model.chart.set(model.viewModel?.status)
                var b = LatLngBounds.builder()
                var biglatitude = 0.0  //最大纬度
                var littlatitude = 180.0 //最小纬度
                var biglon = 0.0  //最大经度
                var littlelon = 180.0 //最小经度
                model.viewModel.status.totalPoint.forEach {
                    if (it.latitude > biglatitude) {
                        biglatitude = it.latitude
                    }
                    if (it.latitude < littlatitude) {
                        littlatitude = it.latitude
                    }
                    if (it.longitude > biglon) {
                        biglon = it.longitude
                    }
                    if (it.longitude < littlelon) {
                        littlelon = it.longitude
                    }
                }
                var start = startMarker?.position
                startMarker?.remove()
                startMarker = null
                model.viewModel.mapActivity.mAmap.clear()
                startMarker = model.viewModel.mapActivity.mapUtils?.createMaker(Location(model.viewModel.status.totalPoint[0]!!.latitude, model.viewModel.status.totalPoint[0]!!.longitude))

                model.viewModel.mapActivity.mAmap.addMarker(MarkerOptions().position(LatLng(model.viewModel.status.totalPoint!![model.viewModel.status.totalPoint.size - 1].latitude, model.viewModel.status.totalPoint!![model.viewModel.status.totalPoint.size - 1].longitude)).anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.select_point)))
                model.viewModel.mapActivity.mAmap.apply {
                    var line = this.addPolyline(PolylineOptions()
                            .color(getColor(R.color.line_color))
                            .width(20f).zIndex(0f))
                    var list = ArrayList<LatLng>()
                    model.viewModel.status.totalPoint.forEach {
                        list.add(LatLng(it.latitude, it.longitude))
                    }
                    line?.points = list
                }
                b.include(LatLng(biglatitude, biglon))    //东北角
                b.include(LatLng(littlatitude, littlelon)) //西南角
                model.viewModel.mapActivity.mAmap.animateCamera(CameraUpdateFactory
                        .newLatLngBounds(b.build(), getWindowWidth()!!, ConvertUtils.dp2px(345F - 130), 50))
                return@Function ""
            }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                model.viewModel.mapActivity.viewModel?.showBottomSheet?.set(true)
                model.behavior.set(BottomSheetBehavior.STATE_EXPANDED)
                var m = (model.viewModel.status.distance * 3.6 / model.viewModel.status.second!!)
                var averageRate = DecimalFormat("0.0").format(m)
                model.share.apply {
                    if (model.viewModel.status.totalPoint.size!! > 3000) {
                        var da = Douglas(model.viewModel.status.totalPoint, 10.0)
                        var mes = da.compress()
                        model.viewModel.status.totalPoint!!.clear()
                        model.viewModel.status.totalPoint!!.addAll(mes)
                        var dis = 0.0
                        model.viewModel.status.totalPoint.forEachIndexed { index, location ->
                            if (index != 0) {
                                dis += AMapUtils.calculateLineDistance(LatLng(model.viewModel.status.totalPoint[index - 1]?.latitude!!, model.viewModel.status.totalPoint[index - 1]?.longitude!!), LatLng(model.viewModel.status.totalPoint[index]?.latitude!!, model.viewModel.status.totalPoint[index]?.longitude!!))
                            }
                            this.hight.add(location.height)
                        }
                        model.viewModel.status.distance = dis
                    }
                    this.Totaldistance.set(DecimalFormat("0.0").format(model.viewModel.status.distance / 1000))
                    this.maxHight.set(model.viewModel.status.maxHeight.toString())
                    this.maxSpeed.set(DecimalFormat("0.0").format(model.viewModel.status.maxSpeed * 3.6))
                    this.Totaltime.set(ConvertUtils.formatTimeS(model.viewModel.status.second?.toInt()?.toLong()!!))
                    this.averageRate.set(averageRate)
                    this.maxUp.set(DecimalFormat("0.0").format(model.viewModel.status.UpValue))
                    model.shareUpLoad.bendAngle = 72
                    model.shareUpLoad.climbing = DecimalFormat("0.0").format(model.viewModel.status.UpValue)
                    model.shareUpLoad.dateTimer = System.currentTimeMillis().toString()

                    model.shareUpLoad.elevationArray = hight
                    model.shareUpLoad.locationArray = model.viewModel.status.totalPoint
                    model.shareUpLoad.topspeed = model.viewModel.status.maxSpeed
                    model.shareUpLoad.metre100Sprint = 10
                    model.shareUpLoad.highestPoint = model.viewModel.status.maxHeight
                    model.shareUpLoad.minkm = m.toString()
                    model.shareUpLoad.mileage = model.viewModel.status.distance
                    model.shareUpLoad.totalTime = model.viewModel.status.second
                }
                model.viewModel?.mapActivity.mapUtils?.CurState = MapControllerUtils.OnShareState
                model.viewModel.mapActivity.mAmap.setOnCameraChangeListener(model.viewModel?.mapActivity.mapUtils)
            }

//        model.fr.basesult.maxOfVisible = totalPoint.size / 12 * 6
//        showTracedLocations(point)


        } else {
            model.dontHaveOneMetre(org.cs.tec.library.Base.Utils.getString(R.string.notEnoughOneKm), org.cs.tec.library.Base.Utils.getString(R.string.cancle_riding), org.cs.tec.library.Base.Utils.getString(R.string.continue_driving), 0)
        }
    }
}