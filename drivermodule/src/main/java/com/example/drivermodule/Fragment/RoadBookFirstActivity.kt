package com.example.drivermodule.Fragment

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.LocationSource
import com.amap.api.maps.TextureMapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MyLocationStyle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.drivermodule.BR
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.getRoadImgUrl
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBookFirstViewModel
import com.example.drivermodule.databinding.ActivityRoadbookFirstBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Utils.ConvertUtils


@Route(path = RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY)
class RoadBookFirstActivity : BaseFragment<ActivityRoadbookFirstBinding, RoadBookFirstViewModel>(), AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, LocationSource {
    override fun deactivate() {

    }

    var mlocationClient: AMapLocationClient? = null
    var mLocationOption: AMapLocationClientOption? = null
    var mListener: LocationSource.OnLocationChangedListener? = null
    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        mListener = listener
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(org.cs.tec.library.Base.Utils.context)
            mLocationOption = AMapLocationClientOption()
            //设置定位监听
            //设置为高精度定位模式
            mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置定位参数
            mLocationOption?.interval = 1000
            mLocationOption?.isLocationCacheEnable = true
            mLocationOption?.isSensorEnable = true
            mLocationOption?.isGpsFirst = false
            mLocationOption?.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Transport
            AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP)
//            mLocationOption?.isOnceLocationLatest = true
            mlocationClient?.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient?.stopLocation()
            mlocationClient?.startLocation()
        }
    }

    override fun initContentView(): Int {
        return R.layout.activity_roadbook_first
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        viewModel?.markerChange(p0)
        return false
    }

    override fun getInfoContents(maker: Marker?): View {
        var view = layoutInflater?.inflate(R.layout.roadbook_popuwindow_custom1, null, false)
        var list = maker!!.snippet.split(";")
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
        return view!!
    }

    override fun getInfoWindow(maker: Marker?): View {
        var view = layoutInflater?.inflate(R.layout.roadbook_popuwindow_custom1, null, false)
        var list = maker!!.snippet.split(";")
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
        return view!!
    }
    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY)
    @JvmField
    var data: HotData? = null
    override fun initVariableId(): Int {
        return BR.roadbook_first_model
    }
    fun setHotData(data: HotData): RoadBookFirstActivity {
        this.data = data
        return this@RoadBookFirstActivity
    }
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
//
//    }
//    override fun initViewModel(): RoadBookFirstViewModel? {
//        return ViewModelProviders.of(this)[RoadBookFirstViewModel::class.java]
//    }

    override fun initMap(savedInstanceState: Bundle?) {
        super.initMap(savedInstanceState)
        first_map.onCreate(savedInstanceState)
//        mAmap = first_map.map
        setUpMap()
        mAmap.uiSettings.isZoomControlsEnabled = false
        mAmap.uiSettings.isMyLocationButtonEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (first_map != null) {
            first_map.onDestroy()
        }

    }

    private fun setUpMap() {
        mAmap.setLocationSource(this)// 设置定位监听
        mAmap.uiSettings.isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
        mAmap.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle()
    }

    lateinit var myLocationStyle: MyLocationStyle
    fun setupLocationStyle() {
        // 自定义系统定位蓝点
        myLocationStyle = MyLocationStyle()
        // 自定义定位蓝点图标

        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked))
        myLocationStyle.radiusFillColor(Color.parseColor("#303FC5C9"))
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.TRANSPARENT)
        //自定义精度范围的圆形边框宽度
        // 设置圆形的填充颜色
//        myLocationStyle.radiusFillColor(Color.TRANSPARENT)
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAmap.myLocationStyle = myLocationStyle
    }

    override fun onPause() {
        super.onPause()
        first_map.onPause()
    }

    override fun onResume() {
        super.onResume()
        first_map.onResume()
    }


    lateinit var mAmap: AMap
    lateinit var first_map : TextureMapView
    lateinit var first_roadbook_recycle :RecyclerView
    lateinit var first_bottom_pager :RecyclerView
    lateinit var behavior_by_rout :LinearLayout
    var behavior_by_routes: BottomSheetBehavior<LinearLayout>? = null
    override fun initData() {
        super.initData()
        first_roadbook_recycle =   binding!!.root!!.findViewById(R.id.first_roadbook_recycle)
        first_bottom_pager = binding!!.root!!.findViewById(R.id.first_bottom_pager)
        behavior_by_rout = binding!!.root!!.findViewById(R.id.behavior_by_rout)
        var hot = arguments!!.getSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY) as HotData
        if (hot != null) {
            this.data = hot
        }
        first_map =  binding!!.root!!.findViewById(R.id.first_map)
        mAmap = first_map.map
//        mAmap.clear()
        mAmap.setInfoWindowAdapter(this)
        mAmap.setOnMarkerClickListener(this)
        behavior_by_routes = BottomSheetBehavior.from<LinearLayout>(behavior_by_rout)
        behavior_by_routes!!.state = BottomSheetBehavior.STATE_COLLAPSED
        LinearSnapHelper().attachToRecyclerView(first_bottom_pager)
        first_roadbook_recycle.isNestedScrollingEnabled = true
        viewModel?.inject(this)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        first_map.onSaveInstanceState(outState)
    }
//
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

}