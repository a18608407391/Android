package com.elder.logrecodemodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.ViewModel.SameCityViewModel
import com.elder.logrecodemodule.databinding.ActivitySearchBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_search.*


@Route(path = RouterUtils.LogRecodeConfig.SEARCH_MEMBER)
class SearchActivity : BaseActivity<ActivitySearchBinding, SameCityViewModel>() {

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null


    override fun initVariableId(): Int {
        return BR.search_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_search
    }

    override fun initViewModel(): SameCityViewModel? {
        return ViewModelProviders.of(this)[SameCityViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        ets.setOnEditorActionListener(mViewModel)
        mViewModel?.inject(this)
    }
}