package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.services.core.LatLonPoint
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadDetailViewModel
import com.example.drivermodule.databinding.ActivityRoadDetailBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.MapModuleConfig.ROAD_DETAIL)
class RoadDetailActivity : BaseFragment<ActivityRoadDetailBinding, RoadDetailViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_road_detail
    }
    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_DATA)
    @JvmField
    var data: ArrayList<LatLonPoint>? = null
    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_DISTANCE)
    @JvmField
    var distance: Float = 0F
    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_TIME)
    @JvmField
    var time: Long = 0
    override fun initVariableId(): Int {
        return BR.road_detail_model
    }
    fun setValue(data: ArrayList<LatLonPoint>, distance: Float, time: Long): RoadDetailActivity {
        this.data = data
        this.distance = distance
        this.time = time
        return this@RoadDetailActivity
    }
    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }

}