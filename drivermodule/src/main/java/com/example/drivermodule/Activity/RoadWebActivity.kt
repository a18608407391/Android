package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.HotData
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBook.RoadWebViewModel
import com.example.drivermodule.databinding.ActivityRoadwebBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY)
class RoadWebActivity : BaseActivity<ActivityRoadwebBinding, RoadWebViewModel>() {

    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_WEB_TYPE)
    @JvmField
    var type: Int = 0

    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_WEB_ID)
    @JvmField
    var id: String? = null

    override fun initVariableId(): Int {
        return BR.road_web_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_roadweb
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    override fun initViewModel(): RoadWebViewModel? {
        return ViewModelProviders.of(this)[RoadWebViewModel::class.java]
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }


}