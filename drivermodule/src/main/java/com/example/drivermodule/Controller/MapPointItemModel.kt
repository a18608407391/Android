package com.example.drivermodule.Controller

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.RegeocodeResult
import com.chad.library.adapter.base.BaseQuickAdapter
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.Location
import com.example.drivermodule.Adapter.AddPointItemAdapter
import com.example.drivermodule.BR
import com.example.drivermodule.Entity.PointEntity
import com.example.drivermodule.Entity.RouteDetailEntity
import com.example.drivermodule.Entity.RouteEntity
import com.example.drivermodule.Overlay.NaviDrivingRouteOverlay
import com.example.drivermodule.R
import com.example.drivermodule.Sliding.SlidingUpPanelLayout
import com.example.drivermodule.Fragment.MapFragment
import com.example.drivermodule.Utils.ErrorInfo
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.elder.zcommonmodule.Component.ItemViewModel
import com.example.drivermodule.Activity.RoadDetailActivity
import com.example.drivermodule.Fragment.SearchActivity
import com.example.drivermodule.Utils.AMapUtil
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat



//地图选点控制器

class MapPointItemModel : ItemViewModel<MapFrViewModel>(), SlidingUpPanelLayout.PanelSlideListener, BaseQuickAdapter.OnItemChildClickListener {
    override fun onItemChildClick(p0: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        //顶部途经点列表点击事件
        pointList.removeAt(position)
        adapter?.setNewData(pointList)
        viewModel?.status!!.passPointDatas.removeAt(position)
        mapFr.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), viewModel!!.status!!.passPointDatas)
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {

    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
       //底部路线详情弹出窗
        when (newState) {
            SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                RoadDetailItems.clear()
                listvisible.set(false)
            }
            SlidingUpPanelLayout.PanelState.EXPANDED -> {

            }
        }
    }


    //地图选点逻辑处理
    var listvisible = ObservableField<Boolean>(false)   //顶部recycleView是否显示
    var startMaker: Marker? = null                                  //开始位置
    var screenMaker: Marker? = null                               //可移动maker
    var dataEmpty = ObservableField<Boolean>(false)         //
    var finalyText = ObservableField<String>(getString(R.string.location_select))           //重点位置文案
    var choiceVisible = ObservableField<Boolean>(true)      //显示状态切换
    var pointList = ArrayList<PointEntity>()                         //显示模式1 显示途经点列表展开
    var finallyMarker: Marker? = null                              //重点的marker
    var panelState = ObservableField<SlidingUpPanelLayout.PanelState>(SlidingUpPanelLayout.PanelState.HIDDEN) //底部滑动栏显示控制
    lateinit var adapter: AddPointItemAdapter              //顶部列表适配器
    var CurState = 0  //列表状态，收缩状态为0  展示状态为1
    var SingleList = ArrayList<PointEntity>().apply {   // 显示模式2 ，途经点列表收起，显示当前有多少个途经点
        this.add(PointEntity("", LatLonPoint(0.0, 0.0)))
    }

    fun SearchResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        //搜索回调
        if (data != null) {
            var tip = data?.getSerializable("tip") as PoiItem
            if (tip != null) {
                if (requestCode == RESULT_STR) {
                    //搜索途径点或终点
                    if (finallyMarker == null) {
                        viewModel.status.navigationEndPoint = Location(tip.latLonPoint.latitude, tip.latLonPoint.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, tip.title)
                        finalyText?.set(tip.title)
                        mapFr.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), viewModel?.status?.passPointDatas!!)
                    } else {
                        viewModel?.status?.passPointDatas?.add(Location(tip.latLonPoint.latitude, tip.latLonPoint.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, tip.title))
                        pointList?.add(PointEntity(tip.title, tip.latLonPoint))
                        var PointEntity = SingleList?.get(0)
                        PointEntity?.address = getString(R.string.way_point) + pointList?.size + "个"
                        SingleList?.set(0, PointEntity!!)
                        adapter?.notifyDataSetChanged()
                        mapFr?.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), viewModel?.status?.passPointDatas!!)
                    }
                } else if (requestCode == EDIT_FINAL_POINT) {
                    //编辑终点
                    if (tip.latLonPoint?.latitude != null && tip.latLonPoint?.longitude != null) {
                        if (finallyMarker != null) {
                            finallyMarker?.position = AMapUtil.convertToLatLng(tip.latLonPoint)
                            finalyText?.set(tip.title)
                            viewModel.status.navigationEndPoint = Location(tip.latLonPoint.latitude, tip.latLonPoint.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, tip.title)
                            mapFr?.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), mapFr.viewModel?.status?.passPointDatas!!)
                        } else {
                            viewModel.status.navigationEndPoint = Location(tip.latLonPoint.latitude, tip.latLonPoint.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, tip.title)
                            finalyText?.set(tip.snippet)
                            finallyMarker = mapFr?.mAmap!!.addMarker(MarkerOptions().position(AMapUtil.convertToLatLng(tip.latLonPoint)).anchor(0.5f, 0.5f)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.select_point)))
                            mapFr?.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), viewModel!!.status?.passPointDatas!!)
                            if (viewModel?.status?.startDriver?.get() == DriverCancle) {
                                viewModel?.status?.startDriver?.set(DriverCancle)
                            } else {
                                viewModel?.status!!.startDriver?.set(Prepare_Navigation)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onComponentFinish() {
        SearchPoint()
    }


    fun onInfoWindowClick(it: Marker?) {
        addPoint(it!!)
    }


    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it?.type) {
            RxBusEven.DriverMapPointRegeocodeSearched -> {
                if (it.value == null) {
                    return
                }
                var regeocdeResult = it.value as RegeocodeResult
                var addressName = ""
                if (regeocdeResult.regeocodeAddress?.aois != null && regeocdeResult.regeocodeAddress?.aois?.size != 0) {
                    if (regeocdeResult.regeocodeAddress.aois[0].aoiName != null) {
                        addressName = regeocdeResult.regeocodeAddress?.district + regeocdeResult.regeocodeAddress.aois[0].aoiName
                    } else {
                        addressName = regeocdeResult.regeocodeAddress?.formatAddress!!
                    }
                } else {
                    addressName = regeocdeResult.regeocodeAddress?.formatAddress!!
                }
                screenMaker?.title = addressName
                if (screenMaker != null) {
                    screenMaker?.showInfoWindow()
                }
            }
            RxBusEven.MapCameraChangeFinish -> {
                if (screenMaker != null) {
                    mapFr.mapUtils?.queryGeocoder(LatLonPoint(screenMaker?.position?.latitude!!, screenMaker?.position?.longitude!!))
                }
            }
        }
    }

    lateinit var mapFr: MapFragment
    override fun ItemViewModel(viewModel: MapFrViewModel): ItemViewModel<MapFrViewModel> {
        mapFr = viewModel?.mapActivity
        adapter = AddPointItemAdapter(R.layout.district_item, SingleList)
        adapter.onItemChildClickListener = this
        adapter.setModel(this)
        return super.ItemViewModel(viewModel)
    }

    fun changeMap(curPosition: Location) {
        if (viewModel?.status.navigationStartPoint == null) {
            if (viewModel?.status.locationLat.size == 0) {
                viewModel?.status.locationLat.add(curPosition)
            }
            viewModel?.status.navigationStartPoint = curPosition
        }
        mapFr.mAmap.clear()
        startMaker = mapFr.mapUtils?.createMaker(Location(viewModel.status.navigationStartPoint!!.latitude, viewModel.status.navigationStartPoint!!.longitude))
        screenMaker = mapFr.mapUtils?.createScreenMarker()
        mapFr.mapUtils?.queryGeocoder(LatLonPoint(viewModel?.status.navigationStartPoint!!.latitude, viewModel?.status.navigationStartPoint!!.longitude))
        mapFr.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        mapFr.mAmap.isMyLocationEnabled = false
        mapFr.myLocationStyle.showMyLocation(false)
    }

    fun closeBottom() {
        listvisible.set(false)
        RoadDetailItems.clear()
        panelState.set(SlidingUpPanelLayout.PanelState.COLLAPSED)
    }


    fun showBottom() {
        listvisible.set(true)
        itemRestore.forEach {
            RoadDetailItems.add(it)
        }
        CoroutineScope(uiContext).launch {
            delay(500)
            panelState.set(SlidingUpPanelLayout.PanelState.EXPANDED)
        }
    }

    fun onClick(view: View) {
        when (view?.id) {
            R.id.detail_click -> {
                if (!listvisible.get()!!) {
                    showBottom()
                } else {
                    closeBottom()
                }
            }
            R.id.navi_btn -> {
                startNavigation()
            }
            R.id.add_point -> {
                if (pointList.size < 16) {
                    var search = ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).navigation() as SearchActivity
                    var fr = viewModel?.mapActivity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
                    fr.start(search!!)
//      ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).withInt(RouterUtils.MapModuleConfig.SEARCH_MODEL, 1).navigation(mapFr.activity, RESULT_STR)
                } else {
                    Toast.makeText(context, getString(R.string.max_passPoint), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.edit_point -> {
                if (CurState == 0) {
                    Log.e("result", "pointList" + pointList.size)
                    adapter.setNewData(pointList)
                    CurState = 1
                    //展开列表
                    dataEmpty.set(true)
                } else {
                    //收缩列表
                    dataEmpty.set(false)
                    adapter.setNewData(SingleList)
                    CurState = 0
                }
            }
            R.id.edit_finally -> {
                var fr = viewModel?.mapActivity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
                fr.startForResult((ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).navigation() as SearchActivity).setModel(3), EDIT_FINAL_POINT)
//                ARouter.getInstance().build(RouterUtils.MapModuleConfig.SEARCH_ACTIVITY).withInt(RouterUtils.MapModuleConfig.SEARCH_MODEL, 3).navigation(mapFr.activity, EDIT_FINAL_POINT)

            }
        }
    }

    private fun startNavigation() {
        if (viewModel?.status.navigationStartPoint == null || viewModel?.status.navigationEndPoint == null) {
            return
        }
        viewModel?.status.navigationType = curRouteEntity?.id!!.get()!!
        var list = ArrayList<LatLng>()
        viewModel?.status.passPointDatas.forEach {
            list.add(LatLng(it.latitude, it.longitude))
        }
        if (viewModel?.status.startDriver.get() == DriverCancle) {
            //当前在骑行
            viewModel?.startDriver(2)
        } else {
            viewModel?.startNavi(list, 2)
        }
        returnDriverFr()
        mapFr!!.mapUtils!!.navi.destroy()
    }

    fun addPoint(it: Marker) {
        if (finallyMarker == null) {
            //添加终点，并显示路线轨迹
            viewModel.status.navigationEndPoint = Location(it.position.latitude, it.position.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, it.title)
            finalyText?.set(it.title)
            it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.finaly_point))
            finallyMarker = mapFr?.mAmap!!.addMarker(MarkerOptions().position(it.position).anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.select_point)))
            mapFr?.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), viewModel?.status?.passPointDatas!!)
        } else {
            if (viewModel?.status?.passPointDatas?.size!! < 16) {
                viewModel?.status?.passPointDatas?.add(Location(it.position.latitude, it.position.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, it.title))
                pointList?.add(PointEntity(it.title, converLatPoint(it.position)))
                var PointEntity = SingleList?.get(0)
                PointEntity?.address = getString(R.string.way_point) + pointList?.size + "个"
                SingleList?.set(0, PointEntity!!)
                adapter?.notifyDataSetChanged()
                mapFr?.mapUtils?.setDriverRoute(converLatPoint(startMaker?.position!!), LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), viewModel!!.status?.passPointDatas!!)
                if (viewModel?.status?.startDriver?.get() == DriverCancle) {
                    viewModel?.status?.startDriver?.set(DriverCancle)
                } else {
                    viewModel?.status?.startDriver?.set(Prepare_Navigation)
                }
            } else {
                Toast.makeText(context, getString(R.string.max_passPoint), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun CalculateCallBack(result: AMapCalcRouteResult) {
        if (result.errorCode == 0) {
            items.clear()
            result?.routeid!!.forEachIndexed { index, it ->
                var path = mapFr.mapUtils?.navi?.naviPaths!![it]
                var entity = RouteEntity()
                if (index == 0) {
                    entity.select.set(true)
                } else {
                    entity.select.set(false)
                }
                entity.id.set(it)
                entity.title.set(path?.labels)
                if (path?.tollCost!! < 1) {
                    entity.distance.set(DecimalFormat("0.0").format(path?.allLength!! / 1000) + "KM")
                } else {
                    entity.distance.set(DecimalFormat("0.0").format(path?.allLength!! / 1000) + "KM" + " " + path?.tollCost + "￥")
                }
                entity.time.set(ConvertUtils.millis2FitTimeSpan(path?.allTime!! * 1000.toLong(), 3))
                items.add(entity)
            }
            drawRouteLine(items[0])
        } else {
            Log.e("result", "错误！！！！！！！！！" + result.errorCode + result.errorDetail)
            Toast.makeText(mapFr.activity, ErrorInfo.getError(result.errorCode), Toast.LENGTH_SHORT).show()
            viewModel?.mapActivity!!._mActivity!!.dismissProgressDialog()
        }
    }

    var routeDistance = 0       //规划路径长度
    var routeTime = 0            //规划路径时间
    var curRouteEntity: RouteEntity? = null        //规划路径的实体类
    private fun drawRouteLine(routeEntity: RouteEntity?) {
        //绘制路线
        curRouteEntity = routeEntity
        var path = mapFr.mapUtils?.navi?.naviPaths!![routeEntity!!.id.get()]
        itemRestore?.clear()
        var entity = RouteDetailEntity()
        entity.position = 0
        itemRestore.add(entity)
        path?.steps?.forEachIndexed { index, it ->
            var item = RouteDetailEntity()
            item.position = index + 1
            item.iconType = it.iconType
            item.roadName = it.links.get(0).roadName
            if (it.isArriveWayPoint) {
                item.roadName = item.roadName + "(经过途径点)"
            }
            if (it.length < 1000) {
                item.distance = it.length.toString() + "米"
            } else {
                item.distance = DecimalFormat("0.0").format(it.length / 1000) + "公里"
            }
            if (it.trafficLightCount > 0) {
                item.distance = item.distance + "红绿灯" + it.trafficLightCount + "个"
            }
            itemRestore.add(item)
        }
        itemRestore.sortBy {
            it.position
        }
        if (listvisible.get()!!) {
            RoadDetailItems.clear()
            itemRestore.forEach {
                RoadDetailItems.add(it)
            }
        }
        var drivingRouteOverlay = NaviDrivingRouteOverlay(context, mapFr.mAmap, path, path?.coordList!!.get(0), if (finallyMarker == null) path.endPoint else NaviLatLng(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude), path!!.wayPoint)
        mapFr.mAmap.isMyLocationEnabled = false
        drivingRouteOverlay.setNodeIconVisibility(true)//设置节点marker是否显示
        drivingRouteOverlay.setThroughPointIconVisibility(true)
        drivingRouteOverlay.setIsColorfulline(true)//是否用颜色展示交通拥堵情况，默认true
        drivingRouteOverlay.removeFromMap()
        drivingRouteOverlay.addToMap()
        routeDistance = path.allLength
        routeTime = path.allTime
        if (path.wayPoint.size == 0) {
            if (screenMaker != null) {
                screenMaker!!.remove()
                screenMaker = null
            }
            if (finallyMarker != null) {
                finallyMarker!!.remove()
                finallyMarker = null
            }
            if (startMaker != null) {
                startMaker?.remove()
                startMaker = null
            }
            screenMaker = drivingRouteOverlay.addRemoveMarker(AMapUtil.convertToLatLng(LatLonPoint(viewModel.status.navigationStartPoint!!.latitude, viewModel.status.navigationStartPoint!!.longitude)))
            startMaker = drivingRouteOverlay?.startMarker
            finallyMarker = drivingRouteOverlay?.endMarker
            drivingRouteOverlay.zoomSpanAll()
        } else {
            screenMaker = drivingRouteOverlay.addRemoveMarker(drivingRouteOverlay.convertToLatLng(path!!.wayPoint[path.wayPoint.size - 1]))
        }
        if (panelState.get() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            Log.e("result", "展开")
            CoroutineScope(uiContext).launch {
                panelState.set(SlidingUpPanelLayout.PanelState.COLLAPSED)
            }
        }
    }

    fun onComponentClick() {
        returnDriverFr()
    }

    fun returnDriverFr() {
        //关闭地图选点 返回到骑行
        listvisible.set(false)
        RoadDetailItems.clear()
        panelState.set(SlidingUpPanelLayout.PanelState.HIDDEN)
        RoadDetailItems.clear()
        itemRestore.clear()
        items.clear()
        viewModel?.changerFragment(0)
        mapFr.mAmap.clear()
        mapFr.mAmap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        if (viewModel?.status!!.startDriver.get() == Prepare_Navigation || viewModel?.status!!.startDriver.get() == DriverPause || viewModel?.status!!.startDriver.get() == Drivering) {
            if (viewModel?.status!!.startDriver.get() == Prepare_Navigation) {
                reset()
                mapFr.mAmap.isMyLocationEnabled = true
                mapFr.myLocationStyle.showMyLocation(true)
                viewModel?.status!!.startDriver.set(DriverCancle)
            } else {
                reset()
                mapFr.mAmap.isMyLocationEnabled = false
                mapFr.myLocationStyle.showMyLocation(false)
                mapFr.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
                mapFr.mAmap.myLocationStyle = mapFr.myLocationStyle
                mapFr.getDrverController()!!.driverController.continueDriver()
            }
        } else {
            //Cancle 了
            reset()
            mapFr.mAmap.isMyLocationEnabled = true
            mapFr.myLocationStyle.showMyLocation(true)
            viewModel?.status!!.locationLat.clear()
        }
        if (viewModel?.status?.navigationType == 0) {
            viewModel?.status!!.passPointDatas.clear()
            viewModel?.status!!.navigationStartPoint = null
            viewModel?.status!!.navigationEndPoint = null
        }
//        component.setHomeStyle()
        finalyText.set(getString(R.string.location_select))
        var t = SingleList.get(0)
        t.address = ""
        SingleList[0] = t
        adapter.notifyDataSetChanged()
        if (viewModel?.backStatus!!) {
            //该标记为返回到组队
            if (viewModel?.tab.selectedTabPosition == 1) {
                viewModel.changerFragment(1)
                var model = viewModel?.items[0] as DriverItemModel
                model.GoTeam()
            } else {
                viewModel.selectTab(1)
            }
            viewModel?.backStatus = false
        }
    }


    fun reset() {
        //初始化地图选点数据
        if (screenMaker != null) {
            screenMaker?.remove()
        }
        screenMaker = null
        if (finallyMarker != null) {
            finallyMarker?.remove()
        }

        finallyMarker = null
//        startPoint = null
        pointList.clear()
        if (startMaker != null) {
            startMaker?.remove()
        }
        startMaker = null
    }

    fun SearchPoint() {
        //搜索点
        if (viewModel.status.navigationEndPoint == null) {
            Toast.makeText(context, getString(R.string.no_passPoint), Toast.LENGTH_SHORT).show()
            return
        }
        var datas = ArrayList<LatLonPoint>()
        datas.add(0, LatLonPoint(viewModel.status.navigationStartPoint!!.latitude, viewModel.status.navigationStartPoint!!.longitude))
        viewModel?.status!!.passPointDatas.forEach {
            datas.add(LatLonPoint(it.latitude, it.longitude))
        }
        datas.add(LatLonPoint(viewModel.status.navigationEndPoint!!.latitude, viewModel.status.navigationEndPoint!!.longitude))
        var fr = viewModel?.mapActivity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
        fr?.startForResult((ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_DETAIL)
                .navigation() as RoadDetailActivity).setValue(datas, routeDistance * 1F, routeTime.toLong()), ROAD_DETAIL_RETURN_VALUE)

//                .withFloat(RouterUtils.MapModuleConfig.ROAD_DISTANCE, routeDistance * 1F)
//                .withLong(RouterUtils.MapModuleConfig.ROAD_TIME, routeTime.toLong())
//                .withSerializable(RouterUtils.MapModuleConfig.ROAD_DATA, datas)

    }

    var items = ObservableArrayList<RouteEntity>()

    var bindingCommand = BindingCommand(object : BindingConsumer<RouteEntity> {
        override fun call(t: RouteEntity) {
            //底部线路选择点击事件回调
            if (!t.select.get()!!) {
                var list = ArrayList<RouteEntity>()
                items.forEach {
                    it.select.set(false)
                    list.add(it)
                }
                var index = list.indexOf(t)
                t.select.set(true)

                mapFr.mapUtils!!.navi!!.selectRouteId(t!!.id.get()!!)
                mapFr.mapUtils!!.navi!!.selectMainPathID(mapFr?.mapUtils!!.navi.naviPaths[Integer.valueOf(t!!.id.get()!!)]!!.pathid)
                list.set(index, t)
                mapFr.mAmap.clear()
                items.clear()
                items.addAll(list)
                drawRouteLine(t)
            }
        }
    })


    var itemRestore = ArrayList<RouteDetailEntity>()
    var RoadDetailItems = ObservableArrayList<RouteDetailEntity>()

    var RoadDetailItemsBinding = ItemBinding.of<RouteDetailEntity> { itemBinding, position, item ->
        if (item.position == 0) {
            itemBinding.set(BR.route_deteal_list, R.layout.roate_detail_first_layout)
        } else if (item.position == 999) {
            itemBinding.set(BR.route_deteal_list, R.layout.roate_detail_first_layout)
        } else {
            itemBinding.set(BR.route_deteal_list, R.layout.roate_detail_layout)
        }
    }

    var RoadDetailAdapter = BindingRecyclerViewAdapter<RouteDetailEntity>()       //路线详情适配器

}