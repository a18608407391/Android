package com.example.drivermodule

import android.databinding.ObservableArrayList
import android.util.Log
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.AMapNaviViewListener
import com.amap.api.navi.enums.NaviType
import com.amap.api.navi.model.*
import com.autonavi.tbt.TrafficFacilityInfo
import com.elder.zcommonmodule.Entity.Location
import com.example.drivermodule.Utils.NaviUtil
import com.example.drivermodule.ViewModel.NavigationViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Bus.event.RxBusEven.Companion.NAVIGATION_DATA
import kotlinx.android.synthetic.main.activity_navigation.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Utils.ConvertUtils


class AMapNaviViewComponent : AMapNaviViewListener, AMapNaviListener {
    var map: NavigationViewModel
    //    var restart = false
    var naviDatasPoint = ObservableArrayList<Location>()
    var naviDatasStart: Location? = null
    var NaviStartTime: Long = 0
    var NaviDistance = 0
    var CurDistance = 0

    constructor(map: NavigationViewModel) {
        this.map = map
//        timer = object : CountDownTimer(8000, 1000) {
//            override fun onFinish() {
//                if (restart) {
//                    this.start()
//                } else {
//                    map.isStartNavigation.set(true)
//                    map.mAMapNaviView?.recoverLockMode()
//                }
//            }
//
//            override fun onTick(millisUntilFinished: Long) {
//                if (map.isRecoverLockMode == 3) {
//                    restart = true
//                } else if (map.isRecoverLockMode == 2) {
//                    this.cancel()
//                } else if (map.isRecoverLockMode == 0) {
//                    restart = false
//                }
//            }
//        }
    }


    override fun onInitNaviFailure() {
//        Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show()

        Log.e("result", "onInitNaviFailure")
    }

    override fun onInitNaviSuccess() {
        Log.e("result", "onInitNaviSuccess")
    }

    override fun onStartNavi(type: Int) {
        //开始导航回调
        Log.e("result", "onStartNavi")
    }

    override fun onTrafficStatusUpdate() {
        //
        Log.e("result", "onTrafficStatusUpdate")
    }


    var location: AMapNaviLocation? = null

    override fun onLocationChange(amapLocation: AMapNaviLocation) {
        this.location = amapLocation
        RxBus.default?.post(RxBusEven.getInstance(NAVIGATION_DATA,amapLocation))
//        RxBus.default?.post(amapLocation)
    }

    override fun onGetNavigationText(type: Int, text: String) {
        //播报类型和播报文字回调
        Log.e("result", "onGetNavigationText")
    }

    override fun onGetNavigationText(s: String) {
        Log.e("result", "onGetNavigationText")
    }

    override fun onEndEmulatorNavi() {
        //结束模拟导航
        map.stop()
        Log.e("result", "onEndEmulatorNavi")
    }

    override fun onArriveDestination() {
        //到达目的地
        map.stop()
        Log.e("result", "onArriveDestination")
    }

    override fun onCalculateRouteFailure(errorInfo: Int) {
        //路线计算失败
        Log.e("dm", "--------------------------------------------")
//        Log.i("dm", "路线计算失败：错误码=" + errorInfo + ",Error Message= " + ErrorInfo.getError(errorInfo))
        Log.i("dm", "错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/")
        Log.e("dm", "--------------------------------------------")
//        Toast.makeText(this, "errorInfo：" + errorInfo + ",Message：" + ErrorInfo.getError(errorInfo), Toast.LENGTH_LONG).show()
        Log.e("result", "onCalculateRouteFailure")
    }

    override fun onReCalculateRouteForYaw() {
        //偏航后重新计算路线回调
        Log.e("result", "onReCalculateRouteForYaw")
    }

    override fun onReCalculateRouteForTrafficJam() {
        //拥堵后重新计算路线回调

        Log.e("result", "onReCalculateRouteForTrafficJam")
    }

    override fun onArrivedWayPoint(wayID: Int) {
        //到达途径点
        Log.e("result", "onArrivedWayPoint")
    }

