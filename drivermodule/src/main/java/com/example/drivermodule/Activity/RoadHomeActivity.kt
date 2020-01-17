package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.REQUEST_SEARCH_ROADBOOK
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBook.AcRoadBookViewModel
import com.example.drivermodule.databinding.ActivityRoadbookBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_roadbook.*


@Route(path = RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY)
class RoadHomeActivity : BaseActivity<ActivityRoadbookBinding, AcRoadBookViewModel>() {
    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_CURRENT_TYPE)
    @JvmField
    var type: Int = 0

    override fun initVariableId(): Int {
        return BR.roadbook_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_roadbook
    }

    override fun initViewModel(): AcRoadBookViewModel? {
        return ViewModelProviders.of(this)[AcRoadBookViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        road_book_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabLayout))
        mTabLayout.setupWithViewPager(road_book_viewpager)
        swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swipe.setOnRefreshListener(mViewModel)
        mViewModel?.inject(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOAD_ROADBOOK) {
            if (data != null) {
                setResult(REQUEST_LOAD_ROADBOOK, data)
                finish()
            }
        }
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
}