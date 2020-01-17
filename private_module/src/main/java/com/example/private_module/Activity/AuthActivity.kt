package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.REQUEST_BOND_CAR
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.AuthViewModel
import com.example.private_module.databinding.ActivityAuthBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.PrivateModuleConfig.MemberAuth)
class AuthActivity : BaseActivity<ActivityAuthBinding, AuthViewModel>() {
    override fun initVariableId(): Int {
        return BR.auth_ViewModel
    }
    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_auth
    }
    override fun initViewModel(): AuthViewModel? {
        return ViewModelProviders.of(this)[AuthViewModel::class.java]
    }
    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("result","执行++++++")
            mViewModel?.initDatas()
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        finish()
    }
}