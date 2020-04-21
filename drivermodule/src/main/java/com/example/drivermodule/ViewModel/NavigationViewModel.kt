package com.example.drivermodule.ViewModel

import android.databinding.ObservableField
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviView
import com.amap.api.navi.model.NaviLatLng
import com.zk.library.Base.BaseViewModel
import org.cs.tec.library.Base.Utils.context
import com.amap.api.services.route.RouteSearch
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Component.DriverComponent
import com.elder.zcommonmodule.Entity.SoketBody.SoketNavigation
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.drivermodule.AMapNaviViewComponent
import com.example.drivermodule.Activity.NavigationActivity
import com.example.drivermodule.R
import com.example.drivermodule.Utils.TTSController
import com.zk.library.Base.BaseApplication
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import java.lang.Exception
import kotlin.collections.ArrayList


class NavigationViewModel : BaseViewModel() {
    var mAMapNaviView: AMapNaviView? = null
    var mTtsManager: TTSController? = null
    var mEndLatlng: NaviLatLng? = null
    var mStartLatlng: NaviLatLng? = null
    var isRecoverLockMode = 0
    var drivercomponent = DriverComponent()
    var totalTime = ObservableField<String>("00:00")
    var totalDistance = ObservableField<String>("0M")
    var nextAddress = ObservableField<String>("")

    var sList: MutableList<NaviLatLng> = ArrayList()
    var eList: MutableList<NaviLatLng> = ArrayList()
    var mWayPointList: MutableList<NaviLatLng> = ArrayList()
    var component = AMapNaviViewComponent(this)
    lateinit var navigationActivity: NavigationActivity
    lateinit var mAMapNavi: AMapNavi

    var navigation: SoketNavigation? = null
    fun inject(navigationActivity: NavigationActivity) {
        this.navigationActivity = navigationActivity
        mAMapNaviView = navigationActivity.anavi_view
        var opition = mAMapNaviView?.viewOptions
        opition?.isAutoLockCar = true
        mAMapNaviView?.viewOptions = opition
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
        mAMapNavi.setUseInnerVoice(true)

        mStartLatlng = NaviLatLng(navigationActivity.start!!.latitude, navigationActivity.start!!.longitude)
        mEndLatlng = NaviLatLng(navigationActivity.end!!.latitude, navigationActivity.end!!.longitude)
        navigationActivity.list?.forEach {
            mWayPointList.add(NaviLatLng(it.latitude, it.longitude))
        }
        startNavi()
    }

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it?.type) {
            RxBusEven.DriverCancleByNavigation -> {
                mAMapNavi.stopNavi()
                mAMapNaviView?.onDestroy()
                mAMapNavi.destroy()
                navigationActivity.finish()
            }
            RxBusEven.DriverNavigationRouteChange -> {
                var it = it.value as SoketNavigation
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
            }
            RxBusEven.DriverNavigationChange -> {

            }
        }
    }

    fun startNavi() {
        if (navigationActivity?.type == Driver_Navigation) {
            component.naviDatasPoint.clear()
            component.NaviStartTime = 0
            component.NaviDistance = 0
            component.CurDistance = 0
            component.NaviStartTime = System.currentTimeMillis()
        }
        sList.clear()
        eList.clear()
        sList.add(mStartLatlng!!)
        eList.add(mEndLatlng!!)
        var strategy = RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION
        try {
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy)
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.navi_arrow -> {
                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
                if (navigationActivity.type < 1) {
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
        mStartLatlng = NaviLatLng(component.location?.coord?.latitude!!, component.location?.coord!!.longitude)
        mEndLatlng = NaviLatLng(it.navigation_end?.latitude!!, it.navigation_end?.longitude!!)
        mWayPointList.clear()
        if (!it.wayPoint.isNullOrEmpty()) {
            it.wayPoint!!.forEach {
                mWayPointList.add(NaviLatLng(it?.latitude!!, it?.longitude!!))
            }
        }
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
            BaseApplication.getInstance().curActivity = 1
            RxBus.default?.post(RxBusEven.getInstance(RxBusEven.NAVIGATION_FINISH))
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue").navigation(navigationActivity, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    navigationActivity.finish()
                }
            })
        }
    }
}