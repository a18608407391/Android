package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBook.RoadBookSearchViewModel
import com.example.drivermodule.databinding.ActivityRoadbookSearchBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_roadbook_search.*


@Route(path = RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY)
class RoadBookSearchActivity : BaseActivity<ActivityRoadbookSearchBinding, RoadBookSearchViewModel>() {
    override fun initVariableId(): Int {
        return BR.roadbooksearch_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_roadbook_search
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
        etx.setOnEditorActionListener(mViewModel)
    }

    override fun initViewModel(): RoadBookSearchViewModel? {
        return ViewModelProviders.of(this)[RoadBookSearchViewModel::class.java]
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOAD_ROADBOOK) {
            setResult(REQUEST_LOAD_ROADBOOK, data)
            finish()
        }
    }
    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
}