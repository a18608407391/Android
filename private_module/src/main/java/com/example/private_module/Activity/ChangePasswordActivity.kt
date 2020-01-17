package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.ChangePasswordViewModel
import com.example.private_module.databinding.ActivityCgPassBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PrivateModuleConfig.CHANGEPASS)
class ChangePasswordActivity : BaseActivity<ActivityCgPassBinding, ChangePasswordViewModel>() {
    override fun initVariableId(): Int {
        return BR.change_pass_model
    }
    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.fullScreen(this)
        return R.layout.activity_cg_pass
    }
    override fun initViewModel(): ChangePasswordViewModel? {
        return ViewModelProviders.of(this)[ChangePasswordViewModel::class.java]
    }
    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
}