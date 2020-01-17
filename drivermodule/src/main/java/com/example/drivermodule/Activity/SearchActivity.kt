package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.services.core.LatLonPoint
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.SearchViewModel
import com.example.drivermodule.databinding.ActivitySearchLocationBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.MapModuleConfig.SEARCH_ACTIVITY)
class SearchActivity : BaseActivity<ActivitySearchLocationBinding, SearchViewModel>() {
    @Autowired(name = RouterUtils.MapModuleConfig.SEARCH_MODEL)
    @JvmField
    var model: Int = 0
    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_search_location
    }

    override fun initVariableId(): Int {
        return BR.Search_ViewModel
    }

    override fun initViewModel(): SearchViewModel? {
        return ViewModelProviders.of(this)[SearchViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}