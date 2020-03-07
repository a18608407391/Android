package com.example.drivermodule.ViewModel

import android.databinding.ObservableField
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviView
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.navi.view.TrafficBarView
import com.amap.api.navi.view.TrafficButtonView
import com.example.drivermodule.AMapNaviViewComponent
import com.example.drivermodule.TTSController
import com.zk.library.Base.BaseViewModel
import org.cs.tec.library.Base.Utils.context
import com.amap.api.services.route.RouteSearch
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Component.DriverComponent
import com.elder.zcommonmodule.DataBases.UpdateDriverStatus
import com.elder.zcommonmodule.DataBases.queryDriverStatus
import com.elder.zcommonmodule.Entity.SoketBody.SoketNavigation
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.drivermodule.AMapUtil
import com.example.drivermodule.Activity.MapActivity
import com.example.drivermodule.Activity.NavigationActivity
import com.example.drivermodule.R
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import java.lang.Exception
import kotlin.collections.ArrayList


class NavigationViewModel : BaseViewModel() {
    var mAMapNaviView: AMapNaviView? = null
    var mTtsManager: TTSController? = null
    var mEndLatlng: NaviLatLng? = null
    var mStartLatlng: NaviLatLng? = null
    var isRecoverLockMode = 0
    var drivercomponent = DriverComponent()
    var isStartNavigation = ObservableField<Boolean>(true)
    var totalTime = ObservableField<String>("00:00")
    var totalDistance = ObservableField<String>("0M")
    var nextAddress = ObservableField<String>("")

    var sList: MutableList<NaviLatLng> = ArrayList()
    var eList: MutableList<NaviLatLng> = ArrayList()
    var mWayPointList: List<NaviLatLng>? = null
    var component = AMapNaviViewComponent(this)
    lateinit var navigationActivity: NavigationActivity
    lateinit var mAMapNavi: AMapNavi

    var navigation: SoketNavigation? = null
    fun inject(navigationActivity: NavigationActivity) {
        this.navigationActivity = navigationActivity
        mAMapNaviView = navigationActivity.anavi_view
        RxSubscriptions.add(RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "DriverCancle") {
                mAMapNavi.stopNavi()
                mAMapNaviView?.onDestroy()
                mAMapNavi.destroy()
                navigationActivity.finish()
            }
        })
        var opition = mAMapNaviView?.viewOptions
        opition?.isAutoLockCar = true
        mAMapNaviView?.viewOptions = opition
        RxSubscriptions.add(RxBus.default?.toObservable(SoketNavigation::class.java)?.subscribe {
            this.navigation = it
            if (it.type == null) {
                if (!navigationActivity.onStart) {
                    changeRoute(it)
                } else {
                    CoroutineScope(uiContext).launch {
                        createDistrictDialog()
                    }
                }
            } else {
                changeRoute(it)
            }
        })
        var viewOptions = mAMapNaviView?.viewOptions

//主动隐藏蚯蚓线
        mAMapNaviView?.setOnMapTouchListener {
            if (MotionEvent.ACTION_UP == it.action) {
                isRecoverLockMode = 0
            } else {
                isRecoverLockMode = 3
            }
        }
        viewOptions?.isLayoutVisible = false
        viewOptions?.isTrafficBarEnabled = false
