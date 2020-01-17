package com.example.drivermodule.Activity

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.TileOverlayOptions
import com.amap.api.maps.model.UrlTileProvider
import com.amap.api.navi.AMapNavi
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.SmoothViewModel
import com.example.drivermodule.databinding.ActivitySmoothBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_smooth.*
import kotlinx.android.synthetic.main.fragment_driver.*
import org.cs.tec.library.Base.Utils.context
import java.net.URL

@Route(path = RouterUtils.MapModuleConfig.SMOOTH_ACTIVITY)
class SmoothActivity : BaseActivity<ActivitySmoothBinding, SmoothViewModel>(), LocationSource, AMapLocationListener {
    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.errorCode == 0) {
                mListener?.onLocationChanged(amapLocation)// 显示系统小蓝点
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
                myLocationStyle.showMyLocation(false)
                smoothmap.map.myLocationStyle = myLocationStyle
            }
        }
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient?.stopLocation()
            mlocationClient?.onDestroy()
        }
        mlocationClient = null
    }

    private var mListener: LocationSource.OnLocationChangedListener? = null
    private var mLocationOption: AMapLocationClientOption? = null
    var mlocationClient: AMapLocationClient? = null
    override fun activate(p0: LocationSource.OnLocationChangedListener?) {
        mListener = p0
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(context)
            mLocationOption = AMapLocationClientOption()
            //设置定位监听
            mlocationClient?.setLocationListener(this)
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

    override fun initVariableId(): Int {
        return BR.smooth_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_smooth
    }

    override fun initViewModel(): SmoothViewModel? {
        return ViewModelProviders.of(this)[SmoothViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    lateinit var navi: AMapNavi
    override fun setMap(savedInstanceState: Bundle?) {
        super.setMap(savedInstanceState)
        smoothmap.onCreate(savedInstanceState)
        smoothmap.map.mapType = AMap.MAP_TYPE_SATELLITE
//        var url = "https://mt3.google.cn/maps/vt?lyrs=s@194&hl=zh-CN&gl=cn&x=%d&y=%d&z=%d"
//        var tileOverlayOptions = TileOverlayOptions().tileProvider(object : UrlTileProvider(256, 256) {
//            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
//                try {
//                    return URL(String.format(url, x, y, zoom))
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                return null
//            }
//        })
//        tileOverlayOptions.diskCacheEnabled(true)
//                .diskCacheDir("/storage/emulated/0/amap/OMCcache")
//                .diskCacheSize(100000)
//                .memoryCacheEnabled(true)
//                .memCacheSize(100000)
//                .zIndex(-9999F)
//
//
//
//        smoothmap.map.apply {
//            this.isTrafficEnabled = false
//            this.showMapText(false)
//            this.showBuildings(false)
//        }
//        smoothmap.map.addTileOverlay(tileOverlayOptions)

//        navi = AMapNavi.getInstance(context)
//        navi.addAMapNaviListener(mViewModel)
//        smoothmap.setAMapNaviViewListener(mViewModel)
//        navi.setEmulatorNaviSpeed(75)
        smoothmap.map.moveCamera(CameraUpdateFactory.zoomTo(18F))
        smoothmap.map.setLocationSource(this)// 设置定位监听
        smoothmap.map.uiSettings.isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
        smoothmap.map.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle()
    }

    lateinit var myLocationStyle: MyLocationStyle
    private fun setupLocationStyle() {
        myLocationStyle = MyLocationStyle()
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked))
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.TRANSPARENT)
        //自定义精度范围的圆形边框宽度
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.TRANSPARENT)
        // 将自定义的 myLocationStyle 对象添加到地图上
        smoothmap.map.myLocationStyle = myLocationStyle
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        smoothmap.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        smoothmap.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        smoothmap.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        smoothmap.onResume()
    }
}