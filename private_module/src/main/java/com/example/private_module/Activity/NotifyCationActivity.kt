package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.NotifyCationViewModel
import com.example.private_module.databinding.ActivityNotifycationBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.PrivateModuleConfig.NOTIFYCATION)
class NotifyCationActivity : BaseActivity<ActivityNotifycationBinding, NotifyCationViewModel>() {
    override fun initVariableId(): Int {
        return BR.notifymodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_notifycation
    }

    override fun initViewModel(): NotifyCationViewModel? {
        return ViewModelProviders.of(this)[NotifyCationViewModel::class.java]
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
    override fun initData() {
        super.initData()

        mViewModel?.inject(this)
    }

}