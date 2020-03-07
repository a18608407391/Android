package com.example.drivermodule.ViewModel

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.route.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.Location
import com.example.drivermodule.AMapUtil
import com.example.drivermodule.Activity.MapActivity
import com.example.drivermodule.Adapter.AddPointAdapter
import com.example.drivermodule.BR
import com.example.drivermodule.Component.MapPointController
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.Entity.RouteEntity
import com.example.drivermodule.Overlay.DrivingRouteOverlay
import com.example.drivermodule.R
import com.example.drivermodule.Ui.MapPointFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.float_district_layout.*
import kotlinx.android.synthetic.main.fragment_map_point.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.Utils.ConvertUtils
import java.text.DecimalFormat


class MapPointViewModel : BaseViewModel, RouteSearch.OnRouteSearchListener, BaseQuickAdapter.OnItemChildClickListener {
    var CurrentClickTime: Long = 0
    fun onComponentClick(view: View) {
        Log.e("result", "返回骑行界面")
        if (System.currentTimeMillis() - CurrentClickTime < 1500) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        returnDriverFr()
    }

    fun onComponentFinish(view: View) {
        SearchPoint()
    }

    var routeDistance = 0F
    var routeTime = 0L
    var p0: DriveRouteResult? = null
    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
        mapActivity.mAmap.clear()
        if (p1 == 1000) {
            mapActivity.dismissProgressDialog()
            this.p0 = p0
            var currentDistance = 0F
            var currentTime = 0L
            var moneyS = 0F
            var distanceIndex = 0   //距离最短
            var timeIndex = 0        //时间最少
            var money = 0              //花费最少
            var other = -1
            p0?.paths?.forEachIndexed { index, drivePath ->
                if (index == 0) {
                    currentDistance = drivePath.distance
                    currentTime = drivePath.duration
                    moneyS = drivePath.tolls
                } else {
                    if (currentDistance > drivePath.distance) {
                        currentDistance = drivePath.distance
                        distanceIndex = index
                    }
                    if (currentTime > drivePath.duration) {
                        currentTime = drivePath.duration
                        timeIndex = index
                    }
                    if (moneyS > drivePath.tolls) {
                        moneyS = drivePath.tolls
                        money = index
                    }
                }
            }
            if (!items.isEmpty()) {
                items.clear()
            }
            p0!!.paths.forEachIndexed { index, drivePath ->
                Log.e("result", drivePath.duration.toString() + "规划时间")
                Log.e("result", drivePath.distance.toString() + "规划距离")
                var entity = RouteEntity()
                if (index == 0) {
                    entity.select.set(true)
                }
                var count = 0
                if (timeIndex == index) {
                    entity.title.set(getString(R.string.speed_min))
                } else if (distanceIndex == index) {
                    entity.title.set(getString(R.string.distance_min))
                } else if (money == index) {
                    entity.title.set(getString(R.string.money_min))
                } else {
                    var str = ""
                    if (count == 0) {
                        str = "一"
                    } else if (count == 1) {
                        str = "二"
                    }
                    entity.title.set(getString(R.string.other_min) + str)
                }
                entity.time.set(ConvertUtils.millis2FitTimeSpan(drivePath.duration * 1000, 3))
                if (drivePath.tolls < 1) {
                    entity.distance.set(DecimalFormat("0.00").format(drivePath.distance / 1000) + "KM")
                } else {
                    entity.distance.set(DecimalFormat("0.00").format(drivePath.distance / 1000) + "KM" + " " + drivePath.tolls + "￥")
                }
                items.add(entity)
            }

            items.sortBy {
                it.title.get()!!.length
            }
            initLinear()
            drawRouteLine(0)
//            mapUtils?.CurState = MapUtils.OnShareState
//            fr.map_view.map.setOnCameraChangeListener(mapUtils)
//            mapActivity?.mViewModel?.showBottomSheet?.set(true)
//            fr.map_view.map.getMapScreenShot(mapUtils)
//            fr.map_view.invalidate()
            choiceVisible.set(true)
        } else {
            if (p1 == 1102) {
                Toast.makeText(context, "高德服务端请求链接超时", Toast.LENGTH_SHORT).show()
            } else if (p1 == 1103) {
                Toast.makeText(context, "读取服务结果返回超时", Toast.LENGTH_SHORT).show()
            } else if (p1 == 1101) {
                Toast.makeText(context, "引擎返回数据异常", Toast.LENGTH_SHORT).show()
            } else if (p1 == 1806) {
                Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show()
            }
            mapActivity.dismissProgressDialog()
        }
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
    }

    lateinit var mapActivity: MapActivity
    var dataEmpty = ObservableField<Boolean>(false)
    var mapPointController = MapPointController(this)
    //地图选点终点文本
    var choiceVisible = ObservableField<Boolean>(false)
    var RegeocodeResultDispose: Disposable? = null
    var finalyText = ObservableField<String>(getString(R.string.location_select))
    lateinit var pointAdapter: AddPointAdapter
    lateinit var mapPointFragment: MapPointFragment
    lateinit var driverModel: DriverViewModel
    fun inject(mapPointFragment: MapPointFragment) {
        mapActivity = mapPointFragment.activity as MapActivity
        this.mapPointFragment = mapPointFragment
        this.driverModel = mapActivity.getDrverFragment().viewModel!!
        initView(mapActivity)
//        component.title.set(getString(R.string.line_route))
//        component.rightVisibleType.set(true)
//        component.rightText.set(getString(R.string.road_detail))
//        component.arrowVisible.set(false)
//        component.setCallBack(this)
        mapActivity.mapUtils!!.mRoutePath.setRouteSearchListener(this)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        mapPointController.pointList.removeAt(position)
        pointAdapter?.setNewData(mapPointController.pointList)
        mapActivity.getDrverFragment().viewModel?.status!!.passPointDatas.removeAt(position)
        mapActivity.mapUtils?.setDriverRoute(converLatPoint(mapPointController.startMaker?.position!!), LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), mapActivity.getDrverFragment().viewModel!!.status!!.passPointDatas)
    }

    var StringDispose: Disposable? = null


    fun drawRouteLine(index: Int) {
        var drivingRouteOverlay = DrivingRouteOverlay(context, mapActivity.mAmap, p0?.paths!![index], p0?.startPos, if (mapPointController.finallyMarker == null) p0?.targetPos else LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), p0?.driveQuery!!.passedByPoints)
        mapActivity.mAmap.isMyLocationEnabled = false
        drivingRouteOverlay.setNodeIconVisibility(true)//设置节点marker是否显示
        drivingRouteOverlay.setThroughPointIconVisibility(true)
        drivingRouteOverlay.setIsColorfulline(true)//是否用颜色展示交通拥堵情况，默认true
        drivingRouteOverlay.removeFromMap()
        drivingRouteOverlay.addToMap()
        routeDistance = p0?.paths!![index].distance
        routeTime = p0?.paths!![index].duration
        if (!p0!!.driveQuery.hasPassPoint()) {
            mapPointController.screenMaker = drivingRouteOverlay.addRemoveMarker(AMapUtil.convertToLatLng(LatLonPoint(driverModel.status.navigationStartPoint!!.latitude, driverModel.status.navigationStartPoint!!.longitude)))
            mapPointController.finallyMarker = drivingRouteOverlay?.endMarker
            drivingRouteOverlay.zoomSpanAll()
        } else {
            mapPointController.screenMaker = drivingRouteOverlay.addRemoveMarker(AMapUtil.convertToLatLng(p0?.driveQuery!!.passedByPoints[p0?.driveQuery!!.passedByPoints.size - 1]))
        }
    }


    constructor() {
        RegeocodeResultDispose = RxBus.default?.toObservable(RegeocodeResult::class.java)?.subscribe {
            var addressName = ""
            if (it.regeocodeAddress?.aois != null && it.regeocodeAddress?.aois?.size != 0) {
                if (it.regeocodeAddress.aois[0].aoiName != null) {
                    addressName = it.regeocodeAddress?.district + it.regeocodeAddress.aois[0].aoiName
                } else {
                    addressName = it.regeocodeAddress?.formatAddress!!
                }
            } else {
                addressName = it.regeocodeAddress?.formatAddress!!
            }
            mapPointController?.screenMaker?.title = addressName
            if (mapPointController.screenMaker != null) {
                mapPointController.screenMaker?.showInfoWindow()
            }
        }
        StringDispose = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "onCameraChangeFinish") {
                if (mapPointController.screenMaker != null) {
                    mapActivity.mapUtils?.queryGeocoder(LatLonPoint(mapPointController.screenMaker?.position?.latitude!!, mapPointController.screenMaker?.position?.longitude!!))
                }
            }
        }
        RxSubscriptions.add(StringDispose)
        RxSubscriptions.add(RegeocodeResultDispose)
    }

    fun returnDriverFr() {
        //关闭地图选点
        p0 = null
        choiceVisible.set(false)
        mapPointFragment.bottom_route_choice.removeAllViews()
        items.clear()
        mapActivity.mViewModel?.changerFragment(0)
        mapActivity.getDrverFragment().viewModel?.status!!.curModel = 0
        mapActivity.mAmap.clear()
        mapActivity.mAmap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        if (mapActivity.getDrverFragment().viewModel?.status!!.startDriver.get() == Prepare_Navigation || mapActivity.getDrverFragment().viewModel?.status!!.startDriver.get() == DriverPause || mapActivity.getDrverFragment().viewModel?.status!!.startDriver.get() == Drivering) {
            if (mapActivity.getDrverFragment().viewModel?.status!!.startDriver.get() == Prepare_Navigation) {
                mapPointController.reset()
                mapActivity.mAmap.isMyLocationEnabled = true
                mapActivity.getDrverFragment().viewModel?.status!!.startDriver.set(DriverCancle)
            } else {
                mapPointController.reset()
                mapActivity.mAmap.isMyLocationEnabled = true
                mapActivity.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
                mapActivity.myLocationStyle.showMyLocation(false)
                mapActivity.mAmap.myLocationStyle = mapActivity.myLocationStyle
                mapActivity.getDrverFragment().viewModel!!.driverController.continueDriver()
            }
        } else {
            //Cancle 了
            mapPointController.reset()
            mapActivity.mAmap.isMyLocationEnabled = true
            mapActivity.getDrverFragment().viewModel?.status!!.locationLat.clear()
        }
        if (mapActivity.getDrverFragment().viewModel?.status?.navigationType == 0) {
            mapActivity.getDrverFragment().viewModel?.status!!.passPointDatas.clear()
            mapActivity.getDrverFragment().viewModel?.status!!.navigationStartPoint = null
            mapActivity.getDrverFragment().viewModel?.status!!.navigationEndPoint = null
        }
//        component.setHomeStyle()
        finalyText.set(getString(R.string.location_select))
        var t = mapPointController.SingleList.get(0)
        t.address = ""
        mapPointController.SingleList[0] = t
        pointAdapter.notifyDataSetChanged()

        if (mapActivity.getDrverFragment().viewModel?.backStatus!!) {
//            mapActivity.getDrverFragment().viewModel?.GoTeam()
            mapActivity.mViewModel?.selectTab(1)
        }
    }

    fun startNavigation() {
        //开始导航
        driverModel?.status.navigationDistance = mapActivity.getMapPointFragment().viewModel?.routeDistance!!
        driverModel?.status.navigationTime = mapActivity.getMapPointFragment().viewModel?.routeTime!!
        if (mapActivity.getDrverFragment().viewModel?.status!!.startDriver.get() == DriverCancle) {
            mapActivity.getDrverFragment().viewModel?.startDrive(true)
        } else {
            var wayPoint = ArrayList<LatLng>()
            Log.e("result", "途经点数量" + driverModel?.status.passPointDatas.size.toString())
            driverModel?.status.passPointDatas.forEach {
                wayPoint.add(LatLng(it.latitude, it.longitude))
            }
            mapActivity.getDrverFragment().viewModel?.startNavi(driverModel.status?.navigationStartPoint!!, driverModel?.status?.navigationEndPoint!!, wayPoint, false)
        }
    }

    fun SearchPoint() {
        //搜索点
        if (driverModel.status.navigationEndPoint == null) {
            Toast.makeText(context, getString(R.string.no_passPoint), Toast.LENGTH_SHORT).show()
            return
        }
        var datas = ArrayList<LatLonPoint>()
        datas.add(0, LatLonPoint(driverModel.status.navigationStartPoint!!.latitude, driverModel.status.navigationStartPoint!!.longitude))
        mapActivity.getDrverFragment().viewModel?.status!!.passPointDatas.forEach {
            datas.add(LatLonPoint(it.latitude, it.longitude))
        }
        datas.add(LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude))
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_DETAIL)
                .withFloat(RouterUtils.MapModuleConfig.ROAD_DISTANCE, routeDistance)
                .withLong(RouterUtils.MapModuleConfig.ROAD_TIME, routeTime)
                .withSerializable(RouterUtils.MapModuleConfig.ROAD_DATA, datas)
                .navigation()
    }

    fun SearchResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //搜索回调
        Log.e("result", "搜索返回" + requestCode)
        if (data?.extras != null) {
            var tip = data?.extras!!["tip"] as PoiItem
            if (data?.extras!!["tip"] != null) {
                if (requestCode == RESULT_STR) {
                    if (mapPointController?.finallyMarker == null) {
                        driverModel.status.navigationEndPoint = Location(tip.latLonPoint.latitude, tip.latLonPoint.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, tip.title)
                        finalyText?.set(tip.title)
                        mapActivity?.mapUtils?.setDriverRoute(converLatPoint(mapPointController?.startMaker?.position!!), LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), mapActivity.getDrverFragment().viewModel?.status?.passPointDatas!!)
                    } else {
                        mapActivity.getDrverFragment().viewModel?.status?.passPointDatas?.add(Location(tip.latLonPoint.latitude, tip.latLonPoint.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, tip.title))
                        mapPointController?.pointList?.add(PointEntity(tip.title, tip.latLonPoint))
                        var PointEntity = mapPointController?.SingleList?.get(0)
                        PointEntity?.address = getString(R.string.way_point) + mapPointController?.pointList?.size + "个"
                        mapPointController?.SingleList?.set(0, PointEntity!!)
                        pointAdapter?.notifyDataSetChanged()
                        mapActivity?.mapUtils?.setDriverRoute(converLatPoint(mapPointController?.startMaker?.position!!), LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), mapActivity.getDrverFragment().viewModel?.status?.passPointDatas!!)
                    }
                } else if (requestCode == EDIT_FINAL_POINT) {
                    if (tip.latLonPoint?.latitude != null && tip.latLonPoint?.longitude != null) {
                        if (mapPointController?.finallyMarker != null) {
                            mapPointController?.finallyMarker?.position = AMapUtil.convertToLatLng(tip.latLonPoint)
                            finalyText?.set(tip.title)
                            driverModel.status.navigationEndPoint = Location(tip.latLonPoint.latitude, tip.latLonPoint.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, tip.title)
                            mapActivity?.mapUtils?.setDriverRoute(converLatPoint(mapPointController?.startMaker?.position!!), LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), mapActivity.getDrverFragment().viewModel?.status?.passPointDatas!!)
                        } else {
                            driverModel.status.navigationEndPoint = Location(tip.latLonPoint.latitude, tip.latLonPoint.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, tip.title)
                            finalyText?.set(tip.snippet)


//                            mapActivity.getDrverFragment().viewModel!!.driverController?.movemaker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.finaly_point))


                            mapPointController?.finallyMarker = mapActivity?.mAmap!!.addMarker(MarkerOptions().position(AMapUtil.convertToLatLng(tip.latLonPoint)).anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.select_point)))
                            mapActivity?.mapUtils?.setDriverRoute(converLatPoint(mapPointController?.startMaker?.position!!), LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), mapActivity.getDrverFragment().viewModel!!.status?.passPointDatas!!)
                            if (mapActivity.getDrverFragment().viewModel?.status?.startDriver?.get() == DriverCancle) {
                                mapActivity.getDrverFragment().viewModel?.status?.startDriver?.set(DriverCancle)
                            } else {
                                mapActivity.getDrverFragment().viewModel?.status!!.startDriver?.set(Prepare_Navigation)
                            }
                        }
                    }
                }
            }
        }
    }

    fun addPoint(it: Marker) {
        if (mapPointController?.finallyMarker == null) {
            //添加终点，并显示路线轨迹
            driverModel.status.navigationEndPoint = Location(it.position.latitude, it.position.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, it.title)
            finalyText?.set(it.title)
            it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.finaly_point))
            mapPointController?.finallyMarker = mapActivity?.mAmap!!.addMarker(MarkerOptions().position(it.position).anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.select_point)))
            mapActivity?.mapUtils?.setDriverRoute(converLatPoint(mapPointController?.startMaker?.position!!), LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), mapActivity.getDrverFragment().viewModel?.status?.passPointDatas!!)