    override fun onGpsOpenStatus(enabled: Boolean) {
        //GPS开关状态回调
        Log.e("result", "onGpsOpenStatus")
    }

    override fun onNaviSetting() {
        //底部导航设置点击回调

        Log.e("result", "onNaviSetting")
    }

    override fun onNaviMapMode(isLock: Int) {
        //地图的模式，锁屏或锁车

        Log.e("result", "onNaviMapMode")
    }

    override fun onNaviCancel() {
//        finish()
        Log.e("result", "onNaviCancel")
    }

    override fun onNaviTurnClick() {
        //转弯view的点击回调
        Log.e("result", "onNaviTurnClick")
    }

    override fun onNextRoadClick() {
        //下一个道路View点击回调
        Log.e("result", "onNextRoadClick")
    }

    override fun onScanViewButtonClick() {
        //全览按钮点击回调
        Log.e("result", "onScanViewButtonClick")
    }

    override fun onNaviInfoUpdated(naviInfo: AMapNaviInfo) {
        //过时
        Log.e("result", "onNaviInfoUpdated")
    }

    override fun updateCameraInfo(aMapCameraInfos: Array<AMapNaviCameraInfo>) {
        Log.e("result", "updateCameraInfo")
    }

    override fun onServiceAreaUpdate(amapServiceAreaInfos: Array<AMapServiceAreaInfo>) {
        Log.e("result", "onServiceAreaUpdate")
    }

    override fun onNaviInfoUpdate(naviInfo: NaviInfo) {
        //导航过程中的信息更新，请看NaviInfo的具体说明
        if (naviInfo.m_RouteRemainDis > NaviDistance) {
            NaviDistance = naviInfo.m_RouteRemainDis
        } else {
            CurDistance = naviInfo.m_RouteRemainDis
        }
        map.totalTime.set(ConvertUtils.millis2FitTimeSpan(naviInfo.pathRetainTime.toLong() * 1000, 3))
        map.totalDistance.set("剩余" + NaviUtil.formatKM(naviInfo.pathRetainDistance))
        map.nextAddress.set(naviInfo.m_NextRoadName)
        var status = map.mAMapNavi.naviPath.trafficStatuses
        map.navigationActivity.progress.update(map.mAMapNavi.naviPath.allLength, naviInfo.m_RouteRemainDis, status)


//            if (NaviDistance != 0 && CurDistance != 0) {
//                var s = map.drive.viewModel?.status!!.distance + NaviDistance - CurDistance
//                map.totalDistance.set(NaviUtil.formatKM(s.toInt()))
//            } else {
//                var distanceTv = ""
//                if (map.drive.viewModel?.status!!.distance > 1000) {
//                    distanceTv = DecimalFormat("0.00").format(map.drive.viewModel?.status!!.distance / 1000) + "KM"
//                } else {
//                    distanceTv = DecimalFormat("0.00").format(map.drive.viewModel?.status!!.distance) + "M"
//                }
//                map.totalDistance.set(distanceTv)
//            }
//        NaviDistance = naviInfo.m_RouteRemainDis
//        CurDistance = naviInfo.m_RouteRemainDis

        if (naviInfo.iconBitmap != null) {
            map.navigationActivity.anext_turn_view.setImageBitmap(naviInfo.iconBitmap)
        } else {
            map.navigationActivity.anext_turn_view.setIconType(naviInfo.iconType)
        }
        RxBus.default?.post(naviInfo.iconBitmap)

        map.navigationActivity.anext_turn_distance.text = NaviUtil.formatKM(naviInfo.curStepRetainDistance).split("米")[0]
    }

    override fun OnUpdateTrafficFacility(trafficFacilityInfo: TrafficFacilityInfo) {
        //已过时
        Log.e("result", "OnUpdateTrafficFacility")
    }

