package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.SettingViewModel
import com.example.private_module.databinding.ActivitySettingBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PrivateModuleConfig.USER_SETTING)
class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>() {
    @Autowired(name = RouterUtils.PrivateModuleConfig.SETTING_CATEGORY)
    @JvmField
    var entity: Int = 0

    override fun initVariableId(): Int {
        return BR.setting_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_setting
    }

    override fun initViewModel(): SettingViewModel? {
        return ViewModelProviders.of(this)[SettingViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    override fun doPressBack() {
        super.doPressBack()
        if (entity == 0) {
            finish()
        } else {
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
            finish()
        }
    }
}