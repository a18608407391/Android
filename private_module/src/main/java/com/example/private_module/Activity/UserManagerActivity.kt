package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.UserManagerViewModel
import com.example.private_module.databinding.ActivityUsermanegerBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PrivateModuleConfig.USERMANAGER)
class UserManagerActivity : BaseActivity<ActivityUsermanegerBinding, UserManagerViewModel>() {
    override fun initVariableId(): Int {
        return BR.usermanager
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_usermaneger
    }


    override fun doPressBack() {
        super.doPressBack()
        finish()
    }


    override fun initViewModel(): UserManagerViewModel? {
        return ViewModelProviders.of(this)[UserManagerViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
}