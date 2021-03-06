package com.example.drivermodule.ViewModel

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MyLocationStyle
import com.example.drivermodule.R
import com.zk.library.Base.BaseViewModel
import com.elder.zcommonmodule.Component.DriverComponent
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Drivering
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Inteface.Locationlistener
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.example.drivermodule.BR
import com.example.drivermodule.Controller.DriverItemModel
import com.example.drivermodule.Controller.MapPointItemModel
import com.example.drivermodule.Controller.RoadBookItemModel
import com.example.drivermodule.Controller.TeamItemModel
import com.google.gson.Gson
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.DataBases.UpdateDriverStatus
import com.elder.zcommonmodule.DataBases.insertDriverStatus
import com.elder.zcommonmodule.DriverPause
import com.elder.zcommonmodule.Entity.SoketBody.SoketTeamStatus
import com.elder.zcommonmodule.Entity.StartRidingRequest
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.drivermodule.Fragment.MapFragment
import com.example.drivermodule.Sliding.SlidingUpPanelLayout
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseFragment
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Weidge.NoScrollViewPager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MapFrViewModel : BaseViewModel(), AMap.OnMarkerClickListener, AMap.OnCameraChangeListener, TitleComponent.titleComponentCallBack, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, DriverComponent.onFiveClickListener, HttpInteface.startDriverResult {
    var driverType = 0

    var exTv = ObservableField<String>()

    override fun startDriverSuccess(it: String) {
        mapActivity.dismissProgressDialog()
        status.driverNetRecord = Gson().fromJson(it, StartRidingRequest::class.java)
        status.startDriver.set(Drivering)
        mapActivity.getDrverController().driverStatus.set(Drivering)
        mapActivity.getDrverController().timer = Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        status.StartTime = System.currentTimeMillis()
//        viewModel?.component!!.Drivering.set(true)
        insertDriverStatus(status)
        mapActivity.getDrverController().timerDispose = mapActivity.getDrverController().timer?.subscribe {
            if (status.startDriver.get() != DriverPause) {
                status.second++
                mapActivity.getDrverController().driverTime.set(ConvertUtils.formatTimeS(status.second))
                UpdateDriverStatus(status)
            }
        }
        var wayPoint = ArrayList<LatLng>()
        if (!status?.passPointDatas.isNullOrEmpty()) {
            status?.passPointDatas.forEach {
                wayPoint.add(LatLng(it.latitude, it.longitude))
            }
        }
        if (status.navigationStartPoint == null) {
            status.navigationStartPoint = curPosition
        }

        //默认0，骑行页面点击骑行
        if (driverType != 0) {
            startNavi(wayPoint, driverType)
        }
    }

    override fun startDriverError(error: Throwable) {
        mapActivity.dismissProgressDialog()
    }

    override fun FiveBtnClick(view: View) {
        if (view.id == R.id.setting_btn) {
            if (curPosition != null) {
                mapActivity.mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(curPosition!!.latitude, curPosition!!.longitude), 15F), 1000, object : AMap.CancelableCallback {
                    override fun onFinish() {
                    }

                    override fun onCancel() {
                    }
                })
            }
        } else if (view.id == R.id.sos_btn) {
            var intent = Intent(Intent.ACTION_CALL)
            var data = Uri.parse("tel:120")
            intent.data = data
            mapActivity.startActivity(intent)
        } else {
            if (currentPosition == 0) {
                (items[0] as DriverItemModel).onFiveBtnClick(view)
            } else if (currentPosition == 2) {
//            mapActivity.getMapPointFragment().viewModel?.FiveBtnClick(view)
            } else if (currentPosition == 3) {
//            mapActivity.getRoadBookFragment().viewModel?.FiveBtnClick(view)
            } else if (currentPosition == 1) {
                (items[1] as TeamItemModel).onFiveBtnClick(view)
            }
        }
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        if (p0!!.position == currentPosition) {
            return
        }
        if (p0!!.position == 2 && currentPosition == 3) {
            return
        }
        when (p0?.position) {
            0 -> {
                if (currentPosition == 1) {
                    mapActivity.getTeamController()?.backToDriver()
                } else if (currentPosition == 2) {
                    mapActivity.getRoadBookController()?.backToDriver()
                    changerFragment(0)
                }
            }
            1 -> {
                if (currentPosition == 0) {

                    mapActivity.getDrverController()?.GoTeam()
                } else if (currentPosition == 2) {
                    mapActivity.getRoadBookController()?.backToDriver()
                    mapActivity.getDrverController()?.GoTeam()
                }
            }
            2 -> {
                if (mapActivity.getRoadBookController() == null) {
                    return
                }
                if (currentPosition == 0 || currentPosition == 1) {
                    //跳转到路书
                    if (currentPosition == 1) {
                        mapActivity.getTeamController()?.backToRoad()
                    }
                    if (mapActivity.getRoadBookController()?.netWorkData == null) {
                        var date = PreferenceUtils.getString(mapActivity.activity!!, PreferenceUtils.getString(context, USERID) + "hot")
                        if (date == null && mapActivity.hotData == null) {
                            if (curPosition != null) {
                                var fr = mapActivity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
                                var bundle = Bundle()
                                bundle.putSerializable(RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT, curPosition)
                                bundle.putInt(RouterUtils.MapModuleConfig.ROAD_CURRENT_TYPE, 1)
                                startFragment(fr, RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY, bundle, REQUEST_LOAD_ROADBOOK)
                            }
                        } else {
                            if (date == null) {
                                mapActivity.getRoadBookController()?.data = mapActivity.hotData
                            } else {
                                mapActivity.getRoadBookController()?.data = Gson().fromJson<HotData>(date, HotData::class.java)
                            }
                            changerFragment(2)
                            mapActivity.getRoadBookController()?.doLoadDatas(mapActivity.getRoadBookController()?.data!!)

                        }
                    } else {
                        changerFragment(2)
                        if (mapActivity.getRoadBookController().selecPosition != 0) {
                            mapActivity.getRoadBookController()!!.select_position.set(0)
                        }
                        mapActivity.getRoadBookController()?.recycleComponent?.initDatas(mapActivity.getRoadBookController()?.netWorkData!!, mapActivity.getRoadBookController()?.data, 0)
                    }
                }
            }
        }
    }

    lateinit var status: DriverDataStatus        //本地骑行状态类
    var TeamStatus: SoketTeamStatus? = null      //本地组队状态类，目前作用不大
    var backStatus: Boolean = false              //返回骑行界面/组队界面状态 前往地图选点判断参数
    //    var driverController = DriverController(this)
    var cur = 0L
    var curPosition: Location? = null

    override fun onComponentClick(view: View) {
        if (currentPosition == 3) {
            (items[3] as MapPointItemModel).onComponentClick()
        } else {
            if (showBottomSheet!!.get()!!) {
                var model = items!![0] as DriverItemModel
                resetDriver(model)
            }
            CoroutineScope(uiContext).launch {
                delay(200)
                var even = RxBusEven()
                even.type = RxBusEven.DriverReturnRequest
                RxBus.default?.post(even)
            }
        }
        return
    }


    override fun onComponentFinish(view: View) {
        if (currentPosition == 0) {
            (items[0] as DriverItemModel).onComponentFinish()
        } else if (currentPosition == 1) {
            (items[1] as TeamItemModel).onComponentFinish()
        } else if (currentPosition == 3) {
            (items[3] as MapPointItemModel).onComponentFinish()
        } else if (currentPosition == 2) {
            (items[2] as RoadBookItemModel).onComponentFinish(view)
        }
    }

    var adapter = BindingViewPagerAdapter<ItemViewModel<MapFrViewModel>>()
    var items = ObservableArrayList<ItemViewModel<MapFrViewModel>>()

    var itemBinding = ItemBinding.of<ItemViewModel<MapFrViewModel>> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.driver_item, R.layout.itemmodel_driver)
            }
            1 -> {
                itemBinding.set(BR.team_item, R.layout.itemmodel_team)
            }
            2 -> {
                itemBinding.set(BR.roadbook_item, R.layout.itemmodel_roadbook)
            }
            3 -> {
                itemBinding.set(BR.map_point_item, R.layout.itemmodel_map_point)
            }
        }
    }


    override fun onCameraChangeFinish(p0: CameraPosition?) {
        var even = RxBusEven()
        even.type = RxBusEven.MapCameraChangeFinish
        RxBus.default?.post(even)
    }

    override fun onCameraChange(p0: CameraPosition?) {
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        when (currentPosition) {
            2 -> {
                mapActivity.getRoadBookController()?.markerChange(p0!!)
            }
        }
        return false
    }


    var component = DriverComponent()


    var showBottomSheet = ObservableField<Boolean>(false)
    var mFragments = ArrayList<Fragment>()
    lateinit var mapActivity: MapFragment
    fun inject(mapActivity: MapFragment) {
        this.mapActivity = mapActivity
        items.apply {
            this.add(DriverItemModel().ItemViewModel(this@MapFrViewModel))
            this.add(TeamItemModel().ItemViewModel(this@MapFrViewModel))
            this.add(RoadBookItemModel().ItemViewModel(this@MapFrViewModel))
            this.add(MapPointItemModel().ItemViewModel(this@MapFrViewModel))
        }
        initTab()
        component.setHomeStyle()
        component.setCallBack(this)
        component.setOnFiveClickListener(this)

//        changerFragment(0)
    }


    fun receiveLocation(it: AMapLocation) {
        if (it.errorCode != 0 || it.gpsAccuracyStatus == -1) {

        }
        if (items.size != 0) {
            (items[0] as DriverItemModel).onLocation(it)
            (items[1] as TeamItemModel).onLocation(it)
        }
    }

    var listeners: ArrayList<Locationlistener> = ArrayList()

    lateinit var tab: TabLayout
    lateinit var fr_main_rootlay: NoScrollViewPager
    private fun initTab() {
        tab = mapActivity.binding?.root!!.findViewById(R.id.topTab)
        fr_main_rootlay = mapActivity.binding?.root!!.findViewById(R.id.fr_main_rootlay)
        tab.addTab(tab.newTab().setText(getString(R.string.driver)))
        tab.addTab(tab.newTab().setText(getString(R.string.team)))
        tab.addTab(tab.newTab().setText(getString(R.string.road_book_nomal_title)))
        tab.addOnTabSelectedListener(this)
    }

    fun selectTab(position: Int) {
        var tabs = tab.getTabAt(position)
        tabs?.select()
    }

    var currentPosition = 0
    fun changerFragment(position: Int) {
        currentPosition = position
        if (position == 0) {
            if (status.startDriver.get() == Drivering) {
                mapActivity.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
            } else {
                mapActivity.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
            }
        } else {
            mapActivity.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        }
        mapActivity.mAmap.myLocationStyle = mapActivity.myLocationStyle
        if (position == 2) {
            component.Drivering.set(false)
            component.rightIcon.set(context.getDrawable(R.drawable.three_point))
        } else {
            component.Drivering.set(true)
            component.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
        }
        if (position == 3) {
            component.titleVisible.set(true)
            tab.visibility = View.GONE
            component.rightVisibleType.set(true)
            component.title.set(getString(R.string.location_select))
            component.rightText.set(getString(R.string.road_detail))
            component.type.set(0)
        } else {
            component.titleVisible.set(false)
            component.rightVisibleType.set(false)
            component.title.set("")
            tab.visibility = View.VISIBLE
            component.rightText.set("")
            component.type.set(1)
        }
        if (mapActivity.isAdded) {
            fr_main_rootlay.currentItem = currentPosition
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.fr_share_btn -> {
                var fr = mapActivity.getDrverController()
                resetDriver(fr)
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.MapModuleConfig.SHARE_ENTITY, fr?.share!!)
                startFragment(mapActivity.parentFragment!!, RouterUtils.MapModuleConfig.SHARE_ACTIVITY, bundle)
            }
        }
    }


    fun resetDriver(fr: DriverItemModel) {
        //骑行结束底部分享按钮
        showBottomSheet.set(false)
        fr.panelState.set(SlidingUpPanelLayout.PanelState.HIDDEN)
        fr.bottomLayoutVisible.set(true)
        status = DriverDataStatus()
        component.Drivering.set(true)
        fr.driverDistance.set("0M")
        fr.driverTime.set("00:00")
    }

    fun startDriver(type: Int) {
        this.driverType = type
        //开始骑行逻辑操作
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
            return
        }
        mapActivity.showProgressDialog(getString(R.string.start_driver))
        HttpRequest.instance.startDriver = this
        HttpRequest.instance.startDriver(HashMap())
    }

    fun startNavi(wayPoint: ArrayList<LatLng>, b: Int) {
        if (status.navigationEndPoint != null && status.navigationStartPoint != null) {
            var list = ArrayList<LatLng>()
            if (wayPoint != null && wayPoint.size != 0) {
                wayPoint.forEach {
                    list.add(it)
                }
            }
            if (b == 1) {
                //骑行页面搜索点击开始导航
                status.navigationType = 1
            } else if (b == 2) {
                //地图选点点击开始导航
            } else if (b == 3) {
                status.navigationType = 1
                //组队页面点击开始导航
            } else if (b == 4) {
                //路书界面点击开始导航
                status.navigationType = 1
            }
            if (BaseApplication.MinaConnected) {
                //组队状态下，队长分发导航信息
                if (mapActivity.getTeamController().teamer.toString() == mapActivity.user.data!!.id) {
                    mapActivity.getTeamController().sendNavigationNotify()
                }
            }

            //开始导航
            mapActivity?.mAmap?.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(status.navigationStartPoint!!.latitude, status.navigationStartPoint!!.longitude)))
            if (status.navigationStartPoint != null && status.navigationEndPoint != null) {
                mapActivity.NavigationStart = true
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.NAVIGATION)
                        .withSerializable(RouterUtils.MapModuleConfig.NAVIGATION_DATA, list)
                        .withSerializable(RouterUtils.MapModuleConfig.NAVIGATION_START, status.navigationStartPoint)
                        .withSerializable(RouterUtils.MapModuleConfig.NAVIGATION_End, status.navigationEndPoint)
                        .withInt(RouterUtils.MapModuleConfig.NAVIGATION_TYPE, status.navigationType)
                        .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                        .navigation()
            }
        }
    }
}