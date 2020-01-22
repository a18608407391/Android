package com.example.drivermodule.Activity

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.MapViewModel
import com.example.drivermodule.databinding.ActivityMapBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MyLocationStyle
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.REQUEST_CREATE_JOIN
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.RESULT_POINT
import com.elder.zcommonmodule.Entity.HotData
import com.zk.library.Bus.ServiceEven
import com.example.drivermodule.Ui.DriverFragment
import com.example.drivermodule.Ui.MapPointFragment
import com.example.drivermodule.Ui.RoadBookFragment
import com.example.drivermodule.Ui.TeamFragment
import com.example.drivermodule.Utils.MapUtils
import com.example.drivermodule.ViewModel.TeamViewModel
import com.zk.library.Base.BaseApplication
import kotlinx.android.synthetic.main.activity_map.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


@Route(path = RouterUtils.MapModuleConfig.MAP_ACTIVITY)
class MapActivity : BaseActivity<ActivityMapBinding, MapViewModel>(), LocationSource, AMap.InfoWindowAdapter, AMap.OnMapClickListener, AMap.OnMapTouchListener, AMap.OnInfoWindowClickListener {
    override fun onInfoWindowClick(it: Marker?) {
        if (it!!.title == null || it!!.title == "null" || getTeamFragment().viewModel?.markerList?.containsValue(it)!!) {

        } else {
            if (mViewModel?.currentPosition == 0) {
                if (it.position != null && it.position.longitude != null && it.position.latitude != null && !it.title.isEmpty() && !it.title.contains("台湾省")) {
                    getDrverFragment().onInfoWindowClick(it)
                } else {
                    Toast.makeText(context, getString(R.string.cannot_go_to_there), Toast.LENGTH_SHORT).show()
                }
            } else if (mViewModel?.currentPosition == 1) {


            } else if (mViewModel?.currentPosition == 2) {
                if (it.position != null && it.position.longitude != null && it.position.latitude != null && !it.title.isEmpty() && !it.title.contains("台湾省")) {
                    getMapPointFragment().onInfoWindowClick(it)
                } else {
                    Toast.makeText(context, getString(R.string.cannot_go_to_there), Toast.LENGTH_SHORT).show()
                }
            } else if (mViewModel?.currentPosition == 3) {

            }
        }
    }

    var mapUtils: MapUtils? = null


    override fun onTouch(p0: MotionEvent?) {
    }

    override fun onMapClick(p0: LatLng?) {
        var fr = mViewModel?.mFragments!![1] as TeamFragment
        fr?.MapClick(p0)
    }

    override fun getInfoContents(maker: Marker?): View {
        var s = mViewModel?.mFragments!![1] as TeamFragment
        var flag = s.viewModel?.markerList?.containsValue(maker)
        if (flag!!) {
            var view = layoutInflater?.inflate(R.layout.team_popuwindow_custom, null, false)
            s.custionView(maker, view)
            return view!!
        } else {
            if (maker?.title == null || maker?.title.isEmpty() || maker?.title == "null") {
                return null!!
            } else {
                if (getRoadBookFragment().viewModel?.makerList!!.contains(maker)) {
                    var view = layoutInflater?.inflate(R.layout.roadbook_popuwindow_custom1, null, false)
                    getRoadBookFragment().viewModel?.customView(maker, view)
                    return view!!
                } else {
                    var view = layoutInflater?.inflate(R.layout.popuwindow_custom, null, false)
                    custionView(maker, view)
                    return view!!
                }
            }
        }
    }

    override fun getInfoWindow(maker: Marker?): View {
        var s = mViewModel?.mFragments!![1] as TeamFragment
        var flag = s.viewModel?.markerList?.containsValue(maker)
        if (flag!!) {
            var view = layoutInflater?.inflate(R.layout.team_popuwindow_custom, null, false)
            s.custionView(maker, view)
            return view!!
        } else {
            if (maker?.title == null || maker?.title.isEmpty() || maker?.title == "null") {
                return null!!
            } else {
                if (getRoadBookFragment().viewModel?.makerList!!.contains(maker)) {
                    var view = layoutInflater?.inflate(R.layout.roadbook_popuwindow_custom1, null, false)
                    getRoadBookFragment().viewModel?.customView(maker, view)
                    return view!!
                } else {
                    var view = layoutInflater?.inflate(R.layout.popuwindow_custom, null, false)
                    custionView(maker, view)
                    return view!!
                }
            }
        }
    }

    private fun custionView(maker: Marker?, view: View?) {
        var title = maker?.title.toString()
        var ti = view?.findViewById<TextView>(R.id.window_title)
        ti?.text = title
        var type = view?.findViewById<TextView>(R.id.navigation_type)
        type?.text = maker?.snippet.toString()
    }

