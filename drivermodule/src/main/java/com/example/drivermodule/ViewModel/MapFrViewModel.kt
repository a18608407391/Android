package com.example.drivermodule.ViewModel

import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
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
import com.example.drivermodule.Fragment.RoadHomeActivity
import com.example.drivermodule.Fragment.MapFragment
import com.zk.library.Base.BaseFragment
import com.zk.library.Bus.ServiceEven
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_map.*
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MapFrViewModel : BaseViewModel(), AMap.OnMarkerClickListener, AMap.OnMarkerDragListener, AMap.OnCameraChangeListener, TitleComponent.titleComponentCallBack, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, DriverComponent.onFiveClickListener, HttpInteface.startDriverResult {
    var driverType = 0

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
        Log.e("result", "开始骑行后" + Gson().toJson(status))
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
                        } else {
                            mapActivity.getRoadBookController()?.recycleComponent?.initDatas(mapActivity.getRoadBookController()?.netWorkData!!, mapActivity.getRoadBookController()?.data, 0)
                        }
                    }
                }
            }
        }
    }

    lateinit var status: DriverDataStatus
    var TeamStatus: SoketTeamStatus? = null
    var backStatus: Boolean = false
    //    var driverController = DriverController(this)
    var cur = 0L
    var curPosition: Location? = null

    override fun onComponentClick(view: View) {

        if (currentPosition == 3) {
            (items[3] as MapPointItemModel).onComponentClick()
        } else {
            var even = RxBusEven()
            even.type = RxBusEven.DriverReturnRequest
            RxBus.default?.post(even)
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

    override fun onMarkerDragEnd(p0: Marker?) {
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
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
    var a: Disposable? = null
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
        a = RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            if (listeners != null) {
                listeners.forEachIndexed { index, locationlistener ->
                    locationlistener?.onLocation(it)
                }
            }
        }

        RxSubscriptions.add(a)
        component.setCallBack(this)
        component.setOnFiveClickListener(this)

//        changerFragment(0)
    }


    var listeners: ArrayList<Locationlistener> = ArrayList()

    lateinit var tab: TabLayout
    private fun initTab() {
        tab = mapActivity.binding?.root!!.findViewById(R.id.topTab)
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
            mapActivity.fr_main_rootlay.currentItem = currentPosition
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.fr_share_btn -> {
                var fr = mapActivity.getDrverController()

                showBottomSheet.set(false)
//                RxBus.default?.postSticky(fr?.viewModel?.share!!)
//                fr.behaviors.isHideable = true
//                fr.behaviors.state = BottomSheetBehavior.STATE_EXPANDED
                a?.dispose()
                fr.cancleDriver(true)
                startFragment(mapActivity.parentFragment!!, RouterUtils.MapModuleConfig.SHARE_ACTIVITY)
//                ARouter.getInstance().build(RouterUtils.MapModuleConfig.SHARE_ACTIVITY).navigation(mapActivity.activity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
            }
        }
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
                status.navigationType = 1
            }
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