//        navigationActivity.next_turn_view.setColorFilter(Color.WHITE)
        mAMapNaviView?.lazyNextTurnTipView = navigationActivity.anext_turn_view
        navigationActivity.progress.setUnknownTrafficColor(Color.BLUE)
        navigationActivity.progress.setSmoothTrafficColor(Color.GREEN)
        navigationActivity.progress.setSlowTrafficColor(Color.YELLOW)
        navigationActivity.progress.setJamTrafficColor(Color.DKGRAY)
        navigationActivity.progress.setVeryJamTrafficColor(Color.BLACK)
        viewOptions?.isTrafficLine = false
        viewOptions?.isTrafficLayerEnabled = false
        viewOptions?.isReCalculateRouteForTrafficJam = false
        mAMapNaviView?.viewOptions = viewOptions
        navigationActivity.anavi_view.setAMapNaviViewListener(component)
        mAMapNavi = AMapNavi.getInstance(context)
        mAMapNavi.addAMapNaviListener(component)
        mAMapNavi.setEmulatorNaviSpeed(250)
        mAMapNavi.setUseInnerVoice(true)
        startNavi()
    }

    fun startNavi() {
        if (navigationActivity?.type == Driver_Navigation) {
            component.naviDatasPoint.clear()
            component.NaviStartTime = 0
            component.NaviDistance = 0
            component.CurDistance = 0
            component.NaviStartTime = System.currentTimeMillis()
        }
        var wayPoint = ArrayList<NaviLatLng>()
        navigationActivity.list?.forEachIndexed { index, latLng ->
            if (index == 0) {
                mStartLatlng = converNaviLatLngPoint(converLatPoint(latLng))
            } else if (index == navigationActivity.list!!.size - 1) {
                mEndLatlng = converNaviLatLngPoint(converLatPoint(latLng))
            } else {
                wayPoint.add(converNaviLatLngPoint(converLatPoint(latLng)))
            }
        }
        mWayPointList = wayPoint
        sList.add(mStartLatlng!!)
        eList.add(mEndLatlng!!)
        var strategy = RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION
        try {
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e("result", "开始导航路径规划")
        mAMapNavi.calculateDriveRoute(sList, eList, wayPoint, strategy)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.navi_arrow -> {
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).navigation()
                if (navigationActivity.type != 1) {
                    finish()
                }
            }
            R.id.stop_navagation -> {
                mAMapNavi.stopNavi()
                stop()
            }
            R.id.all_model -> {
                if (mAMapNaviView?.isRouteOverviewNow!!) {
                    display.set(true)
                    mAMapNaviView?.recoverLockMode()
                } else {
                    display.set(false)
                    mAMapNaviView?.displayOverview()
                }
            }
        }
    }

    var display = ObservableField<Boolean>()


    fun changeRoute(it: SoketNavigation) {
        navigationActivity.list?.clear()
        navigationActivity.list?.add(LatLng(component.location?.coord?.latitude!!, component.location?.coord!!.longitude))
        if (!it.wayPoint.isNullOrEmpty()) {
            it.wayPoint!!.forEach {
                navigationActivity.list?.add(AMapUtil.convertToLatLng(it))
            }
        }
        navigationActivity.list?.add(LatLng(it.navigation_end?.latitude!!, it.navigation_end?.longitude!!))
        startNavi()
    }

    var notifyRouteChange: NormalDialog? = null
    fun createDistrictDialog() {
        if (notifyRouteChange == null) {
            notifyRouteChange = DialogUtils.createNomalDialog(navigationActivity, getString(R.string.route_change), getString(R.string.ignore), getString(R.string.change_route))
            notifyRouteChange!!.setOnBtnClickL(OnBtnClickL {
                notifyRouteChange?.dismiss()
            }, OnBtnClickL {
                notifyRouteChange?.dismiss()
                changeRoute(navigation!!)
            })
        }
        notifyRouteChange?.show()
    }

    fun stop() {
        //结束导航
        Toast.makeText(context, getString(R.string.navigation_will_close), Toast.LENGTH_SHORT).show()
        CoroutineScope(uiContext).launch {
            delay(2000)
            mAMapNavi.stopNavi()
            mAMapNaviView?.onDestroy()
            mAMapNavi.destroy()
            BaseApplication.getInstance().curActivity = 2
            if (AppManager.get()?.getActivity(MapActivity::class.java) != null) {
                RxBus.default?.post("NavigationFinish")
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue").navigation(navigationActivity,object :NavCallback(){
                    override fun onArrival(postcard: Postcard?) {
                        navigationActivity.finish()
                    }
                })
            } else {
                Log.e("result","MapActivity为空")
             var status =  queryDriverStatus(PreferenceUtils.getString(context, USERID))[0]
                status.navigationType = 0
                status.passPointDatas.clear()
                UpdateDriverStatus(status)
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue").navigation(navigationActivity,object :NavCallback(){
                    override fun onArrival(postcard: Postcard?) {
                        navigationActivity.finish()
                    }
                })
            }
        }


//        if (drive.viewModel?.status?.navigationType == Driver_Navigation) {
//            drive.viewModel?.status?.startDriver?.set(Drivering)
//            if (component.naviDatasPoint.size != 0) {
//                drive.viewModel?.driverController?.naviDatasPoint?.clear()
//                component.naviDatasPoint.forEach {
//                    drive.viewModel?.driverController?.naviDatasPoint?.add(it)
//                }
//                if (component.CurDistance != 0) {
//                    drive.viewModel?.status!!.distance = drive.viewModel?.status!!.distance + component.NaviDistance - component.CurDistance
//                }
//            }
//        } else if (drive.viewModel?.status?.navigationType == Nomal_Navigation) {
//            drive.viewModel?.status?.startDriver?.set(DriverCancle)
//        }
//        drive.viewModel?.UiChange(0)
//        if (boolean) {
//            drive.viewModel?.driverController?.driverOver()
//        }
//        drive.viewModel?.status!!.navigationType = 0
//        drive.viewModel?.mapPointController?.endPoint = null
    }
}