    var mLocationOption: AMapLocationClientOption? = null
    var mListener: LocationSource.OnLocationChangedListener? = null
    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient?.stopLocation()
            mlocationClient?.onDestroy()
        }
        mlocationClient = null
    }

    fun setDriverStyle() {
        mLocationOption?.isSensorEnable = false
        mlocationClient?.setLocationOption(mLocationOption)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        myLocationStyle.showMyLocation(false)
        mAmap.myLocationStyle = myLocationStyle
    }


    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        mListener = listener
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(context)
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

    var onStart = false
    var driverData = DriverDataStatus()

    @Autowired(name = RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY)
    @JvmField
    var resume: String? = null


    @Autowired(name = RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY_ROAD)
    @JvmField
    var hotData: HotData? = null


    override fun initVariableId(): Int {
        return BR.mapViewModel
    }


    lateinit var mAmap: AMap
    override fun initMap(savedInstanceState: Bundle?) {
        super.initMap(savedInstanceState)
        map_view.onCreate(savedInstanceState)
        mAmap = map_view.map
        setUpMap()
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        mAmap.uiSettings.isZoomControlsEnabled = false
        mAmap.uiSettings.isMyLocationButtonEnabled = false
        mAmap.setInfoWindowAdapter(this)
        mAmap.setOnMarkerDragListener(mViewModel)
        mAmap.setOnCameraChangeListener(mViewModel)
        mAmap.setOnMarkerClickListener(mViewModel)
        mAmap.setOnMapClickListener(this)
        mAmap.setOnMapTouchListener(this)
        mAmap.setOnInfoWindowClickListener(this)
        mapUtils = MapUtils(this)
    }

    private fun setUpMap() {
        mAmap.setLocationSource(this)// 设置定位监听
        mAmap.uiSettings.isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
        mAmap.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle()
    }

    var mlocationClient: AMapLocationClient? = null
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


    override fun initContentView(savedInstanceState: Bundle?): Int {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_map
    }

    override fun initViewModel(): MapViewModel? {
        return ViewModelProviders.of(this)[MapViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    fun getDrverFragment(): DriverFragment {
        return mViewModel?.mFragments!![0] as DriverFragment
    }

    fun getTeamFragment(): TeamFragment {
        return mViewModel?.mFragments!![1] as TeamFragment
    }

    fun getMapPointFragment(): MapPointFragment {
        return mViewModel?.mFragments!![2] as MapPointFragment
    }

    fun getRoadBookFragment(): RoadBookFragment {
        return mViewModel?.mFragments!![3] as RoadBookFragment
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("result", "requestCode" + requestCode + "resultCode" + resultCode)
        if (resultCode == REQUEST_CREATE_JOIN) {
            mViewModel?.changerFragment(1)
            (mViewModel?.mFragments!![1] as TeamFragment).doCreate(data)
        } else {
            if (requestCode == RESULT_POINT) {
                (mViewModel?.mFragments!![0] as DriverFragment).setResult(requestCode, resultCode, data)
            } else {
                if (requestCode == REQUEST_LOAD_ROADBOOK) {
                    if (data != null) {
                        mViewModel?.changerFragment(3)
                        getRoadBookFragment().viewModel?.doLoadDatas(data!!)
                    } else {
                        Log.e("result", "data为Null")
                        if (mViewModel?.currentPosition != 3) {
                            mViewModel?.selectTab(mViewModel?.currentPosition!!)
                        }
                    }
                } else {
                    getMapPointFragment().viewModel?.SearchResult(requestCode, resultCode, data)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        map_view.onSaveInstanceState(outState)
    }

    override fun doPressBack() {
        super.doPressBack()
        mViewModel?.onComponentClick(View(this))
    }
    override fun onResume() {
        super.onResume()
        map_view.onResume()
        onStart = true
        BaseApplication.getInstance().curActivity = 2
    }
    override fun onDestroy() {
        map_view.onDestroy()
        dismissProgressDialog()
        dismissDialog()
        RxSubscriptions.remove(mViewModel?.a)
        RxSubscriptions.remove(getMapPointFragment().viewModel?.RegeocodeResultDispose)
        RxSubscriptions.remove(getDrverFragment().viewModel?.StringDispose)
        getTeamFragment().viewModel?.removeDispose()

        var pos = ServiceEven()
        pos.type = "MinaServiceCancle"
        RxBus.default?.post(pos)
        super.onDestroy()
//        context.startService(Intent(context, LowLocationService::class.java).setAction(SERVICE_CANCLE_MINA))
        Log.e("result", "关机检测")

    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
        onStart = false
    }

}