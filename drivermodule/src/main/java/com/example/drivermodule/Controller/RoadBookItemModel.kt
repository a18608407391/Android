package com.example.drivermodule.Controller

import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.graphics.drawable.Drawable
import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.AMapNaviPath
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.RouteSearch
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.Transition
import com.elder.zcommonmodule.Entity.HotData
import com.example.drivermodule.ViewModel.MapFrViewModel
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.SoketBody.SoketNavigation
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.getRoadImgUrl
import com.example.drivermodule.Fragment.RoadBookSearchActivity
import com.example.drivermodule.BR
import com.example.drivermodule.Component.SimpleItemRecycleComponent
import com.example.drivermodule.Entity.RoadBook.*
import com.example.drivermodule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ucar.myapplication.Wedge.ChoiceBagPopupWindow
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat


class RoadBookItemModel : ItemViewModel<MapFrViewModel>(), HttpInteface.RoadBookDetail, ChoiceBagPopupWindow.PopWindowClick {
    override fun onPopWindowItemClick(view: View) {
        var fr = viewModel?.mapActivity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
        fr.startForResult(ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY).navigation() as RoadBookSearchActivity, REQUEST_LOAD_ROADBOOK)
    }

    //路书逻辑处理
    var netWorkData: ArrayList<RoadDetailEntity>? = null
    var data: HotData? = null
    var bottomVisible = ObservableField<Boolean>(false)
    var FrondVisible = ObservableField<Boolean>(false)
    var pathList = HashMap<String, AMapNaviPath>()
    var dayCount = ObservableField<Int>()
    var title = ObservableField<String>()
    var distance_andtime = ObservableField<String>()
    var pointAddress = ObservableField<String>()
    var discribe = ObservableField<String>()
    var id = ObservableField<String>()
    var time = 0L
    var indexPath = 0
    var makerList = ArrayList<Marker>()
    var lines: Polyline? = null
    var currentRoad: RoadDetailEntity? = null
    var currentRoadList: ArrayList<RoadDetailEntity>? = null
    var scrollPosition = ObservableField(-1)
    //recyclerView滑动到指定位置
    var lastMarker: Marker? = null

    var pagerAdapter = BindingRecyclerViewAdapter<BottomHoriDatas>()
    var itemBinding = ItemBinding.of<BottomHoriDatas>(BR.horiDatas, R.layout.roadbook_bottom_hori_item_layout).bindExtra(BR.road_model,this@RoadBookItemModel)
    var items = ObservableArrayList<BottomHoriDatas>()

    var recycleComponent = SimpleItemRecycleComponent(this)
    var times = 0L


    override fun getRoadBookDetailSuccess(it: String) {

        Log.e("result", "getRoadBookDetailSuccess" + it)
        viewModel?.mapActivity.dismissProgressDialog()
        CoroutineScope(ioContext).async {
            netWorkData = Gson().fromJson<ArrayList<RoadDetailEntity>>(it, object : TypeToken<ArrayList<RoadDetailEntity>>() {}.type)
            launch(uiContext) {
                behavior_by_routes?.set(BottomSheetBehavior.STATE_HIDDEN)
                recycleComponent.initDatas(netWorkData!!, data, 0)
            }
        }
    }

    fun BottomhoriClick(view: BottomHoriDatas) {

    }

    fun toNavi(data: BottomHoriDatas) {
        if (viewModel?.status.navigationType == 0) {
            viewModel?.status!!.navigationEndPoint = Location(data.lat, data.lon, System.currentTimeMillis().toString(), 0F, 0.0, 0F, data.describe.get()!!)
            if (viewModel?.status.startDriver.get() == DriverCancle) {
                viewModel?.startDriver(4)
            } else {
                viewModel?.startNavi(ArrayList(), 4)
            }
        } else {
            if (viewModel?.status.navigationEndPoint?.latitude != data.lat || viewModel?.status.navigationEndPoint!!.longitude != data.lon) {
                var socket = SoketNavigation()
                socket.navigation_end = Location(data.lat, data.lon, System.currentTimeMillis().toString(), 0F, 0.0, 0F, data.describe.get()!!)
                socket.wayPoint = ArrayList()
                socket.type = "road"
                RxBus.default?.post(socket)
            }
            var list = ArrayList<LatLng>()
            viewModel?.status!!.navigationEndPoint = Location(data.lat, data.lon, System.currentTimeMillis().toString(), 0F, 0.0, 0F, data.describe.get()!!)
            viewModel?.startNavi(list, 4)
//        driverViewModel?.startNavi(driverViewModel?.status!!.navigationStartPoint!!, driverViewModel?.status!!.navigationEndPoint!!, list, false)
        }

    }

    var behavior_by_routes = ObservableField<Int>(BottomSheetBehavior.STATE_HIDDEN)

    override fun getRoadBookDetailError(ex: Throwable) {
        viewModel?.mapActivity.dismissProgressDialog()
        Log.e("result", "getRoadBookDetailError" + ex.message)
    }

    fun MiddleRoadClick(data: BottomHoriDatas) {
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 0).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, data.roadid).navigation()
    }

    var ScrollChangeCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (lastMarker == null) {
                return
            }
            markerChange(makerList[t])
        }
    })


    var select_position = ObservableField(0)
    //TabLayout 标签选择

    fun markerChange(p0: Marker) {
        if (System.currentTimeMillis() - times < 200) {
            times = System.currentTimeMillis()
            return
        }
        if (!makerList.contains(p0)) {
            return
        }
        if (p0 == lastMarker) {
            return
        }
        if (selecPosition == 0) {
            //HOME
            select_position.set(Integer.valueOf(p0?.snippet) + 1)
//            selectTab()
//            initTabSelect()
//
//            var date = driverViewModel?.recycleComponent.horiDatas[index + 1]
//
//            driverViewModel?.recycleComponent.click(date)
        } else {
            if (lastMarker != null) {
                var inflater = viewModel.mapActivity.activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var view = inflater.inflate(R.layout.roadbook_maker_layout, null)
                var tv = view.findViewById<TextView>(R.id.marker_tv)
                var img = view.findViewById<ImageView>(R.id.marker_icon)
                img.setImageResource(R.drawable.green_oval)
                tv.text = (makerList.indexOf(lastMarker!!) + 1).toString()
                lastMarker?.setIcon(BitmapDescriptorFactory.fromView(view))
                lastMarker?.showInfoWindow()
            }
            var inflater = viewModel.mapActivity.activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.roadbook_maker_layout, null)
            var img = view.findViewById<ImageView>(R.id.marker_icon)
            img.setImageResource(R.drawable.orange_oval)
            var tv = view.findViewById<TextView>(R.id.marker_tv)
            tv.text = (makerList.indexOf(p0) + 1).toString()
            p0?.setIcon(BitmapDescriptorFactory.fromView(view))
            p0?.showInfoWindow()
            lastMarker = p0
            if (items.size == 0) {
                if (currentRoad?.pointList!!.size <= 1) {
                    FrondVisible.set(false)
                } else {
                    FrondVisible.set(true)
                }
                currentRoad?.pointList?.forEachIndexed { index, roadDetailItem ->
                    var bo = BottomHoriDatas()
                    bo.roadid = roadDetailItem.id.toString()
                    if (index == 0) {
                        bo.endtype.set(false)
                        bo.frondtype.set(true)
                    } else if (index == currentRoad?.pointList!!.size - 1) {
                        bo.frondtype.set(false)
                        bo.endtype.set(true)
                    } else {
                        if (currentRoad?.pointList!!.size == 1) {
                            bo.frondtype.set(false)
                            bo.endtype.set(false)
                        } else {
                            bo.frondtype.set(true)
                            bo.endtype.set(true)
                        }
                    }
                    bo.topTv.set(roadDetailItem.routeName)
                    bo.describe.set(roadDetailItem.address)
                    bo.lat = roadDetailItem.lat
                    bo.lon = roadDetailItem.lng
                    bo.imgUrl.set(getRoadImgUrl(roadDetailItem.imgUrl))
                    var number = ""
                    if (index + 1 < 10) {
                        number = "0" + (index + 1).toString()
                    } else {
                        number = (index + 1).toString()
                    }
                    bo.number.set(number)
                    items.add(bo)
                }
            }
            behavior_by_routes!!.set(BottomSheetBehavior.STATE_COLLAPSED)
            var s = makerList.indexOf(p0)
            if (s == makerList.size - 1) {
                FrondVisible.set(false)
            } else {
                FrondVisible.set(true)
            }
            scrollPosition.set(s)
            bottomVisible.set(false)
        }
    }


    var selecPosition = 0
    var tabSelect = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            selecPosition = t
            initTabSelect(t, true)
        }
    })

    fun initTabSelect(position: Int, type: Boolean) {
        if (recycleComponent!!.allData != null) {
            if (position == 0) {
                items!!.clear()
                recycleComponent.initDatas(netWorkData!!, data, 0)
                createMarker(recycleComponent!!.allData!!)
//                bottomVisible!!.set(false)
                behavior_by_routes!!.set(BottomSheetBehavior.STATE_COLLAPSED)
            } else {
                items!!.clear()
                recycleComponent.megerList.removeAll()
                CoroutineScope(uiContext).launch {
                    delay(500)
                    var li = recycleComponent!!.allData!![position - 1]
                    createMarker(li)
                    title!!.set("Day" + position)
                    pointAddress!!.set(li.allRoutepoint)
                    var t = DecimalFormat("0.0").format(li.aboutdis / 1000) + "km"
                    var k = ConvertUtils.duration2Min(li.ridingtime.toInt())
                    distance_andtime.set("距离" + t + "  " + "预计" + k)
                    discribe!!.set(li.simpIntroduction)
                    id!!.set(li.id.toString())
//                bottomVisible!!.set(true)

                    var m = RoadBookDistanceEntity()
                    m.visible.set(true)
                    recycleComponent.megerList.insertItem(m)
                    var list = ObservableArrayList<RoadBookListEntity>()
                    var ro = RoadBookListEntity()
                    ro.visible.set(true)
                    list.add(ro)
                    var recy = RoadBookRecycleEntity()
//                driverViewModel?.recycleComponent.megerList.insertItem(m)
//                driverViewModel?.recycleComponent.megerList.insertList(list)
                    recycleComponent.megerList.insertItem(recy)
                    recycleComponent.changeIndex(position - 1, recycleComponent!!.allData!![position - 1], true)
                    behavior_by_routes!!.set(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.item_road_line_detail -> {
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 1).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, id.get()).navigation()
            }
        }
    }

    fun onComponentFinish(view: View) {
        var dialog = ChoiceBagPopupWindow(viewModel.mapActivity.activity!!)
        dialog.click = this
        dialog.contentView.findViewById<TextView>(com.elder.zcommonmodule.R.id.pop_delete).setOnClickListener {
            PreferenceUtils.putString(viewModel.mapActivity.activity, PreferenceUtils.getString(context, USERID) + "hot", null)
            data = null
            netWorkData!!.clear()
            netWorkData = null
            viewModel.mapActivity.hotData = null
            viewModel?.selectTab(0)
            dialog.dismiss()
        }
        dialog.showAsDropDown(view)
    }

    fun customView(maker: Marker, view: View?) {
        var list = maker.snippet.split(";")
        var img = view!!.findViewById<ImageView>(R.id.left_img)
        var top = view!!.findViewById<TextView>(R.id.top_tv)
        var bottom = view!!.findViewById<TextView>(R.id.bottom_tv)
        var corner = RoundedCorners(ConvertUtils.dp2px(5F))
        var opition = RequestOptions().transform(corner).error(R.drawable.artboard).override(ConvertUtils.dp2px(32F), ConvertUtils.dp2px(32F))
        Glide.with(img).asDrawable().apply(opition).load(getRoadImgUrl(list[0])).into(img)
        top.text = list[1]
        if (list[2].isNullOrEmpty() || list[2] == "null") {
            bottom.text = ""
        } else {
            bottom.text = list[2]
        }
    }

    fun onInfoWindowClick(it: Marker?) {

    }

    fun doLoadDatas(data: HotData) {
        viewModel.mapActivity.showProgressDialog(getString(R.string.getRoadBookLoading))
        if (data == null) {
            return
        }
        var m = PreferenceUtils.getString(viewModel.mapActivity.activity, PreferenceUtils.getString(context, USERID) + "hot")
        var d = Gson().fromJson<HotData>(m, HotData::class.java)
        if (d != null) {
            if (data!!.id != d.id) {
                PreferenceUtils.putString(viewModel.mapActivity.activity, PreferenceUtils.getString(context, USERID) + "hot", Gson().toJson(data))
            }
        } else {
            PreferenceUtils.putString(viewModel.mapActivity.activity, PreferenceUtils.getString(context, USERID) + "hot", Gson().toJson(data))
        }
        this.data = data
        HttpRequest.instance.RoadBookDetail = this
        var map = HashMap<String, String>()
        map["guideId"] = data!!.id.toString()
        HttpRequest.instance.getRoadBookDetail(map)
    }

    fun backToDriver() {
        if (lines != null) {
            lines?.remove()
            lines = null
        }
        if (makerList.size != 0) {
            makerList.forEach {
                it.remove()
            }
            makerList.clear()
        }
        if (lastMarker != null) {
            lastMarker?.remove()
            lastMarker = null
        }
        currentRoad = null
        currentRoadList = null
        behavior_by_routes?.set(BottomSheetBehavior.STATE_HIDDEN)
        pathList.clear()
        indexPath = 0
        items.clear()
        bottomVisible.set(false)
    }

    fun NextRoadClick(view: View) {
        var index = makerList.indexOf(lastMarker)
        var ms = items[index + 1]
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 3).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, items[index].roadid + "," + ms.roadid).navigation()
    }

    fun createMarker(data: ArrayList<RoadDetailEntity>) {
        Log.e("result", "data" + data.size)
        if (!makerList.isEmpty()) {
            makerList.forEach {
                it.remove()
            }
            makerList.clear()
            lastMarker?.remove()
            lastMarker = null
            pathList.clear()
            indexPath = 0
        }
        if (lines != null) {
            lines!!.remove()
            lines = null
        }
        this.currentRoadList = data
        lines = viewModel.mapActivity.mAmap.addPolyline(PolylineOptions()
                .color(R.color.line_color)
                .width(18f).zIndex(0f).color(getColor(R.color.line_color)))
        var list = ArrayList<LatLng>()
        var b = LatLngBounds.builder()
        data.forEachIndexed { index, roadDetailItem ->
            var inflater = viewModel.mapActivity.activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.roadbook_popuwindow_custom, null)
            var tv = view.findViewById<TextView>(R.id.window_day)
            tv.text = "D" + (index + 1).toString()
            var tv1 = view.findViewById<TextView>(R.id.window_day_count)
            var img = view.findViewById<ImageView>(R.id.window_img)
            tv1.text = roadDetailItem.pointList!!.size.toString()
            var corner = RoundedCorners(ConvertUtils.dp2px(5F))
            var opition = RequestOptions().transform(corner).error(R.drawable.road_windown_img).override(ConvertUtils.dp2px(32F), ConvertUtils.dp2px(32F))
            Glide.with(img).load(getRoadImgUrl(roadDetailItem.pointList!![0].imgUrl)).apply(opition).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    super.onResourceReady(resource, transition)
                    img.setImageDrawable(resource)
                    var maker = viewModel.mapActivity.mAmap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromView(view)).position(LatLng(roadDetailItem.lat, roadDetailItem.lng)))
                    maker.isClickable = true
                    maker.snippet = index.toString()
                    makerList.add(maker)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    img.setImageDrawable(errorDrawable)
                    var maker = viewModel.mapActivity.mAmap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromView(view)).position(LatLng(roadDetailItem.lat, roadDetailItem.lng)))
                    maker.isClickable = true
                    maker.snippet = index.toString()
                    makerList.add(maker)
                    super.onLoadFailed(errorDrawable)
                }
            })
            list.add(LatLng(roadDetailItem.lat, roadDetailItem.lng))
            b.include(LatLng(roadDetailItem.lat, roadDetailItem.lng))
        }
        var pass = ArrayList<Location>()
        var fromAndTo = RouteSearch.FromAndTo(LatLonPoint(list[0].latitude, list[0].longitude), LatLonPoint(list[list.size - 1].latitude, list[list.size - 1].longitude))
        if (list.size > 2) {
            list.forEachIndexed { index, latLng ->
                if (index != 0 && index != list.size - 1) {
                    pass.add(Location(latLng.latitude, latLng.longitude))
                }
            }
        }
        indexPath = 0
        if (pathList.isEmpty()) {
            viewModel.mapActivity.showProgressDialog(getString(R.string.loading_route))
            viewModel?.mapActivity.mapUtils?.setDriverRoute(fromAndTo.from, fromAndTo.to, pass)
//            var query = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION,
//                    pass, null, "")
//            mRoutePath?.calculateDriveRouteAsyn(query)
        } else {
            drawPath(pathList["0"])
        }
    }

    fun drawPath(p0: AMapNaviPath?) {
        if (!pathList.containsKey(indexPath.toString())) {
            pathList.put(indexPath.toString(), p0!!)
        }
        var list = ArrayList<LatLng>()
        var b = LatLngBounds.builder()
        p0?.coordList!!.forEach {
            list.add(LatLng(it.latitude, it.longitude))
            b.include(LatLng(it.latitude, it.longitude))
        }
        viewModel.mapActivity.mAmap.animateCamera(CameraUpdateFactory
                .newLatLngBounds(b.build(), getWindowWidth()!!, ConvertUtils.dp2px(345F - 130), 150))
        lines?.points = list
    }

    fun createMarker(data: RoadDetailEntity) {
        if (!makerList.isEmpty()) {
            makerList.forEach {
                it.remove()
            }
            makerList.clear()
            lastMarker?.remove()
            lastMarker = null
            if (lines != null) {
                lines!!.remove()
                lines = null
            }
        }
        this.currentRoad = data
        lines = viewModel.mapActivity.mAmap.addPolyline(PolylineOptions()
                .color(R.color.theme_color)
                .width(18f).zIndex(0f).color(getColor(R.color.theme_color)))
        var list = ArrayList<Location>()
        data.pointList!!.forEachIndexed { index, roadDetailItem ->
            var inflater = viewModel.mapActivity.activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.roadbook_maker_layout, null)
            var tv = view.findViewById<TextView>(R.id.marker_tv)
            tv.text = (index + 1).toString()
            var maker = viewModel.mapActivity.mAmap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromView(view)).position(LatLng(roadDetailItem.lat, roadDetailItem.lng)))
            maker.isClickable = true
            maker.title = "1"
            maker.snippet = roadDetailItem.imgUrl + ";" + roadDetailItem.routeName + ";" + roadDetailItem.address
            list.add(Location(roadDetailItem.lat, roadDetailItem.lng))
            makerList.add(maker)
        }

        var pass = ArrayList<LatLonPoint>()
        var fromAndTo = RouteSearch.FromAndTo(LatLonPoint(list[0].latitude, list[0].longitude), LatLonPoint(list[list.size - 1].latitude, list[list.size - 1].longitude))
        if (list.size > 2) {
            list.forEachIndexed { index, latLng ->
                if (index != 0 && index != list.size - 1) {
                    pass.add(LatLonPoint(latLng.latitude, latLng.longitude))
                }
            }
        }

        indexPath = recycleComponent.allData!!.indexOf(data) + 1
        if (pathList.containsKey(indexPath.toString())) {
            drawPath(pathList[indexPath.toString()])
        } else {
            viewModel.mapActivity.showProgressDialog(getString(R.string.loading_route))
            viewModel?.mapActivity.mapUtils?.setDriverRoute(fromAndTo.from, fromAndTo.to, list)

//            var query = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION,
//                    pass, null, "")
//            mRoutePath?.calculateDriveRouteAsyn(query)
        }
    }

    fun CalculateCallBack(result: AMapCalcRouteResult) {
        if (result.errorCode == 0) {
            var path = viewModel?.mapActivity.mapUtils?.navi?.naviPaths!![result.routeid[0]]
            drawPath(path)
        }
        viewModel?.mapActivity.dismissProgressDialog()
    }
}