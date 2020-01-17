package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.RequestViewModel
import com.example.private_module.databinding.ActivityRequestIdeaBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils

@Route(path = RouterUtils.PrivateModuleConfig.USERREQUEST)
class RequestIdeaActivity : BaseActivity<ActivityRequestIdeaBinding, RequestViewModel>() {
    override fun initVariableId(): Int {
        return BR.request_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_request_idea
    }
    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun initViewModel(): RequestViewModel? {
        return ViewModelProviders.of(this)[RequestViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        mViewModel?.inject(this)

    }


}