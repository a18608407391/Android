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
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.MapModuleConfig.ROAD_DETAIL)
class RoadDetailActivity : BaseActivity<ActivityRoadDetailBinding, RoadDetailViewModel>() {
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

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_road_detail
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun initViewModel(): RoadDetailViewModel? {
        return ViewModelProviders.of(this)[RoadDetailViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

}