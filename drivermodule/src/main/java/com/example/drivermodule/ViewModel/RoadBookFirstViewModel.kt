package com.example.drivermodule.ViewModel

import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.Transition
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.drivermodule.Fragment.RoadBookFirstActivity
import com.example.drivermodule.BR
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.getRoadImgUrl
import com.example.drivermodule.Component.SimpleItemRecycleComponent
import com.example.drivermodule.Entity.RoadBook.*
import com.example.drivermodule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget
import kotlinx.android.synthetic.main.activity_roadbook_first.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.*
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat


class RoadBookFirstViewModel : BaseViewModel(), HttpInteface.RoadBookDetail, RouteSearch.OnRouteSearchListener, TitleComponent.titleComponentCallBack, HttpInteface.DownLoadRoodBook, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
    override fun onTabReselected(p0: TabLayout.Tab?) {
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {

        initTabSelect(p0!!.position, true)
    }

    override fun DownLoadRoadBookSuccess(resp: BaseResponse) {
        activity.dismissProgressDialog()
        if (resp.code == 0) {
            var ho = PreferenceUtils.getString(activity.activity, PreferenceUtils.getString(context, USERID) + "hot")
            var s = Gson().fromJson<HotData>(ho, HotData::class.java)
            if (s == null || s.id == activity.data!!.id) {
                var bundle = Bundle()
                bundle.putSerializable("hotdata", activity.data)
                activity.setFragmentResult(REQUEST_LOAD_ROADBOOK, bundle)
                activity._mActivity!!.onBackPressedSupport()
//                var intent = Intent()
//                intent.putExtra("hotdata", activity.data)
//                activity.setResult(REQUEST_LOAD_ROADBOOK, intent)
//                finish()
            } else {
                var dialog = DialogUtils.createNomalDialog(activity.activity!!, getString(R.string.isExChangeLine), getString(R.string.cancle), getString(R.string.confirm))
                dialog.setOnBtnClickL(OnBtnClickL {
                    dialog.dismiss()
                }, OnBtnClickL {
                    dialog.dismiss()
                    var bundle = Bundle()
                    bundle.putSerializable("hotdata", activity.data)
                    activity.setFragmentResult(REQUEST_LOAD_ROADBOOK, bundle)
                    activity._mActivity!!.onBackPressedSupport()
                })
                dialog.show()
            }
        } else {
            Toast.makeText(context, resp.msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun initTabSelect(position: Int, type: Boolean) {
        if (recycleComponent.allData != null) {
            if (position == 0) {
                items!!.clear()
                recycleComponent.initDatas(netWorkData!!, data, 0)
                createMarker(recycleComponent!!.allData!!)
//                bottomVisible!!.set(false)
                activity.behavior_by_routes!!.state = BottomSheetBehavior.STATE_COLLAPSED
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
                    activity.behavior_by_routes!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }

    fun drawPath(p0: DriveRouteResult?) {
        if (!pathList.containsKey(indexPath.toString())) {
            pathList.put(indexPath.toString(), p0!!)
        }
        var list = ArrayList<LatLng>()
        var b = LatLngBounds.builder()
        p0?.paths!![0]!!.steps.forEach {
            it.polyline.forEach {
                list.add(LatLng(it.latitude, it.longitude))
                b.include(LatLng(it.latitude, it.longitude))
            }
        }

        activity.mAmap.animateCamera(CameraUpdateFactory
                .newLatLngBounds(b.build(), getWindowWidth()!!, ConvertUtils.dp2px(345F - 130), 150))
        lines?.points = list
    }

    override fun DownLoadRoadBookError(ex: Throwable) {
        Log.e("result", ex.message)
        activity.dismissProgressDialog()
    }

    override fun onComponentClick(view: View) {
        activity.setFragmentResult(REQUEST_LOAD_ROADBOOK, null)
        activity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
        //加载路书
        activity.showProgressDialog(getString(R.string.http_loading))
        var map = HashMap<String, String>()
        map["guideId"] = activity.data?.id.toString()
        HttpRequest.instance.DownLoadRoadBook(map)
    }

    var bottomVisible = ObservableField<Boolean>(false)

    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
        if (p1 == 1000) {
            drawPath(p0)
        }
        activity.dismissProgressDialog()
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
    }


    override fun getRoadBookDetailSuccess(it: String) {
        activity.dismissProgressDialog()
        if (activity.data != null) {
            CoroutineScope(ioContext).async {
                netWorkData = Gson().fromJson<ArrayList<RoadDetailEntity>>(it, object : TypeToken<ArrayList<RoadDetailEntity>>() {}.type)
                launch(uiContext) {
                    recycleComponent.initDatas(netWorkData!!, activity.data, 0)
                }
            }
        }
    }

    override fun getRoadBookDetailError(ex: Throwable) {
        activity.dismissProgressDialog()
    }

    var netWorkData: ArrayList<RoadDetailEntity>? = null
    var data: HotData? = null
    var title = ObservableField<String>()
    var id = ObservableField<String>()
    var pointAddress = ObservableField<String>()
    var dayCount = ObservableField<Int>()
    var distance_andtime = ObservableField<String>()

    var discribe = ObservableField<String>()

    var recycleComponent = SimpleItemRecycleComponent(this)


    var pagerAdapter = BindingRecyclerViewAdapter<BottomHoriDatas>()

    var itemBinding = ItemBinding.of<BottomHoriDatas>(BR.horiDatas, R.layout.roadbook_first_bottom_hori_item_layout).bindExtra(BR.road_model, this)

    var items = ObservableArrayList<BottomHoriDatas>()
    var ScrollChangeCommand = BindingCommand(object : BindingConsumer<Int> {

        override fun call(t: Int) {
            if (lastMarker == null) {
                return
            }
            if (t == 0) {
                var layout = activity.first_bottom_pager.layoutManager as LinearLayoutManager
                if (layout.findLastCompletelyVisibleItemPosition() >= 0) {
                    markerChange(makerList[layout.findLastCompletelyVisibleItemPosition()])
                }
                activity.mAmap.moveCamera(CameraUpdateFactory.newLatLng(lastMarker!!.position))
            }
        }
    })


    fun doLoadDatas(data: HotData) {
        activity.showProgressDialog(getString(R.string.getRoadBookLoading))
        HttpRequest.instance.RoadBookDetail = this
        if (data != null) {
            this.data = data
            var map = HashMap<String, String>()
            map["guideId"] = data!!.id.toString()
            HttpRequest.instance.getRoadBookDetail(map)
        } else {
            Log.e("result", "返回的值是null")
        }
    }

    fun BottomhoriClick(data: BottomHoriDatas) {

    }

    var componet = TitleComponent()
    var makerList = ArrayList<Marker>()
    var lines: Polyline? = null
    var lastMarker: Marker? = null
    var mRoutePath: RouteSearch? = null
    var currentRoad: RoadDetailEntity? = null
    var indexPath = 0
    var pathList = HashMap<String, DriveRouteResult>()
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
        lines = activity.mAmap.addPolyline(PolylineOptions()
                .color(R.color.line_color)
                .width(18f).zIndex(0f).color(getColor(R.color.line_color)))
        var list = ArrayList<LatLng>()
        data.pointList!!.forEachIndexed { index, roadDetailItem ->
            var inflater = activity.activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.roadbook_maker_layout, null)
            var tv = view.findViewById<TextView>(R.id.marker_tv)
            tv.text = (index + 1).toString()
            var maker = activity.mAmap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromView(view)).position(LatLng(roadDetailItem.lat, roadDetailItem.lng)))
            maker.isClickable = true
            maker.title = "1"
//            maker.title =
            maker.snippet = roadDetailItem.imgUrl + ";" + roadDetailItem.routeName + ";" + roadDetailItem.address + ":" + index
            list.add(LatLng(roadDetailItem.lat, roadDetailItem.lng))
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
            activity.showProgressDialog(getString(R.string.loading_route))
            var query = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION,
                    pass, null, "")
            mRoutePath?.calculateDriveRouteAsyn(query)
        }
    }

    var times = 0L

    fun selectTab(position: Int) {
        var tabs = activity.first_road_tab.getTabAt(position)
        tabs?.select()
    }

    fun markerChange(p0: Marker?) {
        if (System.currentTimeMillis() - times < 200) {
            times = System.currentTimeMillis()
            return
        }
        if (p0 == lastMarker) {
            return
        }

        if (activity.first_road_tab.selectedTabPosition == 0) {
            //HOME
            Log.e("result", p0?.snippet + "当前值")

            var index = Integer.valueOf(p0?.snippet)
            selectTab(index + 1)
        } else {
            if (lastMarker != null) {
                var inflater = activity.activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var view = inflater.inflate(R.layout.roadbook_maker_layout, null)
                var tv = view.findViewById<TextView>(R.id.marker_tv)
                var img = view.findViewById<ImageView>(R.id.marker_icon)
                img.setImageResource(R.drawable.green_oval)
                tv.text = (makerList.indexOf(lastMarker!!) + 1).toString()
                lastMarker?.setIcon(BitmapDescriptorFactory.fromView(view))
                lastMarker?.showInfoWindow()
            }
            var inflater = activity.activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.roadbook_maker_layout, null)
            var img = view.findViewById<ImageView>(R.id.marker_icon)
            img.setImageResource(R.drawable.orange_oval)
            var tv = view.findViewById<TextView>(R.id.marker_tv)
            tv.text = (makerList.indexOf(p0) + 1).toString()
            p0?.setIcon(BitmapDescriptorFactory.fromView(view))
            p0?.showInfoWindow()
            lastMarker = p0
            if (items.size == 0) {
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
            if (currentRoad?.pointList!!.size <= 1) {
                FrondVisible.set(false)
            } else {
                FrondVisible.set(true)
            }
            activity.behavior_by_routes!!.state = BottomSheetBehavior.STATE_COLLAPSED
            var s = makerList.indexOf(p0)
            activity.first_bottom_pager.smoothScrollToPosition(s)
            bottomVisible.set(false)

        }
    }

    lateinit var activity: RoadBookFirstActivity

    fun inject(roadBookFirstActivity: RoadBookFirstActivity) {
        this.activity = roadBookFirstActivity
        mRoutePath = RouteSearch(activity.activity)
        mRoutePath!!.setRouteSearchListener(this)
        activity.mAmap.setOnMarkerClickListener {
            Log.e("result", "markerChange")
            markerChange(it)
            return@setOnMarkerClickListener true
        }
        componet.title.set(getString(R.string.road_book_nomal_title))
        componet.rightIcon.set(context.getDrawable(R.drawable.road_path_icon))
        componet.rightText.set("")
        componet.rightVisibleType.set(false)
        componet.arrowVisible.set(false)
        componet.callback = this
        HttpRequest.instance.downLoadRoad = this
        CoroutineScope(uiContext).launch {
            delay(500)
            doLoadDatas(roadBookFirstActivity.data!!)
        }

        roadBookFirstActivity.first_road_tab.addOnTabSelectedListener(this)
    }

    var currentRoadList: ArrayList<RoadDetailEntity>? = null
    fun createMarker(data: ArrayList<RoadDetailEntity>) {
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

        this.currentRoadList = data
        lines = activity.mAmap.addPolyline(PolylineOptions()
                .color(R.color.line_color)
                .width(18f).zIndex(0f).color(getColor(R.color.line_color)))
        var list = ArrayList<LatLng>()
        var b = LatLngBounds.builder()
        data.forEachIndexed { index, roadDetailItem ->
            var inflater = activity.activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.roadbook_popuwindow_custom, null)
            var tv = view.findViewById<TextView>(R.id.window_day)
            tv.text = "D" + (index + 1).toString()
            var tv1 = view.findViewById<TextView>(R.id.window_day_count)
            var img = view.findViewById<ImageView>(R.id.window_img)
            tv1.text = roadDetailItem.pointList!!.size.toString()
            var corner = RoundedCorners(ConvertUtils.dp2px(5F))
            var opition = RequestOptions().transform(corner).error(R.drawable.road_windown_img).override(ConvertUtils.dp2px(32F), ConvertUtils.dp2px(32F))
            if (activity == null) {
                Glide.with(img.context).load(getRoadImgUrl(roadDetailItem.pointList!![0].imgUrl)).apply(opition).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        super.onResourceReady(resource, transition)
                        img.setImageDrawable(resource)
                        var maker = activity.mAmap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromView(view)).position(LatLng(roadDetailItem.lat, roadDetailItem.lng)))
                        maker.isClickable = true
                        maker.snippet = index.toString()
                        makerList.add(maker)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        img.setImageDrawable(errorDrawable)
                        var maker = activity.mAmap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromView(view)).position(LatLng(roadDetailItem.lat, roadDetailItem.lng)))
                        maker.isClickable = true
                        maker.snippet = index.toString()
                        makerList.add(maker)
                        super.onLoadFailed(errorDrawable)
                    }
                })
            }

            list.add(LatLng(roadDetailItem.lat, roadDetailItem.lng))
            b.include(LatLng(roadDetailItem.lat, roadDetailItem.lng))
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
        indexPath = 0
        if (pathList.isEmpty()) {
            activity.showProgressDialog(getString(R.string.loading_route))
            var query = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION,
                    pass, null, "")
            mRoutePath?.calculateDriveRouteAsyn(query)
        } else {
            drawPath(pathList["0"])
        }
    }


    fun BackRoadClick(data: BottomHoriDatas) {
        var index = items.indexOf(data)
        var ms = items[index - 1]
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 3).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, ms.roadid + "," + data.roadid).navigation()
    }

    fun FrontRoadClick(data: BottomHoriDatas) {
        var index = items.indexOf(data)
        var ms = items[index + 1]
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 3).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, data.roadid + "," + ms.roadid).navigation()
    }

    fun MiddleRoadClick(data: BottomHoriDatas) {
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 0).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, data.roadid).navigation()
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.road_book_first_detail -> {
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 1).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, id.get()).navigation()
            }
        }
    }

    var FrondVisible = ObservableField<Boolean>(false)

    fun NextRoadClick(view: View) {
        var index = makerList.indexOf(lastMarker)
        var ms = items[index + 1]
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 3).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, items[index].roadid + "," + ms.roadid).navigation()
    }
}