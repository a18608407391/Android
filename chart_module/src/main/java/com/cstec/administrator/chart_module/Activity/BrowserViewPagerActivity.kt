package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.BrowserViewModel
import com.cstec.administrator.chart_module.databinding.ActivityBrowserPagerBinding
import com.zk.library.Base.BaseActivity


//用于浏览图片
class BrowserViewPagerActivity : BaseActivity<ActivityBrowserPagerBinding, BrowserViewModel>() {
    override fun initVariableId(): Int {
        return BR.brower_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_browser_pager
    }

    override fun initViewModel(): BrowserViewModel? {
        return ViewModelProviders.of(this)[BrowserViewModel::class.java]
    }
}