//            if (mapActivity.getDrverFragment().viewModel?.status?.startDriver?.get() == DriverCancle) {
//                mapActivity.getDrverFragment().viewModel?.isCanclePrepare = true
//                mapActivity.getDrverFragment().viewModel?.status?.startDriver?.set(Prepare_Navigation)
//            } else {
//                mapActivity.getDrverFragment().viewModel?.status?.startDriver?.set(Prepare_Navigation)
//            }
        } else {
            if (mapActivity.getDrverFragment().viewModel?.status?.passPointDatas?.size!! < 16) {
                mapActivity.getDrverFragment().viewModel?.status?.passPointDatas?.add(Location(it.position.latitude, it.position.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, it.title))
                mapPointController?.pointList?.add(PointEntity(it.title, converLatPoint(it.position)))
                var PointEntity = mapPointController?.SingleList?.get(0)
                PointEntity?.address = getString(R.string.way_point) + mapPointController?.pointList?.size + "个"
                mapPointController?.SingleList?.set(0, PointEntity!!)
                pointAdapter?.notifyDataSetChanged()
                mapActivity?.mapUtils?.setDriverRoute(converLatPoint(mapPointController?.startMaker?.position!!), LatLonPoint(driverModel.status.navigationEndPoint!!.latitude, driverModel.status.navigationEndPoint!!.longitude), mapActivity.getDrverFragment().viewModel!!.status?.passPointDatas!!)
                if (mapActivity.getDrverFragment().viewModel?.status?.startDriver?.get() == DriverCancle) {
                    mapActivity.getDrverFragment().viewModel?.status?.startDriver?.set(DriverCancle)
                } else {
                    mapActivity.getDrverFragment().viewModel?.status?.startDriver?.set(Prepare_Navigation)
                }
            } else {
                Toast.makeText(context, getString(R.string.max_passPoint), Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.navi_btn -> {
                startNavigation()
            }
            R.id.add_point -> {
                if (mapPointController.pointList.size < 16) {
                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).withInt(RouterUtils.MapModuleConfig.SEARCH_MODEL, mapActivity.getDrverFragment().viewModel?.status?.curModel!!).navigation(mapActivity, RESULT_STR)
                } else {
                    Toast.makeText(context, getString(R.string.max_passPoint), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.edit_point -> {
                if (mapPointController.CurState == 0) {
                    pointAdapter.setNewData(mapPointController.pointList)
                    mapPointController.CurState = 1
                    //展开列表
                    dataEmpty.set(true)
                } else {
                    //收缩列表
                    dataEmpty.set(false)
                    pointAdapter.setNewData(mapPointController.SingleList)
                    mapPointController.CurState = 0
                }
//                showBottomChangePositionDialog()
            }
            R.id.edit_finally -> {
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).withInt(RouterUtils.MapModuleConfig.SEARCH_MODEL, 3).navigation(mapActivity, EDIT_FINAL_POINT)
            }
        }
    }


    var items = ObservableArrayList<RouteEntity>()
    private fun initView(mapActivity: MapActivity) {
        mapActivity.district_recycleView.layoutManager = LinearLayoutManager(mapActivity)
        pointAdapter = AddPointAdapter(R.layout.district_item, mapPointController.SingleList)
        pointAdapter.onItemChildClickListener = this
        var dragCallBack = ItemDragAndSwipeCallback(pointAdapter)
        dragCallBack.setDragMoveFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN)
        var dragHelp = ItemTouchHelper(dragCallBack)
        dragHelp.attachToRecyclerView(mapActivity.district_recycleView)
        pointAdapter!!.enableDragItem(dragHelp, R.id.drag_layout, true)
        pointAdapter!!.setModel(this)
        pointAdapter!!.setOnItemDragListener(pointAdapter)
        mapActivity.district_recycleView.adapter = pointAdapter
    }

    fun onFiveClick(view: View) {

    }

    var lastView: RouteEntity? = null
    fun initLinear() {
        var linear = mapPointFragment.bottom_route_choice
        linear.removeAllViews()

        items.forEachIndexed { index, routeEntity ->
            var t = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var binding = DataBindingUtil.inflate<ViewDataBinding>(t, R.layout.route_choice_layout, linear, false)
            var view = binding.root
            if (index == 0) {
                lastView = routeEntity
            }
            binding.setVariable(BR.routeEntitiy, routeEntity)
            binding.setVariable(BR.route_model, this@MapPointViewModel)
            linear.addView(view)
        }
        linear.invalidate()
    }


    var currentIndex = 0

    fun BottomChoice(routeEntity: RouteEntity) {
        if (routeEntity == null) {
            return
        }
        if (!routeEntity.select.get()!!) {
            lastView?.select?.set(false)
            routeEntity.select.set(true)
            lastView = routeEntity
            mapActivity.mAmap.clear()
            currentIndex = items.indexOf(routeEntity)
            drawRouteLine(currentIndex)
        }
    }
}