    override fun OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfo: AMapNaviTrafficFacilityInfo) {
        //已过时
        Log.e("result", "OnUpdateTrafficFacility")
    }

    override fun showCross(aMapNaviCross: AMapNaviCross) {
        //显示转弯回调
        Log.e("result", "showCross")
    }

    override fun hideCross() {
        //隐藏转弯回调
        Log.e("result", "hideCross")
    }

    override fun showLaneInfo(laneInfos: Array<AMapLaneInfo>, laneBackgroundInfo: ByteArray, laneRecommendedInfo: ByteArray) {
        //显示车道信息
        Log.e("result", "showLaneInfo")
    }

    override fun hideLaneInfo() {
        //隐藏车道信息
        Log.e("result", "hideLaneInfo")
    }

    override fun onCalculateRouteSuccess(ints: IntArray) {
        //多路径算路成功回调

        Log.e("result", "onCalculateRouteSuccess")
    }

    override fun notifyParallelRoad(i: Int) {
        if (i == 0) {
//            Toast.makeText(this, "当前在主辅路过渡", Toast.LENGTH_SHORT).show()
            Log.d("wlx", "当前在主辅路过渡")
            return
        }

        if (i == 1) {
//            Toast.makeText(this, "当前在主路", Toast.LENGTH_SHORT).show()

            Log.d("wlx", "当前在主路")
            return
        }

        if (i == 2) {
//            Toast.makeText(this, "当前在辅路", Toast.LENGTH_SHORT).show()

            Log.d("wlx", "当前在辅路")
        }
        Log.e("result", "notifyParallelRoad")
    }

    override fun OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfos: Array<AMapNaviTrafficFacilityInfo>) {
        //更新交通设施信息

        Log.e("result", "OnUpdateTrafficFacility")
    }

    override fun updateAimlessModeStatistics(aimLessModeStat: AimLessModeStat) {
        //更新巡航模式的统计信息
        Log.e("result", "updateAimlessModeStatistics")
    }

    override fun updateAimlessModeCongestionInfo(aimLessModeCongestionInfo: AimLessModeCongestionInfo) {
        //更新巡航模式的拥堵信息
        Log.e("result", "updateAimlessModeCongestionInfo")
    }

    override fun onPlayRing(i: Int) {
        Log.e("result", "onPlayRing")
    }


    override fun onLockMap(isLock: Boolean) {
        //锁地图状态发生变化时回调
        Log.e("result", "onLockMap")
    }

    override fun onNaviViewLoaded() {
        Log.e("result", "onNaviViewLoaded")
        Log.d("wlx", "导航页面加载成功")
        Log.d("wlx", "请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑")
    }

    override fun onMapTypeChanged(i: Int) {
        Log.e("result", "onMapTypeChanged")
    }

    override fun onNaviViewShowMode(i: Int) {
        Log.e("result", "onNaviViewShowMode$i")
        if (i == 1) {
            map.display.set(true)
        }
//        if (i == 3) {
//            map.isStartNavigation.set(false)
//            timer?.start()
////            map.mAMapNaviView?.naviMode = CAR_UP_MODE
//        }
    }

//    var timer: CountDownTimer? = null

    override fun onNaviBackClick(): Boolean {
        return false
    }

    override fun showModeCross(aMapModelCross: AMapModelCross) {
    }

    override fun hideModeCross() {
    }

    override fun updateIntervalCameraInfo(aMapNaviCameraInfo: AMapNaviCameraInfo, aMapNaviCameraInfo1: AMapNaviCameraInfo, i: Int) {
    }

    override fun showLaneInfo(aMapLaneInfo: AMapLaneInfo) {
        //25 18.26 5
    }

    override fun onCalculateRouteSuccess(aMapCalcRouteResult: AMapCalcRouteResult) {
        if (map.navigationActivity?.type > 1) {
            Log.e("result","导航路径选择编号" + map.navigationActivity.type)
            map.mAMapNavi.selectRouteId(map.navigationActivity.type)
        }
        map.mAMapNavi.startNavi(NaviType.GPS)
    }

    override fun onCalculateRouteFailure(aMapCalcRouteResult: AMapCalcRouteResult) {
        Log.e("result","onCalculateRouteFailure" + aMapCalcRouteResult.errorDetail)
    }

    override fun onNaviRouteNotify(aMapNaviRouteNotifyData: AMapNaviRouteNotifyData) {
    }
}